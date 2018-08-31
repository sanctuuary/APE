package SAT.models;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.models.constructs.Predicate;

public class SLTL_formula_F extends SLTL_formula {

	public SLTL_formula_F(Predicate formula) {
		super(formula);
	}

	public SLTL_formula_F(boolean sign, Predicate formula) {
		super(sign, formula);
	}

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton
	 *            - automaton of all the module states
	 * @param typeAutomaton
	 *            - automaton of all the type states
	 * @return CNF representation of the SLTL formula
	 */
	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {

		String constraints = "";

		String negSign;
		// Check whether the atom is expected to be negated or not
		if (super.getSign()) {
			negSign = "";
		} else {
			negSign = "-";
		}
		// Distinguishing whether the atom under the modal operator is type
		// or module.
		if (super.getSubFormula().getType().matches("type")) {
			for (TypeBlock typeBlock : typeAutomaton.getTypeBlocks()) {
				for (TypeState typeState : typeBlock.getTypeStates()) {
					constraints += negSign
							+ mappings.add(super.getSubFormula().getPredicate(), typeState.getStateName()) + " ";
				}
			}
			constraints += "0\n";
		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				constraints += negSign + mappings.add(super.getSubFormula().getPredicate(), moduleState.getStateName())
						+ " ";
			}
			constraints += "0\n";
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
		return "F";
	}

}