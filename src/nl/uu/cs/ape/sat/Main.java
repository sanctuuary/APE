package nl.uu.cs.ape.sat;

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
		rootType.addSubType(allTypes.getEmptyType().getTypeID());

		/*
		 * create constraints from the module.csv file and update allModules and
		 * allTypes sets
		 */
		AllModules annotated_modules = new AllModules(
				StaticFunctions.readModuleXML(config.getTOOL_ANNOTATIONS_PATH(), allModules, allTypes));
		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, config.getPILEPINE(),
				allTypes.getEmptyType(), mappings);

//		print all the tools
//		for (Entry<String, AbstractModule> mapModule : annotated_modules.getModules().entrySet()) {
//			System.out.println(mapModule.getValue().print());
//		}

		/*
		 * printing the Module and Taxonomy Tree
		 */
		allModules.getRootModule().printTree(" ", allModules);
//		allTypes.getRootType().printTree(" ", allTypes);

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
		for (int i = 0; i < allSolutions.size(); i++) {
			StaticFunctions.write2file(allSolutions.get(i).getRelevantSolution() + "\n", new File(config.getSOLUTION_PATH()), first);
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
		Integer noExecutions = APEConfig.getConfig().getNO_EXECUTIONS();
		if (noExecutions != null && noExecutions > 0) {
			for (int i = 0; i < 5 && i < allSolutions.size(); i++) {
				
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/vedran/Desktop/APE/workflow "+i+".sh", false)));
				out.println("");
				out.close();
				
				SAT_solution currSol = allSolutions.get(i);
				currSol.getRelevantSolutionModules(allModules);
				for(Module curr: currSol.getRelevantSolutionModules(allModules)) {
					if(curr.getModuleExecution() != null)
							curr.getModuleExecution().run("/home/vedran/Desktop/APE/workflow "+i+".sh");
				}
			}
		}
		System.out.println(".sh files generated.");
		/*
		 * TODO: use tool multiple times, consider removing permutations, SWL output?
		 */

	}

}
