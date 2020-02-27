package nl.uu.cs.ape.sat.models;

import java.util.SortedSet;
import java.util.TreeSet;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code TaxonomyPredicateHelper} class represents an abstract class used
 * strictly to represent artificially generated abstract terms, used to abstract
 * over existing taxonomy terms.<br>
 * Object of this class represent disjunctions of conjunctions of existing
 * taxonomy predicates.
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
	 * Creates an abstract module from @predicateName and @predicateID. If @isTool
	 * is true, module is an actual tool, otherwise it's an abstract/non-tool
	 * module.
	 * 
	 * @param predicateName - module name
	 * @param predicateID   - unique module identifier
	 * @param rootNode      - ID of the Taxonomy Root node corresponding to the
	 *                      Module.
	 * @param nodeType      - {@link NodeType} object describing the type w.r.t. the
	 *                      Module Taxonomy.
	 */
	public AuxTaxonomyPredicate(TaxonomyPredicate predicate, LogicOperation logicOp) {
		super(predicate.getRootNode(), predicate.getNodeType());
		this.taxonomyPredicate = predicate;
		this.logicOp = logicOp;
		this.containingPredicates = new TreeSet<TaxonomyPredicate>();
	}


	public TaxonomyPredicate getTaxonomyPredicate() {
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
	 * @see nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate#hashCode()
	 */
	@Override
	public int hashCode() {
		return taxonomyPredicate.hashCode();
	}


	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return taxonomyPredicate.equals(obj);
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
