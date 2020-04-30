package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONException;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.utils.APEUtils;

public class LocalRun {

	public static void main(String[] args) {

		String path = "/home/vedran/git/QuAnGIS/WorkflowSynthesis/";
		String subPath = "test/";
		String fileName = "ape.configuration";
		if (!APEUtils.isValidReadFile(path + subPath + fileName)) {
			System.err.println("Bad path.");
			return;
		}

		File file = null;
		try {
			file = File.createTempFile("temp", null);
			file.deleteOnExit();
			String content = APEUtils.readFile(path + subPath + fileName, Charset.defaultCharset());
			content = content.replace("./", path);
			APEUtils.write2file(content, file, false);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		APE apeFramework = null;
		try {
			apeFramework = new APE(file.getAbsolutePath());
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		}
		SATsolutionsList solutions;
		try {
			solutions = apeFramework.runSynthesis(file.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return;
		}
		
		/*
		 * Writing solutions to the specified file in human readable format
		 */
		if (solutions == null) {
			
		} else if (solutions.isEmpty()) {
			System.out.println("UNSAT");
		} else {
			try {
				apeFramework.writeSolutionToFile(solutions);
				apeFramework.writeDataFlowGraphs(solutions, RankDir.TOP_TO_BOTTOM);
//				apeFramework.writeControlFlowGraphs(solutions, RankDir.LEFT_TO_RIGHT);
				apeFramework.writeExecutableWorkflows(solutions);
			} catch (IOException e) {
				System.err.println("Error in writing the solutions. to the file system.");
				e.printStackTrace();
			}

		}

	}
}
