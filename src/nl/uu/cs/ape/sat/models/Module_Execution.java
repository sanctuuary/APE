package nl.uu.cs.ape.sat.models;

import java.io.IOException;

public interface Module_Execution {
	
	String getContent();
	void run(String path) throws IOException;
}
