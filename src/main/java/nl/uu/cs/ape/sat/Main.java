package nl.uu.cs.ape.sat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.constraints.AllConstraintTamplates;
import nl.uu.cs.ape.sat.models.APEConfig;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.SAT_solution;
import nl.uu.cs.ape.sat.models.Type;

import javax.xml.parsers.*;
import java.io.*;

public class Main {

	/**
	 * Configuration class and the tag used in the config file
	 */
	private static APEConfig config;
	private static final String CONFIGURATION_FILE = "ape.configuration";

	public static void main(String[] args) throws IOException {

		config = APEConfig.getConfig();
		if (config == null || config.getConfigNode() == null) {
			return;
		}

		/*
		 * Check whether the config file is supposed to be updated or the default values
		 * should be used.
		 */
		if (args.length == 0) {
			if (!config.defaultConfigSetup()) {
				System.out.println("Please validate the syntax of the configuration file: ./" + CONFIGURATION_FILE
						+ " in order to be able to run the library correctly.");
				return;
			}
		} else {
			System.out.println("Invalid number of arguments.");
			return;
		}

		/*
		 * Variables defining the current and maximum lengths and solutions count.
		 */
		int solutionsFound = 0;
		int solutionsFoundMax = (config.getMax_no_solutions() > 0) ? config.getMax_no_solutions() : 1000;
		int solutionLength = (config.getSolution_min_length() > 0) ? config.getSolution_min_length() : 1;
		int solutionLengthMax = (config.getSolution_max_length() > 0) ? config.getSolution_max_length() : 20;

		/**
		 * Provides mapping from each atom to a number, and vice versa
		 */
		AtomMapping mappings = new AtomMapping();

		/*
		 * encode the taxonomies as objects - generate the list of all types / modules
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

		AbstractModule rootModule = allModules.getRootModule();
		Type rootType = allTypes.getRootType();

		/*
		 * Define the empty type, representing the absence of types
		 */
		rootType.addSubType(allTypes.getEmptyType().getTypeID());

		/*
		 * Update allModules and allTypes sets based on the module.csv file
		 */
		AllModules annotated_modules = new AllModules(
				StaticFunctions.readModuleXML(config.getTool_annotations_path(), allModules, allTypes));

		/*
		 * Define set of all constraint formats
		 */
		AllConstraintTamplates allConsTemplates = new AllConstraintTamplates();
		if(!config.getDebug_mode()) {
			StaticFunctions.initializeConstraints(allConsTemplates);
		} else {
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tConstraint templates:");
			System.out.println("-------------------------------------------------------------");
			System.out.println(StaticFunctions.initializeConstraints(allConsTemplates) + "\n");
		}

		/**
		 * List of all the solutions
		 */
		List<SAT_solution> allSolutions = new ArrayList<SAT_solution>();

		/*
		 * printing the Module and Taxonomy Tree
		 */
		if (config.getDebug_mode()) {
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
		while (solutionsFound < solutionsFoundMax && solutionLength <= solutionLengthMax) {
			System.out.println("\n-------------------------------------------------------------");
			System.out.println("\tWorkflow discovery - length " + solutionLength);
			System.out.println("-------------------------------------------------------------");
			String cnf = "";

			ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
			TypeAutomaton typeAutomaton = new TypeAutomaton();

			/*
			 * generate the automaton in CNF
			 */
			StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, solutionLength,
					config.getMax_no_tool_outputs());

			/*
			 * Encode the workflow input
			 */
			 String inputDataEncoding = StaticFunctions.encodeInputData(config.getProgram_inputs(), typeAutomaton, solutionLength,
					allTypes.getEmptyType(), mappings, allTypes);
			 if (inputDataEncoding == null) {
				 return;
			 }
			 cnf += inputDataEncoding; 
			/*
			 * Create constraints from the module.csv file
			 */
			cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, config.getShared_memory(),
					allTypes.getEmptyType(), mappings);

			/*
			 * Create the constraints enforcing: 1. Mutual exclusion of the tools 2.
			 * Mandatory usage of the tools - from taxonomy. 3. Adding the constraints
			 * enforcing the taxonomy structure.
			 */
			cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
			cnf += allModules.moduleMandatoryUsage(annotated_modules, moduleAutomaton, mappings);
			cnf += allModules.moduleEnforceTaxonomyStructure(rootModule.getModuleID(), moduleAutomaton, mappings);

			/*
			 * Create the constraints enforcing: 1. Mutual exclusion of the types/formats 2.
			 * Mandatory usage of the types in the transition nodes (note: "empty type" is
			 * considered a type) 3. Adding the constraints enforcing the taxonomy
			 * structure.
			 */
			cnf += allTypes.typeMutualExclusion(typeAutomaton, mappings);
			cnf += allTypes.typeMandatoryUsage(rootType, typeAutomaton, mappings);
			cnf += allTypes.typeEnforceTaxonomyStructure(rootType.getTypeID(), typeAutomaton, mappings);

			/*
			 * Encode the constraints from the paper manually
			 */
			cnf += StaticFunctions.generateSLTLConstraints(config.getConstraints_path(), allConsTemplates, allModules,
					allTypes, mappings, moduleAutomaton, typeAutomaton);

			/*
			 * Counting the number of variables and clauses that will be given to the SAT
			 * solver TODO Improve this approach, no need to read the whole String again.
			 */
			int variables = mappings.getSize();
			int clauses = StringUtils.countMatches(cnf, " 0");
			String sat_input_header = "p cnf " + variables + " " + clauses + "\n";

			/*
			 * Create a temp file that will be used as input for the SAT solver.
			 */
			File temp_sat_input = null;
			try {
				temp_sat_input = File.createTempFile("sat_input_" + solutionLength + "_len_", ".cnf");
//				temp_sat_input.deleteOnExit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * Fixing the input and output files for easier testing.
			 */

			StaticFunctions.write2file(sat_input_header + cnf, temp_sat_input, false);

			long realStartTime = System.currentTimeMillis();
			List<SAT_solution> currSolutions = StaticFunctions.solve(temp_sat_input.getAbsolutePath(), mappings,
					allModules, allTypes, solutionsFound, solutionsFoundMax, solutionLength);
			solutionsFound += currSolutions.size();
			/**
			 * Add current solutions to list of all solutions.
			 */
			allSolutions.addAll(currSolutions);
			long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			if ((solutionsFound >= solutionsFoundMax - 1) || solutionLength == solutionLengthMax) {
				System.out.println("\nAPE found " + solutionsFound + " solutions. Total solving time: "
						+ (realTimeElapsedMillis / 1000F) + " sec.");
			} else {
//				System.out.println("Found " + solutionsFound + " solutions. Solving time: "
//						+ (realTimeElapsedMillis / 1000F) + " sec.");
			}

			/**
			 * Increase the size of the workflow for the next depth iteration
			 */
			solutionLength++;
		}
		/*
		 * Writing solutions to the specified file in human readable format
		 */
		boolean first = false;
		if (allSolutions.isEmpty()) {
			System.out.println("UNSAT");
			return;
		}
		for (int i = 0; i < allSolutions.size(); i++) {
			StaticFunctions.write2file(allSolutions.get(i).getRelevantSolutionWithTypes() + "\n",
					new File(config.getSolution_path()), first);
			first = true;
		}

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
			for (int i = 0; i < noExecutions && i < allSolutions.size(); i++) {

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
