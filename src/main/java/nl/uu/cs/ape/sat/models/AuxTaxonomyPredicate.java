package nl.uu.cs.ape.sat.models;

import java.util.SortedSet;
import java.util.TreeSet;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

/**
 * The {@code AuxTaxonomyPredicate} class represents an abstract class used
 * strictly to represent artificially generated abstract terms, used to abstract
 * over existing taxonomy terms.<br>
 * Object of this class represent disjunctions of conjunctions of existing
 * taxonomy predicates.
 * 
 * Class is meant to be used only by the {@link APEDomainSetup#generateAuxiliaryPredicate(SortedSet, LogicOperation)}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AuxTaxonomyPredicate extends TaxonomyPredicate {

	/** Corresponding TaxonomyPredicate */
	private final TaxonomyPredicate taxonomyPredicate;
	
	private SortedSet<TaxonomyPredicate> containingPredicates;
	
	/** Field defines the connective between the subclasses of the predicate. */
	private final LogicOperation logicOp;


	/**
	 * Create an auxiliary predicate.
	 * @param predicate
	 * @param logicOp
	 */
	public AuxTaxonomyPredicate(TaxonomyPredicate predicate, LogicOperation logicOp) {
		super(predicate.getRootNodeID(), predicate.getNodeType());
		this.taxonomyPredicate = predicate;
		this.logicOp = logicOp;
		this.containingPredicates = new TreeSet<TaxonomyPredicate>();
	}


	public TaxonomyPredicate getTaxonomyPredicates() {
		return taxonomyPredicate;
	}


	/** @return the field {@link logicOp}. */
	public LogicOperation getLogicOp() {
		return logicOp;
	}

	/**
	 * Adds a concrete predicate to be contained in the abstract one, if not add already.
	 * 
	 * @param predicate - predicate that will be added to be generalized over
	 * @return True if predicate was added, false otherwise.
	 */
	public void addConcretePredicate(TaxonomyPredicate predicate) {
		if(predicate != null) {
			this.containingPredicates.add(predicate);
		}
	}
	
	/**
	 * Return the list of {@link TaxonomyPredicate}s that are generalized over using the helper predicate.
	 * @return all {@link TaxonomyPredicate}s generalized over.
	 */
	public SortedSet<TaxonomyPredicate> getGeneralizedPredicates(){
		return this.containingPredicates;
	}


	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel#getPredicateID()
	 */
	@Override
	public String getPredicateID() {
		return taxonomyPredicate.getPredicateID();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((containingPredicates == null) ? 0 : containingPredicates.hashCode());
		result = prime * result + ((logicOp == null) ? 0 : logicOp.hashCode());
		result = prime * result + ((taxonomyPredicate == null) ? 0 : taxonomyPredicate.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuxTaxonomyPredicate other = (AuxTaxonomyPredicate) obj;
		if (containingPredicates == null) {
			if (other.containingPredicates != null)
				return false;
		} else if (!containingPredicates.equals(other.containingPredicates))
			return false;
		if (logicOp != other.logicOp)
			return false;
		if (taxonomyPredicate == null) {
			if (other.taxonomyPredicate != null)
				return false;
		} else if (!taxonomyPredicate.equals(other.taxonomyPredicate))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate#getPredicateLabel()
	 */
	@Override
	public String getPredicateLabel() {
		return taxonomyPredicate.getPredicateLabel();
	}


	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate#getType()
	 */
	@Override
	public String getType() {
		return taxonomyPredicate.getType();
	}
	
	@Override
	public String toString() {
		String print = logicOp.toStringSign();
		for(TaxonomyPredicate param : containingPredicates) {
			print = print.concat(param.toShortString()).concat(logicOp.toStringSign());
		}
		return print; 
	}

	@Override
	public String toShortString() {
		return getPredicateID();
	}

}
