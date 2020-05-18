package nl.uu.cs.ape.sat;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.utils.APEUtils;

public class Main {

	public static void main(String[] args) {
		String path;
		if(args.length == 1) {
			path = args[0];
		} else {
			path = "./ape.configuration";
		}
		if (!APEUtils.isValidReadFile(path)) {
			System.err.println("Bad path.");
			return;
		}


		APE apeFramework = null;
		try {
			apeFramework = new APE(path);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		} catch (ExceptionInInitializerError e) {
			System.err.println(e.getMessage());
			return;
		}
		
		SATsolutionsList solutions;
		try {
			solutions = apeFramework.runSynthesis(path, apeFramework.getDomainSetup());
		} catch (IOException e) {
			System.err.println("Error in synthesis execution. Writing to the file system failed.");
			return;
		}

		/*
		 * Writing solutions to the specified file in human readable format
		 */
		if (solutions.isEmpty()) {
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
