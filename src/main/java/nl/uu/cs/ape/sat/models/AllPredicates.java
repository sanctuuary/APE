/**
 * 
 */
package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
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
	private List<String> taxonomyRoots;

	public AllPredicates(List<String> dataTaxonomyRoots) {
		this.taxonomyRoots = dataTaxonomyRoots;
		this.predicates = new HashMap<String, TaxonomyPredicate>();
	}

	/**
	 * Returns the root predicate of the taxonomy.
	 * 
	 * @return The root predicate.
	 */
	public List<TaxonomyPredicate> getRootPredicates() {
		List<TaxonomyPredicate> rootpredicates = new ArrayList<>();
		for (String rootID : taxonomyRoots) {
			rootpredicates.add(get(rootID));
		}
		return rootpredicates;
	}

//	/**
//	 * Set the root predicate of the taxonomy.
//	 * 
//	 */
//	public void setRootPredicate(TaxonomyPredicate root) {
//		this.taxonomyRoot = root.getPredicateID();
//	}

	/**
	 * Returns the ID of the root predicate of the taxonomy.
	 * 
	 * @return The root predicate.
	 */
	public List<String> getRootsIDs() {
		return taxonomyRoots;
	}

	public abstract TaxonomyPredicate addPredicate(TaxonomyPredicate newPredicate) throws Exception;

	/** Returns the runtime class of the predicates that belong to the Object. */
	public abstract Class<?> getPredicateClass();


	/**
	 * Remove the parts of the taxonomy that are not in use for the given set of
	 * available tools and types in the domain.
	 * 
	 * @return {@code true} if the trimming finished successfully, {@code false}
	 *         otherwise.
	 */
	public boolean trimTaxonomy() {
		for (TaxonomyPredicate root : getRootPredicates()) {
			List<TaxonomyPredicate> toRemove = new ArrayList<TaxonomyPredicate>();
			for (TaxonomyPredicate subClass : APEUtils.safe(root.getSubPredicates())) {
				if (subClass == null) {
				} else if (subClass.getIsRelevant()) {
					trimSubTaxonomy(subClass);
				} else {
					toRemove.add(subClass);
					trimSubTaxonomy(subClass);
				}
			}
			root.removeAllSubPredicates(toRemove);
		}
		return true;
	}

	/**
	 * Remove the parts of the given subtaxonomy that are not in use for the given
	 * set of available tools and types in the domain.
	 * 
	 * @param subTaxRoot - subtaxonomy that is to be trimmed
	 * @return {@code true} if the trimming finished successfully, {@code false}
	 *         otherwise.
	 */
	public boolean trimSubTaxonomy(TaxonomyPredicate subTaxRoot) {
		if (subTaxRoot == null) {
			return true;
		}
		List<TaxonomyPredicate> toRemove = new ArrayList<TaxonomyPredicate>();
		for (TaxonomyPredicate subClass : APEUtils.safe(subTaxRoot.getSubPredicates())) {
			if (subClass == null) {
			} else if (subClass.getIsRelevant()) {
				trimSubTaxonomy(subClass);
			} else {
				toRemove.add(subClass);
				trimSubTaxonomy(subClass);
			}
		}
		if (!subTaxRoot.getIsRelevant()) {
			this.predicates.remove(subTaxRoot.getPredicateID());
		}
		subTaxRoot.removeAllSubPredicates(toRemove);
		return true;
	}

	/**
	 * Return the predicates from the domain.
	 * 
	 * @return
	 */
	protected Map<String, TaxonomyPredicate> getPredicates() {
		return this.predicates;
	}

	/**
	 * Returns the Predicate to which the specified key is mapped, or null if this
	 * map contains no mapping for the key.
	 * 
	 * @param predicateID - the key whose associated Predicate is to be returned
	 * @return the Predicate to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	public TaxonomyPredicate get(String predicateID) {
		return this.predicates.get(predicateID);
	}

	/**
	 * Method return all the element that belong to the subTree.
	 * 
	 * @param subTreeRoot - root of the subTree
	 * @return List of data types.
	 */
	public SortedSet<TaxonomyPredicate> getElementsFromSubTaxonomy(TaxonomyPredicate subTreeRoot)
			throws NullPointerException {
		if (subTreeRoot == null) {
			throw new NullPointerException("Given subtaxonomy type does not exist.");
		}
		SortedSet<TaxonomyPredicate> elements = new TreeSet<>();
		elements.add(subTreeRoot);

		for (TaxonomyPredicate subType : APEUtils.safe(subTreeRoot.getSubPredicates())) {
			elements.addAll(getElementsFromSubTaxonomy(subType));
		}

		return elements;
	}
}
