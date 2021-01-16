package nl.uu.cs.ape.sat.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;

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

}
