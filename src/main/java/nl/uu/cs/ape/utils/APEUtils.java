package nl.uu.cs.ape.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.CNFClause;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtomVar;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.parser.SLTLxSATVisitor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The {@link APEUtils} class is used for storing {@code Static} methods.
 *
 * @author Vedran Kasalica
 */
public final class APEUtils {

	private final static Map<String, Long> timers = new HashMap<>();
	private final static PrintStream original = System.err;
	private final static PrintStream nullStream = new PrintStream(new OutputStream() {
		@Override
		public void write(int b) {
		}
	});

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private APEUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Encode ape constraints string.
	 * 
	 * @param synthesisEngine
	 *
	 * @param domainSetup     Domain information, including all the existing tools
	 *                        and types.
	 * @param mappings        Mapping function.
	 * @param moduleAutomaton Module automaton.
	 * @param typeAutomaton   Type automaton.
	 * @return The CNF representation of the SLTL constraints in our project.
	 */
	public static String encodeAPEConstraints(SATSynthesisEngine synthesisEngine, APEDomainSetup domainSetup,
			SATAtomMappings mappings,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		StringBuilder cnf_SLTL = new StringBuilder();
		int currConst = 0;

		for (ConstraintTemplateData constraint : domainSetup.getUnformattedConstr()) {
			currConst++;
			/* ENCODE THE CONSTRAINT */
			if (domainSetup.getConstraintTamplate(constraint.getConstraintID()) == null) {
				System.err.println("Constraint ID provided: '" + constraint.getConstraintID()
						+ "' is not valid. Constraint skipped.");
			} else {
				String currConstrEncoding = constraintSATEncoding(constraint.getConstraintID(),
						constraint.getParameters(), domainSetup, moduleAutomaton, typeAutomaton, mappings);
				if (currConstrEncoding == null) {
					System.err
							.println("Error in constraint file. Constraint no: " + currConst + ". Constraint skipped.");
				} else {
					cnf_SLTL.append(currConstrEncoding);
				}
			}
		}

		/*
		 * Parse the constraints specified in SLTLx.
		 */
		for (String constraint : domainSetup.getSLTLxConstraints()) {
			Set<SLTLxFormula> sltlxFormulas = SLTLxSATVisitor.parseFormula(synthesisEngine, constraint);
			for (SLTLxFormula sltlxFormula : sltlxFormulas) {
				sltlxFormula.getConstraintCNFEncoding(synthesisEngine)
						.forEach(sltlxString -> cnf_SLTL.append(sltlxString));
			}
		}

		return cnf_SLTL.toString();
	}

	/**
	 * Function used to provide SAT encoding of a constrain based on the constraint
	 * ID specified and provided parameters.
	 *
	 * @param constraintID    ID of the constraint.
	 * @param list            Parameters for the constraint template.
	 * @param domainSetup     Domain information, including all the existing tools
	 *                        and types.
	 * @param moduleAutomaton Module automaton.
	 * @param typeAutomaton   Type automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of the SAT encoding for the specified
	 *         constraint.
	 */
	public static String constraintSATEncoding(String constraintID, List<TaxonomyPredicate> list,
			APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			SATAtomMappings mappings) {

		return domainSetup.getConstraintTamplate(constraintID).getConstraint(list, domainSetup, moduleAutomaton,
				typeAutomaton, mappings);
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
	 * Reads the file and provides the JSONObject that represents its content.
	 *
	 * @param file the JSON file
	 * @return JSONObject representing the content of the file.
	 * @throws IOException   Error if the file is corrupted
	 * @throws JSONException Error if the file is not in expected JSON format
	 */
	public static JSONObject readFileToJSONObject(File file) throws IOException, JSONException {
		String content = FileUtils.readFileToString(file, "utf-8");
		return new JSONObject(content);
	}

	/**
	 * Reads the file and provides the JSONArray that represents its content.
	 *
	 * @param file the JSON file
	 * @return JSONArray representing the content of the file.
	 * @throws IOException   Error if the file is corrupted
	 * @throws JSONException Error if the file is not in expected JSON format
	 */
	public static JSONArray readFileToJSONArray(File file) throws IOException, JSONException {
		String content = FileUtils.readFileToString(file, "utf-8");
		return new JSONArray(content);
	}

	/*
	 * Transforms the propositional formula into the CNF form.
	 *
	 * @param propositionalFormula - propositional formula
	 *
	 * @return CNF representation of the formula
	 */
	// public static String convert2CNF(String propositionalFormula, SATAtomMappings
	// mappings) {
	// final FormulaFactory f = new FormulaFactory();
	// final PropositionalParser p = new PropositionalParser(f);
	//
	// Formula formula;
	// try {
	// formula = p.parse(propositionalFormula.replace('-', '~'));
	// final Formula cnf = formula.cnf();
	// String transformedCNF = cnf.toString().replace('~', '-').replace(") & (", "
	// 0\n").replace(" | ", " ")
	// .replace("(", "").replace(")", "") + " 0\n";
	// boolean exists = true;
	// int counterErrors = 0;
	// String auxVariable = "";
	// while (exists) {
	// auxVariable = "@RESERVED_CNF_" + counterErrors + " ";
	// if (transformedCNF.contains("@RESERVED_CNF_")) {
	// transformedCNF = transformedCNF.replace(auxVariable, mappings.getNextAuxNum()
	// + " ");
	// } else {
	// exists = false;
	// }
	// counterErrors++;
	// }
	// return transformedCNF;
	// } catch (ParserException e) {
	// e.printStackTrace();
	// return null;
	// }
	//
	// }

	/**
	 * Create the full class IRI (ID) based on the label and the OWL prefix.
	 *
	 * @param label             Label of the current term.
	 * @param ontologyPrefixIRI OWL prefix information.
	 * @return String representing full OWL class IRI.
	 * @throws IllegalArgumentException Error if the given label is an empty String.
	 */
	public static String createClassIRI(String label, String ontologyPrefixIRI) throws IllegalArgumentException {
		if (label == null || label.equals("")) {
			throw new IllegalArgumentException("The OWL object label cannot be an empty String.");
		} else if (label.startsWith("http")) {
			return label;
		} else {
			return ontologyPrefixIRI + label;
		}
	}

	/**
	 * Create the full set of class IRI's (ID) based on the labels and the OWL
	 * prefix.
	 *
	 * @param taxonomyTerms     Tool labels.
	 * @param ontologyPrefixIRI OWL prefix information.
	 * @return Set of strings representing full OWL class IRI.
	 */
	public static Set<String> createIRIsFromLabels(Set<String> taxonomyTerms, String ontologyPrefixIRI) {
		Set<String> taxonomyTermIRIs = new HashSet<>();
		for (String taxonomyTermLabel : taxonomyTerms) {
			taxonomyTermIRIs.add(createClassIRI(taxonomyTermLabel, ontologyPrefixIRI));
		}
		return taxonomyTermIRIs;
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
	public static List<JSONObject> getListFromJson(File jsonFile, String key) throws IOException, JSONException {
		String content = FileUtils.readFileToString(jsonFile, "utf-8");
		JSONObject jsonObject = new JSONObject(content);

		return getListFromJson(jsonObject, key, JSONObject.class);

	}

	/**
	 * The method return a list of {@code <T>} elements that correspond to a given
	 * key in the given json object. If the key corresponds to a {@link JSONArray}
	 * all the elements are put in a {@link List}, otherwise if the key corresponds
	 * to a {@code <T>} list will contain only that object.
	 *
	 * @param <T>        Class to which the elements should belong to.
	 * @param jsonObject {@link JSONObject} that is being explored.
	 * @param key        Key label that corresponds to the elements.
	 * @param clazz      Class to which the elements should belong to.
	 * @return List of elements that corresponds to the key. If the key does not
	 *         exists returns empty list.
	 */
	public static <T> List<T> getListFromJson(JSONObject jsonObject, String key, Class<T> clazz) {
		List<T> jsonList = new ArrayList<>();
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
	 * @param <T>       Class to which the elements should belong to.
	 * @param jsonArray JSON array object.
	 * @param clazz     Class type that the elements of the array are.
	 * @return List of objects of type {@link T}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getListFromJsonList(JSONArray jsonArray, Class<T> clazz) {
		List<T> newList = new ArrayList<>();
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
		if (path == null || path.equals("")) {
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
	 * @param runConfig   Configuration of the APE run.
	 * @param domainSetup Domain information, including all the existing tools and
	 *                    types.
	 */
	public static void debugPrintout(APERunConfig runConfig, APEDomainSetup domainSetup) {
		if (runConfig.getDebugMode()) {

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
			domainSetup.getAllModules().getRootModule().printTree(" ", domainSetup.getAllModules());
			System.out.println("\n-------------------------------------------------------------");
			System.out.println("\tData Taxonomy dimensions:");
			for (TaxonomyPredicate dimension : domainSetup.getAllTypes().getRootPredicates()) {
				System.out.println("\n-------------------------------------------------------------");
				System.out.println("\t" + dimension.getPredicateLabel() + "Taxonomy:");
				System.out.println("-------------------------------------------------------------");
				dimension.printTree(" ", domainSetup.getAllTypes());
			}
			System.out.println("-------------------------------------------------------------");
			System.out.println("\tLabels Taxonomy:");
			System.out.println("-------------------------------------------------------------");
			domainSetup.getAllTypes().getLabelRoot().printTree(" ", domainSetup.getAllTypes());

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

			int i = 1;
			for (Type input : runConfig.getProgramInputs()) {
				System.out.println((i++) + ". program input is " + input.toShortString());
			}
			System.out.println("-------------------------------------------------------------");
			i = 1;
			for (Type output : runConfig.getProgramOutputs()) {
				System.out.println((i++) + ". program output is " + output.toShortString());
			}
			System.out.println("-------------------------------------------------------------");
		}
	}

	/**
	 * Print header to illustrate the part of the synthesis that is being performed.
	 *
	 * @param argument Order number of the (sub)title.
	 * @param title    The mail content of the title.
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
	 * @param <E>      Any type.
	 * @param currList List/set that is being evaluated.
	 * @return An empty list in case of {@code currList == null}, or
	 *         {@code currList} otherwise.
	 */
	public static <E> Collection<E> safe(Collection<E> currList) {
		return currList == null ? Collections.emptyList() : currList;
	}

	/**
	 * Provide a safe interface for getting an element from a list. In order to
	 * bypass "index out of bounds" error.
	 *
	 * @param <E>      Any type.
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
	 * Functions sets an element into a list at a specific place, if the element
	 * already exists, it overrides it. If the element is out of bound it creates
	 * null elements to fit the given size of the array and then adds the new
	 * element. If the index is negative number it does not change the array.
	 *
	 * @param <E>     Any type.
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
		try (InputStream stream = IOUtils.toInputStream(inputString, "UTF-8")) {
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
	 * Timer start if in debug mode.
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

	public static long timerTimeLeft(String timerID, long timeout) {
		if (timers.get(timerID) == -1) {
			return 0;
		}

		long elapsedTimeMs = System.currentTimeMillis() - timers.get(timerID);
		long timeLeftMs = timeout - elapsedTimeMs;
		return timeLeftMs;

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

		// APEUtils.printMemoryStatus(true);
	}

	/**
	 * Timer print solutions.
	 *
	 * @param timerID        the timer id
	 * @param solutionsFound the solutions found
	 * @return The time counted by the timer.
	 */
	public static long timerPrintSolutions(String timerID, int solutionsFound) {
		if (timers.get(timerID) == -1) {
			return -1;
		}
		long printTime = System.currentTimeMillis() - timers.get(timerID);
		System.out.println("\n"
				+ "APE found " + solutionsFound + " solutions.\n"
				+ "Total APE runtime: \t\t" + (printTime / 1000F) + " sec.\n"
				+ "Total encoding time: \t\t" + (SATSynthesisEngine.encodingTime / 1000F) + " sec.\n"
				+ "Total SAT solving time: \t" + (SATSynthesisEngine.satSolvingTime / 1000F) + " sec.");
		System.out.println();
		return printTime;
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
				// reading inputs
				JSONArray apeInputs = new JSONArray();
				JSONArray bioInputs = function.getJSONArray("input");
				// for each input
				for (int j = 0; j < bioInputs.length(); j++) {
					JSONObject bioInput = bioInputs.getJSONObject(j);
					JSONObject apeInput = new JSONObject();
					JSONArray apeInputTypes = new JSONArray();
					JSONArray apeInputFormats = new JSONArray();
					// add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "data", JSONObject.class)) {
						apeInputTypes.put(bioType.getString("uri"));
					}
					apeInput.put("data_0006", apeInputTypes);
					// add all data formats (or just the first one)
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "format", JSONObject.class)) {
						apeInputFormats.put(bioType.getString("uri"));
					}
					apeInput.put("format_1915", apeInputFormats);

					apeInputs.put(apeInput);
				}
				apeJsonTool.put("inputs", apeInputs);

				// reading outputs
				JSONArray apeOutputs = new JSONArray();
				JSONArray bioOutputs = function.getJSONArray("output");
				// for each output
				for (int j = 0; j < bioOutputs.length(); j++) {

					JSONObject bioOutput = bioOutputs.getJSONObject(j);
					JSONObject apeOutput = new JSONObject();
					JSONArray apeOutputTypes = new JSONArray();
					JSONArray apeOutputFormats = new JSONArray();
					// add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "data", JSONObject.class)) {
						apeOutputTypes.put(bioType.getString("uri"));
					}
					apeOutput.put("data_0006", apeOutputTypes);
					// add all data formats
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "format", JSONObject.class)) {
						apeOutputFormats.put(bioType.getString("uri"));
					}
					apeOutput.put("format_1915", apeOutputFormats);

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
	public static String convertCNF2humanReadable(InputStream temp_sat_input, SATAtomMappings mappings) {
		StringBuilder humanReadable = new StringBuilder();
		Scanner scanner = new Scanner(temp_sat_input);
		scanner.nextLine();
		while (scanner.hasNextInt()) {
			int intAtom = scanner.nextInt();

			if (intAtom == 0) {
				humanReadable.append("\n");
			} else if (intAtom > -3 & intAtom < 3) {
				if (intAtom == 1 || intAtom == -2) {
					humanReadable.append("true ");
				} else {
					humanReadable.append("false ");
				}
			} else if (intAtom > 0) {
				SLTLxAtom atom = mappings.findOriginal(intAtom);
				if (atom == null) {
					SLTLxAtomVar varAtom = mappings.findOriginalVar(intAtom);
					humanReadable.append(varAtom.toString()).append(" ");
				} else {
					humanReadable.append(atom.toString()).append(" ");
				}

			} else if (intAtom < 0) {
				SLTLxAtom atom = mappings.findOriginal(-intAtom);
				if (atom == null) {
					SLTLxAtomVar varAtom = mappings.findOriginalVar(-intAtom);
					if (varAtom == null)
						System.out.println(intAtom);
					humanReadable.append("-").append(varAtom.toString()).append(" ");
				} else {
					humanReadable.append("-").append(atom.toString()).append(" ");
				}

			}
		}
		scanner.close();

		return humanReadable.toString();
	}

	public static void write2file(InputStream temp_sat_input, File file, Boolean append) throws IOException {
		StringBuilder humanReadable = new StringBuilder();
		Scanner scanner = new Scanner(temp_sat_input);

		while (scanner.hasNextLine()) {
			String str = scanner.nextLine();

			humanReadable.append(str).append("\n");
		}
		scanner.close();

		APEUtils.write2file(humanReadable.toString(), file, append);
	}

	/**
	 * Return the string without its last character.
	 *
	 * @param str Given string.
	 * @return A copy of the given string without its last character.
	 */
	public static String removeLastChar(String str) {
		if (str != null && str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * Return the string without its last N characters.
	 *
	 * @param str Given string.
	 * @param n   Number of characters to be removed.
	 * @return A copy of the given string without its last character.
	 */
	public static String removeNLastChar(String str, int n) {
		if (str != null && str.length() > 0) {
			str = str.substring(0, str.length() - n);
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
			abstractLabel.append(label.getPredicateLabel()).append(logicOp.toStringSign());
		}

		return abstractLabel.toString();
	}

	/**
	 * Print a warning to the console for the user to see.
	 *
	 * @param message The warning message.
	 * @param params  additional parameters (uses String.format)
	 */
	public static void printWarning(String message, Object... params) {
		System.out.println("\u001B[35mWARNING: " + String.format(message, params) + "\u001B[0m");
	}

	/**
	 * Disable System.err temporarily, enable again with {@link #enableErr}.
	 * 
	 */
	public static void disableErr() {
		System.setErr(nullStream);
	}

	/**
	 * Reset System.err to normal.
	 * 
	 */
	public static void enableErr() {
		System.setErr(original);
	}

	/**
	 * Clone the given JSON object
	 * 
	 * @param original - original JSON object
	 * @return copy of the original JSONObject.
	 */
	public static JSONObject clone(JSONObject original) {
		return new JSONObject(original, JSONObject.getNames(original));
	}

	/**
	 * Append text to the existing file. It adds the text at the end of the content
	 * of the file.
	 * 
	 * @param file    - existing file
	 * @param content - content that should be appended
	 * @throws IOException          in case of an I/O error
	 * @throws NullPointerException if the file is null
	 */
	public static void appendToFile(File file, String content) throws IOException, NullPointerException {
		Writer fileWriter = new FileWriterWithEncoding(file, "ASCII", true);
		BufferedWriter writer = new BufferedWriter(fileWriter, 8192 * 4);
		writer.write(content);
		writer.close();
	}

	/**
	 * Append text to the existing file. It adds the text at the end of the content
	 * of the file.
	 * 
	 * @param file    - existing file
	 * @param content - content that should be appended
	 * @throws IOException          in case of an I/O error
	 * @throws NullPointerException if the file is null
	 */
	public static void appendSetToFile(File file, Set<String> content) throws IOException, NullPointerException {
		Writer fileWriter = new FileWriterWithEncoding(file, "ASCII", true);
		BufferedWriter writer = new BufferedWriter(fileWriter, 8192 * 4);
		for (String str : content) {
			writer.write(str);
		}
		writer.close();
	}

	/**
	 * Append text to the existing file. It adds the text at the end of the content
	 * of the file.
	 * 
	 * @param file        - existing file
	 * @param cnfEncoding - cnf clauses that should be appended
	 * @throws IOException          in case of an I/O error
	 * @throws NullPointerException if the file is null
	 */
	public static void appendToFile(File file, Set<CNFClause> cnfEncoding) throws IOException, NullPointerException {
		StringBuilder string = new StringBuilder();
		cnfEncoding.forEach(clause -> {
			string.append(clause.toCNF());
		});
		Writer fileWriter = new FileWriterWithEncoding(file, "ASCII", true);
		BufferedWriter writer = new BufferedWriter(fileWriter, 8192 * 4);
		writer.write(string.toString());
		writer.close();
	}

	/**
	 * Prepend text to the existing file content and create a new file out of it.
	 * It adds the text at the beginning, before the existing content of the file.
	 * 
	 * @param file
	 * @param prefix
	 * @throws IOException
	 */
	public static File concatIntoFile(String prefix, File file) throws IOException {
		LineIterator li = FileUtils.lineIterator(file);
		File tempFile = File.createTempFile("prependPrefix", ".tmp");
		tempFile.deleteOnExit();
		Writer fileWriter = new FileWriterWithEncoding(tempFile, "ASCII", true);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		try {
			writer.write(prefix);
			while (li.hasNext()) {
				writer.write(li.next());
				writer.write("\n");
			}
		} finally {
			writer.close();
			li.close();
		}
		return tempFile;
	}

	public static int countLines(File cnfEncoding) {
		int lines = 0;
		try (BufferedReader b = new BufferedReader(new FileReader(cnfEncoding))) {
			while (b.readLine() != null) {
				lines++;

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * Visualises in the command line the memory status of the VM at the given step,
	 * if debug mode is on.
	 * 
	 * @param debugMode - true if debug mode is on
	 */
	public static void printMemoryStatus(boolean debugMode) {
		if (!debugMode) {
			return;
		}
		double currMemMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
		double totalMemMB = (Runtime.getRuntime().totalMemory()) / 1024 / 1024;
		double oneCharValueMB = 100;
		int currMemChars = (int) Math.ceil(currMemMB / oneCharValueMB);
		int totalMemChars = (int) Math.ceil(totalMemMB / oneCharValueMB);

		StringBuilder memoryStatus = new StringBuilder();
		IntStream.range(0, currMemChars).forEach(step -> memoryStatus.append("#"));
		IntStream.range(currMemChars, totalMemChars).forEach(step -> memoryStatus.append(" "));
		System.out.print("\n[" + memoryStatus.toString() + "]\t" + totalMemMB + "\tMB \r");

	}

	/**
	 * Get all unique pairs of PredicateLabels within the collection.
	 * 
	 * @param set - Set of PredicateLabel that should be used to create the pairs
	 * @return Set of unique pairs.
	 */
	public static Set<Pair<PredicateLabel>> getUniquePairs(Collection<? extends PredicateLabel> set) {
		Set<Pair<PredicateLabel>> pairs = new HashSet<Pair<PredicateLabel>>();
		set.stream().forEach(ele1 -> {
			set.stream().filter(ele2 -> ele1.compareTo(ele2) < 0)
					.forEach(ele2 -> {
						pairs.add(new Pair<PredicateLabel>(ele1, ele2));
					});
		});
		return pairs;
	}

	/**
	 * Get unique pairs of elements within 2 collections.
	 * 
	 * @param set1 - Set of elements that should be used to create the first
	 *             elements of the pairs
	 * @param set2 - Set of elements that should be used to create the second
	 *             elements of the pairs
	 * @return Set of unique pairs.
	 */
	public static <T> Set<Pair<T>> getUniquePairs(Collection<T> set1, Collection<T> set2) {
		Set<Pair<T>> pairs = new HashSet<Pair<T>>();
		set1.stream().forEach(ele1 -> {
			set2.stream().forEach(ele2 -> {
				pairs.add(new Pair<T>(ele1, ele2));
			});
		});
		return pairs;
	}

	/**
	 * Read file content from the given path (local path or a public URL) and return
	 * the content as a File object.
	 * 
	 * @param path - Local path or a public URL with the content.
	 * @return File containing info provided at the path.
	 * @throws IOException Exception in case of a badly formatted path or file.
	 */
	public static File readFileFromPath(String path) throws IOException {

		try {
			new URL(path).toURI();
			return readFileFromURL(path);
		} catch (MalformedURLException | URISyntaxException e1) {
			return new File(path);
		}

	}

	/**
	 * Read content from a URL and return it as a file.
	 * 
	 * @param file_url - URL of the content
	 * @return File containing info provided at the URL.
	 * @throws IOException Exception in case of a badly formatted URL or file.
	 */
	private static File readFileFromURL(String file_url) throws IOException {
		File loadedFile = new File(file_url);
		FileUtils.copyURLToFile(
				new URL(file_url),
				loadedFile,
				1000,
				1000);
		return loadedFile;
	}

}
