package SAT.models;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;

/**
 * The class is used to represent general SLTL constraints and to generate the
 * corresponding CNF representation. The form currently supports only trivial
 * formulas under the modal quantifier (atomic type and module formulas).
 * 
 * @author VedranPC
 *
 */
public abstract class SLTL_formula {

	private Predicate predicate;
	private boolean negated;

	public SLTL_formula(Predicate predicate) {
		this.predicate = predicate;
		negated = false;
	}

	/**
	 * Modal operator is performed over @formula. In case of value of @negated
	 * being true, @formula
	 * 
	 * @param predicate
	 * @param negated
	 */
	public SLTL_formula(Predicate predicate, boolean negated) {
		this.predicate = predicate;
		this.negated = negated;
	}

	/**
	 * Setting whether the predicate is negated or not. If @negation is true the predicate
	 * will be negated.
	 * 
	 * @param negation
	 */
	public void setNegated(boolean negation) {
		this.negated = negation;
	}

	/**
	 * Returns true if predicate is negated, false otherwise.
	 * 
	 * @return
	 */
	public boolean getNegated() {
		return negated;
	}

	/**
	 * Function returns the formula that was specified under the SLTL operator.
	 * Currently only predicates.
	 * 
	 * @return
	 */
	public Predicate getSubFormula() {
		return predicate;
	}

	/**
	 * Returns the type of the SLTL formula [F, G or X].
	 * 
	 * @return
	 */
	public abstract String getType();

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return
	 */
	public abstract String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings);

	public static String ite(Predicate if_predicate, Predicate then_predicate, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		if (if_predicate.getType().matches("type")) {

		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				if (!moduleState.isLast()) {
					constraints += "-" + mappings.add(if_predicate.getPredicate(), moduleState.getStateName()) + " ";
					for (int i = moduleState.getStateNumber() + 1; i < moduleAutomaton.size(); i++) {
						constraints += mappings.add(then_predicate.getPredicate(), moduleAutomaton.get(i).getStateName()) + " ";
					}
					constraints += "0\n";
				}
			}
		}

		return constraints;
	}
}
