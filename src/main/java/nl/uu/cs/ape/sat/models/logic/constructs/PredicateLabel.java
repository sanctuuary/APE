package nl.uu.cs.ape.sat.models.logic.constructs;

/**
 * The {@code PredicateLabel} class is used to represent a label that describes predicates, such as data types, operations or states in the system. 
 *
 * @author Vedran Kasalica
 *
 */
public interface PredicateLabel extends Comparable<PredicateLabel>{

	/** 
	 * Get string that corresponds to the predicate.
	 * @return String representation of the predicate.
	 */
	public String getPredicateID();
	
	@Override
	public int hashCode();
	
	@Override
	public boolean equals(Object obj);

}
