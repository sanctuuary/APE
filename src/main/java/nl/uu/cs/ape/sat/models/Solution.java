package nl.uu.cs.ape.sat.models;

import java.util.List;

/**
 * The {@code Solution} class is sed as an interface for solutions that can be implemented in different formats.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class Solution {

	
	public abstract String getSolution();
	
	public abstract String getRelevantSolution();
	
	public abstract List<Module> getRelevantSolutionModules(AllModules allModules);
	
	public abstract boolean isSat();
}
