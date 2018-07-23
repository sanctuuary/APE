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

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;
import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;
import SAT.models.Module;
import SAT.models.SAT_solution;
import SAT.models.SLTL_formula;
import SAT.models.SLTL_formula_F;
import SAT.models.Type;

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

		AbstractModule modules_with_xyz_input = allModules.get("Modules_with_xyz_file_input");
		AbstractModule modules_with_xyz_output = allModules.get("Modules_with_xyz_file_output");
		// constraints.add(???);
		cnf_SLTL += SLTL_formula.ite(modules_with_xyz_output, modules_with_xyz_input, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G2: If Modules_with_grid_file_input then use
		 * Modules_with_grid_file_output
		 */
		AbstractModule modules_with_grid_input = allModules.get("Modules_with_grid_file_input");
		AbstractModule modules_with_grid_output = allModules.get("Modules_with_grid_file_output");
		cnf_SLTL += SLTL_formula.ite(modules_with_grid_output, modules_with_grid_input, moduleAutomaton, typeAutomaton,
				mappings);

		/*
		 * Constraint G3: If Modules_with_color_palette_input then use
		 * Modules_with_color_palette_output
		 */
		AbstractModule modules_with_color_palette_input = allModules.get("Modules_with_color_palette_input");
		AbstractModule modules_with_color_palette_output = allModules.get("Modules_with_color_palette_output");
		cnf_SLTL += SLTL_formula.ite(modules_with_color_palette_output, modules_with_color_palette_input,
				moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G4: Do not use module 3D_surfaces
		 * 
		 * AbstractModule _3d_surfaces = allModules.get("3D_surfaces"); SLTL_formula_G
		 * g4 = new SLTL_formula_G(_3d_surfaces, true); constraints.add(g4); cnf_SLTL +=
		 * g4.getCNF(moduleAutomaton, typeAutomaton, mappings);
		 */

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

		/*
		 * Constraint E1.1: Use Draw_boundary_frame in the synthesis
		 */
		AbstractModule draw_boundary_frame = allModules.get("Draw_boundary_frame");
		SLTL_formula_F e1_1 = new SLTL_formula_F(draw_boundary_frame);
		constraints.add(e1_1);
		cnf_SLTL += e1_1.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E1.2: Use Write_title in the synthesis
		 */
		AbstractModule write_title = allModules.get("Write_title");
		SLTL_formula_F e1_2 = new SLTL_formula_F(write_title);
		constraints.add(e1_2);
		cnf_SLTL += e1_2.getCNF(moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E1.3: Use Draw_time_stamp_logo in the synthesis
		 */
		AbstractModule draw_time_stamp_logo = allModules.get("Draw_time_stamp_logo");
		SLTL_formula_F e1_4 = new SLTL_formula_F(draw_time_stamp_logo);
		constraints.add(e1_4);
		cnf_SLTL += e1_4.getCNF(moduleAutomaton, typeAutomaton, mappings);

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
	public static void write2file(String text, String file, boolean append) {
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

	public static String readSATdefinition(String file, AtomMapping mappings) {

		String line = "";
		String textSplitBy = " ";
		BufferedReader textReader;
		String solution = "";

		try {
			textReader = new BufferedReader(new FileReader(file));
			String sat = textReader.readLine();
			/*
			 * check whether it is SAT or UNSAT
			 */
			if (!sat.matches("UNSAT")) {

				while ((line = textReader.readLine()) != null) {
					String[] terms = line.split(textSplitBy, -1);
					for (String term : terms) {
						if (term.startsWith("-")) {
							solution += "-";
							solution += mappings.findOriginal(Integer.parseInt(term.substring(1))) + " ";
						} else if (!term.matches("0")) {
							solution += mappings.findOriginal(Integer.parseInt(term)) + " ";
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

	public static String readSATsolution(String file, AtomMapping mappings, AllModules allModules, AllTypes allTypes) {

		String line = "";
		String textSplitBy = " ";
		BufferedReader textReader;
		String solution = "";

		try {
			textReader = new BufferedReader(new FileReader(file));
			String sat = textReader.readLine();
			/*
			 * check whether it is SAT or UNSAT
			 */
			if (!sat.matches("UNSAT")) {

				while ((line = textReader.readLine()) != null) {
					String[] terms = line.split(textSplitBy, -1);
					for (String term : terms) {
						if (term.startsWith("-")) {
						} else if (!term.matches("0")) {

							String atomID = mappings.findOriginal(Integer.parseInt(term));
							if (atomID == null) {
								solution += " Atom: " + term + " cannot be mapped back.\n";
							} else {
								String predicate = atomID.split("\\(")[0];
								AbstractModule tmpModule = allModules.get(predicate);
								if (!predicate.matches("empty") && (tmpModule != null && tmpModule instanceof Module)) {
									solution += atomID + " ";
									solution += "\n";
								} else if (!predicate.matches("empty") && tmpModule == null) {
									Type tmpType = allTypes.get(predicate);
									if (tmpType != null && tmpType.isSimpleType()) {
										solution += atomID + " ";
										solution += "\n";
									}
								}

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

	/**
	 * Returns the Object SAT_solution by parsing the SAT output file @file. In case of the UNSAT solution the object list of literals is NULL and SAT_solution.isUnsat() returns true, otherwise the list of parsed literals is returned and SAT_solution.isUnsat() returnes false. 
	 * @param file - file to be parsed for the SAT solutions
	 * @param mappings - atom mappings
	 * @param allModules - set of all the modules
	 * @param allTypes - set of all the types
	 * @return SAT_solution object.
	 */
	public static SAT_solution getSATsolution(String file, AtomMapping mappings, AllModules allModules,
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
//		System.out.println("|          ...solving compleated!       |\n" + "|          Time:"
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


}
