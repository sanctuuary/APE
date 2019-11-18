package nl.uu.cs.ape.sat.models;

import java.io.IOException;
/**
 * TODO: Not implemented yet in practice.
 * @author Vedran Kasalica
 *
 */
public interface ModuleExecution {
	
	String getContent();
	void run(String path) throws IOException;
}
