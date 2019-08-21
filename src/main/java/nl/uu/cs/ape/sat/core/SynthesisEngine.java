package nl.uu.cs.ape.sat.core;

import java.io.IOException;

/**
 * The {@code SynthesisEngine} interface is used as a template in order to implement different synthesis approaches over the given input.
 *  
 * @author Vedran Kasalica
 *
 */
public interface SynthesisEngine {
	
	public String synthesisEncoding() throws IOException;
	
	public boolean synthesisExecution();
	

}
