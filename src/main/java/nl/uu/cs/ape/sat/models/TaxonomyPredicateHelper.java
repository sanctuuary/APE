package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
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
public class TaxonomyPredicateHelper implements PredicateLabel {

	private final String predicateName;
	private final String predicateID;
	/** Field defines the connective between the subclasses of the predicate. */
	private final LogicOperation logicOp;
	/**
	 * Set of all the predicates that are subsumed by the abstract predicate (null
	 * if the predicate is a leaf)
	 */
	private List<TaxonomyPredicate> subPredicates;

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
	public TaxonomyPredicateHelper(TaxonomyPredicate predicate, LogicOperation logicOp) {
		this.predicateName = predicate.getPredicateLabel();
		this.predicateID = predicate.getPredicateID();
		this.logicOp = logicOp;
		subPredicates = new ArrayList<TaxonomyPredicate>();
	}

	@Override
	public int hashCode() {
		return predicateID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		TaxonomyPredicateHelper other = (TaxonomyPredicateHelper) obj;
		return this.predicateID.equals(other.getPredicateID());
	}

	public String getPredicateID() {
		return predicateID;
	}

	public String getPredicateLabel() {
		return predicateName;
	}


	/** @return the field {@link logicOp}. */
	public LogicOperation getLogicOp() {
		return logicOp;
	}

	/**
	 * Adds a sub-predicate to the current one, if they are not defined already.
	 * 
	 * @param predicate - predicate that will be added as a subclass
	 * @return True if sub-predicate was added, false otherwise.
	 */
	public void addSubPredicate(TaxonomyPredicate predicate) {
		subPredicates.add(predicate);
	}
	
	public List<TaxonomyPredicate> getSubPredicates(){
		return subPredicates;
	}

}
