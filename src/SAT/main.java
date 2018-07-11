package SAT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.models.*;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDException;
import de.jabc.plugin.ontEDAPI.Exceptions.OntEDMissingImportException;

public class main {

	private static AtomMapping mappings;

	/**
	 * Updates the list of All Modules by annotating the existing ones (or adding
	 * non-existing) using the I/O Types from the @file. Returns the list of Updated
	 * Modules.
	 * 
	 * @param file
	 *            - path to the .CSV file containing tool annotations
	 * @param allModules
	 *            - list of all existing modules
	 * @param allTypes
	 *            - list of all existing types
	 * @return the list of all annotated Modules in the process (possibly empty
	 *         list)
	 */
	public static List<Module> readCSV(String file, AllModules allModules, AllTypes allTypes) {

		List<Module> modulesNew = new ArrayList<Module>();

		String line = "";
		String cvsSplitBy = ",";
		BufferedReader csvReader;

		try {
			csvReader = new BufferedReader(new FileReader(file));
			/*
			 * skip the firts line
			 */
			csvReader.readLine();
			while ((line = csvReader.readLine()) != null) {
				String[] stringModule = line.split(cvsSplitBy, -1);
				modulesNew.add(Module.moduleFromString(stringModule, allModules, allTypes));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return modulesNew;
	}

	/**
	 * Generating the mutual exclusion for each pair of types in each state of
	 * typeAutomaton.
	 * 
	 * @param types
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	public static String typeMutualExclusion(AllTypes allTypes, TypeAutomaton typeAutomaton) {

		String constraints = "";

		return constraints;
	}

	public static void main(String[] args) {

		int automata_bound = 10;
		int branching = 2;
		boolean pipeline = false;
		String cnf = "";

		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
		TypeAutomaton typeAutomaton = new TypeAutomaton();

		/*
		 * TODO use the mapping for each atom to a number
		 */
		mappings = new AtomMapping();

		/*
		 * generate the automaton in CNF
		 */
		for (int i = 0; i < automata_bound; i++) {
			ModuleState tmpModuleState = new ModuleState("S" + i, i);
			if (i == 0) {
				tmpModuleState.setFirst();
			} else if (i == automata_bound - 1) {
				tmpModuleState.setLast();
			}
			moduleAutomaton.addState(tmpModuleState);

			TypeBlock tmpTypeBlock = new TypeBlock();
			for (int j = 0; j < branching; j++) {
				TypeState tmpTypeState = new TypeState("S" + i + "." + j, j);
				tmpTypeBlock.addState(tmpTypeState);
			}
			typeAutomaton.addBlock(tmpTypeBlock);
		}

		/*
		 * encode the taxonomies as objects - generate the list of all types / modules
		 */

		AllModules allModules = new AllModules();
		AllTypes allTypes = new AllTypes();

		String taxonomy = "file:/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/GMT_UseCase_taxonomy.owl";
		try {
			OWLExplorer.getObjectsFromTaxonomy(taxonomy, allModules, allTypes);
		} catch (OntEDException | IOException | OntEDMissingImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * create constraints for module.csv
		 */
		AllModules annotated_modules = new AllModules(
				readCSV("/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/modules.csv", allModules, allTypes));
		cnf += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, pipeline, mappings);

		/*
		 * printing the Module and Taxonomy Tree
		 */
		// allModules.get("ModulesTaxonomy").printTree(" ",allModules);
		// allTypes.get("TypesTaxonomy").printTree(" ", allTypes);

		/*
		 * create constraints on the mutual exclusion and mandatory usage of the tools - from taxonomy
		 */
		cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
		cnf += allModules.moduleMandatoryUsage("ModulesTaxonomy",moduleAutomaton, mappings);

		/*
		 * create constraints on the mutual exclusion of the types, mandatory usage of
		 * the types is not required (they can be empty)
		 */
		cnf += allTypes.typeMutualExclusion(typeAutomaton, mappings);
		
//		System.out.println(cnf);
		/*
		 * TODO encode the constraints from the paper manually
		 */
		List<SLTL_formula> constraints = new ArrayList<>();
		// use module/type in the synthesis
		AbstractModule draw_water = new AbstractModule("draw_water", "draw_water", true);
		SLTL_formula_F e0_1 = new SLTL_formula_F(draw_water);
		constraints.add(e0_1);
		 System.out.println(e0_1.getCNF(moduleAutomaton, typeAutomaton,mappings));

		// don't use module/type in the synthesis
		AbstractModule _3d_surfaces = new AbstractModule("3d_surfaces", "3d_surfaces", true);
		SLTL_formula_G g5 = new SLTL_formula_G(_3d_surfaces, true);
		constraints.add(g5);
		 System.out.println(g5.getCNF(moduleAutomaton, typeAutomaton, mappings));

		/*
		 * TODO if using module X use module Y subsequently
		 */
		AbstractModule modules_with_xyz_input = new AbstractModule("Modules_with_xyz_input", "Modules_with_xyz_input",
				false);
		AbstractModule modules_with_xyz_output = new AbstractModule("Modules_with_xyz_output",
				"Modules_with_xyz_output", false);
		// constraints.add(???);
		System.out.println(
				SLTL_formula.ite(modules_with_xyz_input, modules_with_xyz_output, moduleAutomaton, typeAutomaton,mappings));

		/*
		 *  TODO use the mapping to encode and implement SAT
		 */

	}

}
