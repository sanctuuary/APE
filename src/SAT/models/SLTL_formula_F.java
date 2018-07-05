package SAT.models;


import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;

public class SLTL_formula_F extends SLTL_formula {

	public SLTL_formula_F(Atom formula) {
		super(formula);
	}
	
	public SLTL_formula_F(Atom formula, boolean negated) {
		super(formula, negated);
	}

	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String constraints = "";

		String negSign;
//		Check whether the atom is expected to be negated or not
		if (super.getNegated()){
			negSign = "-";
		} else {
			negSign = "";
		}
		// Distinguishing whether the atom under the modal operator is type
		// or module.
		if (super.getSubFormula().getType().matches("type")) {
			for (TypeBlock typeBlock : typeAutomaton.getTypeAutomaton()) {
				for (TypeState typeState : typeBlock.getTypeStates()) {
					constraints += negSign + super.getSubFormula().getAtom() + "(" + typeState.getStateName() + ") ";
				}
			}
			constraints += "0\n";
		} else {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				constraints += negSign + super.getSubFormula().getAtom() + "(" + moduleState.getStateName() + ") ";
			}
			constraints += "0\n";
		}
		return constraints;
	}

	@Override
	public String getType() {
		return "F";
	}

}