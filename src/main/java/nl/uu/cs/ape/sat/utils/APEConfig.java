package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.models.enums.NodeType;

/**
 * The {@code APEConfig} (singleton) class is used to define the configuration
 * variables required for the proper execution of the library.
 * 
 * @author Vedran Kasalica
 *
 */
public class APEConfig {

	/**
	 * Tags used in the ape.config file
	 */
	private final String ONTOLOGY_TAG = "ontology_path";
	private final String ONTOLOGY_PREFIX = "ontologyPrexifIRI";
	private final String TOOL_ONTOLOGY_TAG = "toolsTaxonomyRoot";
	private final String DATA_ONTOLOGY_TAG = "dataTaxonomyRoot";
	private final String SUBONTOLOGY_TAG = "dataSubTaxonomyRoot";
	private final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";
	private final String CONSTRAINTS_TAG = "constraints_path";
	private final String SHARED_MEMORY_TAG = "shared_memory";
	private final String SOLUTION_PATH_TAG = "solutions_path";
	private final String SOLUTION_MIN_LENGTH_TAG = "solution_min_length";
	private final String SOLUTION_MAX_LENGTH_TAG = "solution_max_length";
	private final String MAX_NOSOLUTIONS_TAG = "max_solutions";
	private final String EXECUTIONSCRIPTS_FOLDER_TAG = "execution_scripts_folder";
	private final String NOEXECUTIONS_TAG = "number_of_execution_scripts";
	private final String SOLUTION_GRAPS_FOLDER_TAG = "solution_graphs_folder";
	private final String NO_GRAPHS_TAG = "number_of_generated_graphs";
	private final String PROGRAM_INPUTS_TAG = "inputs";
	private final String PROGRAM_OUTPUTS_TAG = "outputs";
	private final String USEWORKFLOW_INPUT = "use_workflow_input";
	private final String USE_ALL_GENERATED_DATA = "use_all_generated_data";
	private final String DEBUG_MODE_TAG = "debug_mode";

	/** Path to the taxonomy file */
	private String ontologyPath;
	/** Prefix used to define OWL class IDs */
	private String ontologyPrefixURI;
	/**
	 * Nodes in the ontology that correspond to the roots of module and data
	 * taxonomies.
	 */
	private String toolTaxonomyRoot, dataTaxonomyRoot;
	/**
	 * List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each respresents a data dimension (e.g. data type, data format, etc.).
	 */
	private List<String> dataTaxonomySubroots;

	/** Path to the XML file with all tool annotations. */
	private String toolAnnotationsPath;

	/** Path to the file with all workflow constraints. */
	private String constraintsPath;

	/**
	 * {@code true} if the shared memory structure should be used, {@code false} in
	 * case of a restrictive message passing structure.
	 */
	private Boolean sharedMemory;

	/**
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	private String solutionPath;

	/**
	 * Min and Max possible length of the solutions (length of the automaton). For
	 * no upper limit, max length should be set to 0.
	 */
	private Integer solutionMinLength, solutionMaxLength;

	/** Max number of solution that the solver will return. */
	private Integer maxNoSolutions;

	/**
	 * Path to the folder that will contain all the scripts generated based on the
	 * candidate workflows.
	 */
	private String executionScriptsFolder;
	/**
	 * Number of the workflow scripts that should be generated from candidate
	 * workflows. Default is 0.
	 */
	private Integer noExecutions;

	/**
	 * Path to the folder that will contain all the figures/graphs generated based
	 * on the candidate workflows.
	 */
	private String solutionGraphsFolder;
	/**
	 * Number of the solution graphs that should be generated from candidate
	 * workflows. Default is 0.
	 */
	private Integer noGraphs;

	/** Output branching factor (max number of outputs per tool). */
	private Integer maxNoTool_outputs = 3;

	/** Input branching factor (max number of inputs per tool). */
	private Integer maxNoToolInputs = 3;

	/** Input types of the workflow. */
	private List<DataInstance> programInputs;
	/** Output types of the workflow. */
	private List<DataInstance> program_outputs;

	/**
	 * Determines the required usage for the data instances that are given as
	 * workflow input:<br>
	 * {@link ConfigEnum#ALL} if all the workflow inputs have to be used,<br>
	 * {@link ConfigEnum#ONE} if one of the workflow inputs should be used or <br>
	 * {@link ConfigEnum#NONE} if none of the workflow inputs has to be used
	 */
	private ConfigEnum useWorkflowInput;
	/**
	 * Determines the required usage for the generated data instances:<br>
	 * {@link ConfigEnum#ALL} if all the generated data has to be used,<br>
	 * {@link ConfigEnum#ONE} if one of the data instances that are generated as
	 * output, per tool, has to be used or <br>
	 * {@link ConfigEnum#NONE} if none of the data instances is obligatory to use.
	 */
	private ConfigEnum useAllGeneratedData;
	/** {@code true} if debug mode is turned on. */
	private Boolean debugMode;

	/** Configurations used to read "ape.configuration" file. */
	private JSONObject coreConfiguration;
	
	/** Configurations used to describe the synthesis run. */
	private JSONObject runConfiguration;

	/**
	 * Initialize the configuration of the project.
	 * @throws IOException error in reading the configuration file
	 * @throws JSONException error in parsing the configuration file
	 */
	public APEConfig(String congifPath) throws IOException, JSONException {
		if (congifPath == null) {
			throw new IOException("The configuration file path is not provided correctly.");
		}
		
		dataTaxonomySubroots = new ArrayList<String>();
		programInputs = new ArrayList<DataInstance>(); 
		program_outputs = new ArrayList<DataInstance>();
		
		File file = new File(congifPath);

		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		coreConfiguration = new JSONObject(content);

		if(!coreConfigSetup()) {
			throw new JSONException("Core configuration failed.");
		}
	}
	
	/**
	 * Initialize the configuration of the project.
	 * @throws JSONException error in parsing the configuration object
	 */
	public APEConfig(JSONObject configObject) throws JSONException {
		if (configObject == null) {
			throw new JSONException("Core configuration error. The provided JSON object is null.");
		}
		
		dataTaxonomySubroots = new ArrayList<String>();
		programInputs = new ArrayList<DataInstance>(); 
		program_outputs = new ArrayList<DataInstance>();
		
		// Convert JSON string to JSONObject
		coreConfiguration = configObject;
		
		if(!coreConfigSetup()) {
			throw new JSONException("Core configuration failed.");
		}

	}

	/** Setup the configuration for the current run of the synthesis. */
	public boolean setupRunConfiguration(String congifPath) throws IOException, JSONException {
		if (congifPath == null) {
			throw new IOException("The configuration file path is not provided correctly.");
		}
		
		File file = new File(congifPath);
		String content = FileUtils.readFileToString(file, "utf-8");

		// Convert JSON string to JSONObject
		runConfiguration = new JSONObject(content);
		
		if(!runConfigSetup()) {
			throw new JSONException("Run configuration failed.");
		}
		return true;
	}
	
	/** Setup the configuration for the current run of the synthesis. */
	public boolean setupRunConfiguration(JSONObject configObject) throws JSONException {
		if (configObject == null) {
			throw new JSONException("Run configuration error. The provided JSON object is null.");
		}
		
		// Convert JSON string to JSONObject
		runConfiguration = configObject;
		
		if(!runConfigSetup()) {
			throw new JSONException("Run configuration failed.");
		}
		return true;
	}

	/**
	 * Setting up the core configuration of the library.
	 * 
	 * @return {@code true} if the method successfully set-up the configuration,
	 *         {@code false} otherwise.
	 */
	private boolean coreConfigSetup() {

		try {
			ontologyPath = coreConfiguration.getString(ONTOLOGY_TAG);
			if (!isValidConfigReadFile(ONTOLOGY_TAG, ontologyPath)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}
		try {
			ontologyPrefixURI = coreConfiguration.getString(ONTOLOGY_PREFIX);
		} catch (JSONException JSONException) {
			ontologyPrefixURI="";
		}
		try {
			toolTaxonomyRoot = APEUtils.createClassURI(coreConfiguration.getString(TOOL_ONTOLOGY_TAG), getOntologyPrefixURI());
			if (toolTaxonomyRoot == null || toolTaxonomyRoot == "") {
				System.err.println("Incorrect format of " + TOOL_ONTOLOGY_TAG + " tag in the config file.");
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + TOOL_ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.dataTaxonomyRoot = APEUtils.createClassURI(coreConfiguration.getString(DATA_ONTOLOGY_TAG), getOntologyPrefixURI());
			if (dataTaxonomyRoot == null || this.dataTaxonomyRoot == "") {
				System.err.println("Incorrect format of " + DATA_ONTOLOGY_TAG + " tag in the config file.");
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + DATA_ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			List<String> tmpDataSubontology = APEUtils.getListFromJson(coreConfiguration, SUBONTOLOGY_TAG, String.class);
			for (String subTaxonomy : tmpDataSubontology) {
				dataTaxonomySubroots.add(APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()));
			}
		} catch (JSONException JSONException) {
			/* Configuration does not have the type sub-ontology */
		}

		try {
			this.toolAnnotationsPath = coreConfiguration.getString(TOOL_ANNOTATIONS_TAG);
			if (!isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, this.toolAnnotationsPath)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err
					.println("Tag '" + TOOL_ANNOTATIONS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.solutionPath = coreConfiguration.getString(SOLUTION_PATH_TAG);
			if (!isValidConfigWriteFile(SOLUTION_PATH_TAG, this.solutionPath)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + SOLUTION_PATH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.executionScriptsFolder = coreConfiguration.getString(EXECUTIONSCRIPTS_FOLDER_TAG);
			if (!isValidConfigWriteFolder(EXECUTIONSCRIPTS_FOLDER_TAG, this.executionScriptsFolder)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + EXECUTIONSCRIPTS_FOLDER_TAG
					+ "' in the configuration file is not provided. Solution workflows will not be executable.");
			this.executionScriptsFolder = null;
		}

		try {
			this.solutionGraphsFolder = coreConfiguration.getString(SOLUTION_GRAPS_FOLDER_TAG);
			if (!isValidConfigWriteFolder(SOLUTION_GRAPS_FOLDER_TAG, this.solutionGraphsFolder)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + SOLUTION_GRAPS_FOLDER_TAG
					+ "' in the configuration file is not provided. Solution graphs will not be generated.");
			this.solutionGraphsFolder = null;
		}

		return true;
	}
	
	/**
	 * Setting up the core configuration of the library.
	 * 
	 * @return {@code true} if the method successfully set-up the configuration,
	 *         {@code false} otherwise.
	 */
	private boolean runConfigSetup() {

		try {
			this.constraintsPath = runConfiguration.getString(CONSTRAINTS_TAG);
			if (!isValidConfigReadFile(CONSTRAINTS_TAG, this.constraintsPath)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + CONSTRAINTS_TAG
					+ "' in the configuration file is not provided correctly. No constraints will be applied.");
			this.constraintsPath = null;
		}

		try {
			this.sharedMemory = runConfiguration.getBoolean(SHARED_MEMORY_TAG);
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + SHARED_MEMORY_TAG
					+ "' in the configuration file is not provided correctly. Default value is: true.");
			this.sharedMemory = true;
		}

		try {
			this.solutionMinLength = runConfiguration.getInt(SOLUTION_MIN_LENGTH_TAG);
			if (this.solutionMinLength < 1) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + SOLUTION_MIN_LENGTH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.solutionMaxLength = runConfiguration.getInt(SOLUTION_MAX_LENGTH_TAG);
			if (this.solutionMaxLength < 1) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + SOLUTION_MAX_LENGTH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		if (solutionMaxLength != 0 && solutionMaxLength < solutionMinLength) {
			System.err.println("MAX solution length cannot be smaller than MIN solution length.");
			return false;
		}

		try {
			this.maxNoSolutions = runConfiguration.getInt(MAX_NOSOLUTIONS_TAG);
			if (this.maxNoSolutions < 0) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err
					.println("Tag '" + MAX_NOSOLUTIONS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.noExecutions = runConfiguration.getInt(NOEXECUTIONS_TAG);
			if (this.noExecutions < 0) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + NOEXECUTIONS_TAG
					+ "' in the configuration file is not provided correctly. Default value is: 0.");
			this.noExecutions = 0;
		}

		try {
			this.noGraphs = runConfiguration.getInt(NO_GRAPHS_TAG);
			if (this.noExecutions < 0) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + NO_GRAPHS_TAG
					+ "' in the configuration file is not provided correctly. Default value is: 0.");
			this.noGraphs = 0;
		}
		programInputs.clear();
		try {
			for (JSONObject jsonModuleInput : APEUtils.getListFromJson(runConfiguration, PROGRAM_INPUTS_TAG, JSONObject.class)) {
				DataInstance input;
				if((input = getDataInstance(jsonModuleInput)) != null) {
					programInputs.add(input);
				}
			}
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + PROGRAM_INPUTS_TAG
					+ "' is not provided in the configuration file. Program will have no inputs.");
			programInputs.clear();
		}

		program_outputs.clear();
		try {
			for (JSONObject jsonModuleOutput : APEUtils.getListFromJson(runConfiguration, PROGRAM_OUTPUTS_TAG, JSONObject.class)) {
				DataInstance output;
				if((output = getDataInstance(jsonModuleOutput)) != null) {
					program_outputs.add(output);
				}
			}
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + PROGRAM_OUTPUTS_TAG
					+ "' is not provided in the configuration file. Program will have no outputs.");
			program_outputs.clear();
		}

		try {
			String tempUseWInput = runConfiguration.getString(USEWORKFLOW_INPUT);
			this.useWorkflowInput = isValidConfigEnum(USEWORKFLOW_INPUT, tempUseWInput);
			if (this.useWorkflowInput == null) {
				this.useWorkflowInput = ConfigEnum.ALL;
				System.out.println("Tag " + USEWORKFLOW_INPUT
						+ "' in the configuration file is not provided. Default value is: ALL.");
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + USEWORKFLOW_INPUT + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			String tempUseGenData = runConfiguration.getString(USE_ALL_GENERATED_DATA);
			this.useAllGeneratedData = isValidConfigEnum(USE_ALL_GENERATED_DATA, tempUseGenData);
			if (this.useWorkflowInput == null) {
				this.useWorkflowInput = ConfigEnum.ONE;
				System.out.println("Tag " + USE_ALL_GENERATED_DATA
						+ "' in the configuration file is not provided. Default value is: ONE.");
			}

		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + USE_ALL_GENERATED_DATA + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.debugMode = runConfiguration.getBoolean(DEBUG_MODE_TAG);
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + DEBUG_MODE_TAG
					+ "' in the configuration file is not provided correctly. Default value is: false.");
			this.debugMode = false;
		}

		return true;
	}
	
	private DataInstance getDataInstance(JSONObject jsonModuleInput) {
	DataInstance dataInstances = new DataInstance();
	for (String typeSuperClassLabel : jsonModuleInput.keySet()) {
		String typeSuperClassURI = APEUtils.createClassURI(typeSuperClassLabel, getOntologyPrefixURI());
		for (String currTypeLabel : APEUtils.getListFromJson(jsonModuleInput, typeSuperClassLabel, String.class)) {
			String currTypeURI = APEUtils.createClassURI(currTypeLabel, getOntologyPrefixURI());
			if (dataTaxonomySubroots.contains(typeSuperClassURI)) {
				dataInstances.addType(new Type(currTypeLabel, currTypeURI, typeSuperClassURI, NodeType.UNKNOWN));
			} else {
				System.err.println("Error in the configuration file. The data subtaxonomy '" + typeSuperClassLabel
						+ "' was not defined, but it was used as a root ot the input type '" + currTypeURI + "'.");
				return null;
			}
		}
	}

	if (!dataInstances.getTypes().isEmpty()) {
		return dataInstances;
	}
	
	return null;
	
	}

	/**
	 * @return the {@link #ontologyPath}
	 */
	public String getOntologyPath() {
		return ontologyPath;
	}
	
	/**
	 * @return the {@link #ontologyPrefixURI}
	 */
	public String getOntologyPrefixURI() {
		return (ontologyPrefixURI != null) ? ontologyPrefixURI : "";
	}

	/**
	 * @return the {@link #toolTaxonomyRoot}
	 */
	public String getToolTaxonomyRoot() {
		return toolTaxonomyRoot;
	}

	/**
	 * @return the {@link #dataTaxonomyRoot}
	 */
	public String getDataTaxonomyRoot() {
		return dataTaxonomyRoot;
	}

	/**
	 * @return the {@link #dataTaxonomySubroots}
	 */
	public List<String> getDataTaxonomySubroots() {
		return dataTaxonomySubroots;
	}

	/**
	 * @return the {@link #toolAnnotationsPath}
	 */
	public String getToolAnnotationsPath() {
		return toolAnnotationsPath;
	}

	/**
	 * @return the {@link #constraintsPath}
	 */
	public String getConstraintsPath() {
		return constraintsPath;
	}

	/**
	 * Returns {@code true} if the shared memory structure should be used, i.e. if the generated data is available in memory to all the tools used subsequently,
	 *  or {@code false} in case of a restrictive message passing structure, i.e. if the generated data is available only to the tool next in sequence..
	 * @return {@code true} if the shared memory structure should be used, {@code false} in
	 * case of a restrictive message passing structure.
	 */
	public Boolean getSharedMemory() {
		return sharedMemory;
	}

	/**
	 * @return the {@link #solutionPath}
	 */
	public String getSolutionPath() {
		return solutionPath;
	}

	/**
	 * @return the {@link #solutionMinLength}
	 */
	public Integer getSolutionMinLength() {
		return solutionMinLength;
	}

	/**
	 * @return the {@link #solutionMaxLength}
	 */
	public Integer getSolutionMaxLength() {
		return solutionMaxLength;
	}

	/**
	 * @return the {@link #maxNoSolutions}
	 */
	public Integer getMaxNoSolutions() {
		return maxNoSolutions;
	}

	/**
	 * @return the {@link #executionScriptsFolder}
	 */
	public String getExecutionScriptsFolder() {
		return executionScriptsFolder;
	}

	/**
	 * @return the {@link #noExecutions}
	 */
	public Integer getNoExecutions() {
		return noExecutions;
	}

	/**
	 * @return the {@link #solutionGraphsFolder}
	 */
	public String getSolutionGraphsFolder() {
		return solutionGraphsFolder;
	}

	/**
	 * @return the {@link #noGraphs}
	 */
	public Integer getNoGraphs() {
		return noGraphs;
	}

	/**
	 * @return the {@link #maxNoTool_outputs}
	 */
	public Integer getMaxNoToolOutputs() {
		return maxNoTool_outputs;
	}

	/**
	 * @return the {@link #maxNoToolInputs}
	 */
	public Integer getMaxNoToolInputs() {
		return maxNoToolInputs;
	}

	/**
	 * @return the {@link #programInputs}
	 */
	public List<DataInstance> getProgramInputs() {
		return programInputs;
	}

	/**
	 * @return the {@link #program_outputs}
	 */
	public List<DataInstance> getProgram_outputs() {
		return program_outputs;
	}

	/**
	 * @return the {@link #useWorkflowInput}
	 */
	public ConfigEnum getUseWorkflowInput() {
		return useWorkflowInput;
	}

	/**
	 * @return the {@link #useAllGeneratedData}
	 */
	public ConfigEnum getUseAllGeneratedData() {
		return useAllGeneratedData;
	}

	/**
	 * @return the {@link #debugMode}
	 */
	public Boolean getDebugMode() {
		return debugMode;
	}

	/**
	 * @return the {@link #configNode}
	 */
	public JSONObject getCoreConfigJsonObj() {
		return coreConfiguration;
	}
	
	/**
	 * @return the {@link #runConfiguration}
	 */
	public JSONObject getRunConfigJsonObj() {
		return runConfiguration;
	}

	/**
	 * Function that returns the tags that are used in the JSON files. Function
	 * can be used to rename the tags.
	 * 
	 * @param tag that is used
	 * @return
	 */
	public static String getJsonTags(String tag) {
		switch (tag) {
		case "id":
			return "id";
		case "label":
			return "label";
		case "inputs":
			return "inputs";
		case "taxonomyOperations":
			return "taxonomyOperations";
		case "outputs":
			return "outputs";
		case "implementation":
			return "implementation";
		case "code":
			return "code";
		default:
			return null;
		}
	}

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (f.isDirectory()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is a directory.");
			return false;
		} else {
			if (!f.getParentFile().isDirectory()) {
				System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a valid path.");
				return false;
			} else {
				if (!f.canWrite() && !f.getParentFile().canWrite()) {
					System.err.println(
							"Tag '" + tag + "':\nProvided path: \"" + path + "\" is missing the writing permission.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFolder(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isDirectory()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a directory.");
			return false;
		} else if (!f.canWrite()) {
			System.err
					.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is missing the writing permission.");
			return false;
		}
		return true;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists and can be read, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigReadFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isFile()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a file.");
			return false;
		} else {
			if (!f.canRead()) {
				System.err.println(
						"Tag '" + tag + "':\nProvided file: \"" + path + "\" is missing the reading permission.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided string represent an integer number, and
	 * return the number if it does. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag          - corresponding tag from the config file
	 * @param stringNumber - provided string
	 * @return Integer number represented with the string, {@code null} in case of a
	 *         bad String format.
	 */
	private static Integer isValidConfigInt(String tag, String stringNumber) {
		if (stringNumber == null || stringNumber == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else if (!StringUtils.isNumeric(stringNumber)) {
			System.err.println(
					"Tag '" + tag + "':\nProvided number: \"" + stringNumber + "\" is not in a correct format.");
			return null;
		}

		return Integer.parseInt(stringNumber);
	}

	/**
	 * Method checks whether the provided string represent a boolean value, and
	 * return the boolean if it does. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag        - corresponding tag from the config file
	 * @param stringBool - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	private static Boolean isValidConfigBoolean(String tag, String stringBool) {
		if (stringBool == null || stringBool == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else {
			Boolean boolVal = BooleanUtils.toBooleanObject(stringBool);
			if (boolVal == null) {
				System.err.println("Tag '" + tag + "':\nProvided boolean value: \"" + stringBool
						+ "\" is not in a correct format.");
				return null;
			} else {
				return boolVal;
			}
		}
	}

	/**
	 * Method checks whether the provided string represent an enumeration value
	 * ({@link ConfigEnum}), and return the {@link ConfigEnum} if it does. Method is
	 * tailored for verifying config file fields.
	 *
	 * @param tag        - corresponding tag from the config file
	 * @param stringEnum - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	private static ConfigEnum isValidConfigEnum(String tag, String stringEnum) {
		if (stringEnum == null || stringEnum == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else {
			if (stringEnum.toUpperCase().equals("ALL")) {
				return ConfigEnum.ALL;
			} else if (stringEnum.toUpperCase().equals("ONE")) {
				return ConfigEnum.ONE;
			} else if (stringEnum.toUpperCase().equals("NONE")) {
				return ConfigEnum.NONE;
			} else {
				System.err.println("Tag '" + tag + "':\nProvided boolean value: \"" + stringEnum
						+ "\" is not in a correct format.");
			}
		}
		return null;
	}

}
