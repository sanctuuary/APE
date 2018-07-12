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

import SAT.automaton.AtomMapping;
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
	 * Returns the CNF representation of the SLTL constraints in our project
	 * 
	 * @param allModules
	 *            - list of all modukles
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return A string representing SLTL constraints into CNF
	 */
	private static String generateSLTLConstraints(AllModules allModules, AllTypes allTypes,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		List<SLTL_formula> constraints = new ArrayList<>();
		/*
		 * Constraint G1: If Modules_with_xyz_file_input then use
		 * Modules_with_xyz_file_output
		 */

		AbstractModule modules_with_xyz_input = allModules.get("Modules_with_xyz_file_input");
		AbstractModule modules_with_xyz_output = allModules.get("Modules_with_xyz_file_output");
		// constraints.add(???);
		cnf_SLTL += SLTL_formula.ite(modules_with_xyz_input, modules_with_xyz_output, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G2: If Modules_with_grid_file_input then use
		 * Modules_with_grid_file_output
		 */
		AbstractModule modules_with_grid_input = allModules.get("Modules_with_grid_file_input");
		AbstractModule modules_with_grid_output = allModules.get("Modules_with_grid_file_output");
		cnf_SLTL += SLTL_formula.ite(modules_with_grid_input, modules_with_grid_output, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G3: If Modules_with_color_palette_input then use
		 * Modules_with_color_palette_output
		 */
		AbstractModule modules_with_color_palette_input = allModules.get("Modules_with_color_palette_input");
		AbstractModule modules_with_color_palette_output = allModules.get("Modules_with_color_palette_output");
		cnf_SLTL += SLTL_formula.ite(modules_with_color_palette_input, modules_with_color_palette_output,
				moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G4: Do not use module 3D_surfaces
		 */

		AbstractModule _3d_surfaces = allModules.get("3D_surfaces");
		SLTL_formula_G g4 = new SLTL_formula_G(_3d_surfaces, true);
		constraints.add(g4);
		cnf_SLTL += g4.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G5: Use the data type Plots
		 */

		Type plots = allTypes.get("Plots");
		SLTL_formula_F g5 = new SLTL_formula_F(plots);
		constraints.add(g5);
		cnf_SLTL += g5.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.1: Use Draw_water in the synthesis
		 */
		AbstractModule draw_water = allModules.get("Draw_water");
		SLTL_formula_F e0_1 = new SLTL_formula_F(draw_water);
		constraints.add(e0_1);
		cnf_SLTL += e0_1.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.2: Use Draw_land in the synthesis
		 */
		AbstractModule draw_land = allModules.get("Draw_land");
		SLTL_formula_F e0_2 = new SLTL_formula_F(draw_land);
		constraints.add(e0_2);
		cnf_SLTL += e0_2.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.3: Use Draw_political_bourders in the synthesis
		 */
		AbstractModule draw_political_bourders = allModules.get("Draw_political_borders");
		SLTL_formula_F e0_3 = new SLTL_formula_F(draw_political_bourders);
		constraints.add(e0_3);
		cnf_SLTL += e0_3.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.4: Use Display_PostScript as last module in the solution
		 */
		AbstractModule display_PostScript = allModules.get("Display_PostScript_files");
		cnf_SLTL += SLTL_formula.useAsLastModule(display_PostScript, moduleAutomaton, typeAutomaton, mappings);

		return cnf_SLTL;
	}

	public static String readSATsolution(String file, AtomMapping mappings) {

		String line = "";
		String cvsSplitBy = " ";
		BufferedReader csvReader;
		String solution = "";

		try {
			csvReader = new BufferedReader(new FileReader(file));
			String sat = csvReader.readLine();
			/*
			 * check whether it is SAT or UNSAT
			 */
			if (!sat.matches("UNSAT")) {

				while ((line = csvReader.readLine()) != null) {
					String[] terms = line.split(cvsSplitBy, -1);
					for (String term : terms) {
						if (term.startsWith("-")) {
							if (!sat.matches("SAT")) {
								solution += "-";
								solution += mappings.findOriginal(Integer.parseInt(term.substring(1))) + " ";
							}
						} else if (!term.matches("0")) {
							solution += mappings.findOriginal(Integer.parseInt(term)) + " ";
							if (sat.matches("SAT")) {
								solution += "\n";
							}
						} else {
							solution += "\n";
						}

					}
				}
			} else {
				solution = "The problem is Unsatisfiable";
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return solution;
	}

	public static void main(String[] args) {

		int automata_bound = 5;
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
		// allModules.get("ModulesTaxonomy").printTree(" ", allModules);
		// allTypes.get("TypesTaxonomy").printTree(" ", allTypes);

		/*
		 * create constraints on the mutual exclusion and mandatory usage of the tools -
		 * from taxonomy. Adding the constraints about the taxonomy structure.
		 */
		
		
		cnf += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
		cnf += allModules.moduleMandatoryUsage("ModulesTaxonomy", moduleAutomaton, mappings);
		cnf += allModules.moduleEnforceTaxonomyStructure("ModulesTaxonomy", moduleAutomaton, mappings);
		/*
		 * create constraints on the mutual exclusion of the types, mandatory usage of
		 * the types is not required (they can be empty)
		cnf += allTypes.typeMutualExclusion(typeAutomaton, mappings);

		/*
		 * TODO encode the constraints from the paper manually
		 */
		cnf += generateSLTLConstraints(allModules, allTypes, moduleAutomaton, typeAutomaton);

		int variables = mappings.getSize();
		int clauses = StringUtils.countMatches(cnf, " 0");
		String description = "p cnf " + variables + " " + clauses + "\n";

		String cnf_file = "/home/vedran/Desktop/cnf.txt";
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cnf_file), "utf-8"))) {
			writer.write(description);
			writer.write(cnf);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(
					"/home/vedran/Documents/minisat/core/minisat /home/vedran/Desktop/cnf.txt /home/vedran/Desktop/cnf-sol.txt");
			pr.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String cnf_file_translated = "/home/vedran/Desktop/cnf-translated.txt";

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(cnf_file_translated), "utf-8"))) {
			writer.write(readSATsolution(cnf_file, mappings));
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String solution_file = "/home/vedran/Desktop/cnf-sol.txt";
		String solution_file_translated = "/home/vedran/Desktop/cnf-sol-translated.txt";

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(solution_file_translated), "utf-8"))) {
			writer.write(readSATsolution(solution_file, mappings));
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(cnf);
	}

}
