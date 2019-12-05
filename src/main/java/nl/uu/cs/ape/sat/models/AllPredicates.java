/**
 * 
 */
package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code AllPredicates} class is used to 
 *
 * @author Vedran Kasalica
 *
 */
public abstract class AllPredicates {

	private Map<String, TaxonomyPredicate> predicates;
	/** Root of the taxonomy. */
	private String taxonomyRoot;
	
	public AllPredicates(String dataTaxonomyRoot) {
		this.taxonomyRoot = dataTaxonomyRoot;
		this.predicates = new HashMap<String, TaxonomyPredicate> ();
	}
	
	/**
	 * Returns the root predicate of the taxonomy.
	 * 
	 * @return The root predicate.
	 */
	public TaxonomyPredicate getRootPredicate() {
		return get(taxonomyRoot);
	}
	
	/**
	 * Set the root predicate of the taxonomy.
	 * 
	 */
	public void setRootPredicate(TaxonomyPredicate root) {
		this.taxonomyRoot = root.getPredicateID();
	}
	
	/**
	 * Returns the ID of the root predicate of the taxonomy.
	 * 
	 * @return The root predicate.
	 */
	public String getRootID() {
		return taxonomyRoot;
	}
	
	/**
	 * Returns a list of final predicates.
	 * 
	 * @return list of types
	 */
	private List<TaxonomyPredicate> getAllNonEmptyPredicates() {

		List<TaxonomyPredicate> allNonEmptyTypes = new ArrayList<TaxonomyPredicate>();
		for (TaxonomyPredicate type : this.predicates.values()) {
			if (!(type.isEmptyPredicate() || type.isRootPredicate())) {
				allNonEmptyTypes.add(type);
			}
		}
		return allNonEmptyTypes;
	}
	
	/**
	 * 
	 */
	public boolean trimTaxonomy() {
		TaxonomyPredicate root = get(taxonomyRoot);
		List<String> toRemove = new ArrayList<String>();
		for(String subClassID : APEUtils.safe(root.getSubPredicates())) {
			TaxonomyPredicate subClass = get(subClassID);
			if(subClass == null) {
				toRemove.add(subClassID);
				continue;
			}
			if(subClass.getIsRelevant()) {
				trimSubTaxonomy(subClass);
			} else {
				toRemove.add(subClassID);
				trimSubTaxonomy(subClass);
			}
		}
		root.removeAllSubPredicates(toRemove);
		return true;
	}
	
	public boolean trimSubTaxonomy(TaxonomyPredicate subTaxRoot) {
		if(subTaxRoot == null) {
			return true;
		}
		List<String> toRemove = new ArrayList<String>();
		for(String subClassID : APEUtils.safe(subTaxRoot.getSubPredicates())) {
			TaxonomyPredicate subClass = get(subClassID);
			if(subClass == null) {
				toRemove.add(subClassID);
				continue;
			}
			if(subClass.getIsRelevant()) {
				trimSubTaxonomy(subClass);
			} else {
				toRemove.add(subClassID);
				trimSubTaxonomy(subClass);
			}
		}
		if(subTaxRoot.getIsRelevant()) {
			subTaxRoot.removeAllSubPredicates(toRemove);
		} else {
			this.predicates.remove(subTaxRoot.getPredicateID());
		}
		
		return true;
	}
	
	/**
	 * Return the predicates from the domain.
	 * @return
	 */
	protected Map<String, TaxonomyPredicate> getPredicates(){
		return this.predicates;
	}

	/**
	 * Returns the Predicate to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * @param predicateID - the key whose associated Predicate is to be returned
	 * @return the Predicate to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public TaxonomyPredicate get(String predicateID) {
		return this.predicates.get(predicateID);
	}
	
	
}
