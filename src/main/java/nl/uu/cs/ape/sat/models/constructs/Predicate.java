package nl.uu.cs.ape.sat.models.constructs;

public interface Predicate {

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
