package nl.uu.cs.ape.sat.models;

import java.util.SortedSet;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

public interface AuxiliaryPredicate extends PredicateLabel {

	public LogicOperation getLogicOp();
	
	public SortedSet<TaxonomyPredicate> getGeneralizedPredicates();
	
}
