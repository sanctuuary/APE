package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.ConstraintTemplateData;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.logic.constructs.Atom;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The {@link APEUtils} class is used for storing {@code Static} methods.
 *
 * @author Vedran Kasalica
 */
public final class APEUtils {

	private final static String TOOLS_JSOM_TAG = "functions";
	private final static String CONSTR_JSON_TAG = "constraints";
	private final static String CONSTR_ID_TAG = "constraintid";
	private final static String CONSTR_PARAM_JSON_TAG = "parameters";
	private final static Map<String, Long> timers = new HashMap<String, Long>();

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private APEUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Method read the constraints from a JSON file and updates the
	 * {@link APEDomainSetup} object accordingly.
	 *
	 * @param constraintsPath Path to the constraint file.
	 * @param domainSetup     Object that represents the domain variables.
	 * @throws IOException  Error in handling the constraints file.
	 */
	public static void readConstraints(String constraintsPath, APEDomainSetup domainSetup) throws IOException {
		if (constraintsPath == null) {
			return;
		}
		String constraintID = null;
		int currNode = 0;
		List<TaxonomyPredicate> parameters;
		List<JSONObject> constraints = getListFromJson(constraintsPath, CONSTR_JSON_TAG);

		for (JSONObject jsonConstraint : safe(constraints)) {
			currNode++;
			/* READ THE CONSTRAINT */
			try {
				constraintID = jsonConstraint.getString(CONSTR_ID_TAG);

				List<JSONArray> jsonConstParam = getListFromJson(jsonConstraint, CONSTR_PARAM_JSON_TAG,
						JSONArray.class);
				parameters = new ArrayList<TaxonomyPredicate>();
				/* for each constraint parameter */
				for (JSONArray jsonParam : jsonConstParam) {
					SortedSet<TaxonomyPredicate> currParameter = new TreeSet<TaxonomyPredicate>();
					for (String paramLabel : getListFromJsonList(jsonParam, String.class)) {
						String paramURI = createClassURI(paramLabel, domainSetup.getOntologyPrefixURI());
						/* generate the corresponding ConstraintParameter object */
						TaxonomyPredicate currParamDimension = domainSetup.getAllModules().get(paramURI);
						if (currParamDimension == null) {
							currParamDimension = domainSetup.getAllTypes().get(paramURI);
						}
						if (currParamDimension == null) {
							System.err.println("Constraint parameter '" + paramURI + "' is not defined in the domain.");
							throw new JSONException("JSON constrains semnatics error.");
						} else {
							currParameter.add(currParamDimension);
						}
					}
					/*
					 * Generate an abstract term to generalize over the set of predicates that
					 * describe the parameter.
					 */
					TaxonomyPredicate absCurrParam = domainSetup.generateAuxiliaryPredicate(currParameter,
							LogicOperation.AND);
					parameters.add(absCurrParam);
				}
			} catch (JSONException e) {
				System.err.println("Error in file: " + constraintsPath + ", at constraint no: " + currNode + " ("
						+ constraintID + "). Bad format. Constraint skipped.");
				continue;
			}
			ConstraintTemplateData currConstr = domainSetup.getConstraintFactory()
					.addConstraintTemplateData(constraintID, parameters);
			if (parameters.stream().filter(predicate -> predicate == null).count() > 0) {
				System.err.println("Constraint argument does not exist in the tool taxonomy.");
			} else {
				domainSetup.addConstraintData(currConstr);
			}
		}
	}

	/**
	 * Encode ape constraints string.
	 *
	 * @param domainSetup     Domain information, including all the existing tools
	 *                        and types.
	 * @param mappings        Mapping function.
	 * @param moduleAutomaton Module automaton.
	 * @param typeAutomaton   Type automaton.
	 * @return The CNF representation of the SLTL constraints in our project.
	 */
	public static String encodeAPEConstraints(APEDomainSetup domainSetup, AtomMappings mappings,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String cnf_SLTL = "";
		int currConst = 0;

		for (ConstraintTemplateData constraint : domainSetup.getUnformattedConstr()) {
			currConst++;
			/* ENCODE THE CONSTRAINT */
			if (domainSetup.getConstraintTamplate(constraint.getConstraintID()) == null) {
				System.err.println("Constraint ID provided: '" + constraint.getConstraintID()
						+ "' is not valid. Constraint skipped.");
				continue;
			} else {
				String currConstrEncoding = constraintSATEncoding(constraint.getConstraintID(),
						constraint.getParameters(), domainSetup, moduleAutomaton, typeAutomaton, mappings);
				if (currConstrEncoding == null) {
					System.err
							.println("Error in constraint file. Constraint no: " + currConst + ". Constraint skipped.");
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
	 * @param constraintID    ID of the constraint.
	 * @param parameters      Parameters for the constraint template.
	 * @param domainSetup     Domain information, including all the existing tools
	 *                        and types.
	 * @param moduleAutomaton Module automaton.
	 * @param typeAutomaton   Type automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of the SAT encoding for the specified
	 *         constraint.
	 */
	public static String constraintSATEncoding(String constraintID, List<TaxonomyPredicate> parameters,
			APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMappings mappings) {
		String constraint = domainSetup.getConstraintTamplate(constraintID).getConstraint(parameters, domainSetup,
				moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

	/**
	 * Used to write the {@code text} to a file {@code file}. If @append is true,
	 * the {@code text} is appended to the {@code file}, otherwise the {@code file}
	 * is rewritten.
	 *
	 * @param text   Text that will be written in the file.
	 * @param file   The system-dependent file name.
	 * @param append If true, then bytes will be written to the end of the file
	 *               rather than the beginning.
	 * @return True if write to file was successful, false otherwise.
	 * @throws IOException Exception if file not found.
	 */
	public static boolean write2file(String text, File file, boolean append) throws IOException {
		FileWriter fw = new FileWriter(file, append);
		fw.write(text);
		fw.close();

		return true;
	}

	/**
	 * Updates the list of All Modules by annotating the existing ones (or adding
	 * non-existing) using the I/O DataInstance from the @file. Returns the list of
	 * Updated Modules.
	 *
	 * @param toolAnnotationsFile JSON file containing tool annotations.
	 * @param domainSetup         Domain information, including all the existing
	 *                            tools and types.
	 * @return The list of all annotated Modules in the process (possibly empty
	 *         list).
	 * @throws IOException  Error in handling a JSON file containing tool annotations.
	 */
	public static boolean readModuleJson(File toolAnnotationsFile, APEDomainSetup domainSetup)
			throws IOException {
		List<Module> modulesNew = new ArrayList<Module>();
		int currModule = 0;
		for (JSONObject jsonModule : safe(getListFromJson(toolAnnotationsFile, TOOLS_JSOM_TAG))) {
			currModule++;
			try {
				Module tmpModule = Module.moduleFromJson(jsonModule, domainSetup);
				if (tmpModule != null) {
					modulesNew.add(tmpModule);
				}
			} catch (JSONException e) {
				System.err.println(e.getMessage());
				System.err.println(
						"Error in file: " + toolAnnotationsFile + ", at tool no: " + currModule + ". Tool skipped.");
				continue;
			}
		}
		if (currModule == 0) {
			System.err.println("No tools were annotated.");
			return false;
		}
		return true;
	}

	/**
	 * Return the content of the file provided under the given path.
	 * 
	 * @param configPath Path to the text file.
	 * @return String representing the content of the file.
	 * @throws IOException Error if the the given file path is not correct or the
	 *                     file is corrupted.
	 */
	public static String getFileContent(String configPath) throws IOException {
		if (configPath == null) {
			throw new NullPointerException("The provided run configuration file path is null.");
		}

		File file = new File(configPath);
		return FileUtils.readFileToString(file, "utf-8");
	}

	/**
	 * Create the full class URI (ID) based on the label and the OWL prefix.
	 *
	 * @param label             Label of the current term.
	 * @param ontologyPrefixURI OWL prefix information.
	 * @return String representing full OWL class URI.
	 * @throws IllegalArgumentException Error if the given label is an empty String.
	 */
	public static String createClassURI(String label, String ontologyPrefixURI) throws IllegalArgumentException{
		if (label == null || label.equals("")) {
			throw new IllegalArgumentException("The OWL object label cannot be an empty String.");
		} else if (label.startsWith("http")) {
			return label;
		} else {
			return ontologyPrefixURI + label;
		}
	}

	/**
	 * Create the full set of class URI's (ID) based on the labels and the OWL
	 * prefix.
	 *
	 * @param taxonomyTerms     Tool labels.
	 * @param ontologyPrefixURI OWL prefix information.
	 * @return Set of strings representing full OWL class URI.
	 */
	public static Set<String> createURIsFromLabels(Set<String> taxonomyTerms, String ontologyPrefixURI) {
		Set<String> taxonomyTermURIs = new HashSet<>();
		for (String taxonomyTermLabel : taxonomyTerms) {
			taxonomyTermURIs.add(createClassURI(taxonomyTermLabel, ontologyPrefixURI));
		}
		return taxonomyTermURIs;
	}

	/*
	 * Transforms the propositional formula into the CNF form.
	 *
	 * @param propositionalFormula - propositional formula
	 * 
	 * @return CNF representation of the formula
	 */
//	public static String convert2CNF(String propositionalFormula, AtomMappings mappings) {
//		final FormulaFactory f = new FormulaFactory();
//		final PropositionalParser p = new PropositionalParser(f);
//
//		Formula formula;
//		try {
//			formula = p.parse(propositionalFormula.replace('-', '~'));
//			final Formula cnf = formula.cnf();
//			String transformedCNF = cnf.toString().replace('~', '-').replace(") & (", " 0\n").replace(" | ", " ")
//					.replace("(", "").replace(")", "") + " 0\n";
//			boolean exists = true;
//			int counter = 0;
//			String auxVariable = "";
//			while (exists) {
//				auxVariable = "@RESERVED_CNF_" + counter + " ";
//				if (transformedCNF.contains("@RESERVED_CNF_")) {
//					transformedCNF = transformedCNF.replace(auxVariable, mappings.getNextAuxNum() + " ");
//				} else {
//					exists = false;
//				}
//				counter++;
//			}
//			return transformedCNF;
//		} catch (ParserException e) {
//			e.printStackTrace();
//			return null;
//		}
//
//	}

	/**
	 * The method return a list of {@link JSONObject} elements that correspond to a
	 * given key in a Json file. If the key corresponds to a {@link JSONArray} all
	 * the elements are put in a {@link List}, otherwise if the key corresponds to a
	 * {@link JSONObject} list will contain only that object.
	 *
	 * @param jsonPath Path to the Json file.
	 * @param key      Key label that corresponds to the elements.
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 * @throws IOException Error if the the given file path is not correct or the file is corrupted.
	 */
	public static List<JSONObject> getListFromJson(String jsonPath, String key) throws IOException {
		return getListFromJson(new File(jsonPath), key);
	}

	/**
	 * The method return a list of {@link JSONObject} elements that correspond to a
	 * given key in a Json file. If the key corresponds to a {@link JSONArray} all
	 * the elements are put in a {@link List}, otherwise if the key corresponds to a
	 * {@link JSONObject} list will contain only that object.
	 *
	 * @param jsonFile File instance containing a json file.
	 * @param key      Key label that corresponds to the elements.
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 * @throws IOException Error in handling a JSON file.
	 */
	public static List<JSONObject> getListFromJson(File jsonFile, String key) throws IOException {
		String content = FileUtils.readFileToString(jsonFile, "utf-8");
		JSONObject jsonObject = new JSONObject(content);
		List<JSONObject> jsonArray = getListFromJson(jsonObject, key, JSONObject.class);

		return jsonArray;

	}

	/**
	 * The method return a list of {@code <T>} elements that correspond to a given
	 * key in the given json object. If the key corresponds to a {@link JSONArray}
	 * all the elements are put in a {@link List}, otherwise if the key corresponds
	 * to a {@code <T>} list will contain only that object.
	 *
	 * @param            <T> Class to which the elements should belong to.
	 * @param jsonObject {@link JSONObject} that is being explored.
	 * @param key        Key label that corresponds to the elements.
	 * @param clazz      Class to which the elements should belong to.
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 */
	public static <T> List<T> getListFromJson(JSONObject jsonObject, String key, Class<T> clazz) {
		List<T> jsonList = new ArrayList<T>();
		try {
			Object tmp = jsonObject.get(key);
			try {
				if (tmp instanceof JSONArray) {
					jsonList = getListFromJsonList((JSONArray) tmp, clazz);
				} else {
					T element = (T) tmp;
					jsonList.add(element);
				}
			} catch (JSONException e) {
				System.err.println("Json parsing error. Expected object '" + clazz.getSimpleName() + "' under the tag '"
						+ key + "'. The following object does not match the provided format:\n"
						+ jsonObject.toString());
				return jsonList;
			}
			return jsonList;
		} catch (JSONException e) {
			return jsonList;
		}

	}

	/**
	 * The method converts the {@link JSONArray} object to {@link List} of objects
	 * of the given structure.
	 *
	 * @param           <T> Class to which the elements should belong to.
	 * @param jsonArray JSON array object.
	 * @param _class    Class type that the elements of the array are.
	 * @return List of objects of type {@link T}.
	 */
	public static <T> List<T> getListFromJsonList(JSONArray jsonArray, Class<T> _class) {
		List<T> newList = new ArrayList<T>();
		for (int i = 0; i < jsonArray.length(); i++) {
			T element = (T) jsonArray.get(i);
			newList.add(element);
		}
		return newList;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions.
	 *
	 * @param path Path to the file.
	 * @return true if the file exists and can be read, false otherwise.
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
	 * Debug printout.
	 *
	 * @param debug       In case that the debug mode is on, print the constraint
	 *                    templates and tool and data taxonomy trees.
	 * @param domainSetup Domain information, including all the existing tools and
	 *                    types.
	 */
	public static void debugPrintout(boolean debug, APEDomainSetup domainSetup) {
		if (debug) {

			/*
			 * Printing the constraint templates
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tConstraint templates:");
			System.out.println("-------------------------------------------------------------");
			System.out.println(domainSetup.getConstraintFactory().printConstraintsCodes() + "\n");

			/*
			 * Printing the Module and Taxonomy Tree
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tTool Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			domainSetup.getAllModules().getRootPredicates().get(0).printTree(" ", domainSetup.getAllModules());
			System.out.println("\n-------------------------------------------------------------");
			System.out.println("\tData Taxonomy dimensions:");
			for (TaxonomyPredicate dimension : domainSetup.getAllTypes().getRootPredicates()) {
				System.out.println("\n-------------------------------------------------------------");
				System.out.println("\t" + dimension.getPredicateLabel() + "Taxonomy:");
				System.out.println("-------------------------------------------------------------");
				dimension.printTree(" ", domainSetup.getAllTypes());
			}

			/*
			 * Printing the tool annotations
			 */
			boolean noTools = true;
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tAnnotated tools:");
			System.out.println("-------------------------------------------------------------");
			for (TaxonomyPredicate module : domainSetup.getAllModules().getModules()) {
				if (module instanceof Module) {
					System.out.println(module.toString());
					noTools = false;
				}
			}
			if (noTools) {
				System.out.println("\tNo annotated tools.");
			}

			/*
			 * Print out the constraints
			 */
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tConstraints:");
			System.out.println("-------------------------------------------------------------");
			for (ConstraintTemplateData constr : domainSetup.getUnformattedConstr()) {
				System.out.println(domainSetup.getConstraintFactory().getDescription(constr));
			}
			if (domainSetup.getUnformattedConstr().isEmpty()) {
				System.out.println("\tNo constraints.");
			}
			System.out.println("-------------------------------------------------------------");
		}
	}

	/**
	 * Print header to illustrate the part of the synthesis that is being performed.
	 *
	 * @param argument TODO
	 * @param title    TODO
	 */
	public static void printHeader(Integer argument, String... title) {
		String arg = (argument == null) ? "" : (" " + argument);

		System.out.println("\n-------------------------------------------------------------");
		System.out.println("\t" + title[0] + arg);
		if (title.length > 1) {
			System.out.println("\t" + title[1] + arg);
		}
		System.out.println("-------------------------------------------------------------");
	}

	/**
	 * Provide a safe interface for iteration trough a list/set.
	 *
	 * @param          <E> Any type.
	 * @param currList List/set that is being evaluated.
	 * @return An empty list in case of {@code currList == null}, or
	 *         {@code currList} otherwise.
	 */
	public static <E> Collection<E> safe(Collection<E> currList) {
		return currList == null ? Collections.emptyList() : currList;
	}

	/**
	 * Provide a safe interface for getting an element from the list. In order to
	 * bypass "index out of bounds" error.
	 *
	 * @param          <E> Any type.
	 * @param currList List of elements.
	 * @param index    Index of the element that is to be returned.
	 * @return Element of the list, or null if the index is out of bounds.
	 */
	public static <E> E safeGet(List<E> currList, int index) {
		if (currList == null || index < 0 || currList.size() <= index) {
			return null;
		} else {
			return currList.get(index);
		}
	}

	/**
	 * Functions sets an element into the list at a specific place, if the element
	 * already exists, it overrides it. If the element is out of bound it creates
	 * null elements to fit the given size of the array and then adds the new
	 * element. If the index is negative number it does not change the array.
	 *
	 * @param         <E> Any type.
	 * @param list    List that is manipulated.
	 * @param index   Absolute position of the new element.
	 * @param element New element to be added to the list.
	 * @throws IndexOutOfBoundsException Exception if the index is out of range
	 *                                   (index &lt; 0).
	 */
	public static <E> void safeSet(List<E> list, int index, E element) {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (list.size() == index) {
			list.add(element);
		} else if (list.size() >= index) {
			list.set(index, element);
		}
		if (list.size() < index) {
			for (int i = list.size(); i < index; i++) {
				list.add(null);
			}
			list.add(element);
		}
	}

	/**
	 * Count number of new lines in a Sting.
	 *
	 * @param inputString String that is evaluated.
	 * @return Number of lines in the String.
	 * @throws IOException In case that the string.
	 */
	public static int countNewLines(String inputString) throws IOException {
		InputStream stream = IOUtils.toInputStream(inputString, "UTF-8");
		try {
			byte[] c = new byte[1024];

			int readChars = stream.read(c);
			if (readChars == -1) {
				// bail out if nothing to read
				return 1;
			}

			// make it easy for the optimizer to tune this loop
			int count = 0;
			while (readChars == 1024) {
				for (int i = 0; i < 1024;) {
					if (c[i++] == '\n') {
						++count;
					}
				}
				readChars = stream.read(c);
			}

			// count remaining characters
			while (readChars != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
				readChars = stream.read(c);
			}

			return count == 0 ? 1 : count;
		} finally {
			stream.close();
		}
	}

	/**
	 * Read the file to a String.
	 *
	 * @param path     Path to the file.
	 * @param encoding The charset encoding.
	 * @return File content as a String.
	 * @throws IOException Error while reading the file.
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Timer start.
	 *
	 * @param timerID   the timer id
	 * @param debugMode the debug mode
	 */
	public static void timerStart(String timerID, Boolean debugMode) {
		if (debugMode) {
			timers.put(timerID, System.currentTimeMillis());
		} else {
			timers.put(timerID, (long) -1);
		}
	}

	/**
	 * Timer restart and print.
	 *
	 * @param timerID     the timer id
	 * @param printString the print string
	 */
	public static void timerRestartAndPrint(String timerID, String printString) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println(printString + " setup time: " + (printTime / 1000F) + " sec.");
		timers.put(timerID, System.currentTimeMillis());
	}

	/**
	 * Timer print solutions.
	 *
	 * @param timerID        the timer id
	 * @param solutionsFound the solutions found
	 */
	public static void timerPrintSolutions(String timerID, int solutionsFound) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println(
				"\nAPE found " + solutionsFound + " solutions. Total solving time: " + (printTime / 1000F) + " sec.");
	}

	/**
	 * Timer print text.
	 *
	 * @param timerID the timer id
	 * @param text    the text
	 */
	public static void timerPrintText(String timerID, String text) {
		if (timers.get(timerID) == -1) {
			return;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println("\n" + text + " Running time: " + (printTime / 1000F) + " sec.");
	}

	/**
	 * Method converts tools annotated using 'bio.tools' standard (see <a href=
	 * "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 * API</a>), into standard supported by the APE library.
	 * <p>
	 * In practice, the method takes a {@link JSONArray} as an argument, where each
	 * {@link JSONObject} in the array represents a tool annotated using 'bio.tools'
	 * standard, and returns a {@link JSONObject} that represents tool annotations
	 * that can be used by the APE library.
	 *
	 * @param bioToolsAnnotation A {@link JSONArray} object, that contains list of
	 *                           annotated tools ({@link JSONObject}s) according the
	 *                           bio.tools specification (see <a href=
	 *                           "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 *                           API</a>)
	 * @return {@link JSONObject} that represents the tool annotation supported by
	 *         the APE library.
	 * @throws JSONException the json exception
	 */
	public static JSONObject convertBioTools2Ape(JSONArray bioToolsAnnotation) throws JSONException {
		JSONArray apeToolsAnnotations = new JSONArray();
		for (int i = 0; i < bioToolsAnnotation.length(); i++) {

			JSONObject bioJsonTool = bioToolsAnnotation.getJSONObject(i);
			List<JSONObject> functions = APEUtils.getListFromJson(bioJsonTool, "function", JSONObject.class);
			int functionNo = 1;
			for (JSONObject function : functions) {
				JSONObject apeJsonTool = new JSONObject();
				apeJsonTool.put("label", bioJsonTool.getString("name"));
				apeJsonTool.put("id", bioJsonTool.getString("biotoolsID") + functionNo++);

				JSONArray apeTaxonomyTerms = new JSONArray();

				JSONArray operations = function.getJSONArray("operation");
				for (int j = 0; j < operations.length(); j++) {
					JSONObject bioOperation = operations.getJSONObject(j);
					apeTaxonomyTerms.put(bioOperation.get("uri"));
				}
				apeJsonTool.put("taxonomyOperations", apeTaxonomyTerms);
//			reading inputs
				JSONArray apeInputs = new JSONArray();
				JSONArray bioInputs = function.getJSONArray("input");
//			for each input
				for (int j = 0; j < bioInputs.length(); j++) {
					JSONObject bioInput = bioInputs.getJSONObject(j);
					JSONObject apeInput = new JSONObject();
					JSONArray apeInputTypes = new JSONArray();
					JSONArray apeInputFormats = new JSONArray();
//				add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "data", JSONObject.class)) {
						apeInputTypes.put(bioType.getString("uri"));
					}
					apeInput.put("data_0006", apeInputTypes);
//				add all data formats (or just the first one)
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "format", JSONObject.class)) {
						apeInputFormats.put(bioType.getString("uri"));
					}
					apeInput.put("format_1915$OR$", apeInputFormats);

					apeInputs.put(apeInput);
				}
				apeJsonTool.put("inputs", apeInputs);

//			reading outputs
				JSONArray apeOutputs = new JSONArray();
				JSONArray bioOutputs = function.getJSONArray("output");
//			for each output
				for (int j = 0; j < bioOutputs.length(); j++) {

					JSONObject bioOutput = bioOutputs.getJSONObject(j);
					JSONObject apeOutput = new JSONObject();
					JSONArray apeOutputTypes = new JSONArray();
					JSONArray apeOutputFormats = new JSONArray();
//				add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "data", JSONObject.class)) {
						apeOutputTypes.put(bioType.getString("uri"));
					}
					apeOutput.put("data_0006", apeOutputTypes);
//				add all data formats
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "format", JSONObject.class)) {
						apeOutputFormats.put(bioType.getString("uri"));
					}
					apeOutput.put("format_1915$OR$", apeOutputFormats);

					apeOutputs.put(apeOutput);
				}
				apeJsonTool.put("outputs", apeOutputs);

				apeToolsAnnotations.put(apeJsonTool);
			}
		}

		return new JSONObject().put("functions", apeToolsAnnotations);
	}

	/**
	 * Convert cnf 2 human readable string.
	 *
	 * @param temp_sat_input the temp sat input
	 * @param mappings       the mappings
	 * @return the string
	 */
	public static String convertCNF2humanReadable(InputStream temp_sat_input, AtomMappings mappings) {
		StringBuffer humanReadable = new StringBuffer();
		Scanner scanner = new Scanner(temp_sat_input);
		scanner.nextLine();
		while (scanner.hasNextInt()) {
			int intAtom = scanner.nextInt();

			if (intAtom > 0) {
				Atom atom = mappings.findOriginal(intAtom);
				humanReadable = humanReadable.append(atom.getPredicate().getPredicateID()).append("(")
						.append(atom.getUsedInStateArgument().getPredicateID()).append(") ");
			} else if (intAtom < 0) {
				Atom atom = mappings.findOriginal(-intAtom);
				humanReadable = humanReadable.append("-").append(atom.getPredicate().getPredicateID()).append("(")
						.append(atom.getUsedInStateArgument().getPredicateID()).append(") ");
			} else {
				humanReadable = humanReadable.append("\n");
			}
		}
		scanner.close();

		return humanReadable.toString();
	}

	/**
	 * Return the string without its last character.
	 *
	 * @param str Given string.
	 * @return Given string without its last character.
	 */
	public static String removeLastChar(String str) {
		if (str != null && str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * Method creates a label based on the list of predicates and the logical
	 * operator.
	 *
	 * @param relatedPredicates List of predicates that should be used to create the
	 *                          new label.
	 * @param logicOp           Logical operator that configures the label.
	 * @return String representing a new label made based on the predicates and the
	 *         logical operator.
	 */
	public static String getLabelFromList(SortedSet<TaxonomyPredicate> relatedPredicates, LogicOperation logicOp) {
		StringBuilder abstractLabel = new StringBuilder(logicOp.toStringSign());
		for (TaxonomyPredicate label : relatedPredicates) {
			abstractLabel = abstractLabel.append(label.getPredicateLabel()).append(logicOp.toStringSign());
		}

		return abstractLabel.toString();
	}

	/**
	 * Print a warning to the console for the user to see.
	 *
	 * @param message The warning message.
	 * @param params additional parameters (uses String.format)
	 */
	public static void printWarning(String message, Object... params) {
		System.out.println("\u001B[35mWARNING: " + String.format(message, params) + "\u001B[0m");
	}


	private final static PrintStream original = System.err;
	private final static PrintStream nullStream = new PrintStream(new OutputStream() {
		@Override
		public void write(int b) { }
	});

	public static void disableErr(){
		System.setErr(nullStream);
	}

	public static void enableErr(){
		System.setErr(original);
	}
}
