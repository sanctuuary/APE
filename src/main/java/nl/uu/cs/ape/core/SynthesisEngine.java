package nl.uu.cs.ape.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.models.Mappings;
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
     */
    public List<SolutionWorkflow> synthesisExecution() throws FileNotFoundException, IOException;

	public TypeAutomaton getTypeAutomaton();

	public APEDomainSetup getDomainSetup();

	public Mappings getMappings();

	public int getSolutionSize();

	public void deleteTempFiles();

}
