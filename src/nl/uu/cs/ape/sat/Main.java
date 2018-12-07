package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.IOException;
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
	 * Configuration class.
	 */
	private static APEConfig config;
//	/**
//	 * Tags used in the config file
//	 */
	private static final String CONFIGURATION_FILE = "ape.configuration";
//	private static final String ONTOLOGY_TAG = "ontology_path";
//	private static final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";
//	private static final String CONSTRAINTS_TAG = "constraints_path";
//	private static final String SOLUTION_TAG = "solutions_path";
//	private static final String SOLUTIION_MIN_LENGTH_TAG = "solution_min_length";
//	private static final String PILEPINE_TAG = "pipeline";
//	private static final String MAX_NO_SOLUTIONS_TAG = "max_solutions";
//	/*
//	 * Max number of solution that the solver will return.
//	 */
//	private static Integer MAX_NO_SOLUTIONS;
//
//	/**
//	 * Path to the taxonomy file
//	 */
//	private static String ONTOLOGY_PATH;
//
//	private static String TOOL_ANNOTATIONS_PATH;
//	/**
//	 * Path to the file that will contain all the solutions to the problem in human
//	 * readable representation.
//	 */
//	private static String SOLUTION_PATH;
//
//	/**
//	 * Path to the file with all workflow constraints.
//	 */
//	private static String CONSTRAINTS_PATH;
//	/**
//	 * Length of the solutions (length of the automaton).
//	 */
//	private static Integer SOLUTIION_MIN_LENGTH;
//	/**
//	 * Output branching factor (max number of outputs per tool).
//	 */
//	private static Integer MAX_NO_TOOL_OUTPUTS = 3;
//	/**
//	 * {@code true} if THE pipeline approach should be used, {@code false} in case
//	 * of general memory approach.
//	 */
//	private static Boolean PILEPINE;
//
//	/**
//	 * Configurations used to read/update the "ape.configuration" file.
//	 */
//	
//	
//	private static Document document;
//	private static Node configNode;
//
//
//	/**
//	 * Setting up the configuration of the library.
//	 * 
//	 * @return {@code true} if the method successfully set-up the configuration,
//	 *         {@code false} otherwise.
//	 */
//	private static boolean defaultConfigSetup() {
//		
//		ONTOLOGY_PATH = configNode.selectSingleNode(ONTOLOGY_TAG).getText();
//		if (!StaticFunctions.isValidConfigReadFile(ONTOLOGY_TAG, ONTOLOGY_PATH)) {
//			return false;
//		}
//
//		TOOL_ANNOTATIONS_PATH = configNode.selectSingleNode(TOOL_ANNOTATIONS_TAG).getText();
//		if (!StaticFunctions.isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, TOOL_ANNOTATIONS_PATH)) {
//			return false;
//		}
//
//		CONSTRAINTS_PATH = configNode.selectSingleNode(CONSTRAINTS_TAG).getText();
//		if (!StaticFunctions.isValidConfigReadFile(CONSTRAINTS_TAG, CONSTRAINTS_PATH)) {
//			return false;
//		}
//
//		SOLUTION_PATH = configNode.selectSingleNode(SOLUTION_TAG).getText();
//		if (!StaticFunctions.isValidConfigWriteFile(SOLUTION_TAG, SOLUTION_PATH)) {
//			return false;
//		}
//
//		SOLUTIION_MIN_LENGTH = StaticFunctions.isValidConfigInt(SOLUTIION_MIN_LENGTH_TAG,
//				configNode.selectSingleNode(SOLUTIION_MIN_LENGTH_TAG).getText());
//		if (SOLUTIION_MIN_LENGTH == null) {
//			return false;
//		}
//
//		MAX_NO_SOLUTIONS = StaticFunctions.isValidConfigInt(MAX_NO_SOLUTIONS_TAG,
//				configNode.selectSingleNode(MAX_NO_SOLUTIONS_TAG).getText());
//		if (MAX_NO_SOLUTIONS == null) {
//			return false;
//		}
//
//		PILEPINE = StaticFunctions.isValidConfigBoolean(PILEPINE_TAG, configNode.selectSingleNode(PILEPINE_TAG).getText());
//		if (PILEPINE == null) {
//			return false;
//		}
//		return true;
//	}
//
//	private static void initDOM4J() {
//		File inputFile = new File(CONFIGURATION_FILE);
//		SAXReader reader = new SAXReader();
//		try {
//			document = reader.read(inputFile);
//
//			configNode = document.selectSingleNode("/configuration");
//
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//
//	}

	public static void main(String[] args) {

		config = APEConfig.getConfig();

//		setupConfig();

		/*
		 * Check whether the config file is supposed to be updated or the default values
		 * should be used.
		 */
		if (args.length == 0) {
			if (!config.defaultConfigSetup()) {
				System.out.println("Please validate the syntax of the configuration file: \"" + CONFIGURATION_FILE
						+ "\" in order to be able to run the library correctly.");
				return;
			}
		} else if (args.length == 1) {
			String newConfigFile = args[0];
			if (StaticFunctions.isValidReadFile(newConfigFile)) {
//				TODO: IMPLEMENT RECONFIG METHOD.
//				customConfigSetup(newConfigFile);
			} else {
				System.out.println("Invalid number of arguments.");
				return;
			}
		}

		String cnf = "";
		AtomMapping mappings = new AtomMapping();
		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
		TypeAutomaton typeAutomaton = new TypeAutomaton();

		/*
		 * Provides mapping from each atom to a number, and vice versa
		 */
		mappings = new AtomMapping();

		/*
		 * generate the automaton in CNF
		 */
		StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, config.getSOLUTIION_MIN_LENGTH(),
				config.getMAX_NO_TOOL_OUTPUTS());

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
//		Type emptyType = Type.generateType("empty", "empty", true, allTypes);
		rootType.addSubType(allTypes.getEmptyType().getTypeID());

		/*
		 * create constraints from the module.csv file and update allModules and
		 * allTypes sets
		 */
		AllModules annotated_modules = new AllModules(
				StaticFunctions.readModuleXML(config.getTOOL_ANNOTATIONS_PATH(), allModules, allTypes));
		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, config.getPILEPINE(),
				allTypes.getEmptyType(), mappings);

		for (Entry<String, AbstractModule> mapModule : annotated_modules.getModules().entrySet()) {
			System.out.println(mapModule.getValue().print());
		}

		/*
		 * printing the Module and Taxonomy Tree
		 */
		allModules.get("ModulesTaxonomy").printTree(" ", allModules);
		allTypes.get("TypesTaxonomy").printTree(" ", allTypes);

		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the tools 2.
		 * Mandatory usage of the tools - from taxonomy. 3. Adding the constraints
		 * enforcing the taxonomy structure.
		 */
		cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
		cnf += allModules.moduleMandatoryUsage(rootModule, moduleAutomaton, mappings);
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
		 * Define set of all constraint formats
		 */
		AllConstraintTamplates allConsTemplates = new AllConstraintTamplates();
//		System.out.println(StaticFunctions.initializeConstraints(allConsTemplates));
		StaticFunctions.initializeConstraints(allConsTemplates);
		/*
		 * encode the constraints from the paper manually
		 */
		cnf += StaticFunctions.generateSLTLConstraints(config.getCONSTRAINTS_PATH(), allConsTemplates, allModules,
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
			temp_sat_input = File.createTempFile("sat_input-", ".cnf");
//			temp_sat_input.deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Fixing the input and output files for easier testing.
		 */

		StaticFunctions.write2file(sat_input_header + cnf, temp_sat_input, false);

		long realStartTime = System.currentTimeMillis();
		List<SAT_solution> allSolutions = StaticFunctions.solve(temp_sat_input.getAbsolutePath(), mappings, allModules,
				allTypes, config.getMAX_NO_SOLUTIONS());
		long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
		System.out.println("\nAPE found " + allSolutions.size() + " solutions. Total solving time: "
				+ (realTimeElapsedMillis / 1000F) + " sec.");

		/*
		 * Writing solutions to the specified file in human readable format
		 */
		boolean first = false;
		if (allSolutions.isEmpty()) {
			System.out.println("UNSAT");
			return;
		}
		for (SAT_solution sol : allSolutions) {
			StaticFunctions.write2file(sol.getRelevantSolution() + "\n", new File(config.getSOLUTION_PATH()), first);
			first = true;
		}

		StaticFunctions.write2file(allSolutions.get(0).getMappedSolution() + "\n",
				new File("/home/vedran/Desktop/sat_solutions_map.txt"), false);

		StaticFunctions.write2file(allSolutions.get(0).getSolution() + "\n",
				new File("/home/vedran/Desktop/sat_solutions_full.txt"), false);

		/*
		 * TODO: use tool multiple times, consider removing permutations, SWL output?
		 */

	}

}
