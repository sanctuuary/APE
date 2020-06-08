package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.*;

/**
 * The {@code AllPredicates} class is used to TODO
 *
 * @author Vedran Kasalica
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

    /*
     * Set the root predicate of the taxonomy.
	 *
	public void setRootPredicate(TaxonomyPredicate root) {
		this.taxonomyRoot = root.getPredicateID();
	}
	*/

    /**
     * Returns the ID of the root predicate of the taxonomy.
     *
     * @return The root predicate.
     */
    public List<String> getRootsIDs() {
        return taxonomyRoots;
    }

    public abstract TaxonomyPredicate addPredicate(TaxonomyPredicate newPredicate) throws Exception;

    /**
     * @return The runtime class of the predicates that belong to the Object.
     */
    public abstract Class<?> getPredicateClass();


    /**
     * Remove the parts of the taxonomy that are not in use for the given set of
     * available tools and types in the domain.
     *
     * @return true if the trimming finished successfully, false otherwise.
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
     * @param subTaxRoot SubTaxonomy that is to be trimmed.
     * @return true if the trimming finished successfully, false otherwise.
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
     * @return The predicates from the domain.
     */
    protected Map<String, TaxonomyPredicate> getPredicates() {
        return this.predicates;
    }

    /**
     * Returns the Predicate to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param predicateID The key whose associated Predicate is to be returned.
     * @return The predicate to which the specified key is mapped, or null if this map contains no mapping for the key.
     */
    public TaxonomyPredicate get(String predicateID) {
        return this.predicates.get(predicateID);
    }

    /**
     * Method return all the element that belong to the subTree.
     *
     * @param subTreeRoot Root of the subTree.
     * @return List of data types.
     */
    public SortedSet<TaxonomyPredicate> getElementsFromSubTaxonomy(TaxonomyPredicate subTreeRoot)
            throws NullPointerException {
        if (subTreeRoot == null) {
            throw new NullPointerException("Given sub-taxonomy type does not exist.");
        }
        SortedSet<TaxonomyPredicate> elements = new TreeSet<>();
        elements.add(subTreeRoot);

        for (TaxonomyPredicate subType : APEUtils.safe(subTreeRoot.getSubPredicates())) {
            elements.addAll(getElementsFromSubTaxonomy(subType));
        }

        return elements;
    }
}
