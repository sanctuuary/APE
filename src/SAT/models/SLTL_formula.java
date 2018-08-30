package SAT.models;

import java.util.List;

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
	/*
	 * Sign of the predicate, <b>false</b> if the predicate is negated, <b>true</b> otherwise
	 */
	private boolean sign;

	public SLTL_formula(Predicate predicate) {
		this.predicate = predicate;
		sign = true;
	}

	/**
	 * Modal operators are performed over <b>formulas</b>. In case of value of <b>sign</b> being
	 * true, <b>formula</b> is positive, otherwise the formula is negative (<b>predicate</b> is negated).
	 * 
	 * @param predicate
	 * @param sign
	 */
	public SLTL_formula(boolean sign, Predicate predicate) {
		this.predicate = predicate;
		this.sign = sign;
	}

	/**
	 * Setting whether the predicate is negated or not. If sign is <b>false</b> the
	 * predicate will be negated.
	 * 
	 * @param sign
	 */
	public void setSign(boolean sign) {
		this.sign = sign;
	}

	/**
	 * Returns <b>false</b> if the predicate is negated, <b>true</b> otherwise.
	 * 
	 * @return
	 */
	public boolean getSign() {
		return sign;
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
	 * Creates a CNF representation of the Constraint: If <b>if_predicate</b> is used,
	 * tool <b>then_predicate</b> has to be used subsequently
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
	 * Creates a CNF representation of the Constraint: Use <b>last_module</b> as last
	 * module in the solution.
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
		
		if (!last_module.getType().matches("type")) {
			List<ModuleState> moduleAutomatonStates = moduleAutomaton.getModuleStates();
			ModuleState lastModuleState = moduleAutomatonStates.get(moduleAutomatonStates.size() -1);
					constraints += mappings.add(last_module.getPredicate(), lastModuleState.getStateName()) + " 0\n";
		}

		return constraints;
	}
}
