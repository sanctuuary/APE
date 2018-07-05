package SAT.models;

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

	private Atom atom;
	private boolean negated;

	public SLTL_formula(Atom atom) {
		this.atom = atom;
		negated = false;
	}

	/**
	 * Modal operator is performed over @formula. In case of value of @negated
	 * being true, @formula
	 * 
	 * @param atom
	 * @param negated
	 */
	public SLTL_formula(Atom atom, boolean negated) {
		this.atom = atom;
		this.negated = negated;
	}

	/**
	 * Setting whether the atom is negated or not. If @negation is true the atom
	 * will be negated.
	 * 
	 * @param negation
	 */
	public void setNegated(boolean negation) {
		this.negated = negation;
	}

	/**
	 * Returns true if atom is negated, false otherwise.
	 * 
	 * @return
	 */
	public boolean getNegated() {
		return negated;
	}

	/**
	 * Function returns the formula that was specified under the SLTL operator.
	 * Currently only atoms.
	 * 
	 * @return
	 */
	public Atom getSubFormula() {
		return atom;
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
	public abstract String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton);

	public static String ite(Atom if_atom, Atom then_atom, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {
		String constraints = "";

		if (if_atom.getType().matches("type")) {

		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				if (!moduleState.isLast()) {
					constraints += "-" + if_atom.getAtom() + "(" + moduleState.getStateName() + ") ";
					for (int i = moduleState.getStateNumber() + 1; i < moduleAutomaton.size(); i++) {
						constraints += then_atom.getAtom() + "(" + moduleAutomaton.get(i).getStateName() + ") ";
					}
					constraints += "0\n";
				}
			}
		}

		return constraints;
	}
}
