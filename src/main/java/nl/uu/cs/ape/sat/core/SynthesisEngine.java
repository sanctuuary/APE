package nl.uu.cs.ape.sat.core;

import java.io.IOException;

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
     */
    public boolean synthesisExecution();

}
