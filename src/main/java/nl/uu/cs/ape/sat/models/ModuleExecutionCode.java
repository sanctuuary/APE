package nl.uu.cs.ape.sat.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ModuleExecutionCode implements ModuleExecution {
	String code;

	ModuleExecutionCode(String code) {
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
