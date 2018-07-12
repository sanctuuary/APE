package SAT.models;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;

public class SLTL_formula_G extends SLTL_formula {

	public SLTL_formula_G(Predicate predicate) {
		super(predicate);
	}

	public SLTL_formula_G(Predicate predicate, boolean negated) {
		super(predicate, negated);
	}

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
	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {

		String constraints = "";
		String negSign;
		// check whether the subformula is expected to be negated or not
		if (super.getNegated()) {
			negSign = "-";
		} else {
			negSign = "";
		}
		// Distinguishing whether the formula under the modal operator is type
		// or module.
		if (super.getSubFormula().getType().matches("type")) {
			for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
				for (TypeState typeState : typeBlock.getTypeStates()) {
					constraints += negSign
							+ mappings.add(super.getSubFormula().getPredicate(), typeState.getStateName()) + " 0\n";
				}
			}
		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				constraints += negSign + mappings.add(super.getSubFormula().getPredicate(), moduleState.getStateName())
						+ " 0\n";
			}
		}
		return constraints;
	}

	/**
	 * Returns the type of the SLTL formula [F, G or X].
	 * 
	 * @return [F, G or X] depending on the type of SLTL formula
	 */
	@Override
	public String getType() {
		return "G";
	}

}
