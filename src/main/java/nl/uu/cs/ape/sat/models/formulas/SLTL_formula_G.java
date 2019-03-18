package nl.uu.cs.ape.sat.models.formulas;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.ModuleState;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeBlock;
import nl.uu.cs.ape.sat.automaton.TypeState;
import nl.uu.cs.ape.sat.models.constructs.Predicate;
import nl.uu.cs.ape.sat.models.*;

public class SLTL_formula_G extends SLTL_formula {

	public SLTL_formula_G(Predicate predicate) {
		super(predicate);
	}

	public SLTL_formula_G(boolean sign, Predicate predicate) {
		super(sign, predicate);
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
		// check whether the sub-formula is negated or not
		if (super.getSign()) {
			negSign = "";
		} else {
			negSign = "-";
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
