package nl.uu.cs.ape.sat;

import java.io.IOException;

public interface SynthesisEngine {
	
	public String synthesisEncoding() throws IOException;
	
	public boolean synthesisExecution();
	

}
