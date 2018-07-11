package SAT.models;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;

public class SLTL_formula_X extends SLTL_formula {

	public SLTL_formula_X(Predicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_X(Predicate predicate, boolean negated) {
		super(predicate, negated);
	}

	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "X";
	}

}
