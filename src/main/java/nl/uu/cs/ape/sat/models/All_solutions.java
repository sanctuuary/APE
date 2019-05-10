package nl.uu.cs.ape.sat.models;

import java.util.List;

public interface All_solutions {

	public Solution getSolutions();
	
	public boolean isEmpty();
	
	public boolean addAll(List<? extends Solution> currSolutions);
	
	
}
