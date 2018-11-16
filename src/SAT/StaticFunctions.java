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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

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

	public static List<String[]> getTuplesFromCSV(String csvFile) {

		List<String[]> constraints = new ArrayList<>();

		try {

			FileReader filereader = new FileReader(csvFile);

			// create csvReader object and skip first Line
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			constraints = csvReader.readAll();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return constraints;
	}

	/**
	 * Returns the CNF representation of the SLTL constraints in our project
	 * 
	 * @param allModules
	 *            - list of all modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return A string representing SLTL constraints into CNF
	 */
	public static String generateSLTLConstraints(String constraintsPath, AllConstraintTamplates allConsTemplates,
			AllModules allModules, AllTypes allTypes, AtomMapping mappings, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		int constraintID;
		List<String> parameters;
		for (String[] currConstr : getTuplesFromCSV(constraintsPath)) {
			try {
				constraintID = Integer.parseInt(currConstr[0]);
			} catch (NumberFormatException e) {
				System.err.println("Constraint ID provided: " + currConstr[0] + " is not Integer.");
				continue;
			}
			if (allConsTemplates.getConstraintTamplate(constraintID) == null) {
				System.err.println("Constraint ID provided: " + currConstr[0] + " is not valid.");
				continue;
			} else {
				parameters = new ArrayList<>();
				for (int i = 1; i < currConstr.length; i++) {
					if (!currConstr[i].isEmpty()) {
						parameters.add(currConstr[i]);
					}
				}
				cnf_SLTL += code_function(constraintID, parameters.toArray(new String[parameters.size()]),
						allConsTemplates, allModules, allTypes, moduleAutomaton, typeAutomaton, mappings);
			}
		}

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
	 * @return String description of all the formats (ID, description and number of
	 *         parameters for each).
	 */
	public static String initializeConstraints(AllConstraintTamplates allConsTemplates) {

		/*
		 * ID: 1 If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
		 * consequently.
		 */
		ConstraintTemplate currTemplate = new Constraint_if_then_module(2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> consequently.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 2 If we use module <b>parameters[0]</b>, then do not use
		 * <b>parameters[1]</b> consequently.
		 */
		currTemplate = new Constraint_if_then_not_module(2,
				"If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b> consequently.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 3 If we use module <b>parameters[0]</b>, then we must have used
		 * <b>parameters[1]</b> prior to it.
		 */
		currTemplate = new Constraint_depend_module(2,
				"If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 4 If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as
		 * a next module in the sequence.
		 */
		currTemplate = new Constraint_next_module(2,
				"If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 5 Use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_module(1, "Use module <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 6 Do not use module <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_module(1, "Do not use module <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 7 Use <b>parameters[0]</b> as last module in the solution.
		 */
		currTemplate = new Constraint_last_module(1, "Use <b>parameters[0]</b> as last module in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 8 Use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_use_type(1, "Use type <b>parameters[0]</b> in the solution.");
		allConsTemplates.addConstraintTamplate(currTemplate);

		/*
		 * ID: 9 Do not use type <b>parameters[0]</b> in the solution.
		 */
		currTemplate = new Constraint_not_use_type(1, "Do not use type <b>parameters[0]</b> in the solution.");
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
	public static String code_function(int constraintID, String[] parameters, AllConstraintTamplates allConsTemplates,
			AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {
		String constraint = allConsTemplates.getConstraintTamplate(constraintID).getConstraint(parameters, allModules,
				allTypes, moduleAutomaton, typeAutomaton, mappings);

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
		ISolver solver = SolverFactory.newDefault();
		// ISolver solver = new ModelIterator(SolverFactory.newDefault(),
		// no_of_solutions); // iteration through at most
		// no_of_solutions solutions
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(dimacsFilePath); // loading CNF encoding of the problem
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
				/*
				 * Adding the negation of the solution as a constraint
				 */
				IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray());
				solver.addClause(negSol);
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
	public static List<Module> readModuleCSV(String file, AllModules allModules, AllTypes allTypes) {

		List<Module> modulesNew = new ArrayList<Module>();

		for (String[] stringModule : getTuplesFromCSV(file)) {
			modulesNew.add(Module.moduleFromString(stringModule, allModules, allTypes));
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

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions.
	 * 
	 * @param tag
	 *            - corresponding tag from the config file
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	public static boolean isValidConfigWriteFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag <" + tag + "> in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (f.isDirectory()) {
			System.err.println("Tag <" + tag + ">:\nProvided path: \"" + path + "\" is a directory.");
			return false;
		} else {
			if (!f.getParentFile().isDirectory()) {
				System.err.println("Tag <" + tag + ">:\nProvided path: \"" + path + "\" is not a valid path.");
				return false;
			} else {
				if (!f.canWrite() && !f.getParentFile().canWrite()) {
					System.err.println(
							"Tag <" + tag + ">:\nProvided path: \"" + path + "\" is missing the writing permission.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions.
	 * 
	 * @param tag
	 *            - corresponding tag from the config file
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists and can be read, {@code false}
	 *         otherwise.
	 */
	public static boolean isValidConfigReadFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag <" + tag + "> in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isFile()) {
			System.err.println("Tag <" + tag + ">:\nProvided path: \"" + path + "\" is not a file.");
			return false;
		} else {
			if (!f.canRead()) {
				System.err.println(
						"Tag <" + tag + ">:\nProvided file: \"" + path + "\" is missing the reading permission.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions.
	 * 
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists and can be read, {@code false}
	 *         otherwise.
	 */
	public static boolean isValidReadFile(String path) {
		if (path == null || path == "") {
			System.err.println("Path is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isFile()) {
			System.err.println("Provided path: \"" + path + "\" is not a file.");
			return false;
		} else {
			if (!f.canRead()) {
				System.err.println("Provided file: \"" + path + "\" is missing the reading permission.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided string represent an integer number, and
	 * return the number if it does
	 * 
	 * @param stringNumber
	 *            - provided string
	 * @return Integer number represented with the string, {@code null} in case of a
	 *         bad String format.
	 */
	public static Integer isValidConfigInt(String tag, String stringNumber) {
		if (stringNumber == null || stringNumber == "") {
			System.err.println("Tag <" + tag + "> in the configuration file is not provided correctly.");
			return null;
		} else if (!StringUtils.isNumeric(stringNumber)) {
			System.err.println(
					"Tag <" + tag + ">:\nProvided number: \"" + stringNumber + "\" is not in a correct format.");
			return null;
		}

		return Integer.parseInt(stringNumber);
	}

	/**
	 * Method checks whether the provided string represent a boolean value, and
	 * return the boolean if it does
	 * 
	 * @param stringBool
	 *            - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	public static Boolean isValidConfigBoolean(String tag, String stringBool) {
		if (stringBool == null || stringBool == "") {
			System.err.println("Tag <" + tag + "> in the configuration file is not provided correctly.");
			return null;
		} else {
			Boolean boolVal = BooleanUtils.toBooleanObject(stringBool);
			if (boolVal == null) {
				System.err.println(
						"Tag <" + tag + ">:\nProvided boolean value: \"" + stringBool + "\" is not in a correct format.");
				return null;
			} else {
				return boolVal;
			}
		}
	}

}
