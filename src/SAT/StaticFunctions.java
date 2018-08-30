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

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;
import SAT.models.Module;
import SAT.models.SAT_solution;
import SAT.models.SLTL_formula;
import SAT.models.SLTL_formula_F;
import SAT.models.SLTL_formula_G;
import SAT.models.Type;
/**
 * The {@code StaticFunctions} class is used for storing {@code Static} methods.
 * 
 * @author Vedran Kasalica
 *
 */
public class StaticFunctions {
	
	
	/**
	 * Returns the CNF representation of the SLTL constraints in our project
	 * 
	 * @param allModules
	 *            - list of all modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return A string representing SLTL constraints into CNF
	 */
	public static String generateSLTLConstraints(AllModules allModules, AllTypes allTypes, AtomMapping mappings,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		List<SLTL_formula> constraints = new ArrayList<>();
		/*
		 * Constraint G1: If Modules_with_xyz_file_input then use
		 * Modules_with_xyz_file_output
		 */
		cnf_SLTL += if_then_module("Modules_with_xyz_file_input", "Modules_with_xyz_file_output", allModules, moduleAutomaton, typeAutomaton,
				mappings);
		

		/*
		 * Constraint G2: If Modules_with_grid_file_input then use
		 * Modules_with_grid_file_output
		 */
		cnf_SLTL += if_then_module("Modules_with_grid_file_input", "Modules_with_grid_file_output", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G3: If Modules_with_color_palette_input then use
		 * Modules_with_color_palette_output
		 */
		cnf_SLTL += if_then_module("Modules_with_color_palette_input", "Modules_with_color_palette_output", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G4: Do not use module 3D_surfaces
		 */ 
		cnf_SLTL += not_use_module("3D_surfaces", allModules, moduleAutomaton, typeAutomaton,
				mappings);
		 

		/*
		 * Constraint G5: Use the data type Plots
		 */
		cnf_SLTL += use_type("Plots", allTypes, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E0.1: Use Draw_water in the synthesis
		 */
		cnf_SLTL += use_module("Draw_water", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E0.2: Use Draw_land in the synthesis
		 */
		cnf_SLTL += use_module("Draw_land", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E0.3: Use Draw_political_bourders in the synthesis
		 */
		cnf_SLTL += use_module("Draw_political_borders", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E0.4: Use Display_PostScript as last module in the solution
		 */
		cnf_SLTL += last_module("Display_PostScript_files", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E1.1: Use Draw_boundary_frame in the synthesis
		 */
		cnf_SLTL += use_module("Draw_boundary_frame", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E1.2: Use Write_title in the synthesis
		 */
		cnf_SLTL += use_module("Write_title", allModules, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint E1.3: Use Draw_time_stamp_logo in the synthesis
		 */
		cnf_SLTL += use_module("Draw_time_stamp_logo", allModules, moduleAutomaton, typeAutomaton,
				mappings);

//		/*
//		 * Constraint E2: Use Add_table in the synthesis
//		 */
//		AbstractModule add_table = allModules.get("Adding_table");
//		SLTL_formula_F e2 = new SLTL_formula_F(add_table);
//		constraints.add(e2);
//		cnf_SLTL += e2.getCNF(moduleAutomaton, typeAutomaton, mappings);
//
//		/*
//		 * Constraint E3: Use 2D_surfaces in the synthesis
//		 */
//		AbstractModule _2D_surfaces = allModules.get("2D_surfaces");
//		SLTL_formula_F e3 = new SLTL_formula_F(_2D_surfaces);
//		constraints.add(e3);
//		cnf_SLTL += e3.getCNF(moduleAutomaton, typeAutomaton, mappings);
//
//		/*
//		 * Constraint E4.1: Use 2D_surfaces again in the synthesis
//		 */
//		AbstractModule _3D_surfaces = allModules.get("3D_surfaces");
//		cnf_SLTL += SLTL_formula.ite(_2D_surfaces, _3D_surfaces, moduleAutomaton, typeAutomaton, mappings);
//
//		/*
//		 * Constraint E4.2: Use Gradient_generation in the synthesis after the first
//		 * 2D_surfaces
//		 */
//		AbstractModule gradient_generation = allModules.get("Gradient_generation");
//		cnf_SLTL += SLTL_formula.ite(_2D_surfaces, gradient_generation, moduleAutomaton, typeAutomaton, mappings);
//
//		/*
//		 * Constraint E4.3: Use Modules_with_xyz_file_output in the synthesis after the
//		 * first 2D_surfaces
//		 */
//		cnf_SLTL += SLTL_formula.ite(_2D_surfaces, modules_with_color_palette_output, moduleAutomaton, typeAutomaton,
//				mappings);

		return cnf_SLTL;
	}
	
	public static String if_then_module(String if_moduleID, String then_moduleID, AllModules allModules,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = "";
		AbstractModule if_module = allModules.get("Modules_with_xyz_file_input");
		AbstractModule then_module = allModules.get("Modules_with_xyz_file_output");
		constraint = SLTL_formula.ite(then_module, if_module, moduleAutomaton, typeAutomaton,
				mappings);
		
		return constraint; 
	}
	
	/*
	 * Use module
	 */
	public static String use_module(String moduleID, AllModules allModules,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = "";
		AbstractModule module = allModules.get(moduleID);
		SLTL_formula_F formula = new SLTL_formula_F(module);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint; 
	}
	
	/*
	 * Do not use module
	 */
	public static String not_use_module(String moduleID, AllModules allModules,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = "";
		
		AbstractModule module = allModules.get(moduleID); 
		SLTL_formula_G formula = new SLTL_formula_G(false, module); 
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint; 
	}
	
	/*
	 * Use type
	 */
	public static String use_type(String typeID, AllTypes allTypes,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = "";

		Type type = allTypes.get(typeID);
		SLTL_formula_F formula = new SLTL_formula_F(type);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);
		
		return constraint; 
	}
	
	/*
	 * Use the module as a first module
	 */
	/*
	 * Use the module as a last module
	 */
	public static String last_module(String moduleID, AllModules allModules,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = "";
		AbstractModule module = allModules.get(moduleID);
		constraint += SLTL_formula.useAsLastModule(module, moduleAutomaton, typeAutomaton, mappings);

		return constraint; 
	}
	
	
	
	
	
	/**
	 * Used to write the @text to a file @file. If @append is TRUE, the @text is
	 * appended to the @file, otherwise the file is rewritten.
	 * 
	 * @param text
	 *            - text that will be written in the file
	 * @param file
	 *            - the system-dependent file name
	 * @param append
	 *            - if true, then bytes will be written to the end of the file
	 *            rather than the beginning
	 */
	public static void write2file(String text, File file, boolean append) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "utf-8"))) {
			writer.write(text + "\n");
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Returns the {@link SAT_solution SAT_solution} by parsing the SAT output {@link java.io.File file} provided by the argument file. In case of the UNSAT solution the object list of literals is {@code NULL} and {@link SAT_solution#isSat()} returns {@code false}, otherwise the list of parsed literals is returned and {@link SAT_solution#isSat()} returns {@code true}. 
	 * @param file - {@link File} to be parsed for the SAT solutions (SAT output)
	 * @param mappings - atom mappings
	 * @param allModules - set of all the {@link Module}s
	 * @param allTypes - set of all the {@link Type}s
	 * @return SAT_solution object.
	 */
	public static SAT_solution getSATsolution(File file, AtomMapping mappings, AllModules allModules,
			AllTypes allTypes) {

		BufferedReader textReader;
		SAT_solution sat_solution = null;

		try {
			textReader = new BufferedReader(new FileReader(file));
			String sat = textReader.readLine();
			/*
			 * check whether it is SAT or UNSAT
			 */
			if (!sat.matches("UNSAT")) {
				String solution = textReader.readLine();
				sat_solution = new SAT_solution(solution, mappings, allModules, allTypes);
			} else {
				sat_solution = new SAT_solution();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sat_solution;
	}

	public static long solve(String miniSatPath, String inputPath, String outputPath) {
//		System.out.println("________________________________________\n" + "|          Solving started.....         |\n"
//				+ "|                                       |");
		long start = System.currentTimeMillis();
		Runtime rt = Runtime.getRuntime();

		try {
			Process pr = rt.exec(miniSatPath + " " + inputPath + " " + outputPath);
			pr.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long elapsedTimeMillis = System.currentTimeMillis() - start;
//		System.out.println("|          ...solving completed!       |\n" + "|          Time:"
//				+ elapsedTimeMillis / 1000F + "                   |\n" + "|_______________________________________|");
		return elapsedTimeMillis;
	}

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
	 * Generate the State automatons (Module and Type) based on the defined length
	 * and branching factor.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param automata_bound - length of the automaton
	 * @param branching - branching factor (max number of outputs for modules)
	 */
	public static void generateAutomaton(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, int automata_bound, int branching) {
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

}
