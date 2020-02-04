/**
 * 
 */
package nl.uu.cs.ape.sat.models.SATEncodingUtils;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.Automaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllPredicates;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code GeneralEncodingUtils} class is used to 
 *
 * @author Vedran Kasalica
 *
 */
public class GeneralEncodingUtils {

	
	/**
	 * Method generates constraints that relate the abstractPredicate to the list of the conjunct/disjoint labels. In essence, the constraints preserves that if the abstract predicate
	 * is used, at least one of the conjunct/disjoint labels has to be used, and vice versa.
	 * @param abstractPred - abstract predicate that should represent the disjunction/conjunction of the allabels
	 * @param allLabels - list of labels out of which one/all should hold
	 * @param mappings - mapping of the labels
	 * @param automaton - state automaton
	 * @param elementType - type of the predicates
	 * @param logicOp - logical operation that is used to group the types (e.g. {@link LogicOperation.OR})                                     
	 * @return constraints that encode the conjunct/disjoint dependency between the list of predicates and the abstract predicate, i.e. if at least one of the disjointLabels is true, the abstract pred should be true, and vice versa. 
	 */
	public static String getConstraintGroupLogicallyPredicatesX(TaxonomyPredicate abstractPred, List<TaxonomyPredicate> disjointLabels, AtomMappings mappings, Automaton automaton, WorkflowElement elementType,  LogicOperation logicOp) {
		
		StringBuilder constraints = new StringBuilder();
		for (State currState : automaton.getAllStates()) {
			if (logicOp == LogicOperation.OR) {
				/*
				 * Ensures that if the abstract predicate is used, at least one of the
				 * disjointLabels has to be used.
				 */
				constraints = constraints.append("-").append(mappings.add(abstractPred, currState, elementType))
						.append(" ");

				for (TaxonomyPredicate disjunctLabel : disjointLabels) {
					constraints = constraints.append(mappings.add(disjunctLabel, currState, elementType)).append(" ");
				}
				constraints = constraints.append(" 0\n");

				/*
				 * Ensures that if at least one of the disjointLabels was used, the abstract
				 * predicate has to be used as well.
				 */
				for (TaxonomyPredicate disjunctLabel : disjointLabels) {
					constraints = constraints.append("-").append(mappings.add(disjunctLabel, currState, elementType))
							.append(" ");
					constraints = constraints.append(mappings.add(abstractPred, currState, elementType)).append(" 0\n");
				}
			} else if (logicOp == LogicOperation.AND) {

				/*
				 * Ensures that if the abstract predicate is used, all of the disjointLabels
				 * have to be used.
				 */
				for (TaxonomyPredicate disjunctLabel : disjointLabels) {
					constraints = constraints.append("-").append(mappings.add(abstractPred, currState, elementType))
							.append(" ");

					constraints = constraints.append(mappings.add(disjunctLabel, currState, elementType))
							.append(" 0\n");
				}

				/*
				 * Ensures that if all of the disjointLabels were used, the abstract predicate
				 * has to be used as well.
				 */
				for (TaxonomyPredicate disjunctLabel : disjointLabels) {
					constraints = constraints.append("-").append(mappings.add(disjunctLabel, currState, elementType))
							.append(" ");
				}
				constraints = constraints.append(mappings.add(abstractPred, currState, elementType)).append(" 0\n");
			}
		}
		return constraints.toString();
	}
	
}
