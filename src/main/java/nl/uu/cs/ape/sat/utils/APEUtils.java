package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * import org.logicng.formulas.Formula;
 * import org.logicng.formulas.FormulaFactory;
 * import org.logicng.io.parsers.ParserException;
 * import org.logicng.io.parsers.PropositionalParser;
 */
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.Types;
import nl.uu.cs.ape.sat.models.enums.NodeType;

/**
 * The {@code StaticFunctions} class is used for storing {@code Static} methods.
 * 
 * @author Vedran Kasalica
 *
 */
public class APEUtils {

	private final static String ROOT_TOOL_XML_path = "/functions/function";
	private final static String ROOT_CONSTR_XML_path = "/constraints/constraint";
	private final static String TOOLS_JSOM_TAG = "functions";
	private final static String CONSTR_JSON_TAG = "constraints";
	private final static String CONSTR_ID_TAG = "constraintid";
	private final static String CONSTR_PARAM_JSON_TAG = "parameters";
	private final static String PROGRAM_IO_TYPE_TAG = "type";
	private final static String PROGRAM_IO_FORMAT_TAG = "format";
	private final static Map<String, Long> timers = new HashMap<String, Long>();

	/**
	 * Returns the CNF representation of the SLTL constraints in our project
	 * 
	 * @param constraintsPath  - path to the Json file (xml files are only partially
	 *                         supported)
	 * @param allConsTemplates
	 * @param allModules
	 * @param allTypes
	 * @param mappings
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return
	 */
	public static String generateSLTLConstraints(String constraintsPath, ConstraintFactory allConsTemplates,
			AllModules allModules, AllTypes allTypes, AtomMapping mappings, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		String constraintID;
		int currNode = 0;
		boolean jsonFile;
		List<String> parameters;
		List<? extends Object> constraints;

		if (constraintsPath.toLowerCase().endsWith(".xml")) {
			System.err.println("Constraints file is XML format. It should be transformed into a JSON file.");
			jsonFile = false;
			constraints = getListFromXML(constraintsPath, ROOT_CONSTR_XML_path);
		} else {
			jsonFile = true;
			constraints = getListFromJson(constraintsPath, CONSTR_JSON_TAG);

		}

		for (Object constraint : constraints) {
			currNode++;
			/* READ THE CONSTRAINT */
			try {
				if (jsonFile) {
					JSONObject jsonConstraint = (JSONObject) constraint;
					constraintID = jsonConstraint.getString(CONSTR_ID_TAG);

					List<String> jsonConstParam = getListFromJson(jsonConstraint, CONSTR_PARAM_JSON_TAG, String.class);
					parameters = new ArrayList<String>();
					for (String jsonParam : jsonConstParam) {
						parameters.add(jsonParam.toString());
					}
				} else {
					Node xmlConstraint = (Node) constraint;
					constraintID = xmlConstraint.selectSingleNode(CONSTR_ID_TAG).getText();
					List<Node> xmlConstParam = xmlConstraint.selectNodes("parameters/parameter");
					parameters = new ArrayList<String>();
					for (Node xmlParam : xmlConstParam) {
						parameters.add(xmlParam.getText());
					}
				}
			} catch (Exception e) {
				System.err.println("Error in file: " + constraintsPath + ", at constraint no: " + currNode
						+ ". Constraint skipped.");
				continue;
			}
			/* ENCODE THE CONSTRAINT */
			if (allConsTemplates.getConstraintTamplate(constraintID) == null) {
				System.err.println("Constraint ID provided: '" + constraintID + "' is not valid. Constraint skipped.");
				continue;
			} else {
				String currConstrEncoding = constraintSATEncoding(constraintID,
						parameters.toArray(new String[parameters.size()]), allConsTemplates, allModules, allTypes,
						moduleAutomaton, typeAutomaton, mappings);
				if (currConstrEncoding == null) {
					System.err.println("Error in file: " + constraintsPath + ", at constraint no: " + currNode
							+ ". Constraint skipped.");
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

		for (Node xmlModule : getListFromXML(file, ROOT_TOOL_XML_path)) {
			Module tmpModule = Module.moduleFromXML(xmlModule, allModules, allTypes);
			if (tmpModule != null) {
				modulesNew.add(tmpModule);
				allModules.addAnnotatedModule(tmpModule.getPredicateID());
			}

		}

		return modulesNew;
	}

	/**
	 * Updates the list of All Modules by annotating the existing ones (or adding
	 * non-existing) using the I/O Types from the @file. Returns the list of Updated
	 * Modules.
	 * 
	 * @param file       - path to the .json file containing tool annotations
	 * @param allModules - list of all existing modules
	 * @param allTypes   - list of all existing types
	 * @return the list of all annotated Modules in the process (possibly empty
	 *         list)
	 */
	public static List<Module> readModuleJson(String file, AllModules allModules, AllTypes allTypes) {
		if (file.toLowerCase().endsWith(".xml")) {
			System.err.println("Tool annotation file is in XML format. It should be transformed into a JSON file.");
			return readModuleXML(file, allModules, allTypes);
		}
		List<Module> modulesNew = new ArrayList<Module>();
		int currModule = 0;
		for (JSONObject xmlModule : getListFromJson(file, TOOLS_JSOM_TAG)) {
			currModule++;
			try {
				Module tmpModule = Module.moduleFromJson(xmlModule, allModules, allTypes);
				if (tmpModule != null) {
					modulesNew.add(tmpModule);
					allModules.addAnnotatedModule(tmpModule.getPredicateID());
				}
			} catch (JSONException e) {
				System.err.println("Error in file: " + file + ", at tool no: " + currModule + ". Tool skipped.");
				continue;
			}
		}

		return modulesNew;
	}

	/**
	 * Transforms the propositional formula into the CNF form.
	 * 
	 * @param propositionalFormula - propositional formula
	 * @return CNF representation of the formula
	 * 
	 *         public static String convert2CNF(String propositionalFormula,
	 *         AtomMapping mappings) { final FormulaFactory f = new
	 *         FormulaFactory(); final PropositionalParser p = new
	 *         PropositionalParser(f);
	 * 
	 *         Formula formula; try { formula =
	 *         p.parse(propositionalFormula.replace('-', '~')); final Formula cnf =
	 *         formula.cnf(); String transformedCNF = cnf.toString().replace('~',
	 *         '-').replace(") & (", " 0\n").replace(" | ", " ") .replace("(",
	 *         "").replace(")", "") + " 0\n"; boolean exists = true; int counter =
	 *         0; String auxVariable = ""; while (exists) { auxVariable =
	 *         "@RESERVED_CNF_" + counter + " "; if
	 *         (transformedCNF.contains("@RESERVED_CNF_")) { transformedCNF =
	 *         transformedCNF.replace(auxVariable, mappings.getNextAuxNum() + " ");
	 *         } else { exists = false; } counter++; } return transformedCNF; }
	 *         catch (ParserException e) { e.printStackTrace(); return null; }
	 * 
	 *         }
	 */

	/**
	 * Get List of nodes that correspond to the elements of the structured XML file.
	 * 
	 * @param xmlPath     - path to the XML file
	 * @param rootXMLpath - root structure of the elements within the path.
	 * @return List of {@link Node} elements of the XML.
	 */
	public static List<Node> getListFromXML(String xmlPath, String rootXMLpath) {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(xmlPath);
			List<Node> functionList = document.selectNodes(rootXMLpath);
			return functionList;
		} catch (DocumentException e) {
			System.err.println("Error parsing the XML file: " + xmlPath);
			return null;
		}
	}

	/**
	 * The method return a list of {@link JSONObject} elements that correspond to a
	 * given key in a Json file. If the key corresponds to a {@link JSONArray} all
	 * the elements are put in a {@link List}, otherwise if the key corresponds to a
	 * {@link JSONObject} list will contain only that object.
	 * 
	 * @param jsonPath - path to the Json file
	 * @param key      - key label that corresponds to the elements
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 */
	public static List<JSONObject> getListFromJson(String jsonPath, String key) {
		try {
			String content = FileUtils.readFileToString(new File(jsonPath), "utf-8");
			// Convert JSON string to JSONObject
			JSONObject jsonObject = new JSONObject(content);

			List<JSONObject> jsonArray = getListFromJson(jsonObject, key, JSONObject.class);

			return jsonArray;

		} catch (IOException e1) {
			System.err.println("Error parsing the Json file: " + jsonPath);
			return null;
		}
	}

	/**
	 * The method return a list of {@link JSONObject} elements that correspond to a
	 * given key in the given json object. If the key corresponds to a
	 * {@link JSONArray} all the elements are put in a {@link List}, otherwise if
	 * the key corresponds to a {@link JSONObject} list will contain only that
	 * object.
	 * 
	 * @param jsonObject - Json object that is being explored
	 * @param key        - key label that corresponds to the elements
	 * @param clazz      - class to which the elements should belong to
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 */
	public static <T> List<T> getListFromJson(JSONObject jsonObject, String key, Class<T> clazz) {
		List<T> jsonList = new ArrayList<T>();
		try {
			Object tmp = jsonObject.get(key);
			try {
				if (tmp instanceof JSONArray) {
					JSONArray elements = (JSONArray) tmp;
					for (int i = 0; i < elements.length(); i++) {
						T element = (T) elements.get(i);
						jsonList.add(element);
					}
				} else {
					T element = (T) tmp;
					jsonList.add(element);
				}
			} catch (JSONException e) {
				System.err.println("Json parsing error. Expected object '" + clazz.getSimpleName() + "' under the tag '"
						+ key + "'. The followig object does not match the provided format:\n" + jsonObject.toString());
				return jsonList;
			}
			return jsonList;
		} catch (JSONException e) {
			return jsonList;
		}

	}

	/**
	 * Transform the {@link JSONArray} object to list of {@link JSONObject} objects.
	 * 
	 * @param jsonArray - {@link JSONArray} that should be transformed.
	 * @return {@link ArrayList} of {@link JSONObject} elements.
	 * 
	 *         public static List<JSONObject> fromJsonArray2JsonObjects(JSONArray
	 *         jsonArray) { List<JSONObject> jsonObjectList = new
	 *         ArrayList<JSONObject>();
	 * 
	 *         for(int i=0; i<jsonArray.length(); i++) {
	 *         jsonObjectList.add(jsonArray.getJSONObject(i)); } return
	 *         jsonObjectList; }
	 */
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
	 * TODO Read the list of inputs or outputs presented in json format and format
	 * them in a list of {@link Types}.
	 * 
	 * @param jsonListIO - {@link JSONArray} of the elements
	 * @return {@link List} of {@link Types}.
	 */
	public static List<Types> readModuleIO(JSONArray jsonListIO) throws JSONException {
		List<Types> listIO = new ArrayList<Types>();

		for (int i = 0; i < jsonListIO.length(); i++) {
			JSONObject moduleIO = jsonListIO.getJSONObject(i);

			Types output = new Types();
			try {
				List<JSONObject> jsonIOTypes = getListFromJson(moduleIO, PROGRAM_IO_TYPE_TAG, JSONObject.class);
				for (JSONObject jsonIOType : jsonIOTypes) {
					String jsonOutputType = moduleIO.getString(PROGRAM_IO_TYPE_TAG);
					output.addType(new Type(jsonOutputType, jsonOutputType,
							APEConfig.getConfig().getData_taxonomy_root(), NodeType.UNKNOWN));
				}
			} catch (JSONException JSONException) {
				/* Configuration output does not have the type */}
			try {
				String jsonOutputFormat = moduleIO.getString(PROGRAM_IO_FORMAT_TAG);
				output.addType(new Type(jsonOutputFormat, jsonOutputFormat,
						APEConfig.getConfig().getData_taxonomy_root(), NodeType.UNKNOWN));
			} catch (JSONException JSONException) {
				/* Configuration output does not have the type */}
			if (!output.getTypes().isEmpty()) {
				listIO.add(output);
			}
		}

		return listIO;
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
//			allTypes.getTypes().get("Lattice").printTree(" ", allTypes);
			System.out.println("-------------------------------------------------------------");
		}
	}

	/**
	 * Print header to illustrate the part of the synthesis that is being performed.
	 */
	public static void printHeader(Integer argument, String... title) {
		String arg = (argument == null) ? "" : (" " + argument);

		System.out.println("\n-------------------------------------------------------------");
		System.out.println("\t" + title[0] + arg);
		if(title.length > 1) {
			System.out.println("\t" + title[1] + arg);
		}
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

	/**
	 * Provide a safe interface for getting an element from the list. In order to
	 * bypass "index out of bounds" error.
	 * 
	 * @param currList - list of elements
	 * @param index    - index of the element that is to be returned
	 * @return Element of the list, or null if the index is out of bounds.
	 */
	public static <E> E safeGet(List<E> currList, int index) {
		if (currList == null || index < 0 || currList.size() <= index) {
			return null;
		} else {
			return currList.get(index);
		}
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

	public static void timerStart(String timerID, Boolean debugMode) {
		if (debugMode) {
			timers.put(timerID, System.currentTimeMillis());
		} else {
			timers.put(timerID, (long) -1);
		}

	}

	public static void timerRestartAndPrint(String timerID, String printString) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println(printString + " setup time: " + (printTime / 1000F) + " sec.");
		timers.put(timerID, System.currentTimeMillis());
	}

	public static void timerPrintSolutions(String timerID, int solutionsFound) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println(
				"\nAPE found " + solutionsFound + " solutions. Total solving time: " + (printTime / 1000F) + " sec.");
	}

	public static void timerPrintText(String timerID, String text) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println("\n" + text + " Running time: " + (printTime / 1000F) + " sec.");
	}

}
