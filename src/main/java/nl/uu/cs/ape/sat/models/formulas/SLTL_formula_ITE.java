package nl.uu.cs.ape.sat.models.formulas;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeBlock;
import nl.uu.cs.ape.sat.models.constructs.Predicate;
import nl.uu.cs.ape.sat.models.*;

public abstract class SLTL_formula_ITE extends SLTL_formula {

	public SLTL_formula_ITE(Predicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_ITE(boolean sign, Predicate formula) {
		super(sign, formula);
	}


}
