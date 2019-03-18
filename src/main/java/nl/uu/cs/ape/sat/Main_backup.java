package nl.uu.cs.ape.sat;

//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import org.apache.commons.configuration2.XMLConfiguration;
//import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
//import org.apache.commons.configuration2.builder.fluent.Configurations;
//import org.apache.commons.configuration2.ex.ConfigurationException;
//import org.apache.commons.lang3.StringUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.dom4j.Node;
//import org.dom4j.io.SAXReader;
//import org.xml.sax.SAXException;
//
//import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
//import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
//import nl.uu.cs.ape.sat.constraints.AllConstraintTamplates;
//import nl.uu.cs.ape.sat.models.AbstractModule;
//import nl.uu.cs.ape.sat.models.AllModules;
//import nl.uu.cs.ape.sat.models.AllTypes;
//import nl.uu.cs.ape.sat.models.AtomMapping;
//import nl.uu.cs.ape.sat.models.SAT_solution;
//import nl.uu.cs.ape.sat.models.Type;
//
//import javax.xml.parsers.*;
//import java.io.*;

public class Main_backup {

//	/**
//	 * Tags used in the config file
//	 */
//	private static final String CONFIGURATION_FILE = "ape.configuration";
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
//	private static FileBasedConfigurationBuilder<XMLConfiguration> builder;
//	private static XMLConfiguration config;
//	
//	
//	private static Document document;
//	private static Node configNode;
//
//	/**
//	 * Setting-up the configuration file.
//	 */
//	private static void setupConfig() {
//		try {
//			Configurations configs = new Configurations();
//			builder = configs.xmlBuilder(CONFIGURATION_FILE);
//			config = builder.getConfiguration();
//		} catch (ConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
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
//		System.out.println("####");
//		System.out.println(ONTOLOGY_PATH);
//		TOOL_ANNOTATIONS_PATH = configNode.selectSingleNode(TOOL_ANNOTATIONS_TAG).getText();
//		if (!StaticFunctions.isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, TOOL_ANNOTATIONS_PATH)) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(TOOL_ANNOTATIONS_PATH);
//		CONSTRAINTS_PATH = configNode.selectSingleNode(CONSTRAINTS_TAG).getText();
//		if (!StaticFunctions.isValidConfigReadFile(CONSTRAINTS_TAG, CONSTRAINTS_PATH)) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(CONSTRAINTS_PATH);
//		SOLUTION_PATH = configNode.selectSingleNode(SOLUTION_TAG).getText();
//		if (!StaticFunctions.isValidConfigWriteFile(SOLUTION_TAG, SOLUTION_PATH)) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(SOLUTION_PATH);
//		SOLUTIION_MIN_LENGTH = StaticFunctions.isValidConfigInt(SOLUTIION_MIN_LENGTH_TAG,
//				configNode.selectSingleNode(SOLUTIION_MIN_LENGTH_TAG).getText());
//		if (SOLUTIION_MIN_LENGTH == null) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(SOLUTIION_MIN_LENGTH);
//		MAX_NO_SOLUTIONS = StaticFunctions.isValidConfigInt(MAX_NO_SOLUTIONS_TAG,
//				configNode.selectSingleNode(MAX_NO_SOLUTIONS_TAG).getText());
//		if (MAX_NO_SOLUTIONS == null) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(MAX_NO_SOLUTIONS);
//		PILEPINE = StaticFunctions.isValidConfigBoolean(PILEPINE_TAG, configNode.selectSingleNode(PILEPINE_TAG).getText());
//		if (PILEPINE == null) {
//			return false;
//		}
//		System.out.println("####");
//		System.out.println(PILEPINE);
//		return true;
//	}
//
//	/**
//	 * Setting up the configuration of the library.
//	 * 
//	 * @return {@code true} if the method successfully set-up the configuration,
//	 *         {@code false} otherwise.
//	 */
//	private static boolean customConfigSetup(String newConfigFile) {
//		try {
//			Configurations configs = new Configurations();
//			FileBasedConfigurationBuilder<XMLConfiguration> newBuilder = configs.xmlBuilder(newConfigFile);
//			XMLConfiguration newConfig = newBuilder.getConfiguration();
//
//			ONTOLOGY_PATH = newConfig.getString(ONTOLOGY_TAG);
//			if (!StaticFunctions.isValidConfigReadFile(ONTOLOGY_TAG, ONTOLOGY_PATH)) {
//				return false;
//			} else {
//				config.setProperty(ONTOLOGY_TAG, ONTOLOGY_PATH);
//			}
//
//			TOOL_ANNOTATIONS_PATH = newConfig.getString(TOOL_ANNOTATIONS_TAG);
//			if (!StaticFunctions.isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, TOOL_ANNOTATIONS_PATH)) {
//				return false;
//			} else {
//				config.setProperty(TOOL_ANNOTATIONS_TAG, TOOL_ANNOTATIONS_PATH);
//			}
//
//			CONSTRAINTS_PATH = newConfig.getString(CONSTRAINTS_TAG);
//			if (!StaticFunctions.isValidConfigReadFile(CONSTRAINTS_TAG, CONSTRAINTS_PATH)) {
//				return false;
//			} else {
//				config.setProperty(CONSTRAINTS_TAG, CONSTRAINTS_PATH);
//			}
//
//			SOLUTION_PATH = newConfig.getString(SOLUTION_TAG);
//			if (!StaticFunctions.isValidConfigWriteFile(SOLUTION_TAG, SOLUTION_PATH)) {
//				return false;
//			} else {
//				config.setProperty(SOLUTION_TAG, SOLUTION_PATH);
//			}
//
//			SOLUTIION_MIN_LENGTH = StaticFunctions.isValidConfigInt(SOLUTIION_MIN_LENGTH_TAG,
//					newConfig.getString(SOLUTIION_MIN_LENGTH_TAG));
//			if (SOLUTIION_MIN_LENGTH == null) {
//				return false;
//			} else {
//				config.setProperty(SOLUTIION_MIN_LENGTH_TAG, SOLUTIION_MIN_LENGTH);
//			}
//
//			MAX_NO_SOLUTIONS = StaticFunctions.isValidConfigInt(MAX_NO_SOLUTIONS_TAG,
//					newConfig.getString(MAX_NO_SOLUTIONS_TAG));
//			if (MAX_NO_SOLUTIONS == null) {
//				return false;
//			} else {
//				config.setProperty(MAX_NO_SOLUTIONS_TAG, MAX_NO_SOLUTIONS);
//			}
//
//			PILEPINE = StaticFunctions.isValidConfigBoolean(PILEPINE_TAG, newConfig.getString(PILEPINE_TAG));
//			if (PILEPINE == null) {
//				return false;
//			} else {
//				config.setProperty(PILEPINE_TAG, PILEPINE);
//			}
//
//			return saveConfigSetup();
//
//		} catch (ConfigurationException e1) {
//			System.err.println("Configuration file could not be loaded.");
//			return false;
//		}
//	}
//
//	/**
//	 * Save the new configuration setup.
//	 * 
//	 * @return {@code true} if the method successfully saved the configuration,
//	 *         {@code false} otherwise.
//	 */
//	private static boolean saveConfigSetup() {
//		try {
//			builder.save();
//		} catch (ConfigurationException e) {
//			System.err.println("Configuration file could not be saved.");
//			return false;
//		}
//
//		return true;
//	}
//
//	private static void initDOM4J() {
//		File inputFile = new File(CONFIGURATION_FILE);
//		SAXReader reader = new SAXReader();
//		try {
//			document = reader.read(inputFile);
//			System.out.println("Root element :" + document.getRootElement().getName());
//
//			Element classElement = document.getRootElement();
//			configNode = document.selectSingleNode("/configuration");
//			System.out.println("----------------------------");
//
//			System.out.println("\nCurrent Element :" + configNode.getName());
////	             System.out.println("Student roll no : " + node.valueOf("@rollno") );
//			System.out.println("Ontology : " + configNode.selectSingleNode("ontology_path").getText());
//
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void main(String[] args) {
//
//		initDOM4J();
//
////		setupConfig();
//
//		/*
//		 * Check whether the config file is supposed to be updated or the default values should be used. 
//		 */
//		if (args.length == 0) {
//			if (!defaultConfigSetup()) {
//				System.out.println("Please validate the syntax of the configuration file: \"" + CONFIGURATION_FILE
//						+ "\" in order to be able to run the library correctly.");
//				return;
//			}
//		} else if (args.length == 1) {
//			String newConfigFile = args[0];
//			if (StaticFunctions.isValidReadFile(newConfigFile)) {
//				customConfigSetup(newConfigFile);
//			} else {
//				System.out.println("Invalid number of arguments.");
//				return;
//			}
//		}
//
//		String cnf = "";
//		AtomMapping mappings = new AtomMapping();
//		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
//		TypeAutomaton typeAutomaton = new TypeAutomaton();
//
//		/*
//		 * Provides mapping from each atom to a number, and vice versa
//		 */
//		mappings = new AtomMapping();
//
//		/*
//		 * generate the automaton in CNF
//		 */
//		StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, SOLUTIION_MIN_LENGTH, MAX_NO_TOOL_OUTPUTS);
//
//		/*
//		 * encode the taxonomies as objects - generate the list of all types / modules
//		 * occurring in the taxonomies defining their submodules/subtypes
//		 */
//
//		AllModules allModules = new AllModules();
//		AllTypes allTypes = new AllTypes();
//
//		OWLReader owlReader = new OWLReader(ONTOLOGY_PATH, allModules, allTypes);
//		Boolean ontologyRead = owlReader.readOntology(); // true if ontology was well-formatted
//
//		if (ontologyRead == false) {
//			System.out.println("Error");
//			return;
//		}
//		AbstractModule rootModule = allModules.get("ModulesTaxonomy");
//		Type rootType = allTypes.get("TypesTaxonomy");
//
//		/*
//		 * Define the empty type, representing the absence of types
//		 */
//		Type emptyType = Type.generateType("empty", "empty", true, allTypes);
//		rootType.addSubType(emptyType.getTypeID());
//
//		/*
//		 * create constraints from the module.csv file and update allModules and
//		 * allTypes sets
//		 */
//		AllModules annotated_modules = new AllModules(
//				StaticFunctions.readModuleCSV(TOOL_ANNOTATIONS_PATH, allModules, allTypes));
//		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, PILEPINE, emptyType, mappings);
//
//		/*
//		 * printing the Module and Taxonomy Tree
//		 */
////		allModules.get("ModulesTaxonomy").printTree(" ", allModules);
////		allTypes.get("TypesTaxonomy").printTree(" ", allTypes);
//
//		/*
//		 * create constraints on the mutual exclusion and mandatory usage of the tools -
//		 * from taxonomy. Adding the constraints about the taxonomy structure.
//		 */
//
//		cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
//		cnf += allModules.moduleMandatoryUsage(rootModule.getModuleID(), moduleAutomaton, mappings);
//		cnf += allModules.moduleEnforceTaxonomyStructure(rootModule.getModuleID(), moduleAutomaton, mappings);
//		/*
//		 * create constraints on the mutual exclusion of the types, mandatory usage of
//		 * the types is not required (they can be empty)
//		 */
//
//		cnf += allTypes.typeMutualExclusion(typeAutomaton, mappings);
//		cnf += allTypes.typeMandatoryUsage(rootType.getTypeID(), typeAutomaton, mappings);
//		cnf += allTypes.typeEnforceTaxonomyStructure(rootType.getTypeID(), emptyType.getTypeID(), typeAutomaton,
//				mappings);
//
//		/*
//		 * Define set of all constraint formats
//		 */
//		AllConstraintTamplates allConsTemplates = new AllConstraintTamplates();
////		System.out.println(StaticFunctions.initializeConstraints(allConsTemplates));
//		StaticFunctions.initializeConstraints(allConsTemplates);
//		/*
//		 * encode the constraints from the paper manually
//		 */
//		cnf += StaticFunctions.generateSLTLConstraints(CONSTRAINTS_PATH, allConsTemplates, allModules, allTypes,
//				mappings, moduleAutomaton, typeAutomaton);
//
//		/*
//		 * Counting the number of variables and clauses that will be given to the SAT
//		 * solver TODO Improve this approach, no need to read the whole String again.
//		 */
//		int variables = mappings.getSize();
//		int clauses = StringUtils.countMatches(cnf, " 0");
//		String sat_input_header = "p cnf " + variables + " " + clauses + "\n";
//
//		/*
//		 * Create a temp file that will be used as input for the SAT solver.
//		 */
//		File temp_sat_input = null;
//		try {
//			temp_sat_input = File.createTempFile("sat_input-", ".cnf");
//			temp_sat_input.deleteOnExit();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		/*
//		 * Fixing the input and output files for easier testing.
//		 */
//
//		StaticFunctions.write2file(sat_input_header + cnf, temp_sat_input, false);
//
//		long realStartTime = System.currentTimeMillis();
//		List<SAT_solution> allSolutions = StaticFunctions.solve(temp_sat_input.getAbsolutePath(), mappings, allModules,
//				allTypes, MAX_NO_SOLUTIONS);
//		long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
//		System.out.println("\nAPE found " + allSolutions.size() + " solutions. Total solving time: "
//				+ (realTimeElapsedMillis / 1000F) + " sec.");
//
//		/*
//		 * Writing solutions to the specified file in human readable format
//		 */
//		boolean first = false;
//		for (SAT_solution sol : allSolutions) {
//			StaticFunctions.write2file(sol.getRelevantSolution() + "\n", new File(SOLUTION_PATH), first);
//			first = true;
//		}
//
//		/*
//		 * TODO: use tool multiple times, consider removing permutations, SWL output?
//		 */
//
//	}

}
