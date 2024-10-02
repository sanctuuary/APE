package nl.uu.cs.ape.solver.minisat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.domain.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.AuxTypePredicate;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxImplication;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegatedConjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegation;

/**
 * The {@code EnforceTypeRelatedRules} class is used to encode SLTLx constraints
 * based on the
 * existing types that would encode the workflow structure.
 *
 * @author Vedran Kasalica
 */
public class EnforceTypeRelatedRules {

    /**
     * Private constructor is used to to prevent instantiation.
     */
    private EnforceTypeRelatedRules() {
        throw new UnsupportedOperationException();
    }

    /**
     * Generating the mutual exclusion for the pair of tools from @modules
     * (excluding abstract modules from the taxonomy) in each memory state
     * of @moduleAutomaton.
     * 
     * @param pair          pair of types from a dimension.
     * @param typeAutomaton System that represents states in the workflow
     * @return String representation of constraints.
     */
    public static Set<SLTLxFormula> memoryTypesMutualExclusion(Pair<PredicateLabel> pair, TypeAutomaton typeAutomaton) {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();
        PredicateLabel firstPair = pair.getFirst();
        PredicateLabel secondPair = pair.getSecond();
        // mutual exclusion of types in all the states (those that represent general
        // memory)
        for (State memTypeState : typeAutomaton.getAllMemoryTypesStates()) {
            fullEncoding.add(
                    new SLTLxNegatedConjunction(
                            new SLTLxAtom(
                                    AtomType.MEMORY_TYPE,
                                    firstPair,
                                    memTypeState),
                            new SLTLxAtom(
                                    AtomType.MEMORY_TYPE,
                                    secondPair,
                                    memTypeState)));
        }
        return fullEncoding;
    }

    /**
     * Generating the mutual exclusion for the pair of tools from @modules
     * (excluding abstract modules from the taxonomy) in each used state
     * of @moduleAutomaton.
     * 
     * @param pair          pair of types from a dimension.
     * @param typeAutomaton System that represents states in the workflow
     * @return String representation of constraints.
     */
    public static Set<SLTLxFormula> usedTypeMutualExclusion(Pair<PredicateLabel> pair, TypeAutomaton typeAutomaton) {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();
        PredicateLabel firstPair = pair.getFirst();
        PredicateLabel secondPair = pair.getSecond();
        // mutual exclusion of types in all the states (those that represent general
        // memory)
        // mutual exclusion of types in all the states (those that represent used
        // instances)
        for (State usedTypeState : typeAutomaton.getAllUsedTypesStates()) {
            fullEncoding.add(
                    new SLTLxNegatedConjunction(
                            new SLTLxAtom(
                                    AtomType.USED_TYPE,
                                    firstPair,
                                    usedTypeState),
                            new SLTLxAtom(
                                    AtomType.USED_TYPE,
                                    secondPair,
                                    usedTypeState)));
        }
        return fullEncoding;
    }

    /**
     * Generating the mandatory usage constraints of root type @rootType in each
     * state of @moduleAutomaton. It enforces that each type instance is either
     * defined on all the dimensions or is empty.
     *
     * @param domainSetup   Domain model
     * @param typeAutomaton System that represents states in the workflow
     * @return String representation of constraints.
     */
    public static Set<SLTLxFormula> typeMandatoryUsage(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton) {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();
        Type empty = domainSetup.getAllTypes().getEmptyType();
        Type dataType = AuxTypePredicate.generateAuxiliaryPredicate(
                domainSetup.getAllTypes().getDataTaxonomyDimensionsAsSortedSet(), LogicOperation.AND, domainSetup);
        // enforcement of types in in all the states (those that represent general
        // memory and used data instances)
        for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
            for (State memTypeState : typeBlock.getStates()) {
                fullEncoding.add(
                        new SLTLxDisjunction(
                                new SLTLxAtom(
                                        AtomType.MEMORY_TYPE,
                                        dataType,
                                        memTypeState),
                                new SLTLxAtom(
                                        AtomType.MEMORY_TYPE,
                                        empty,
                                        memTypeState)));
            }
        }
        for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
            for (State usedTypeState : typeBlock.getStates()) {
                fullEncoding.add(
                        new SLTLxDisjunction(
                                new SLTLxAtom(
                                        AtomType.USED_TYPE,
                                        dataType,
                                        usedTypeState),
                                new SLTLxAtom(
                                        AtomType.USED_TYPE,
                                        empty,
                                        usedTypeState)));
            }
        }

        return fullEncoding;
    }

    /**
     * Generating the mandatory usage of a subtypes in case of the parent type being
     * used, with respect to the Type Taxonomy. The rule starts from the @rootType
     * and it's valid in each state of @typeAutomaton. @emptyType denotes the type
     * that is being used if the state has no type.
     *
     * @param allTypes      Collection of all the types in the domain.
     * @param typeAutomaton System that represents states in the workflow
     * @return The String representation of constraints enforcing taxonomy
     *         classifications.
     */
    public static Set<SLTLxFormula> typeEnforceTaxonomyStructure(AllTypes allTypes, TypeAutomaton typeAutomaton) {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();
        // taxonomy enforcement of types in in all the states (those that represent
        // general memory and used data instances)
        for (TaxonomyPredicate dimension : allTypes.getRootPredicates()) {
            for (Block memTypeBlock : typeAutomaton.getMemoryTypesBlocks()) {
                for (State memTypeState : memTypeBlock.getStates()) {
                    fullEncoding.addAll(
                            typeEnforceTaxonomyStructureForState(dimension, memTypeState, AtomType.MEMORY_TYPE));
                }
            }
            for (Block usedTypeBlock : typeAutomaton.getUsedTypesBlocks()) {
                for (State usedTypeState : usedTypeBlock.getStates()) {
                    fullEncoding
                            .addAll(typeEnforceTaxonomyStructureForState(dimension, usedTypeState, AtomType.USED_TYPE));
                }
            }
        }
        return fullEncoding;
    }

    /**
     * Supporting recursive method for typeEnforceTaxonomyStructure.
     * 
     * @param currType    Current type
     * @param typeState   Current type state
     * @param typeElement Current type element
     * @return Set of the corresponding SLTLx formulas
     */
    private static Set<SLTLxFormula> typeEnforceTaxonomyStructureForState(TaxonomyPredicate currType,
            State typeState, AtomType typeElement) {

        SLTLxAtom superTypeState = new SLTLxAtom(typeElement, currType, typeState);

        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<SLTLxAtom> subTypesStates = new ArrayList<>();
        if (!(currType.getSubPredicates() == null || currType.getSubPredicates().isEmpty())) {
            /*
             * Ensuring the TOP-DOWN taxonomy tree dependency
             */
            for (TaxonomyPredicate subType : currType.getSubPredicates()) {

                SLTLxAtom subTypeState = new SLTLxAtom(typeElement, subType, typeState);
                subTypesStates.add(subTypeState);

                fullEncoding.addAll(typeEnforceTaxonomyStructureForState(subType, typeState, typeElement));
            }
            /*
             * Ensuring the TOP-DOWN taxonomy tree dependency
             */
            fullEncoding.add(
                    new SLTLxImplication(
                            superTypeState,
                            new SLTLxDisjunction(subTypesStates)));
            /*
             * Ensuring the BOTTOM-UP taxonomy tree dependency
             */
            for (SLTLxAtom subTypeState : subTypesStates) {
                fullEncoding.add(
                        new SLTLxImplication(
                                subTypeState,
                                superTypeState));
            }
        }
        return fullEncoding;
    }

    /**
     * Encodes rules that ensure the initial workflow input.
     *
     * @param allTypes       Set of all the types in the domain
     * @param programInputs Input types for the program.
     * @param typeAutomaton  Automaton representing the type states in the model
     * @return The String representation of the initial input encoding.
     * @throws APEConfigException Exception thrown when one of the output types is
     *                            not defined in the taxonomy.
     */
    public static Set<SLTLxFormula> workflowInputs(AllTypes allTypes, List<Type> programInputs,
            TypeAutomaton typeAutomaton) throws APEConfigException {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<State> workflowInputStates = typeAutomaton.getWorkflowInputBlock().getStates();
        for (int i = 0; i < workflowInputStates.size(); i++) {
            State currState = workflowInputStates.get(i);
            if (i < programInputs.size()) {
                Type currType = programInputs.get(i);
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
     * @param programOutputs Output types for the program.
     * @param typeAutomaton   Automaton representing the type states in the model
     * @return String representation of the workflow output encoding.
     * @throws APEConfigException Exception thrown when one of the output types is
     *                            not defined in the taxonomy.
     */
    public static Set<SLTLxFormula> workflowOutputs(AllTypes allTypes, List<Type> programOutputs,
            TypeAutomaton typeAutomaton) throws APEConfigException {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
        for (int i = 0; i < workflowOutputStates.size(); i++) {
            if (i < programOutputs.size()) {
                TaxonomyPredicate currType = programOutputs.get(i);
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
     * Encodes rules that ensure that the initial workflow inputs are not used as
     * workflow outputs.
     *
     * @param typeAutomaton Automaton representing the type states in the model
     * @return The String representation of the initial input encoding.
     * @throws APEConfigException Exception thrown when one of the output types is
     *                            not defined in the taxonomy.
     */
    public static Set<SLTLxFormula> inputsAreNotOutputs(
            TypeAutomaton typeAutomaton) throws APEConfigException {
        Set<SLTLxFormula> fullEncoding = new HashSet<>();

        List<State> workflowInputStates = typeAutomaton.getWorkflowInputBlock().getStates();
        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
        for (Pair<State> pairIO : APEUtils.getUniquePairs(workflowInputStates, workflowOutputStates)) {
            fullEncoding.add(
                    new SLTLxNegation(
                            new SLTLxAtom(
                                    AtomType.MEM_TYPE_REFERENCE,
                                    pairIO.getFirst(),
                                    pairIO.getSecond())));
        }
        return fullEncoding;
    }

}
