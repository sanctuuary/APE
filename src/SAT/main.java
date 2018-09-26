package SAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.models.*;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDException;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDMissingImportException;
import java_cup.sym;

public class main {

	/*
	 * Max number of solution that the solver will return.
	 */
	private static final int no_of_solutions = 10000;

	/**
	 * Path to the taxonomy file
	 */
	private static String taxonomy = "file:/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/GMT_UseCase_taxonomy.owl";

	/**
	 * Path to the folder where all the files used in SAT solving will be created.
	 */
	private static String domainPath = "/home/vedran/Desktop/";
	/**
	 * Path to the CNF definition file, that will be used as an input for the SAT
	 * solver.
	 */
//	private static String sat_input = domainPath + "sat_input.txt";
	/**
	 * Path to the file that will contain the SAT solution, that will be used as an
	 * output for the SAT solver.
	 */
//	private static String sat_output = domainPath + "sat_output.txt";
	/**
	 * Path to the file that will contain 1 temporary the solution to the problem in
	 * human readable representation.
	 */
	private static String sat_solution = domainPath + "sat_solution.txt";
	/**
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	private static String sat_solutions = domainPath + "sat_solutions.txt";

//	private static String miniSat = "/home/vedran/Documents/minisat/core/minisat";

	/**
	 * Length of the automaton.
	 */
	private static int automata_bound = 7;
	/**
	 * Output branching factor (max number of outputs per tool).
	 */
	private static int branching = 3;
	/**
	 * True if pipeline approach should be used, false in case of general memory
	 * approach.
	 */
	private static boolean pipeline = false;

	public static void main(String[] args) throws IOException {

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
		StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, automata_bound, branching);
		
		/*
		 * encode the taxonomies as objects - generate the list of all types / modules
		 */

		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();

		try {
			OWLExplorer.getObjectsFromTaxonomy(taxonomy, allModules, allTypes);
		} catch (OntEDException | IOException | OntEDMissingImportException e) {
			e.printStackTrace();
		}

		AbstractModule rootModule = allModules.get("ModulesTaxonomy");
		Type rootType = allTypes.get("TypesTaxonomy");

		/*
		 * Define the empty type, representing the absence of types
		 */

		Type emptyType = Type.generateType("empty", "empty", true, allTypes);
		rootType.addSubType(emptyType.getTypeID());

		/*
		 * create constraints from the module.csv file
		 */
		AllModules annotated_modules = new AllModules(StaticFunctions
				.readCSV("/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/modules.csv", allModules, allTypes));
		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, pipeline, emptyType, mappings);

		/*
		 * printing the Module and Taxonomy Tree
		 */
		allModules.get("ModulesTaxonomy").printTree(" ", allModules);
		allTypes.get("TypesTaxonomy").printTree(" ", allTypes);

		/*
		 * create constraints on the mutual exclusion and mandatory usage of the tools -
		 * from taxonomy. Adding the constraints about the taxonomy structure.
		 */

		cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
		cnf += allModules.moduleMandatoryUsage(rootModule.getModuleID(), moduleAutomaton, mappings);
		cnf += allModules.moduleEnforceTaxonomyStructure(rootModule.getModuleID(), moduleAutomaton, mappings);
		/*
		 * create constraints on the mutual exclusion of the types, mandatory usage of
		 * the types is not required (they can be empty)
		 */

		cnf += allTypes.typeMutualExclusion(typeAutomaton, mappings);
		cnf += allTypes.typeMandatoryUsage(rootType.getTypeID(), typeAutomaton, mappings);
		cnf += allTypes.typeEnforceTaxonomyStructure(rootType.getTypeID(), emptyType.getTypeID(), typeAutomaton,
				mappings);

		/*
		 * encode the constraints from the paper manually
		 */
		cnf += StaticFunctions.generateSLTLConstraints(allModules, allTypes, mappings, moduleAutomaton, typeAutomaton);

		/*
		 * Counting the number of variables and clauses that will be given to the SAT solver
		 * TODO Improve this approach, no need to read the whole String again.
		 */
		int variables = mappings.getSize();
		int clauses = StringUtils.countMatches(cnf, " 0");
		String sat_input_header = "p cnf " + variables + " " + clauses + "\n";

		/*
		 * Create a temp files that will be used as input and output files for the SAT solver.
		 */
//		File temp_sat_input = File.createTempFile("sat_input-", ".cnf");
//		File temp_sat_output = File.createTempFile("sat_output-", ".cnf");
		/*
		 * Delete both files once the synthesis is over
		 */
//		temp_sat_input.deleteOnExit();
//		temp_sat_output.deleteOnExit();
		
		/*
		 * Fixing the input and output files for easier testing. 
		 */
		File temp_sat_input = new File("/home/vedran/Desktop/sat_input.cnf");
		
		StaticFunctions.write2file(sat_input_header + cnf, temp_sat_input, false);

		long realStartTime = System.currentTimeMillis();
		List<SAT_solution> allSolutions = StaticFunctions.solve(temp_sat_input.getAbsolutePath(),  mappings, allModules, allTypes, no_of_solutions);
		long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
		System.out.println("\nAPE found " + allSolutions.size() + " solutions. Total solving time: " + (realTimeElapsedMillis / 1000F) + " sec.");
		
		/*
		 * Getting the solution from the solver and finding the rest of the solutions (by negating the obtained solution and adding it as an additional clause for the SAT solver)
		 */
		



		boolean first = false;
		for (SAT_solution sol : allSolutions) {
			StaticFunctions.write2file(sol.getRelevantSolution(), new File(sat_solutions), first);
			first = true;
		}

		/*
		 * TODO: consider removing permutations, SWL output?
		 */

		// System.out.println(cnf);
	}

}
