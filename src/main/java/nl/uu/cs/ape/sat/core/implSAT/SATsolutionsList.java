package nl.uu.cs.ape.sat.core.implSAT;

import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.configuration.APERunConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@code All_solutions} class is used to store all the SAT solutions
 * generated by the program, together with the corresponding mappings.
 *
 * @author Vedran Kasalica
 */
public class SATsolutionsList {

    private List<SolutionWorkflow> solutions;

    /**
     * Max number of solutions that should be found.
     */
    private final int maxSolutions;

    /**
     * Mapping of predicates into integers (for SAT encoding).
     */
    private final AtomMappings mappings;

    /**
     * Current colution index.
     */
    private int solutionIndex = 0;
    /**
     * APE run configuration.
     */
    private final APERunConfig runConfig;
    /**
     * Create an object that will contain all the solutions of the synthesis.
     *
     * @param runConfig - setup configuration for the synthesis run.
     */
    public SATsolutionsList(APERunConfig runConfig) {
        this.solutions = new ArrayList<SolutionWorkflow>();
        this.runConfig = runConfig;
        /* Provides mapping from each atom to a number, and vice versa */
        this. mappings = new AtomMappings();

        /* Variables defining the current and maximum lengths and solutions count.*/
        this.maxSolutions = runConfig.getMaxNoSolutions();
        if (this.maxSolutions > 1000) {
            System.out.println("Looking for " + maxSolutions + " solutions might take some time.");
        }
    }

    /**
     * Get the number of solutions that are currently found.
     *
     * @return Number of solutions in the solutions set.
     */
    public int getNumberOfSolutions() {
        return this.solutions.size();
    }

    /**
     * Get max number of solutions that should be found. This number is defined in the ape.config file.
     *
     * @return Max number of solutions that should be found.
     */
    public int getMaxNumberOfSolutions() {
        return maxSolutions;
    }

    /**
     * Get object that contains mappings of all the atoms.
     *
     * @return The {@link AtomMappings} object.
     */
    protected AtomMappings getMappings() {
        return mappings;
    }

    /**
     * Returns true if the list of all the solutions contains no elements.
     *
     * @return true if the list contains no elements.
     */
    public boolean isEmpty() {
        return this.solutions.isEmpty();
    }
    
    /**
     * Get the ape run configuration available at the start.
     * @return the object that contains the run configuration information.
     */
    public APERunConfig getRunConfiguration() {
    	return runConfig;
    }

    /**
     * The procedure resets the encodings specific for a synthesis run (such as auxiliary variables).
     */
    protected void newEncoding() {
        mappings.resetAuxVariables();
    }
    
    /**
     * Appends all of the elements in the specified collection to the end of this list, in the order
     * that they are returned by the specified collection's iterator (optional operation). The behavior
     * of this operation is undefined if the specified collection is modified while the operation
     * is in progress. (Note that this will occur if the specified collection is this list,
     * and it's nonempty.)
     *
     * @param currSolutions Solutions that should be added to the list of all solutions.
     * @return true if this list changed as a result of the call.
     */
    public boolean addSolutions(List<SolutionWorkflow> currSolutions) {
        for (SolutionWorkflow solution : currSolutions) {
            solution.setIndex(solutionIndex++);
            this.solutions.add(solution);
        }
        return false;
    }

    /**
     * Get solution workflow.
     *
     * @param index the index
     * @return the solution workflow
     */
    public SolutionWorkflow get(int index) {
        return this.solutions.get(index);
    }

    /**
     * Returns the number of elements in this list.
     * If this list contains more than Integer.MAX_VALUE elements,
     * returns Integer.MAX_VALUE.
     *
     * @return The number of elements in this list.
     */
    public int size() {
        return this.solutions.size();
    }

    /**
     * Return the stream that represent the solutions.
     *
     * @return list of solutions.
     */
    public Stream<SolutionWorkflow> getStream() {
        return this.solutions.stream();
    }

    /**
     * Return a potentially parallel stream that represent the solutions.
     *
     * @return list of solutions.
     */
    public Stream<SolutionWorkflow> getParallelStream() {
        return this.solutions.parallelStream();
    }
}
