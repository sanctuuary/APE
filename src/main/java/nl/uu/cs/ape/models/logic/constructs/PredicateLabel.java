package nl.uu.cs.ape.models.logic.constructs;

/**
 * The {@code PredicateLabel} class is used to represent a label that describes
 * predicates, such as data types, operations or states in the system.
 *
 * @author Vedran Kasalica
 */
public interface PredicateLabel extends Comparable<PredicateLabel>, APEPredicate {

    /**
     * Get the unique predicate identifier defined as String.
     *
     * @return String representation of the predicate, used to uniquely identify the
     *         predicate.
     */
    public abstract String getPredicateID();

    /**
     * Get string that corresponds to the predicate label (e.g. OWL label). The
     * label is not guaranteed to be unique.
     *
     * @return String representation of the predicate label, used for presentation
     *         in case when the predicate ID is too complex/long.
     * 
     */
    public String getPredicateLabel();

    /**
     * Get long string that corresponds to the full predicate label (e.g. OWL IRI).
     * The long label is not guaranteed to be unique.
     *
     * @return Detailed String representation of the predicate.
     */
    public String getPredicateLongLabel();

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

}
