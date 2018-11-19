package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.constructs.Predicate;

public class SLTL_formula_ITE extends SLTL_formula {

	public SLTL_formula_ITE(Predicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_ITE(boolean sign, Predicate formula) {
		super(sign, formula);
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
