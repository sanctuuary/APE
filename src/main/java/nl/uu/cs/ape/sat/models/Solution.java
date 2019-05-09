package nl.uu.cs.ape.sat.models;

import java.util.List;

/**
 * Used as interface for solutions represented in any format.
 * 
 * @author Vedran Kasalica
 *
 */
public interface Solution {

	
	public String getSolution();
	
	public String getRelevantSolution();
	
	public List<Module> getRelevantSolutionModules(AllModules allModules);
	
	public boolean isSat();
}
