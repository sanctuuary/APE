package nl.uu.cs.ape.models;

import java.util.SortedSet;

import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

public interface AuxiliaryPredicate {

	public LogicOperation getLogicOp();
	
	public SortedSet<TaxonomyPredicate> getGeneralizedPredicates();
	
}
