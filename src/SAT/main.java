package SAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
	private static String sat_input = domainPath + "sat_input.txt";
	/**
	 * Path to the file that will contain the SAT solution, that will be used as an
	 * output foe the SAT solver.
	 */
	private static String sat_output = domainPath + "sat_output.txt";
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

	private static String miniSat = "/home/vedran/Documents/minisat/core/minisat";

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

	/**
	 * Generate the State automatons (Module and Type) based on the defined length
	 * and branching factor.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 */
	public static void generateAutomaton(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		for (int i = 0; i < automata_bound; i++) {
			String i_var;
			if (automata_bound > 10 && i < 10) {
				i_var = "0" + i;
			} else {
				i_var = "" + i;
			}
			ModuleState tmpModuleState = new ModuleState("M" + i_var, i);
			if (i == 0) {
				tmpModuleState.setFirst();
			} else if (i == automata_bound - 1) {
				tmpModuleState.setLast();
			}
			moduleAutomaton.addState(tmpModuleState);

			TypeBlock tmpTypeBlock = new TypeBlock(i);
			for (int j = 0; j < branching; j++) {
				TypeState tmpTypeState = new TypeState("T" + i_var + "." + j, j);
				tmpTypeBlock.addState(tmpTypeState);
			}
			typeAutomaton.addBlock(tmpTypeBlock);
		}
	}

	public static void main(String[] args) {

		String cnf = "";
		AtomMapping mappings = new AtomMapping();
		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
		TypeAutomaton typeAutomaton = new TypeAutomaton();

		/*
		 * TODO use the mapping for each atom to a number
		 */
		mappings = new AtomMapping();

		/*
		 * generate the automaton in CNF
		 */
		generateAutomaton(moduleAutomaton, typeAutomaton);
		/*
		 * encode the taxonomies as objects - generate the list of all types / modules
		 */

		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();

		try {
			OWLExplorer.getObjectsFromTaxonomy(taxonomy, allModules, allTypes);
		} catch (OntEDException | IOException | OntEDMissingImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AbstractModule rootModule = allModules.get("ModulesTaxonomy");
		Type rootType = allTypes.get("TypesTaxonomy");

		/*
		 * add empty type
		 */

		Type emptyType = Type.generateType("empty", "empty", true, allTypes);
		rootType.addSubType(emptyType.getTypeID());

		/*
		 * create constraints for module.csv
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

		int variables = mappings.getSize();
		int clauses = StringUtils.countMatches(cnf, " 0");
		String description = "p cnf " + variables + " " + clauses + "\n";

		StaticFunctions.write2file(description + cnf, sat_input, false);

		long elapsedTimeMillis = StaticFunctions.solve(miniSat, sat_input, sat_output);

		// get solution

		List<SAT_solution> allSolutions = new ArrayList<>();
		long realStartTime = System.currentTimeMillis();
		long realTimeElapsedMillis;
		SAT_solution solution = StaticFunctions.getSATsolution(sat_output, mappings, allModules, allTypes);
		int counter = 0;
		do {
			allSolutions.add(solution);
			StaticFunctions.write2file(
					"\n" + solution.getNegatedMappedSolution(), sat_input,
					true);
			elapsedTimeMillis += StaticFunctions.solve(miniSat, sat_input, sat_output);
			solution = StaticFunctions.getSATsolution(sat_output, mappings, allModules, allTypes);
			counter++;
			if(counter%500==0) {
				realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
				System.out.println("Found " + counter + " solutions. Solving time: " + (elapsedTimeMillis / 1000F) + " sec. Real time: " + (realTimeElapsedMillis / 1000F));
				System.gc();
			}
		} while (solution.isSat());

		System.out.println("Total solving time: " + elapsedTimeMillis / 1000F + " sec");

		// String cnf_file_translated = "/home/vedran/Desktop/cnf-translated.txt";
		//
		// try (Writer writer = new BufferedWriter(
		// new OutputStreamWriter(new FileOutputStream(cnf_file_translated), "utf-8")))
		// {
		// writer.write(readSATdefinition(cnf_file, mappings));
		// writer.close();
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		boolean first = false;
		for (SAT_solution sol : allSolutions) {
			StaticFunctions.write2file(sol.getRelevantSolution(), sat_solutions, first);
			first = true;
		}

		/*
		 * TODO: -permutacije -SWL output?
		 */

		// System.out.println(cnf);
	}

}
