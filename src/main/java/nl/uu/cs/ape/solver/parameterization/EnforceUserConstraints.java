package nl.uu.cs.ape.solver.parameterization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.models.DomainTypes;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.parserSLTLx.SLTLxSATVisitor;
import nl.uu.cs.ape.solver.configuration.Domain;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnforceUserConstraints {

    /**
     * Encodes rules that ensure the initial workflow input.
     *
     * @param allTypes       Set of all the types in the domain
     * @param program_inputs Input types for the program.
     * @param typeAutomaton  Automaton representing the type states in the model
     * @return The String representation of the initial input encoding.
     * @throws APEConfigException Exception thrown when one of the output types is
     *                            not defined in the taxonomy.
     */
    public static Set<SLTLxFormula> workflowInputs(DomainTypes allTypes, List<Type> program_inputs,
            TypeAutomaton typeAutomaton) throws APEConfigException {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<State> workflowInputStates = typeAutomaton.getWorkflowInputBlock().getStates();
        for (int i = 0; i < workflowInputStates.size(); i++) {
            State currState = workflowInputStates.get(i);
            if (i < program_inputs.size()) {
                Type currType = program_inputs.get(i);
                if (allTypes.get(currType.getPredicateID()) == null) {
                    throw APEConfigException.workflowIODataTypeNotInDomain(currType.getPredicateID());
                }
                fullEncoding.add(
                        new SLTLxAtom(
                                AtomType.MEMORY_TYPE,
                                currType,
                                currState));
            } else {
                /* Forcing in the rest of the input states to be empty types. */
                fullEncoding.add(
                        new SLTLxAtom(
                                AtomType.MEMORY_TYPE,
                                allTypes.getEmptyType(),
                                currState));
            }
        }
        return fullEncoding;
    }

    /**
     * Encodes the rules that ensure generation of the workflow output.
     *
     * @param allTypes        Set of all the types in the domain
     * @param program_outputs Output types for the program.
     * @param typeAutomaton   Automaton representing the type states in the model
     * @return String representation of the workflow output encoding.
     * @throws APEConfigException Exception thrown when one of the output types is
     *                            not defined in the taxonomy.
     */
    public static Set<SLTLxFormula> workdlowOutputs(DomainTypes allTypes, List<Type> program_outputs,
            TypeAutomaton typeAutomaton) throws APEConfigException {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
        for (int i = 0; i < workflowOutputStates.size(); i++) {
            if (i < program_outputs.size()) {
                TaxonomyPredicate currType = program_outputs.get(i);
                if (allTypes.get(currType.getPredicateID()) == null) {
                    throw APEConfigException.workflowIODataTypeNotInDomain(currType.getPredicateID());
                }
                fullEncoding.add(
                        new SLTLxAtom(
                                AtomType.USED_TYPE,
                                currType,
                                workflowOutputStates.get(i)));

            } else {
                /* Forcing in the rest of the input states to be empty types. */
                fullEncoding.add(
                        new SLTLxAtom(
                                AtomType.USED_TYPE,
                                allTypes.getEmptyType(),
                                workflowOutputStates.get(i)));
            }

        }

        return fullEncoding;
    }

    /**
     * Encode APE constraints string.
     * 
     * @param synthesisEngine
     *
     * @param domainSetup     Domain information, including all the existing tools
     *                        and types.
     * @param mappings        Mapping function.
     * @param moduleAutomaton Module automaton.
     * @param typeAutomaton   Type automaton.
     * @return The CNF representation of the SLTLx constraints in our project.
     */
    public static String encodeAPEConstraints(SATSynthesisEngine synthesisEngine, Domain domainSetup,
            SATAtomMappings mappings,
            ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

        StringBuilder cnfSLTLx = new StringBuilder();
        int currConst = 0;

        for (ConstraintTemplateData constraint : domainSetup.getUnformattedConstr()) {
            currConst++;
            /* ENCODE THE CONSTRAINT */
            if (domainSetup.getConstraintTemplate(constraint.getConstraintID()) == null) {
                log.warn("Constraint ID provided: '" + constraint.getConstraintID()
                        + "' is not valid. Constraint skipped.");
            } else {
                String currConstrEncoding = constraintSATEncoding(constraint.getConstraintID(),
                        constraint.getParameters(), domainSetup, moduleAutomaton, typeAutomaton, mappings);
                if (currConstrEncoding == null) {
                    log.warn("Error in constraint file. Constraint no: " + currConst + ". Constraint skipped.");
                } else {
                    cnfSLTLx.append(currConstrEncoding);
                }
            }
        }

        /*
         * Parse the constraints specified in SLTLx.
         */
        for (String constraint : domainSetup.getSLTLxConstraints()) {
            Set<SLTLxFormula> sltlxFormulas = SLTLxSATVisitor.parseFormula(synthesisEngine, constraint);
            for (SLTLxFormula sltlxFormula : sltlxFormulas) {
                sltlxFormula.getConstraintCNFEncoding(synthesisEngine)
                        .forEach(cnfSLTLx::append);
            }
        }

        return cnfSLTLx.toString();
    }

    /**
     * Function used to provide SAT encoding of a constrain based on the constraint
     * ID specified and provided parameters.
     *
     * @param constraintID    ID of the constraint.
     * @param list            Parameters for the constraint template.
     * @param domainSetup     Domain information, including all the existing tools
     *                        and types.
     * @param moduleAutomaton Module automaton.
     * @param typeAutomaton   Type automaton.
     * @param mappings        Mapping function.
     * @return String representation of the SAT encoding for the specified
     *         constraint.
     */
    public static String constraintSATEncoding(String constraintID, List<TaxonomyPredicate> list,
            Domain domainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
            SATAtomMappings mappings) {

        return domainSetup.getConstraintTemplate(constraintID).getConstraint(list, domainSetup, moduleAutomaton,
                typeAutomaton, mappings);
    }
}
