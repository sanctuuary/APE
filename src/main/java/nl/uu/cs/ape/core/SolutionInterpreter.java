package nl.uu.cs.ape.core;

import java.util.List;

import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.Module;

/**
 * The {@code SolutionInterpreter} class defines objects used to interpret the solutions given in native formats (e.g. SAT, SMT output).
 *
 * @author Vedran Kasalica
 */
public abstract class SolutionInterpreter {

    /**
     * Gets solution.
     *
     * @return the solution
     */
    public abstract String getSolution();

    /**
     * Gets relevant solution.
     *
     * @return the relevant solution
     */
    public abstract String getRelevantSolution();
    
    /**
     * Returns only the most important part of the solution in human readable format, containing the list of tools in the order of execution.
     * @return
     */
    public abstract String getRelevantToolsInSolution();

    
    /**
     * Returns the complete solution in human readable format.
     *
     * @return String representing the solution.
     */
    public abstract String getCompleteSolution();
    
    
    /**
     * Gets relevant solution modules.
     *
     * @param allModules the all modules
     * @return the relevant solution modules
     */
    public abstract List<Module> getRelevantSolutionModules(AllModules allModules);

    /**
     * Is sat boolean.
     *
     * @return the boolean
     */
    public abstract boolean isSat();

}
