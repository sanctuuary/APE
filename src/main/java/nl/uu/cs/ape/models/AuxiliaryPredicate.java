package nl.uu.cs.ape.models;

import java.util.SortedSet;

import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

public interface AuxiliaryPredicate extends PredicateLabel {

	 /**
     * Gets logic operator used to group the abstracted predicates.
     *
     * @return the field {@link #logicOp}.
     */
	public LogicOperation getLogicOp();
	
	/**
     * Return the list of {@link TaxonomyPredicate}s that are generalized over using the helper predicate.
     *
     * @return all {@link TaxonomyPredicate}s generalized over.
     */
	public SortedSet<TaxonomyPredicate> getGeneralizedPredicates();
	
}
