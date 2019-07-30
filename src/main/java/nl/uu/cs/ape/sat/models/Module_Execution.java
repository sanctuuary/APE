package nl.uu.cs.ape.sat.models;

import java.io.IOException;
/**
 * TODO: Not implemented yet in practice.
 * @author Vedran Kasalica
 *
 */
public interface Module_Execution {
	
	String getContent();
	void run(String path) throws IOException;
}
