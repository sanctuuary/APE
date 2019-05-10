package nl.uu.cs.ape.sat;

import java.io.IOException;

import nl.uu.cs.ape.sat.models.All_solutions;

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
