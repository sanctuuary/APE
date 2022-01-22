package nl.uu.cs.ape.models.logic.constructs;

/**
 * The {@code StateInterface} class is used to represent a label that describes predicates, such as data types, operations or states in the system.
 *
 * @author Vedran Kasalica
 */
public abstract class PredicateLabel implements Comparable<PredicateLabel>, APEPredicate {

    /**
     * Get string that corresponds to the predicate ID.
     *
     * @return String identifying the predicate.
     */
    public abstract String getPredicateID();

    /**
     * Get string that corresponds to the predicate label (e.g. OWL label). The label is not guaranteed to be unique.
     *
     * @return String representation of the predicate.
     */
    public abstract String getPredicateLabel();
    
    /**
     * Get long string that corresponds to the full predicate label (e.g. OWL URI). The long label is not guaranteed to be unique.
     *
     * @return Detailed String representation of the predicate.
     */
    public abstract String getPredicateLongLabel();

    @Override
    public String toString() {
    	return "ID:" + this.getPredicateID();
    }
    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

}
