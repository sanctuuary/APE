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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.constraints.AllConstraintTamplates;
import SAT.models.*;

public class main {

	/*
	 * Max number of solution that the solver will return.
	 */
	private static final int MAX_NO_SOLUTIONS = 10000;

	/**
	 * Path to the taxonomy file
	 */
	private static String ONTOLOGY_PATH = "/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/GMT_UseCase_taxonomy.owl";

	private static String TOOL_ANNOTATIONS_PATH = "/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/modules.csv";
	/**
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	private static String SOLUTION_PATH = "/home/vedran/Desktop/sat_solutions.txt";
	private static String SOLUTION_PATH2 = "/home/vedran/Desktop/sat_solutionsFull.txt";

	/**
	 * Path to the file with all workflow constraints.
	 */
	private static String CONSTRAINTS_PATH = "/home/vedran/Desktop/constraints.csv"
;	
	/**
	 * Length of the automaton (length of the solutions).
	 */
	private static int SOLUTIION_LENGTH = 7;
	/**
	 * Output branching factor (max number of outputs per tool).
	 */
	private static int MAX_NO_TOOL_OUTPUTS = 3;
	/**
	 * {@code true} if THE pipeline approach should be used, {@code false} in case of general memory
	 * approach.
	 */
	private static boolean PILEPINE = false;

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
		StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, SOLUTIION_LENGTH, MAX_NO_TOOL_OUTPUTS);
		
		/*
		 * encode the taxonomies as objects - generate the list of all types / modules occurring in the
		 * taxonomies defining their submodules/subtypes
		 */

		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();

		OWLReader owlReader = new OWLReader(ONTOLOGY_PATH, allModules, allTypes);
		try {
			Boolean ontologyFormat = owlReader.readOntology();		//true if ontology was well-formatted
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
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
		 * create constraints from the module.csv file and update allModules and allTypes sets
		 */
		AllModules annotated_modules = new AllModules(StaticFunctions
				.readModuleCSV(TOOL_ANNOTATIONS_PATH, allModules, allTypes));
		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, PILEPINE, emptyType, mappings);

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
		 * Define set of all constraint formats
		 */
		AllConstraintTamplates allConsTemplates = new AllConstraintTamplates();
		System.out.println(StaticFunctions.initializeConstraints(allConsTemplates ));
		/*
		 * encode the constraints from the paper manually
		 */
		cnf += StaticFunctions.generateSLTLConstraints(CONSTRAINTS_PATH, allConsTemplates, allModules, allTypes, mappings, moduleAutomaton, typeAutomaton);

		/*
		 * Counting the number of variables and clauses that will be given to the SAT solver
		 * TODO Improve this approach, no need to read the whole String again.
		 */
		int variables = mappings.getSize();
		int clauses = StringUtils.countMatches(cnf, " 0");
		String sat_input_header = "p cnf " + variables + " " + clauses + "\n";

		/*
		 * Create a temp file that will be used as input for the SAT solver.
		 */
		File temp_sat_input = File.createTempFile("sat_input-", ".cnf");
		temp_sat_input.deleteOnExit();
		
		
		/*
		 * Fixing the input and output files for easier testing. 
		 */
		
		StaticFunctions.write2file(sat_input_header + cnf, temp_sat_input, false);

		long realStartTime = System.currentTimeMillis();
		List<SAT_solution> allSolutions = StaticFunctions.solve(temp_sat_input.getAbsolutePath(),  mappings, allModules, allTypes, MAX_NO_SOLUTIONS);
		long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
		System.out.println("\nAPE found " + allSolutions.size() + " solutions. Total solving time: " + (realTimeElapsedMillis / 1000F) + " sec.");
		
		/*
		 * Writing solutions to the specified file in human readable format
		 */
		boolean first = false;
//		for (SAT_solution sol : allSolutions) {
//			StaticFunctions.write2file(sol.getSolution() + "\n\n", new File(SOLUTION_PATH2), first);
//			first = true;
//		}
		first = false;
		for (SAT_solution sol : allSolutions) {
			StaticFunctions.write2file(sol.getRelevantSolution() + "\n", new File(SOLUTION_PATH), first);
			first = true;
		}

		/*
		 * TODO: use tool multiple times, consider removing permutations, SWL output?
		 */

	}

}
