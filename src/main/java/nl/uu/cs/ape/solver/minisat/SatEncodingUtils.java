package nl.uu.cs.ape.solver.minisat;

import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.domain.APEDomainSetup;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.parserSLTLx.SLTLxSATVisitor;

/**
 * The {@code SatEncodingUtils} class is used to provide utility functions for
 * encoding constraints in SAT.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SatEncodingUtils {

    /**
     * Encode APE constraints string.
     * 
     * @param synthesisEngine the synthesis engine used to generate the CNF encoding
     *
     * @param domainSetup     Domain information, including all the existing tools
     *                        and types.
     * @param mappings        Mapping function.
     * @param moduleAutomaton Module automaton.
     * @param typeAutomaton   Type automaton.
     * @return The CNF representation of the SLTLx constraints in our project.
     */
    public static String encodeAPEConstraints(SATSynthesisEngine synthesisEngine, APEDomainSetup domainSetup,
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
            APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
            SATAtomMappings mappings) {

        return domainSetup.getConstraintTemplate(constraintID).getConstraint(list, domainSetup, moduleAutomaton,
                typeAutomaton, mappings);
    }

}
