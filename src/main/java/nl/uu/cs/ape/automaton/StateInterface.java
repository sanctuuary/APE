package nl.uu.cs.ape.automaton;

/**
 * The {@code PredicateLabel} class is used to represent a label that describes
 * predicates, such as data types, operations or states in the system.
 *
 * @author Vedran Kasalica
 */
public interface StateInterface {

    /**
     * Get string that corresponds to the predicate ID.
     *
     * @return String identifying the predicate.
     */
    public String getPredicateID();

    /**
     * By default variable states have no absolute state number.
     * 
     * @return If absolute number is not defined, return {@code -1}.
     */
    public default int getAbsoluteStateNumber() {
        return -1;
    }

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

}
