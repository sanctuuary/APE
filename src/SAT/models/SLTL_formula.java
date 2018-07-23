package SAT.models;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.models.constructs.Predicate;

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
	 * Modal operator is performed over @formula. In case of value of @negated being
	 * true, @formula
	 * 
	 * @param predicate
	 * @param negated
	 */
	public SLTL_formula(Predicate predicate, boolean negated) {
		this.predicate = predicate;
		this.negated = negated;
	}

	/**
	 * Setting whether the predicate is negated or not. If @negation is true the
	 * predicate will be negated.
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
	 * @return [F, G or X] depending on the type of SLTL formula
	 */
	public abstract String getType();

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton
	 *            - automaton of all the module states
	 * @param typeAutomaton
	 *            - automaton od all the type states
	 * @return CNF representation of the SLTL formula
	 */
	public abstract String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings);

	/**
	 * Creates a CNF representation of the Constraint: If @if_predicate is used,
	 * tool @then_predicate has to be used subsequently
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return CNF representation of the SLTL formula
	 */
	public static String ite(Predicate if_predicate, Predicate then_predicate, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		if (if_predicate.getType().matches("type")) {

		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				if (!moduleState.isLast()) {
					constraints += "-" + mappings.add(if_predicate.getPredicate(), moduleState.getStateName()) + " ";
					for (int i = moduleState.getStateNumber() + 1; i < moduleAutomaton.size(); i++) {
						constraints += mappings.add(then_predicate.getPredicate(),
								moduleAutomaton.get(i).getStateName()) + " ";
					}
					constraints += "0\n";
				}
			}
		}

		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint: Use @last_module as last
	 * module in the solution
	 * 
	 * @param last_module - the module that will 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return CNF representation of the SLTL formula
	 */
	public static String useAsLastModule(AbstractModule last_module, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		
		if (last_module.getType().matches("type")) {

		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				if (moduleState.isLast()) {
					constraints += mappings.add(last_module.getPredicate(), moduleState.getStateName()) + " 0\n";
				}
			}
		}

		return constraints;
	}
}
