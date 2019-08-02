package nl.uu.cs.ape.sat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.transformations.cnf.CNFConfig;
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

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.ModuleState;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.TypeState;
import nl.uu.cs.ape.sat.automaton.WorkflowElement;
import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.constraints.Constraint;
import nl.uu.cs.ape.sat.constraints.Constraint_depend_module;
import nl.uu.cs.ape.sat.constraints.Constraint_if_then_module;
import nl.uu.cs.ape.sat.constraints.Constraint_if_then_not_module;
import nl.uu.cs.ape.sat.constraints.Constraint_if_use_then_not_type;
import nl.uu.cs.ape.sat.constraints.Constraint_if_use_then_type;
import nl.uu.cs.ape.sat.constraints.Constraint_last_module;
import nl.uu.cs.ape.sat.constraints.Constraint_next_module;
import nl.uu.cs.ape.sat.constraints.Constraint_not_use_module;
import nl.uu.cs.ape.sat.constraints.Constraint_not_use_type;
import nl.uu.cs.ape.sat.constraints.Constraint_prev_module;
import nl.uu.cs.ape.sat.constraints.Constraint_use_module;
import nl.uu.cs.ape.sat.constraints.Constraint_use_type;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.SAT_solution;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.Types;

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
	public static String generateSLTLConstraints(String constraintsPath, ConstraintFactory allConsTemplates,
			AllModules allModules, AllTypes allTypes, AtomMapping mappings, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		String constraintID;
		int currRow = 1;
		List<String> parameters;
		for (String[] currConstr : getTuplesFromCSV(constraintsPath)) {
			currRow++;
			try {
				constraintID = currConstr[0];
			} catch (NumberFormatException e) {
				if (currConstr.length < 2) {
					System.err.println("There is an empty row in the file: " + constraintsPath);
				} else {
					System.err.println("Constraint ID provided: " + currConstr[0] + " is not Integer.");
				}
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
	 * Function used to provide SAT encoding of a constrain based on the constraint
	 * ID specified and provided parameters.
	 * 
	 * @param constraintID - ID of the constraint -
	 * @param parameters
	 * @return String representation of the SAT encoding for the specified
	 *         constraint.
	 */
	public static String constraintSATEncoding(String constraintID, String[] parameters,
			ConstraintFactory allConsTemplates, AllModules allModules, AllTypes allTypes,
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
			Module tmpModule = Module.moduleFromXML(xmlModule, allModules, allTypes);
			if(tmpModule != null) {
				modulesNew.add(tmpModule);
				allModules.addAnnotatedModule(tmpModule.getModuleID());
			}
			
		}

		return modulesNew;
	}

	/**
	 * Transforms the propositional formula into the CNF form.
	 * 
	 * @param propositionalFormula - propositional formula
	 * @return CNF representation of the formula
	 */
	public static String convert2CNF(String propositionalFormula, AtomMapping mappings) {
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);

		Formula formula;
		try {
			formula = p.parse(propositionalFormula.replace('-', '~'));
			final Formula cnf = formula.cnf();
			String transformedCNF = cnf.toString().replace('~', '-').replace(") & (", " 0\n").replace(" | ", " ")
					.replace("(", "").replace(")", "") + " 0\n";
			boolean exists = true;
			int counter = 0;
			String auxVariable = "";
			while (exists) {
				auxVariable = "@RESERVED_CNF_" + counter + " ";
				if (transformedCNF.contains("@RESERVED_CNF_")) {
					transformedCNF = transformedCNF.replace(auxVariable, mappings.getNextAuxNum() + " ");
				} else {
					exists = false;
				}
				counter++;
			}
			return transformedCNF;
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
	 * In case that the debug mode is on, print the constraint templates and tool
	 * and data taxonomy trees.
	 * 
	 * @param allModules         - set of all tools
	 * @param allTypes           - set of all data types
	 * @param constraintsFormats - String list of all constraint templates
	 */
	public static void debugPrintout(boolean debug, AllModules allModules, AllTypes allTypes,
			String constraintsFormats) {
		if (debug) {

			/*
			 * Printing the constraint templates
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tConstraint templates:");
			System.out.println("-------------------------------------------------------------");
			System.out.println(constraintsFormats + "\n");

			/*
			 * Printing the Module and Taxonomy Tree
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tTool Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			allModules.getRootModule().printTree(" ", allModules);
			System.out.println("\n-------------------------------------------------------------");
			System.out.println("\tData Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			allTypes.getRootType().printTree(" ", allTypes);
			System.out.println("-------------------------------------------------------------");
		}
	}

	/**
	 * Print header to specify the current workflow length that is being explored
	 */
	public static void printHeader(int solutionLength) {

		System.out.println("\n-------------------------------------------------------------");
		System.out.println("\tWorkflow discovery - length " + solutionLength);
		System.out.println("-------------------------------------------------------------");
	}

	/**
	 * Provide a safe interface for iteration trough a list.
	 * 
	 * @param          <E>
	 * @param currList - list that is being evaluated
	 * @return An empty list in case of {@code currList == null}, or
	 *         {@code currList} otherwise.
	 */
	public static <E> List<E> safe(List<E> currList) {
		return currList == null ? Collections.EMPTY_LIST : currList;
	}

	public static int countLinesNewFromString(String inputString) throws IOException {
		InputStream is = IOUtils.toInputStream(inputString, "UTF-8");
		try {
			byte[] c = new byte[1024];

			int readChars = is.read(c);
			if (readChars == -1) {
				// bail out if nothing to read
				return 0;
			}

			// make it easy for the optimizer to tune this loop
			int count = 0;
			while (readChars == 1024) {
				for (int i = 0; i < 1024;) {
					if (c[i++] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}

			// count remaining characters
			while (readChars != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
				readChars = is.read(c);
			}

			return count == 0 ? 1 : count;
		} finally {
			is.close();
		}
	}

	private static long timerStartTime = 0;

	public static void startTimer(Boolean debugMode) {
		if (debugMode) {
			timerStartTime = System.currentTimeMillis();
		} else {
			timerStartTime = -1;
		}

	}

	public static void restartTimerNPrint(String printString) {
		if(timerStartTime == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timerStartTime;
		System.out.println(printString + " setup time: " + (printTime / 1000F) + " sec.");
		timerStartTime = System.currentTimeMillis();
	}


}
