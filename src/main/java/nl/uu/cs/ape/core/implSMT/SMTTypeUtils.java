package nl.uu.cs.ape.core.implSMT;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.*;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.smtStruc.SMTComment;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BinarySMTPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ForallStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NandStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBoundedVar;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionArgument;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;
import nl.uu.cs.ape.models.smtStruc.Assertion;
import nl.uu.cs.ape.models.smtStruc.SMT2LibRow;
import nl.uu.cs.ape.utils.APEDomainSetup;

/**
 * The {@code SMTTypeUtils} class is used to encode SAT constraints  based on the type annotations.
 *
 * @author Vedran Kasalica
 */
public class SMTTypeUtils {

    /**
     * Private constructor is used to to prevent instantiation.
     */
    private SMTTypeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Generating the mutual exclusion for each pair of tools from @modules
     * (excluding abstract modules from the taxonomy) in each state
     * of @moduleAutomaton.
     *
     * @param allTypes   - all data types in the domain
     * @return String representation of constraints.
     */
    public static List<SMT2LibRow> typeMutualExclusion(AllTypes allTypes) {

        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        allClauses.add(new SMTComment("Encoding rules of mutual exclusion of data types."));
        
        PredicateLabel firstPair, secondPair;
        SMTBoundedVar state = new SMTBoundedVar("state");
        for (Pair<PredicateLabel> pair : allTypes.getTypePairsForEachSubTaxonomy()) {
            firstPair = pair.getFirst();
            secondPair = pair.getSecond();
            
            // mutual exclusion of types in all the states (those that represent general memory)
            allClauses.add(new Assertion(
					new ForallStatement(
							state,
							new SMTDataType(WorkflowElement.MEMORY_TYPE),
							new NandStatement(
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(firstPair)), 
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.MEMORY_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(secondPair))
									)
							)
					));
            
            // mutual exclusion of types in all the states (those that represent used instances)
            allClauses.add(new Assertion(
					new ForallStatement(
							state,
							new SMTDataType(WorkflowElement.USED_TYPE),
							new NandStatement(
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(firstPair)), 
									new BinarySMTPredicate(new SMTFunctionName(WorkflowElement.USED_TYPE), new SMTFunctionArgument(state), new SMTFunctionArgument(secondPair))
									)
							)
					));
        }
        return allClauses;
    }

    /**
     * Generating the mandatory usage constraints of root type @rootType in each
     * state of @moduleAutomaton. It enforces that each type instance is either
     * defined on all the dimensions or is empty.
     *
     * @param domainSetup   TODO
     * @param typeAutomaton TODO
     * @param mappings      TODO
     * @return String representation of constraints.
     */
    public static List<SMT2LibRow> typeMandatoryUsage(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        Type empty = domainSetup.getAllTypes().getEmptyType();
        Type dataType = AuxTypePredicate.generateAuxiliaryPredicate(domainSetup.getAllTypes().getDataTaxonomyDimensionsAsSortedSet(), LogicOperation.AND, domainSetup);
        // enforcement of types in in all the states (those that represent general
        // memory and used data instances)
        for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
            for (State typeState : typeBlock.getStates()) {
                constraints = constraints.append(mappings.add(dataType, typeState, WorkflowElement.MEMORY_TYPE))
                        .append(" ");
                constraints = constraints.append(mappings.add(empty, typeState, WorkflowElement.MEMORY_TYPE))
                        .append(" 0\n");
            }
        }
        for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
            for (State typeState : typeBlock.getStates()) {
                constraints = constraints.append(mappings.add(dataType, typeState, WorkflowElement.USED_TYPE))
                        .append(" ");
                constraints = constraints.append(mappings.add(empty, typeState, WorkflowElement.USED_TYPE))
                        .append(" 0\n");
            }
        }

        return allClauses;
    }

    /**
     * Generating the mandatory usage of a subtypes in case of the parent type being
     * used, with respect to the Type Taxonomy. The rule starts from the @rootType
     * and it's valid in each state of @typeAutomaton. @emptyType denotes the type
     * that is being used if the state has no type.
     *
     * @param allTypes      TODO
     * @param typeAutomaton TODO
     * @param mappings      TODO
     * @return The String representation of constraints enforcing taxonomy classifications.
     */
    public static List<SMT2LibRow> typeEnforceTaxonomyStructure(AllTypes allTypes, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        // taxonomy enforcement of types in in all the states (those that represent
        // general memory and used data instances)
        for (TaxonomyPredicate dimension : allTypes.getRootPredicates()) {
            for (Block memTypeBlock : typeAutomaton.getMemoryTypesBlocks()) {
                for (State memTypeState : memTypeBlock.getStates()) {
                    constraints = constraints
                            .append(typeEnforceTaxonomyStructureForState(dimension, mappings, memTypeState, WorkflowElement.MEMORY_TYPE));
                }
            }
            for (Block usedTypeBlock : typeAutomaton.getUsedTypesBlocks()) {
                for (State usedTypeState : usedTypeBlock.getStates()) {
                    constraints = constraints.append(typeEnforceTaxonomyStructureForState(dimension, mappings, usedTypeState, WorkflowElement.USED_TYPE));
                }
            }
        }
        return allClauses;
    }

    /**
     * Supporting recursive method for typeEnforceTaxonomyStructure.
     */
    private static List<SMT2LibRow> typeEnforceTaxonomyStructureForState(TaxonomyPredicate currType,
                                                               SATAtomMappings mappings, State typeState, WorkflowElement typeElement) {

        String superType_State = mappings.add(currType, typeState, typeElement).toString();

        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();
        StringBuilder currConstraint = new StringBuilder("-").append(superType_State).append(" ");

        List<String> subTypes_States = new ArrayList<String>();
        if (!(currType.getSubPredicates() == null || currType.getSubPredicates().isEmpty())) {
            /*
             * Ensuring the TOP-DOWN taxonomy tree dependency
             */
            for (TaxonomyPredicate subType : currType.getSubPredicates()) {

                String subType_State = mappings.add(subType, typeState, typeElement).toString();
                currConstraint = currConstraint.append(subType_State).append(" ");
                subTypes_States.add(subType_State);

                constraints = constraints.append(typeEnforceTaxonomyStructureForState(subType, mappings, typeState, typeElement));
            }
            currConstraint = currConstraint.append("0\n");
            /*
             * Ensuring the BOTTOM-UP taxonomy tree dependency
             */
            for (String subType_State : subTypes_States) {
                currConstraint = currConstraint.append("-").append(subType_State).append(" ").append(superType_State)
                        .append(" 0\n");
            }
            return currConstraint.append(constraints).toString();
        } else {
            return "";
        }
    }

    /**
     * Encoding the initial workflow input.
     *
     * @param allTypes       Set of all the types in the domain
     * @param program_inputs Input types for the program.
     * @param typeAutomaton  Automaton representing the type states in the model
     * @param mappings       All the atom mappings
     * @return The String representation of the initial input encoding.
     */
    public static List<SMT2LibRow> encodeInputData(AllTypes allTypes, List<Type> program_inputs, TypeAutomaton typeAutomaton, SMTPredicateMappings mappings) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();

        List<State> workflowInputStates = typeAutomaton.getMemoryTypesBlock(0).getStates();
//        for (int i = 0; i < workflowInputStates.size(); i++) {
//        	State currState = workflowInputStates.get(i);
//            if (i < program_inputs.size()) {
//                Type currType = program_inputs.get(i);
//                    if (allTypes.get(currType.getPredicateID()) == null) {
//                        System.err.println(
//                                "Program input '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
//                        return null;
//                    }
//
//                    encoding = encoding.append(mappings.add(currType, currState, WorkflowElement.MEMORY_TYPE))
//                            .append(" 0\n");
//            } else {
//                /* Forcing in the rest of the input states to be empty types. */
//                encoding = encoding.append(mappings.add(allTypes.getEmptyType(), currState, WorkflowElement.MEMORY_TYPE))
//                        .append(" 0\n");
//            }
//        }
        return allClauses;
    }

    /**
     * Encoding the workflow output. The provided output files have to occur
     * as the final list of "used" data types. In the predefined order.
     *
     * @param allTypes        Set of all the types in the domain
     * @param program_outputs Output types for the program.
     * @param typeAutomaton   Automaton representing the type states in the model
     * @param mappings       All the atom mappings
     * @return String representation of the workflow output encoding.
     */
    public static List<SMT2LibRow> encodeOutputData(AllTypes allTypes, List<Type> program_outputs, TypeAutomaton typeAutomaton, SMTPredicateMappings mappings) {
        List<SMT2LibRow> allClauses = new ArrayList<SMT2LibRow>();

        List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
//        for (int i = 0; i < workflowOutputStates.size(); i++) {
//            if (i < program_outputs.size()) {
//            	TaxonomyPredicate currType = program_outputs.get(i);
//                    if (allTypes.get(currType.getPredicateID()) == null) {
//                        System.err.println(
//                                "Program output '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
//                        return null;
//                    }
//                    encoding = encoding.append(mappings.add(currType, workflowOutputStates.get(i), WorkflowElement.USED_TYPE))
//                            .append(" 0\n");
////					currType.setAsRelevantTaxonomyTerm(allTypes);
//            } else {
//                /* Forcing in the rest of the input states to be empty types. */
//                encoding = encoding.append(mappings.add(allTypes.getEmptyType(), workflowOutputStates.get(i), WorkflowElement.USED_TYPE))
//                        .append(" 0\n");
//            }
//
//        }

        return allClauses;
    }
}
