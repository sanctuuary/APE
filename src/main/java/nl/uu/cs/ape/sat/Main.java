package nl.uu.cs.ape.sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.core.implSAT.All_SAT_solutions;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SAT_solution;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;
import nl.uu.cs.ape.sat.utils.OWLReader;

public class Main {

	/**
	 * Configuration class and the tag used in the config file
	 */
	private static APEConfig config;
	private static final String CONFIGURATION_FILE = "ape.configuration";

	/**
	 * Read the CONFIGURATION_FILE file..
	 * 
	 * @return {@code true} if setup was successful, {@code false} otherwise.
	 */
	private static boolean configSetup() {
		config = APEConfig.getConfig();
		if (config == null || config.getConfigNode() == null) {
			return false;
		}

		/*
		 * Check whether the config file is properly formatted.
		 */
		if (!config.defaultConfigSetup()) {
			System.out.println("Please validate the syntax of the configuration file: ./" + CONFIGURATION_FILE
					+ " in order to be able to run the library correctly.");
			return false;
		}
		return true;
	}
	
	public static void writeToFile(All_SAT_solutions allSolutions) {
		StringBuilder solutions2write = new StringBuilder();
		
		for (int i = 0; i < allSolutions.getSolutionsFound(); i++) {
			solutions2write = solutions2write.append(allSolutions.get(i).getReadableSolution()).append("\n");
		}
		APEUtils.write2file(solutions2write.toString(), new File(config.getSolution_path()), false);
	}
	
	public static void executeWorkflows(All_SAT_solutions allSolutions, AllModules allModules) throws IOException {
		/**
		 * Executing the workflows.
		 */
		Integer noExecutions = APEConfig.getConfig().getNo_executions();
		if (noExecutions != null && noExecutions > 0) {
			for (int i = 0; i < noExecutions && i < allSolutions.getSolutionsFound(); i++) {

				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
						config.getExecution_scripts_folder() + "/workflowSolution_" + i + ".sh", false)));
				out.println("");
				out.close();

				SAT_solution currSol = allSolutions.get(i).getnativeSATsolution();
				currSol.getRelevantSolutionModules(allModules);
				for (Module curr : currSol.getRelevantSolutionModules(allModules)) {
					if (curr.getModuleExecution() != null)
						curr.getModuleExecution()
								.run(config.getExecution_scripts_folder() + "/workflowSolution_" + i + ".sh");
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {

		if(!configSetup()) {
			return;
		}

		/**
		 * List of all the solutions
		 */
		All_SAT_solutions allSolutions = new All_SAT_solutions(config);

		/*
		 * Encode the taxonomies as objects - generate the list of all types / modules
		 * occurring in the taxonomies defining their submodules/subtypes
		 */
		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();

		OWLReader owlReader = new OWLReader(allModules, allTypes);
		Boolean ontologyRead = owlReader.readOntology(); // true if the ontology file is well-formatted

		if (ontologyRead == false) {
			System.out.println("Error occured while reading the provided ontology.");
			return;
		}

		/*
		 * Set the the empty type (representing the absence of types) as a direct child
		 * of root type
		 */
		allTypes.getRootType().addSubType(allTypes.getEmptyType().getPredicateID());

		/*
		 * Update allModules and allTypes sets based on the module.xml file
		 */
		AllModules annotated_modules = new AllModules(
				APEUtils.readModuleXML(config.getTool_annotations_path(), allModules, allTypes));

		/*
		 * Define set of all constraint formats
		 */
		ConstraintFactory constraintFactory = new ConstraintFactory();
		constraintFactory.initializeConstraints();

		/** Print the setup information when necessary. */
		APEUtils.debugPrintout(config.getDebug_mode(), allModules, allTypes, constraintFactory.printConstraintsCodes());

		
		/**
		 * Loop over different lengths of the workflow until either, max workflow length
		 * or max number of solutions has been found.
		 */
		String globalTimerID = "globalTimer";
		APEUtils.timerStart(globalTimerID, true);
		int solutionLength = config.getSolution_min_length();
		while (allSolutions.getSolutionsFound() < allSolutions.getSolutionsFoundMax() && solutionLength <= config.getSolution_max_length()) {

			SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(allModules, allTypes, allSolutions, config,
					annotated_modules, constraintFactory, solutionLength);
			
			APEUtils.printHeader(implSATsynthesis.getSolutionSize());

			/** Encoding of the synthesis problem */
			if (implSATsynthesis.synthesisEncoding() == null) {
				return;
			}
			/** Execution of the synthesis */
			implSATsynthesis.synthesisExecution();
			
			if ((allSolutions.getSolutionsFound() >= allSolutions.getSolutionsFoundMax() - 1) || solutionLength == config.getSolution_max_length()) {
				APEUtils.timerPrint(globalTimerID, allSolutions.getSolutionsFound());
			}
			
			/** Increase the size of the workflow for the next depth iteration */
			solutionLength++;
		}
		
		/*
		 * Writing solutions to the specified file in human readable format
		 */
		if (allSolutions.isEmpty()) {
			System.out.println("UNSAT");
		}
		
		writeToFile(allSolutions);

		executeWorkflows(allSolutions, allModules);

	}
	
}
