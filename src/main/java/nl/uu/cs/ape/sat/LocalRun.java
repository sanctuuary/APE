package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.json.JSONException;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.CWLCreator;
import nl.uu.cs.ape.sat.utils.APEUtils;

public class LocalRun {

	public static void main(String[] args) {

		String path = "/home/vedran/ownCloud/PhD/All Use Cases/Evaluation/New Use Cases/";
		String subPath = "SimpleDemo/";
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
		} catch (ExceptionInInitializerError e) {
			System.err.println(e.getMessage());
		}
		
		SATsolutionsList solutions;
		try {
			solutions = apeFramework.runSynthesis(file.getAbsolutePath(), apeFramework.getDomainSetup());
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
				
//				CWLCreator cwl = new CWLCreator(solutions.get(0), apeFramework.getConfig());
//				APEUtils.write2file(cwl.getCWL(), new File(path + subPath + "tmp"), false);
			} catch (IOException e) {
				System.err.println("Error in writing the solutions. to the file system.");
				e.printStackTrace();
			}

		}

	}
}
