package nl.uu.cs.ape.sat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
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

import nl.uu.cs.ape.sat.automaton.*;
import nl.uu.cs.ape.sat.constraints.*;
import nl.uu.cs.ape.sat.models.*;

/**
 * The {@code StaticFunctions} class is used for storing {@code Static} methods.
 * 
 * @author Vedran Kasalica
 *
 */
public class StaticFunctions {

	private static String ROOT_XML_path = "/functions/function";

	/**
	 * Return the list of all the tuples in the CSV.
	 * 
	 * @param csvFile
	 * @return
	 */
	public static List<String[]> getTuplesFromCSV(String csvFile) {

		List<String[]> constraints = new ArrayList<String[]>();

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
	 * @param allModules      - list of all modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return A string representing SLTL constraints into CNF
	 */
	public static String generateSLTLConstraints(String constraintsPath, AllConstraintTamplates allConsTemplates,
			AllModules allModules, AllTypes allTypes, AtomMapping mappings, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		int constraintID, currRow = 1;
		List<String> parameters;
		for (String[] currConstr : getTuplesFromCSV(constraintsPath)) {
			currRow++;
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
				parameters = new ArrayList<String>();
				for (int i = 1; i < currConstr.length; i++) {
					if (!currConstr[i].isEmpty()) {
						parameters.add(currConstr[i]);
					}
				}
				String currConstrEncoding = constraintSATEncoding(constraintID,
						parameters.toArray(new String[parameters.size()]), allConsTemplates, allModules, allTypes,
						moduleAutomaton, typeAutomaton, mappings);
				if (currConstrEncoding == null) {
					System.err.println(
							"Error in file: " + constraintsPath + ", at row: " + currRow + ". Constraint skipped.");
				} else {
					cnf_SLTL += currConstrEncoding;
				}
			}
		}

		return cnf_SLTL;
	}

	/**
	 * Adding each constraint format in the set of all cons. formats
	 * 
	 * @param allConsTemplates - set that represents all the cons. formats
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

		/*
		 * ID: 10 Use <b>parameters[0]</b> as N-th module in the solution (where
		 * <b>parameters[0]</b> = N).
		 */
//		currTemplate = new Constraint_nth_module(2,
//				"Use <b>parameters[0]</b> as <b>parameters[1]</b>-th (N-th) module in the solution (where <b>parameters[1]</b> = N)");
//		allConsTemplates.addConstraintTamplate(currTemplate);

		return allConsTemplates.printConstraintsCodes();

	}

	/**
	 * Function used to provide SAT encoding of a constrain based on the constraint
	 * ID specified and provided parameters.
	 * 
	 * @param constraintID - ID of the constraint -
	 * @param parameters
	 * @return String representation of the SAT encoding for the specified
	 *         constraint.
	 */
	public static String constraintSATEncoding(int constraintID, String[] parameters,
			AllConstraintTamplates allConsTemplates, AllModules allModules, AllTypes allTypes,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraint = allConsTemplates.getConstraintTamplate(constraintID).getConstraint(parameters, allModules,
				allTypes, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

	/**
	 * Used to write the @text to a file @file. If @append is TRUE, the @text is
	 * appended to the @file, otherwise the file is rewritten.
	 * 
	 * @param text   - text that will be written in the file
	 * @param file   - the system-dependent file name
	 * @param append - if true, then bytes will be written to the end of the file
	 *               rather than the beginning
	 */
	public static void write2file(String text, File file, boolean append) {

		try {
			FileWriter fw = new FileWriter(file, append);
			fw.write(text);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 * @param file       - {@link File} to be parsed for the SAT solutions (SAT
	 *                   output)
	 * @param mappings   - atom mappings
	 * @param allModules - set of all the {@link Module}s
	 * @param allTypes   - set of all the {@link Type}s
	 * @return SAT_solution object.
	 */
	public static SAT_solution getSATsolution(File file, AtomMapping mappings, AllModules allModules, AllTypes allTypes,
			int solutionLength) {

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
				sat_solution = new SAT_solution(solution, mappings, allModules, allTypes, solutionLength);
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
	 * @param dimacsFilePath    - path to the CNF formula in dimacs form
	 * @param mappings          - atom mappings
	 * @param allModules        - list of all the modules
	 * @param allTypes          - list of all the types
	 * @param solutionsFoundMax
	 * @return List of {@link SAT_solution SAT_solutions}. Possibly empty list.
	 */
	public static List<SAT_solution> solve(String dimacsFilePath, AtomMapping mappings, AllModules allModules,
			AllTypes allTypes, int solutionsFound, int solutionsFoundMax, int solutionLength) {
		List<SAT_solution> solutions = new ArrayList<SAT_solution>();
		ISolver solver = SolverFactory.newDefault();
		int timeout = 3600;
		// ISolver solver = new ModelIterator(SolverFactory.newDefault(),
		// no_of_solutions); // iteration through at most
		// no_of_solutions solutions
		solver.setTimeout(timeout); // 1 hour timeout
		long realStartTime = 0;
		long realTimeElapsedMillis;
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(dimacsFilePath); // loading CNF encoding of the problem
			realStartTime = System.currentTimeMillis();
			while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
				SAT_solution sat_solution = new SAT_solution(problem.model(), mappings, allModules, allTypes,
						solutionLength);
				solutions.add(sat_solution);
				solutionsFound++;
				if (solutionsFound % 500 == 0) {
					realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
					System.out.println("Found " + solutionsFound + " solutions. Solving time: "
							+ (realTimeElapsedMillis / 1000F) + " sec.");
				}
				/*
				 * Adding the negation of the positive part of the solution as a constraint
				 * (default negation does not work)
				 */
				IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray());
				solver.addClause(negSol);
			}
		} catch (ParseFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionException e) {
			System.err.println("Unsatisfiable");
		} catch (TimeoutException e) {
			System.err.println("Timeout. Solving took longer than default timeout: " + timeout + " seconds.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (solutionsFound == 0 || solutionsFound % 500 != 0) {
			realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			System.out.println("Found " + solutionsFound + " solutions. Solving time: "
					+ (realTimeElapsedMillis / 1000F) + " sec.");
		}
		
		return solutions;
	}

	/**
	 * Updates the list of All Modules by annotating the existing ones (or adding
	 * non-existing) using the I/O Types from the @file. Returns the list of Updated
	 * Modules.
	 * 
	 * @param file       - path to the .XML file containing tool annotations
	 * @param allModules - list of all existing modules
	 * @param allTypes   - list of all existing types
	 * @return the list of all annotated Modules in the process (possibly empty
	 *         list)
	 */
	public static List<Module> readModuleXML(String file, AllModules allModules, AllTypes allTypes) {
		List<Module> modulesNew = new ArrayList<Module>();

		for (Node xmlModule : getFunctionsFromXML(file)) {
			modulesNew.add(Module.moduleFromXML(xmlModule, allModules, allTypes));
		}

		return modulesNew;
	}

	/**
	 * Transforms the propositional formula into the CNF form.
	 * 
	 * @param propositionalFormula - propositional formula
	 * @return CNF representation of the formula
	 */
	public static String convert2CNF(String propositionalFormula) {
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		Formula formula;
		try {
			formula = p.parse(propositionalFormula.replace('-', '~'));
			final Formula cnf = formula.cnf();
			return cnf.toString().replace('~', '-').replace(") & (", " 0\n").replace(" | ", " ").replace("(", "")
					.replace(")", "") + " 0\n";
		} catch (ParserException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static List<Node> getFunctionsFromXML(String xmlPath) {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(xmlPath);
			List<Node> functionList = document.selectNodes(ROOT_XML_path);
			return functionList;
		} catch (DocumentException e) {
			System.err.println("Error parsing the XML file: " + xmlPath);
			return null;
		}
	}

	/**
	 * Generate the State automatons (Module and Type) based on the defined length
	 * and branching factor.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param automata_bound  - length of the automaton
	 * @param branching       - branching factor (max number of outputs for modules)
	 */
	public static void generateAutomaton(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			int automata_bound, int branching) {
		for (int i = 0; i <= automata_bound; i++) {
			String i_var;
			if (automata_bound > 10 && i < 10) {
				i_var = "0" + i;
			} else {
				i_var = "" + i;
			}
			if (i > 0) {
				ModuleState tmpModuleState = new ModuleState("M" + i_var, i);
				if (i == 1) {
					tmpModuleState.setFirst();
				} else if (i == automata_bound) {
					tmpModuleState.setLast();
				}
				moduleAutomaton.addState(tmpModuleState);

			}

			TypeBlock tmpTypeBlock = new TypeBlock(i);
			for (int j = 0; j < branching; j++) {
				TypeState tmpTypeState = new TypeState("T" + i_var + "." + j, j);
				tmpTypeBlock.addState(tmpTypeState);
			}
			typeAutomaton.addBlock(tmpTypeBlock);
		}
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions.
	 * 
	 * @param path - path to the file
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
	 * Provide a safe interface for iteration throng a list.
	 * @param <E>
	 * @param currList - list that is being evaluated 
	 * @return An empty list in case of {@code currList == null}, or {@code currList} otherwise.
	 */
	public static <E> List<E> safe( List<E> currList ) {
	    return currList == null ? Collections.EMPTY_LIST : currList;
	}

	/**
	 * Encoding the initial workflow input.
	 * @param program_inputs - input types for the program
	 * @param typeAutomaton 
	 * @param solutionLength 
	 * @param emptyType 
	 * @param mappings 
	 * @param allTypes 
	 * @return String representation of the initial input encoding.
	 */
	public static String encodeInputData(List<Types> program_inputs, TypeAutomaton typeAutomaton, int solutionLength, Type emptyType, AtomMapping mappings, AllTypes allTypes) {
		String encoding = "";

		List<TypeState> inputStates = typeAutomaton.getBlock(0).getTypeStates();
		for(int i=0; i < inputStates.size();i++) {
			if(i < program_inputs.size()) {
				List<Type> currTypes = program_inputs.get(i).getTypes();
				for(Type currType : currTypes) {
					if (allTypes.get(currType.getTypeID()) == null) {
						System.err.println("Program input '" + currType.getTypeID() + "' was not defined in the taxonomy.");
						return null;
					}
					encoding += mappings.add(currType.getPredicate(), inputStates.get(i).getStateName()) + " 0\n";
				}
			} else {
				encoding += mappings.add(emptyType.getPredicate(), inputStates.get(i).getStateName()) + " 0\n";
			}
			
		}
		return encoding;
	}
}
	
	
	
	
	
	
	
	
