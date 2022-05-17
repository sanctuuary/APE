package nl.uu.cs.ape.models;

import java.util.SortedSet;

import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Class represents an auxiliary predicates (module or type) that represents a collection of disjoint or conjunct predicates.
 * 
 * @author Vedran Kasalica
 *
 */
public interface AuxiliaryPredicate extends PredicateLabel {

	 /**
     * Gets logic operator used to group the abstracted predicates.
     *
     * @return the field {@link AuxModulePredicate#logicOp}.
     */
	public LogicOperation getLogicOp();
	
	/**
     * Return the list of {@link TaxonomyPredicate}s that are generalized over using the helper predicate.
     *
     * @return all {@link TaxonomyPredicate}s generalized over.
     */
	public SortedSet<TaxonomyPredicate> getGeneralizedPredicates();
	
}
