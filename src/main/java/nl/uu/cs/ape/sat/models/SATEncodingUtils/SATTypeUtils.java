/**
 * 
 */
package nl.uu.cs.ape.sat.models.SATEncodingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.Pair;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code SATTypeUtils} class is used to encode SAT constraints  based on the type annotations.
 *
 * @author Vedran Kasalica
 *
 */
public class SATTypeUtils {

	/** Private constructor is used to to prevent instantiation. */
	private SATTypeUtils() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Generating the mutual exclusion for each pair of tools from @modules
	 * (excluding abstract modules from the taxonomy) in each state
	 * of @moduleAutomaton.
	 * 
	 * @param modules
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	public static String typeMutualExclusion(AllTypes allTypes, TypeAutomaton typeAutomaton, AtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();
		PredicateLabel firstPair, secondPair;
		for (Pair<PredicateLabel> pair : allTypes.getTypePairsForEachSubTaxonomy()) {
			firstPair = pair.getFirst();
			secondPair = pair.getSecond();
			// mutual exclusion of types in all the states (those that represent general memory)
			for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
				for (State typeState : typeBlock.getStates()) {
					constraints = constraints.append("-").append(mappings.add(firstPair, typeState, WorkflowElement.MEMORY_TYPE))
							.append(" ");
					constraints = constraints.append("-").append(mappings.add(secondPair, typeState, WorkflowElement.MEMORY_TYPE))
							.append(" ").append("0\n");
				}
			}
			// mutual exclusion of types in all the states (those that represent used instances)
			for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
				for (State typeState : typeBlock.getStates()) {
					constraints = constraints.append("-").append(mappings.add(firstPair, typeState, WorkflowElement.USED_TYPE))
							.append(" ");
					constraints = constraints.append("-").append(mappings.add(secondPair, typeState, WorkflowElement.USED_TYPE))
							.append(" ").append("0\n");
				}
			}
		}
		return constraints.toString();
	}
	
	/**
	 * Generating the mandatory usage constraints of root type @rootType in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootTypeID      - represent the ID of the root type in the type
	 *                        taxonomy
	 * @param moduleAutomaton - type automaton
	 * @return String representation of constraints
	 */
	public static String typeMandatoryUsage(AllTypes allTypes, TaxonomyPredicate type, TypeAutomaton typeAutomaton, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		// enforcement of types in in all the states (those that represent general
		// memory and used data instances)
		for (Block typeBlock : typeAutomaton.getMemoryTypesBlocks()) {
			for (State typeState : typeBlock.getStates()) {
				constraints = constraints.append(mappings.add(type, typeState, WorkflowElement.MEMORY_TYPE))
						.append(" 0\n");
			}
		}
		for (Block typeBlock : typeAutomaton.getUsedTypesBlocks()) {
			for (State typeState : typeBlock.getStates()) {
				constraints = constraints.append(mappings.add(type, typeState, WorkflowElement.USED_TYPE))
						.append(" 0\n");
			}
		}

		return constraints.toString();
	}

	/**
	 * Generating the mandatory usage of a subtypes in case of the parent type being
	 * used, with respect to the Type Taxonomy. The rule starts from the @rootType
	 * and it's valid in each state of @typeAutomaton. @emptyType denotes the type
	 * that is being used if the state has no type.
	 * 
	 * @param rootTypeID    - represent the ID of the root type in the type taxonomy
	 * @param emptyTypeID   - represent the ID of the empty type in the type
	 *                      taxonomy
	 * @param typeAutomaton - type automaton
	 * @param mappings      - mapping function
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public static String typeEnforceTaxonomyStructure(AllTypes allTypes, TaxonomyPredicate currType, TypeAutomaton typeAutomaton, AtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();
		// taxonomy enforcement of types in in all the states (those that represent
		// general memory and used data instances)
		for (Block memTypeBlock : typeAutomaton.getMemoryTypesBlocks()) {
			for (State memTypeState : memTypeBlock.getStates()) {
				constraints = constraints
						.append(typeEnforceTaxonomyStructureForState(allTypes,currType, mappings, memTypeState, WorkflowElement.MEMORY_TYPE));
			}
		}
		for (Block usedTypeBlock : typeAutomaton.getUsedTypesBlocks()) {
			for (State usedTypeState : usedTypeBlock.getStates()) {
				constraints = constraints.append(typeEnforceTaxonomyStructureForState(allTypes, currType, mappings, usedTypeState, WorkflowElement.USED_TYPE));
			}
		}
		return constraints.toString();
	}

	/**
	 * Supporting recursive method for typeEnforceTaxonomyStructure.
	 * @param typeElement 
	 */
	private static String typeEnforceTaxonomyStructureForState(AllTypes allTypes, TaxonomyPredicate currType,
			AtomMappings mappings, State typeState, WorkflowElement typeElement) {

		String superType_State = mappings.add(currType, typeState, typeElement).toString();

		StringBuilder constraints = new StringBuilder();
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

				constraints = constraints.append(typeEnforceTaxonomyStructureForState(allTypes, subType, mappings, typeState, typeElement));
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
	 * @param program_inputs - input types for the program
	 * @param typeAutomaton
	 * @param solutionLength
	 * @param emptyType
	 * @param mappings
	 * @param allTypes
	 * @return String representation of the initial input encoding.
	 */
	public static String encodeInputData(AllTypes allTypes, List<DataInstance> program_inputs, TypeAutomaton typeAutomaton, AtomMappings mappings) {
		StringBuilder encoding = new StringBuilder();

		List<State> workfloInputStates = typeAutomaton.getMemoryTypesBlock(0).getStates();
		for (int i = 0; i < workfloInputStates.size(); i++) {
			if (i < program_inputs.size()) {
				List<Type> currTypes = program_inputs.get(i).getTypes();
				for (Type currType : currTypes) {
					if (allTypes.get(currType.getPredicateID()) == null) {
						System.err.println(
								"Program input '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
						return null;
					}
					
					encoding = encoding.append(mappings.add(currType, workfloInputStates.get(i), WorkflowElement.MEMORY_TYPE))
							.append(" 0\n");
//					currType.setAsRelevantTaxonomyTerm(allTypes);
				}
			} else {
				/* Forcing in the rest of the input states to be empty types. */
				encoding = encoding.append(mappings.add(allTypes.getEmptyType(), workfloInputStates.get(i), WorkflowElement.MEMORY_TYPE))
						.append(" 0\n");
			}

		}
		return encoding.toString();
	}

	/**
	 * Encoding the workflow output. The provided output files have to occur as the
	 * final set of "used" data types.
	 * 
	 * @param allTypes
	 * @param program_outputs
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of the workflow output encoding.
	 */
	public static String encodeOutputData(AllTypes allTypes, List<DataInstance> program_outputs, TypeAutomaton typeAutomaton, AtomMappings mappings) {
		StringBuilder encoding = new StringBuilder();

		List<State> workflowOutputStates = typeAutomaton.getWorkflowOutputBlock().getStates();
		for (int i = 0; i < workflowOutputStates.size(); i++) {
			if (i < program_outputs.size()) {
				List<Type> currTypes = program_outputs.get(i).getTypes();
				for (Type currType : currTypes) {
					if (allTypes.get(currType.getPredicateID()) == null) {
						System.err.println(
								"Program output '" + currType.getPredicateID() + "' was not defined in the taxonomy.");
						return null;
					}
					encoding = encoding.append(mappings.add(currType, workflowOutputStates.get(i), WorkflowElement.USED_TYPE))
							.append(" 0\n");
//					currType.setAsRelevantTaxonomyTerm(allTypes);
				}
			} else {
				/* Forcing in the rest of the input states to be empty types. */
				encoding = encoding.append(mappings.add(allTypes.getEmptyType(), workflowOutputStates.get(i), WorkflowElement.USED_TYPE))
						.append(" 0\n");
			}

		}

		return encoding.toString();
	}
	
	
}
