package nl.uu.cs.ape.sat.models.constructs;

public interface Predicate {

	public String getPredicate();
	
	@Override
	public int hashCode();
	
	@Override
	public boolean equals(Object obj);
}
