package nl.uu.cs.ape.sat.models.formulas;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.constructs.Predicate;
import nl.uu.cs.ape.sat.models.*;

public class SLTL_formula_X extends SLTL_formula {

	public SLTL_formula_X(Predicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_X(boolean sign, Predicate formula) {
		super(sign, formula);
	}

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton - automaton of all the module states
	 * @param typeAutomaton - automaton od all the type states
	 * @return CNF representation of the SLTL formula
	 */
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
		return "X";
	}

}
