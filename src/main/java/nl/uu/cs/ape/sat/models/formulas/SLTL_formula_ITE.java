package nl.uu.cs.ape.sat.models.formulas;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.models.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.models.*;

public abstract class SLTL_formula_ITE extends SLTL_formula {

	public SLTL_formula_ITE(TaxonomyPredicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_ITE(boolean sign, TaxonomyPredicate formula) {
		super(sign, formula);
	}


}
