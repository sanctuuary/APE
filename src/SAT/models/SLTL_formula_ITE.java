package SAT.models;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;

public class SLTL_formula_ITE extends SLTL_formula {

	public SLTL_formula_ITE(Predicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_ITE(Predicate predicate, boolean negated) {
		super(predicate, negated);
	}

	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the type of the SLTL formula [F, G or X].
	 * 
	 * @return [F, G or X] depending on the type of SLTL formula
	 */
	@Override
	public String getType() {
		return "iT";
	}

}
