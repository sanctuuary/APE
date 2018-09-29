package SAT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import SAT.automaton.*;
import SAT.constraints.*;
import SAT.models.*;

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
	public static String generateSLTLConstraints(AllConstraintTamplates allConsTemplates, AllModules allModules, AllTypes allTypes, AtomMapping mappings,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		List<SLTL_formula> constraints = new ArrayList<>();
		/*
		 * Constraint G1: If Modules_with_xyz_file_input then use
		 * Modules_with_xyz_file_output
		 */
		String[] g1 = {"Modules_with_xyz_file_output", "Modules_with_xyz_file_input"};
		cnf_SLTL += code_function(1, g1, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G2: If Modules_with_grid_file_input then use
		 * Modules_with_grid_file_output
		 */
		String[] g2 = {"Modules_with_grid_file_output", "Modules_with_grid_file_input"};
		cnf_SLTL += code_function(1, g2, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G3: If Modules_with_color_palette_input then use
		 * Modules_with_color_palette_output
		 */
		String[] g3 = {"Modules_with_color_palette_output", "Modules_with_color_palette_input"};
		cnf_SLTL += code_function(1, g3, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G4: Do not use module 3D_surfaces
		 */
		String[] g4 = {"3D_surfaces"};
		cnf_SLTL += code_function(6, g4, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint G5: Use the data type Plots
		 */
		String[] g5 = {"Plots"};
		cnf_SLTL += code_function(8, g5, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.1: Use Draw_water in the synthesis
		 */
		String[] e01 = {"Draw_water"};
		cnf_SLTL += code_function(5, e01, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.2: Use Draw_land in the synthesis
		 */
		String[] e02 = {"Draw_land"};
		cnf_SLTL += code_function(5, e02, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.3: Use Draw_political_bourders in the synthesis
		 */
		String[] e03 = {"Draw_political_borders"};
		cnf_SLTL += code_function(5, e03, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E0.4: Use Display_PostScript as last module in the solution
		 */
		String[] e04 = {"Display_PostScript_files"};
		cnf_SLTL += code_function(7, e04, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E1.1: Use Draw_boundary_frame in the synthesis
		 */
		String[] e11 = {"Draw_boundary_frame"};
		cnf_SLTL += code_function(5, e11, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E1.2: Use Write_title in the synthesis
		 */
		String[] e12 = {"Write_title"};
		cnf_SLTL += code_function(5, e12, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		/*
		 * Constraint E1.3: Use Draw_time_stamp_logo in the synthesis
		 */
		String[] e13 = {"Draw_time_stamp_logo"};
		cnf_SLTL += code_function(5, e13, allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);

		// /*
		// * Constraint E2: Use Add_table in the synthesis
		// */
		// cnf_SLTL += use_module("Draw_time_stamp_logo", allModules, moduleAutomaton,
		// typeAutomaton,
		// mappings);
		// AbstractModule add_table = allModules.get("Adding_table");
		// SLTL_formula_F e2 = new SLTL_formula_F(add_table);
		// constraints.add(e2);
		// cnf_SLTL += e2.getCNF(moduleAutomaton, typeAutomaton, mappings);
		//
		// /*
		// * Constraint E3: Use 2D_surfaces in the synthesis
		// */
		// cnf_SLTL += use_module("2D_surfaces", allModules, moduleAutomaton,
		// typeAutomaton,
		// mappings);
		//
		// /*
		// * Constraint E4.1: Use 2D_surfaces again in the synthesis (changed to use
		// 3D_surfaces after 2D_surfaces)
		// */
		// cnf_SLTL += use_module("Draw_time_stamp_logo", allModules, moduleAutomaton,
		// typeAutomaton,
		// mappings);
		// AbstractModule _3D_surfaces = allModules.get("3D_surfaces");
		// cnf_SLTL += SLTL_formula.ite("2D_surfaces", _3D_surfaces, moduleAutomaton,
		// typeAutomaton, mappings);
		//
		// /*
		// * Constraint E4.2: Use Gradient_generation in the synthesis after the first
		// * 2D_surfaces
		// */
		// AbstractModule gradient_generation = allModules.get("Gradient_generation");
		// cnf_SLTL += SLTL_formula.ite(_2D_surfaces, gradient_generation,
		// moduleAutomaton, typeAutomaton, mappings);
		//
		// /*
		// * Constraint E4.3: Use Modules_with_xyz_file_output in the synthesis after
		// the
		// * first 2D_surfaces
		// */
		// cnf_SLTL += SLTL_formula.ite(_2D_surfaces, modules_with_color_palette_output,
		// moduleAutomaton, typeAutomaton,
		// mappings);

		return cnf_SLTL;
	}

	/**
	 * Adding each constraint format in the set of all cons. formats
	 * 
	 * @param allConsTemplates
	 *            - set that represents all the cons. formats
	 * @return String description of all the formats (ID, description and number of parameters for each).
	 */
	public static String initializeConstraints(AllConstraintTamplates allConsTemplates) {

		/*
		 * ID:	1
		 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> consequently.
		 */
		ConstraintTemplate currTemplate = new Constraint_if_then_module(2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> consequently.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID:	2
		 * If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b> consequently.
		 */
		currTemplate = new Constraint_if_then_not_module(2,
				"If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b> consequently.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID:	3
		 * If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it.
		 */
		currTemplate = new Constraint_depend_module(2,
				"If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		/*
		 * ID:	4
		 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence.
		 */
		currTemplate = new Constraint_next_module(2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		/*
		 * ID:	5
		 * Use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_module(1,
				"Use module <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		/*
		 * ID:	6
		 * Do not use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_module(1,
				"Do not use module <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		/*
		 * ID:	7
		 * Use <b>parameters[0]</b> as last module in the solution.
		 */
		currTemplate = new Constraint_last_module(1,
				"Use <b>parameters[0]</b> as last module in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID:	8
		 *  Use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_type(1,
				"Use type <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		/*
		 * ID:	9
		 *  Do not use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_type(1,
				"Do not use type <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);
		
		return allConsTemplates.printConstraintsCodes();

	}

	/**
	 * Function used to define constrain based on the constraint ID specified
	 * 
	 * @param constraintID
	 *            -
	 * @param parameters
	 * @return
	 */
	public static String code_function(int constraintID, String[] parameters, AllConstraintTamplates allConsTemplates, AllModules allModules, AllTypes allTypes,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = allConsTemplates.getConstraintTamplate(constraintID) .getConstraint(parameters, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);
		
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
			writer.write(text);
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
	 * Returns the {@link SAT_solution SAT_solution} by parsing the SAT output
	 * {@link java.io.File file} provided by the argument file. In case of the UNSAT
	 * solution the object list of literals is {@code NULL} and
	 * {@link SAT_solution#isSat()} returns {@code false}, otherwise the list of
	 * parsed literals is returned and {@link SAT_solution#isSat()} returns
	 * {@code true}.
	 * 
	 * @param file
	 *            - {@link File} to be parsed for the SAT solutions (SAT output)
	 * @param mappings
	 *            - atom mappings
	 * @param allModules
	 *            - set of all the {@link Module}s
	 * @param allTypes
	 *            - set of all the {@link Type}s
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

	/**
	 * Returns a set of {@link SAT_solution SAT_solutions} by parsing the SAT
	 * output. In case of the UNSAT the list is empty.
	 * 
	 * @param dimacsFilePath
	 *            - path to the CNF formula in dimacs form
	 * @param mappings
	 *            - atom mappings
	 * @param allModules
	 *            - list of all the modules
	 * @param allTypes
	 *            - list of all the types
	 * @return List of {@link SAT_solution SAT_solutions}. Possibly empty list.
	 */
	public static List<SAT_solution> solve(String dimacsFilePath, AtomMapping mappings, AllModules allModules,
			AllTypes allTypes, int no_of_solutions) {
		List<SAT_solution> solutions = new ArrayList<>();

		ISolver solver = new ModelIterator(SolverFactory.newDefault(), no_of_solutions); //iteration through at most no_of_solutions solutions
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(dimacsFilePath); //loading CNF encoding of the problem
			int solutionNo = 0;
			long realStartTime = System.currentTimeMillis();
			long realTimeElapsedMillis;
			while (solutionNo < no_of_solutions && problem.isSatisfiable()) {
				SAT_solution sat_solution = new SAT_solution(problem.model(), mappings, allModules, allTypes);
				solutions.add(sat_solution);
				solutionNo++;
				if (solutionNo % 500 == 0) {
					realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
					System.out.println("Found " + solutionNo + " solutions. Solving time: "
							+ (realTimeElapsedMillis / 1000F) + " sec.");
				}
			}
		} catch (ParseFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionException e) {
			System.err.println("Unsatisfiable");
		} catch (TimeoutException e) {
			System.err.println("Timeout");
		}

		return solutions;
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
			 * skip the first line
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
	 * @param automata_bound
	 *            - length of the automaton
	 * @param branching
	 *            - branching factor (max number of outputs for modules)
	 */
	public static void generateAutomaton(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			int automata_bound, int branching) {
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
