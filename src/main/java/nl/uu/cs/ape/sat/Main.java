package nl.uu.cs.ape.sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.models.APEConfig;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.All_SAT_solutions;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.SAT_solution;

public class Main {

	/**
	 * Configuration class and the tag used in the config file
	 */
	private static APEConfig config;
	private static final String CONFIGURATION_FILE = "ape.configuration";

	/**
	 * In case that the debug mode is on, print the constraint templates and tool
	 * and data taxonomy trees.
	 * 
	 * @param allModules         - set of all tools
	 * @param allTypes           - set of all data types
	 * @param constraintsFormats - String list of all constraint templates
	 */
	private static void debugPrintout(AllModules allModules, AllTypes allTypes, String constraintsFormats) {
		if (config.getDebug_mode()) {

			/*
			 * Printing the constraint templates
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tConstraint templates:");
			System.out.println("-------------------------------------------------------------");
			System.out.println(constraintsFormats + "\n");

			/*
			 * Printing the Module and Taxonomy Tree
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tTool Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			allModules.getRootModule().printTree(" ", allModules);
			System.out.println("\n-------------------------------------------------------------");
			System.out.println("\tData Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			allTypes.getRootType().printTree(" ", allTypes);
			System.out.println("-------------------------------------------------------------");
		}
	}

	/**
	 * Print header to specify the current workflow length that is being explored
	 */
	private static void printHeader(int solutionLength) {

		System.out.println("\n-------------------------------------------------------------");
		System.out.println("\tWorkflow discovery - length " + solutionLength);
		System.out.println("-------------------------------------------------------------");
	}

	public static void main(String[] args) throws IOException {

		config = APEConfig.getConfig();
		if (config == null || config.getConfigNode() == null) {
			return;
		}

		/*
		 * Check whether the config file is properly formatted.
		 */
		if (!config.defaultConfigSetup()) {
			System.out.println("Please validate the syntax of the configuration file: ./" + CONFIGURATION_FILE
					+ " in order to be able to run the library correctly.");
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
		allTypes.getRootType().addSubType(allTypes.getEmptyType().getTypeID());

		/*
		 * Update allModules and allTypes sets based on the module.csv file
		 */
		AllModules annotated_modules = new AllModules(
				StaticFunctions.readModuleXML(config.getTool_annotations_path(), allModules, allTypes));

		/*
		 * Define set of all constraint formats
		 */
		ConstraintFactory constraintFactory = new ConstraintFactory();
		constraintFactory.initializeConstraints();

		/** Print the setup information when necessary. */
		debugPrintout(allModules, allTypes, constraintFactory.printConstraintsCodes());


		/*
		 * print all the tools
		 */
//		for (Entry<String, AbstractModule> mapModule : annotated_modules.getModules().entrySet()) {
//			System.out.println(mapModule.getValue().print());
//		}

		/**
		 * Loop over different lengths of the workflow until either, max workflow length
		 * or max number of solutions has been found.
		 */
		long realStartTime = System.currentTimeMillis();
		while (allSolutions.getSolutionsFound() < allSolutions.getSolutionsFoundMax() && allSolutions.getSolutionLengthMax() <= allSolutions.getSolutionLengthMax()) {

			SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(allModules, allTypes, allSolutions, config,
					annotated_modules, constraintFactory);
			
			printHeader(allSolutions.getCurrSolutionLenght());

			if (implSATsynthesis.synthesisEncoding() == null) {
				return;
			}
			
			implSATsynthesis.synthesisExecution();
			long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			
			if ((allSolutions.getSolutionsFound() >= allSolutions.getSolutionsFoundMax() - 1) || allSolutions.getSolutionLengthMax() == allSolutions.getSolutionLengthMax()) {
				System.out.println("\nAPE found " + allSolutions.getSolutionsFound() + " solutions. Total solving time: "
						+ (realTimeElapsedMillis / 1000F) + " sec.");
			}
			
			/** Increase the size of the workflow for the next depth iteration */
			allSolutions.incrementLength();
		}
		/*
		 * Writing solutions to the specified file in human readable format
		 */
		if (allSolutions.isEmpty()) {
			System.out.println("UNSAT");
		}
		
		String solutions2write = "";
		
		for (int i = 0; i < allSolutions.getSolutionsFound(); i++) {
			solutions2write += allSolutions.get(i).getRelevantSolutionWithTypes() + "\n";
		}
		StaticFunctions.write2file(solutions2write, new File(config.getSolution_path()), false);

//		StaticFunctions.write2file(allSolutions.get(0).getMappedSolution() + "\n",
//				new File("/home/vedran/Desktop/sat_solutions_map.txt"), false);
//
//		StaticFunctions.write2file(allSolutions.get(0).getSolution() + "\n",
//				new File("/home/vedran/Desktop/sat_solutions_full.txt"), false);

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

				SAT_solution currSol = allSolutions.get(i);
				currSol.getRelevantSolutionModules(allModules);
				for (Module curr : currSol.getRelevantSolutionModules(allModules)) {
					if (curr.getModuleExecution() != null)
						curr.getModuleExecution()
								.run(config.getExecution_scripts_folder() + "/workflowSolution_" + i + ".sh");
				}
			}
		}
		/*
		 * TODO: use tool multiple times, consider removing permutations, SWL output?
		 */

	}
	
}
