package nl.uu.cs.ape.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.utils.APEDomainSetup;

/**
 * The {@code SynthesisEngine} interface is used as a template in order to implement different synthesis approaches over the given input.
 *
 * @author Vedran Kasalica
 */
public interface SynthesisEngine {

    /**
     * Synthesis encoding boolean.
     *
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean synthesisEncoding() throws IOException;

    /**
     * Synthesis execution boolean.
     *
     * @return the boolean
     * @throws IOException sat encoding not defined 
     * @throws FileNotFoundException Configuration file error
     */
    public List<SolutionWorkflow> synthesisExecution() throws FileNotFoundException, IOException;

    /**
     * Get type automaton.
     * @return The type automaton.
     */
	public TypeAutomaton getTypeAutomaton();

	/**
	 * Get domain model.
	 * @return Object that contains the domain model annotations, params, etc.
	 */
	public APEDomainSetup getDomainSetup();

	/**
	 * Get atom mappings
	 * @return Atom mappings.
	 */
	public SATAtomMappings getMappings();

	/**
	 * Get current solution size.
	 * @return Size of the solution as int.
	 */
	public int getSolutionSize();

	/** 
	 * Delete all temp files generated.
	 */
	public void deleteTempFiles();

}
