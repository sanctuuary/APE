package nl.uu.cs.ape.models;

import java.util.*;

import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code AllPredicates} class is used to group all mappedPredicates of the
 * same type (such as module, data) into one collection.
 *
 * @author Vedran Kasalica
 */
public abstract class AllPredicates {

    /**
     * Map of all predicated mapped to their predicateID.
     */
    private Map<String, TaxonomyPredicate> mappedPredicates;

    /**
     * Root of the taxonomy.
     */
    private List<String> dimensionRoots;

    /**
     * Instantiates AllPredicates.
     *
     * @param taxonomyRoots the taxonomy roots
     */
    protected AllPredicates(List<String> taxonomyRoots) {
        this.dimensionRoots = taxonomyRoots;
        this.mappedPredicates = new HashMap<>();
    }

    /**
     * Returns the root predicate of the taxonomy.
     *
     * @return The root predicate.
     */
    public List<TaxonomyPredicate> getRootPredicates() {
        List<TaxonomyPredicate> rootPredicates = new ArrayList<>();
        for (String rootID : dimensionRoots) {
            rootPredicates.add(get(rootID));
        }
        return rootPredicates;
    }

    /**
     * Returns the ID of the root predicate of the taxonomy including the "APE
     * label" dimension (in case of type taxonomy).
     *
     * @return The root predicate.
     */
    public List<String> getAllRootIDs() {
        return dimensionRoots;
    }

    /**
     * Add predicate taxonomy predicate.
     *
     * @param newPredicate the new predicate
     * @return the taxonomy predicate
     * @throws ExceptionInInitializerError the exception
     */
    public abstract TaxonomyPredicate addPredicate(TaxonomyPredicate newPredicate) throws ExceptionInInitializerError;

    /**
     * Gets predicate class.
     *
     * @return The runtime class of the mappedPredicates that belong to the Object.
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
            List<TaxonomyPredicate> toRemove = new ArrayList<>();
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
     * Remove the parts of the given sub-taxonomy that are not in use for the given
     * set of available tools and types in the domain.
     *
     * @param subTaxRoot SubTaxonomy that is to be trimmed.
     * @return true if the trimming finished successfully, false otherwise.
     */
    public boolean trimSubTaxonomy(TaxonomyPredicate subTaxRoot) {
        if (subTaxRoot == null) {
            return true;
        }
        List<TaxonomyPredicate> toRemove = new ArrayList<>();
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
            this.mappedPredicates.remove(subTaxRoot.getPredicateID());
        }
        subTaxRoot.removeAllSubPredicates(toRemove);
        return true;
    }

    /**
     * Gets mappedPredicates mapped to their IDs.
     *
     * @return The mapping to mappedPredicates from their IDs.
     */
    protected Map<String, TaxonomyPredicate> getMappedPredicates() {
        return this.mappedPredicates;
    }

    /**
     * Returns the Predicate to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param predicateID The key whose associated Predicate is to be returned.
     * @return The predicate to which the specified key is mapped, or null if this
     *         map contains no mapping for the key.
     */
    public TaxonomyPredicate get(String predicateID) {
        return this.mappedPredicates.get(predicateID);
    }

    /**
     * Method returns all the element that belong to the subTree.
     *
     * @param subTreeRoot Root of the subTree.
     * @return List of data types.
     * @throws NullPointerException the null pointer exception
     */
    public SortedSet<TaxonomyPredicate> getElementsFromSubTaxonomy(TaxonomyPredicate subTreeRoot)
            throws NullPointerException {
        if (subTreeRoot == null) {
            throw new NullPointerException("Given sub-taxonomy type cannot be null.");
        }
        SortedSet<TaxonomyPredicate> elements = new TreeSet<>();
        elements.add(subTreeRoot);

        for (TaxonomyPredicate subType : APEUtils.safe(subTreeRoot.getSubPredicates())) {
            elements.addAll(getElementsFromSubTaxonomy(subType));
        }

        return elements;
    }

    /**
     * Check whether the string occurs as one of the roots in the taxonomy.
     * 
     * @param curRootIRI - curr root IRI
     * @return true if the root exists
     */
    public boolean existsRoot(String curRootIRI) {
        return dimensionRoots.contains(curRootIRI);
    }

    /**
     * Put a new TaxonomyPredicate to the mapping.
     * 
     * @param key   - ID of the TaxonomyPredicate
     * @param value - TaxonomyPredicate to be added
     */
    public void put(String key, TaxonomyPredicate value) {
        mappedPredicates.put(key, value);

    }

    /**
     * Remove the TaxonomyPredicate from the mapping.
     * 
     * @param predicateID - ID of the TaxonomyPredicate to be removed
     */
    public void remove(String predicateID) {
        mappedPredicates.remove(predicateID);

    }
}
