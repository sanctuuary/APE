package nl.uu.cs.ape.sat.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Module_Execution_Code implements Module_Execution {
	String code;

	Module_Execution_Code(String code) {
		this.code = code;
	}

	public void run(String path) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
		String[] lines = code.split("EOL");
		for(String currLine : lines) {
			out.println(currLine);
		}
		out.close();

	}

	public String getContent() {
		return code;
	}

}
