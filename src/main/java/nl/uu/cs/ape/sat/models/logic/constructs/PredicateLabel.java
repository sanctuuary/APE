package nl.uu.cs.ape.sat.models.logic.constructs;

/**
 * The {@code PredicateLabel} class is used to represent a label that describes predicates, such as data types, operations or states in the system.
 *
 * @author Vedran Kasalica
 */
public interface PredicateLabel extends Comparable<PredicateLabel> {

    /**
     * Get string that corresponds to the predicate ID.
     *
     * @return String identifying the predicate.
     */
    public String getPredicateID();

    /**
     * Get string that corresponds to the predicate label (e.g. OWL label).
     *
     * @return String representation of the predicate.
     */
    public String getPredicateLabel();
    
    /**
     * Get long string that corresponds to the full predicate label (e.g. OWL URI). The long label is not guaranteed to be unique.
     *
     * @return Detailed String representation of the predicate.
     */
    public String getPredicateLongLabel();

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

}
