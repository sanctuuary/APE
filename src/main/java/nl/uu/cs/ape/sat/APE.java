/**
 * 
 */
package nl.uu.cs.ape.sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.core.implSAT.All_SAT_solutions;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SAT_solution;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.ConstraintData;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;
import nl.uu.cs.ape.sat.utils.OWLReader;

/**
 * The {@code APE} class is the main class of the library and is supposed to be the main interface for working with the library.
 *
 * @author Vedran Kasalica
 *
 */
public class APE {
	/**
	 * Configuration class and the tag used in the config file
	 */
	private static APEConfig config;
	private All_SAT_solutions allSolutions;
	private AllModules allModules;
	private AllTypes allTypes;
	
	/**
	 * Create instance of the APE solver.
	 * @param configPath - path to the APE configuration file. If the string is null the default './ape.config' value is assumed.
	 * @throws IOException error in reading the configuration file
	 * @throws JSONException error in reading the configuration file
	 */
	public APE(String configPath) throws IOException, JSONException{
		config = APEConfig.getConfig(configPath);
		if (config == null || config.getConfigJsonObj() == null) {
			new ExceptionInInitializerError();
		}
		
		if (!config.defaultConfigSetup()) {
			new ExceptionInInitializerError();
		}
	}
	
	/**
	 * Create instance of the APE solver.
	 * @param configPath - the APE configuration JSONObject{@link JSONObject}.
	 * @throws JSONException error in reading the configuration object
	 */
	public APE(JSONObject configObject) throws JSONException{
		config = APEConfig.getConfig(configObject);
		if (config == null || config.getConfigJsonObj() == null) {
			new ExceptionInInitializerError();
		}
		
		if (!config.defaultConfigSetup()) {
			new ExceptionInInitializerError();
		}
	}
	
	/**
	 * Write textual "human readable" version on workflow solutions to a file.
	 * @param allSolutions
	 */
	private void writeSolutionToFile() {
		StringBuilder solutions2write = new StringBuilder();

		for (int i = 0; i < allSolutions.getNumberOfSolutions(); i++) {
			solutions2write = solutions2write.append(allSolutions.get(i).getnativeSATsolution().getRelevantSolution())
					.append("\n");
		}
		APEUtils.write2file(solutions2write.toString(), new File(config.getSolution_path()), false);
	}

	/**
	 * TODO: This feature is not fully functioning yet.
	 * Generating scripts that represent executable versions of the workflow solutions and executing them. 
	 * @param allSolutions
	 * @param allModules
	 * @throws IOException
	 */
	private void executeWorkflows() throws IOException {
		String executionsFolder = config.getExecution_scripts_folder();
		Integer noExecutions = config.getNo_executions();
		if (executionsFolder == null || noExecutions == null || noExecutions == 0 || allSolutions.isEmpty()) {
			return;
		}
		APEUtils.printHeader(null, "Executing first " + noExecutions + " solution");
		APEUtils.timerStart("executingWorkflows", true);

		Arrays.stream(
				new File(executionsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("workflowSolution_")))
				.forEach(File::delete);
		System.out.print("Loading");
		for (int i = 0; i < noExecutions && i < allSolutions.getNumberOfSolutions(); i++) {

			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter(executionsFolder + "/workflowSolution_" + i + ".sh", false)));
			out.println("");
			out.close();
			SAT_solution currSol = allSolutions.get(i).getnativeSATsolution();
			currSol.getRelevantSolutionModules(allModules);
			for (Module curr : currSol.getRelevantSolutionModules(allModules)) {
				if (curr.getModuleExecution() != null) {
					curr.getModuleExecution()
							.run(config.getExecution_scripts_folder() + "/workflowSolution_" + i + ".sh");
				}
			}
			System.out.print(".");
			if (i > 0 && i % 60 == 0) {
				System.out.println();
			}
		}
		APEUtils.timerPrintText("executingWorkflows", "\nWorkflows have been executed.");
	}

	/**
	 * Generate the graphical representations of the workflow solutions.
	 * @param allSolutions
	 * @throws IOException
	 */
	private void generateGraphOutput() throws IOException {
		String graphsFolder = config.getSolution_graphs_folder();
		Integer noGraphs = config.getNo_graphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return;
		}
		APEUtils.printHeader(null, "Geneating graphical representation", "of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);
		System.out.println();
		List<String> images = new ArrayList<String>();
		Arrays.stream(
				new File(graphsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
				.forEach(File::delete);
		System.out.print("Loading");
		for (int i = 0; i < noGraphs && i < allSolutions.getNumberOfSolutions(); i++) {

			String currTitle = "SolutionNo_" + i + "_length_" + allSolutions.get(i).getSolutionlength();
			String filePath = graphsFolder + "/" + currTitle;

			Graph workflowGraph = allSolutions.get(i).getSolutionGraph(currTitle);

			Graphviz.fromGraph(workflowGraph).render(Format.PNG).toFile(new File(filePath));
			images.add(filePath);
			System.out.print(".");
			if (i > 0 && i % 60 == 0) {
				System.out.println();
			}
		}
		APEUtils.timerPrintText("drawingGraphs", "\nGraphical files have been generated.");

	}
	
	/**
	 * Run the synthesis for the given workflow specification.
	 * 
	 * @return {@code true} if the synthesis was successfully performed, {@code false} otherwise.
	 * @throws IOException error in case of not providing a proper configuration file.
	 */
	public boolean runSynthesis() throws IOException {

		/**
		 * List of all the solutions
		 */
		allSolutions = new All_SAT_solutions(config);

		/*
		 * Encode the taxonomies as objects - generate the list of all types / modules
		 * occurring in the taxonomies defining their submodules/subtypes
		 */
		allModules = new AllModules();
		allTypes = new AllTypes();

		OWLReader owlReader = new OWLReader(allModules, allTypes);
		Boolean ontologyRead = owlReader.readOntology(); // true if the ontology file is well-formatted

		if (ontologyRead == false) {
			System.out.println("Error occured while reading the provided ontology.");
			return false;
		}

		/*
		 * Set the the empty type (representing the absence of types) as a direct child
		 * of root type
		 */
		allTypes.getRootPredicate().addSubPredicate(allTypes.getEmptyType().getPredicateID());

		/*
		 * Update allModules and allTypes sets based on the module.json file
		 */
		APEUtils.readModuleJson(config.getTool_annotations_path(), allModules, allTypes);
		
		allModules.trimTaxonomy();
		allTypes.trimTaxonomy();
		
		/*
		 * Define set of all constraint formats
		 */
		ConstraintFactory constraintFactory = new ConstraintFactory();
		constraintFactory.initializeConstraints();
		List<ConstraintData> unformattedConstr = new ArrayList<ConstraintData>();
		
		unformattedConstr = APEUtils.readConstraints(config.getConstraints_path());
		

		/** Print the setup information when necessary. */
		APEUtils.debugPrintout(config.getDebug_mode(), allModules, allTypes, constraintFactory, unformattedConstr);

		/**
		 * Loop over different lengths of the workflow until either, max workflow length
		 * or max number of solutions has been found.
		 */
		String globalTimerID = "globalTimer";
		APEUtils.timerStart(globalTimerID, true);
		int solutionLength = config.getSolution_min_length();
		while (allSolutions.getNumberOfSolutions() < allSolutions.getMaxNumberOfSolutions()
				&& solutionLength <= config.getSolution_max_length()) {

			SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(allModules, allTypes, allSolutions, config, constraintFactory, unformattedConstr, solutionLength);

			APEUtils.printHeader(implSATsynthesis.getSolutionSize(), "Workflow discovery - length");

			/** Encoding of the synthesis problem */
			if (!implSATsynthesis.synthesisEncoding()) {
				System.err.println("Internal error in problem encoding.");
				return false;
			}
			/** Execution of the synthesis */
			implSATsynthesis.synthesisExecution();

			if ((allSolutions.getNumberOfSolutions() >= allSolutions.getMaxNumberOfSolutions() - 1)
					|| solutionLength == config.getSolution_max_length()) {
				APEUtils.timerPrintSolutions(globalTimerID, allSolutions.getNumberOfSolutions());
			}

			/** Increase the size of the workflow for the next depth iteration */
			solutionLength++;
		}

		/*
		 * Writing solutions to the specified file in human readable format
		 */
		if (allSolutions.isEmpty()) {
			System.out.println("UNSAT");
		} else {

			writeSolutionToFile();
			generateGraphOutput();
			executeWorkflows();

		}
		return true;
	}
	
	
}
