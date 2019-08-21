package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.Types;
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
	 * Singleton instance of the class.
	 */
	private static final APEConfig configAPE = new APEConfig();
	/**
	 * Tags used in the ape.config file
	 */
	private final String CONFIGURATION_FILE = "ape.configuration";
	private final String ONTOLOGY_TAG = "ontology_path";
	private final String TOOL_ONTOLOGY_TAG = "toolsTaxonomyRoot";
	private final String DATA_ONTOLOGY_TAG = "dataTaxonomyRoot";
	private final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";
	private final String CONSTRAINTS_TAG = "constraints_path";
	private final String SHARED_MEMORY_TAG = "shared_memory";
	private final String SOLUTION_PATH_TAG = "solutions_path";
	private final String SOLUTION_MIN_LENGTH_TAG = "solution_min_length";
	private final String SOLUTION_MAX_LENGTH_TAG = "solution_max_length";
	private final String MAX_NO_SOLUTIONS_TAG = "max_solutions";
	private final String EXECUTION_SCRIPTS_FOLDER_TAG = "execution_scripts_folder";
	private final String NO_EXECUTIONS_TAG = "number_of_execution_scripts";
	private final String PROGRAM_INPUTS_TAG = "inputs/input";
	private final String PROGRAM_OUTPUTS_TAG = "outputs/output";
	private final String USE_WORKFLOW_INPUT = "use_workflow_input";
	private final String USE_ALL_GENERATED_DATA = "use_all_generated_data";
	private final String DEBUG_MODE_TAG = "debug_mode";


	/** Path to the taxonomy file */
	private String ontology_path;

	/** Nodes in the ontology that correspond to the roots of module and data
	 * taxonomies. */
	private String tool_taxonomy_root, data_taxonomy_root;
	
	/** List of nodes in the ontology that correspond to the roots of data type and data format
	 * taxonomies. */
	private List<String> data_taxonomy_subroots = new ArrayList<String>();
	
	/** Path to the XML file with all tool annotations.*/
	private String tool_annotations_path;
	
	/** Path to the file with all workflow constraints. */
	private String constraints_path;
	
	/** {@code true} if the shared memory structure should be used, {@code false} in case
	 * of a restrictive pipeline structure. */
	private Boolean shared_memory;
	
	/** Path to the file that will contain all the solutions to the problem in human
	 * readable representation. */
	private String solution_path;

	/** Min and Max possible length of the solutions (length of the automaton). For no upper limit, max length should be set to 0. */
	private Integer solution_min_length, solution_max_length;

	/** Max number of solution that the solver will return.*/
	private Integer max_no_solutions;
	
	/** Path to the folder that will contain all the scripts generated based on 
	 * the candidate workflows. */
	private String execution_scripts_folder;
	
	/** Number of the workflow scripts that should be generated from candidate workflows.
	 * Default is 0. */
	private Integer no_executions;

	/** Output branching factor (max number of outputs per tool). */
	private Integer max_no_tool_outputs = 3;
	
	/** Input branching factor (max number of inputs per tool). */
	private Integer max_no_tool_inputs = 3;
	
	/** Input types of the workflow. */
	private List<Types> program_inputs;
	/** Output types of the workflow. */
	private List<Types> program_outputs;
	
	/** {@code true} if all the workflow inputs have to be used, otherwise usage of one is sufficient. */
	private Boolean use_workflow_input;
	/** {@code true} if all the generated data has to be used, otherwise usage of one of the outputs per tool is sufficient. */
	private Boolean use_all_generated_data;
	/** {@code true} if debug mode is turned on. */
	private Boolean debug_mode;
	
	/** Configurations used to read/update the "ape.configuration" file. */
	private Document document;
	private Node configNode;

	/**
	 * Initialize the configuration of the project.
	 */
	private APEConfig() {
		File inputFile = new File(CONFIGURATION_FILE);
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(inputFile);

			configNode = document.selectSingleNode("/configuration");

		} catch (DocumentException e) {
			System.err.println("Configuration file ./" + CONFIGURATION_FILE + " is not provided at location  or its format is corrupted.");
			
		}

	}

	/**
	 * Returns the singleton class representing the library configuration.
	 * 
	 * @return
	 */
	public static APEConfig getConfig() {
		return configAPE;
	}

	/**
	 * Setting up the configuration of the library.
	 * 
	 * @return {@code true} if the method successfully set-up the configuration,
	 *         {@code false} otherwise.
	 */
	public boolean defaultConfigSetup() {

		if(configNode.selectSingleNode(ONTOLOGY_TAG) != null)
		ontology_path = (configNode.selectSingleNode(ONTOLOGY_TAG) != null) ? configNode.selectSingleNode(ONTOLOGY_TAG).valueOf("@value") : null;
		if (!isValidConfigReadFile(ONTOLOGY_TAG, ontology_path)) {
			return false;
		}

		tool_taxonomy_root = (configNode.selectSingleNode(TOOL_ONTOLOGY_TAG) != null) ? configNode.selectSingleNode(TOOL_ONTOLOGY_TAG).valueOf("@value") : null;
		if (tool_taxonomy_root == null || tool_taxonomy_root == "") {
			System.err.println("Incorrect format of " + TOOL_ONTOLOGY_TAG + " tag in the config file.");
			return false;
		}

		Node dataTaxonomyNode = configNode.selectSingleNode(DATA_ONTOLOGY_TAG);
		if(dataTaxonomyNode == null) {
			System.err.println("Incorrect format of " + DATA_ONTOLOGY_TAG + " tag in the config file.");
			return false;
		}
		this.data_taxonomy_root = dataTaxonomyNode.valueOf("@value");
		if (data_taxonomy_root == null || this.data_taxonomy_root == "") {
			System.err.println("Incorrect format of " + DATA_ONTOLOGY_TAG + " tag in the config file.");
			return false;
		}
		
		for(Node dataSubRoots: dataTaxonomyNode.selectNodes("*")) {
			data_taxonomy_subroots.add(dataSubRoots.valueOf("@value"));
		}

		this.tool_annotations_path = (configNode.selectSingleNode(TOOL_ANNOTATIONS_TAG) != null) ? configNode.selectSingleNode(TOOL_ANNOTATIONS_TAG).valueOf("@value") : null;
		if (!isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, this.tool_annotations_path)) {
			return false;
		}
		
		this.constraints_path = (configNode.selectSingleNode(CONSTRAINTS_TAG) != null) ? configNode.selectSingleNode(CONSTRAINTS_TAG).valueOf("@value") : null;
		if (!isValidConfigReadFile(CONSTRAINTS_TAG, this.constraints_path)) {
			return false;
		}
		
		String tempSharedMem = (configNode.selectSingleNode(SHARED_MEMORY_TAG) != null) ? configNode.selectSingleNode(SHARED_MEMORY_TAG).valueOf("@value") : null;
		this.shared_memory = isValidConfigBoolean(SHARED_MEMORY_TAG,tempSharedMem);
		if (this.shared_memory == null) {
			return false;
		}

		this.solution_path = (configNode.selectSingleNode(SOLUTION_PATH_TAG) != null) ? configNode.selectSingleNode(SOLUTION_PATH_TAG).valueOf("@value") : null;
		if (!isValidConfigWriteFile(SOLUTION_PATH_TAG, this.solution_path)) {
			return false;
		}

		String tempMinLength = (configNode.selectSingleNode(SOLUTION_MIN_LENGTH_TAG) != null) ? configNode.selectSingleNode(SOLUTION_MIN_LENGTH_TAG).valueOf("@value") : null;
		this.solution_min_length = isValidConfigInt(SOLUTION_MIN_LENGTH_TAG,tempMinLength);
		if (this.solution_min_length == null) {
			return false;
		} else if(this.solution_min_length < 1) {
			this.solution_min_length = 1;
		}
		
		String tempMaxLength = (configNode.selectSingleNode(SOLUTION_MAX_LENGTH_TAG) != null) ? configNode.selectSingleNode(SOLUTION_MAX_LENGTH_TAG).valueOf("@value") : null;
		this.solution_max_length = isValidConfigInt(SOLUTION_MAX_LENGTH_TAG,tempMaxLength);
		if (this.solution_max_length == null) {
			return false;
		}
		
		if(solution_max_length != 0 && solution_max_length < solution_min_length) {
			System.err.println("MAX solution length cannot be smaller than MIN solution length.");
			return false;
		}

		String tempMaxSolutions = (configNode.selectSingleNode(MAX_NO_SOLUTIONS_TAG) != null) ? configNode.selectSingleNode(MAX_NO_SOLUTIONS_TAG).valueOf("@value") : null;
		this.max_no_solutions = isValidConfigInt(MAX_NO_SOLUTIONS_TAG,tempMaxSolutions);
		if (this.max_no_solutions == null) {
			return false;
		}

		this.execution_scripts_folder = (configNode.selectSingleNode(EXECUTION_SCRIPTS_FOLDER_TAG) != null) ? configNode.selectSingleNode(EXECUTION_SCRIPTS_FOLDER_TAG).valueOf("@value") : null;
		if (!isValidConfigWriteFolder(EXECUTION_SCRIPTS_FOLDER_TAG, this.execution_scripts_folder)) {
			return false;
		}
		
		String tempNoExecutions = (configNode.selectSingleNode(NO_EXECUTIONS_TAG) != null) ? configNode.selectSingleNode(NO_EXECUTIONS_TAG).valueOf("@value") : null;
		this.no_executions = isValidConfigInt(NO_EXECUTIONS_TAG,tempNoExecutions);
		if (this.no_executions == null) {
			return false;
		}
		
		List<Node> xmlModuleInput = configNode.selectNodes(PROGRAM_INPUTS_TAG);
		program_inputs = new ArrayList<Types>();

		for (Node xmlInput : APEUtils.safe(xmlModuleInput)) {
			if (xmlInput.hasContent()) {
				Types input = new Types();
				for (Node xmlType : xmlInput.selectNodes("*")) {
					input.addType(new Type(xmlType.getText(), xmlType.getText(), data_taxonomy_root, NodeType.UNKNOWN));
				}
				program_inputs.add(input);
			}
		}
		
		List<Node> xmlModuleOutput = configNode.selectNodes(PROGRAM_OUTPUTS_TAG);
		program_outputs = new ArrayList<Types>();

		for (Node xmlOutput : APEUtils.safe(xmlModuleOutput)) {
			if (xmlOutput.hasContent()) {
				Types output = new Types();
				for (Node xmlType : xmlOutput.selectNodes("*")) {
					output.addType(new Type(xmlType.getText(), xmlType.getText(), data_taxonomy_root, NodeType.UNKNOWN));
				}
				program_outputs.add(output);
			}
		}
		
		
		String tempUseWInput = (configNode.selectSingleNode(USE_WORKFLOW_INPUT) != null) ? configNode.selectSingleNode(USE_WORKFLOW_INPUT).valueOf("@value") : null;
		this.use_workflow_input = isValidConfigBoolean(USE_WORKFLOW_INPUT, tempUseWInput);
		if (this.use_workflow_input == null) {
			this.use_workflow_input = true;
			System.out.println("The default vaule for tag " + USE_WORKFLOW_INPUT + " is: TRUE.");
		}
		
		String tempUseAllGenInput = (configNode.selectSingleNode(USE_ALL_GENERATED_DATA) != null) ? configNode.selectSingleNode(USE_ALL_GENERATED_DATA).valueOf("@value") : null;
		this.use_all_generated_data = isValidConfigBoolean(USE_ALL_GENERATED_DATA, tempUseAllGenInput);
		if (this.use_all_generated_data == null) {
			this.use_all_generated_data = false;
			System.out.println("The default vaule for tag " + USE_ALL_GENERATED_DATA + " is: FALSE.");
		}
		
		String tempDebugMode = (configNode.selectSingleNode(DEBUG_MODE_TAG) != null) ? configNode.selectSingleNode(DEBUG_MODE_TAG).valueOf("@value") : null;
		this.debug_mode = isValidConfigBoolean(DEBUG_MODE_TAG, tempDebugMode);
		if (this.debug_mode == null) {
			this.debug_mode = false;
			System.out.println("The default vaule for tag " + DEBUG_MODE_TAG + " is: FALSE.");
		}
		
		return true;
	}

	
	

	/**
	 * @return the {@link #configAPE}
	 */
	public static APEConfig getConfigura() {
		return configAPE;
	}

	/**
	 *@return the {@link #ontology_path}
	 */
	public String getOntology_path() {
		return ontology_path;
	}

	/**
	 *@return the {@link #tool_taxonomy_root}
	 */
	public String getTool_taxonomy_root() {
		return tool_taxonomy_root;
	}

	/**
	 *@return the {@link #data_taxonomy_root}
	 */
	public String getData_taxonomy_root() {
		return data_taxonomy_root;
	}

	/**
	 *@return the {@link #data_taxonomy_subroots}
	 */
	public List<String> getData_taxonomy_subroots() {
		return data_taxonomy_subroots;
	}

	/**
	 *@return the {@link #tool_annotations_path}
	 */
	public String getTool_annotations_path() {
		return tool_annotations_path;
	}

	/**
	 *@return the {@link #constraints_path}
	 */
	public String getConstraints_path() {
		return constraints_path;
	}

	/**
	 *@return the {@link #shared_memory}
	 */
	public Boolean getShared_memory() {
		return shared_memory;
	}

	/**
	 *@return the {@link #solution_path}
	 */
	public String getSolution_path() {
		return solution_path;
	}

	/**
	 *@return the {@link #solution_min_length}
	 */
	public Integer getSolution_min_length() {
		return solution_min_length;
	}

	/**
	 *@return the {@link #solution_max_length}
	 */
	public Integer getSolution_max_length() {
		return solution_max_length;
	}

	/**
	 *@return the {@link #max_no_solutions}
	 */
	public Integer getMax_no_solutions() {
		return max_no_solutions;
	}

	/**
	 *@return the {@link #execution_scripts_folder}
	 */
	public String getExecution_scripts_folder() {
		return execution_scripts_folder;
	}

	/**
	 *@return the {@link #no_executions}
	 */
	public Integer getNo_executions() {
		return no_executions;
	}

	/**
	 *@return the {@link #max_no_tool_outputs}
	 */
	public Integer getMax_no_tool_outputs() {
		return max_no_tool_outputs;
	}

	/**
	 *@return the {@link #max_no_tool_inputs}
	 */
	public Integer getMax_no_tool_inputs() {
		return max_no_tool_inputs;
	}

	/**
	 *@return the {@link #program_inputs}
	 */
	public List<Types> getProgram_inputs() {
		return program_inputs;
	}

	/**
	 *@return the {@link #program_outputs}
	 */
	public List<Types> getProgram_outputs() {
		return program_outputs;
	}

	/**
	 *@return the {@link #use_workflow_input}
	 */
	public Boolean getUse_workflow_input() {
		return use_workflow_input;
	}

	/**
	 *@return the {@link #use_all_generated_data}
	 */
	public Boolean getUse_all_generated_data() {
		return use_all_generated_data;
	}

	/**
	 *@return the {@link #debug_mode}
	 */
	public Boolean getDebug_mode() {
		return debug_mode;
	}

	/**
	 *@return the {@link #document}
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 *@return the {@link #configNode}
	 */
	public Node getConfigNode() {
		return configNode;
	}

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag
	 *            - corresponding tag from the config file
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFile(String tag, String path) {
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
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag
	 *            - corresponding tag from the config file
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFolder(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag <" + tag + "> in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isDirectory()) {
			System.err.println("Tag <" + tag + ">:\nProvided path: \"" + path + "\" is not a directory.");
			return false;
		} else if (!f.canWrite()) {
					System.err.println(
							"Tag <" + tag + ">:\nProvided path: \"" + path + "\" is missing the writing permission.");
					return false;
				}
		return true;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag
	 *            - corresponding tag from the config file
	 * @param path
	 *            - path to the file
	 * @return {@code true} if the file exists and can be read, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigReadFile(String tag, String path) {
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
	 * Method checks whether the provided string represent an integer number, and
	 * return the number if it does.  Method is tailored for verifying config file fields.
	 * 
	 * @param stringNumber
	 *            - provided string
	 * @return Integer number represented with the string, {@code null} in case of a
	 *         bad String format.
	 */
	private static Integer isValidConfigInt(String tag, String stringNumber) {
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
	 * return the boolean if it does.  Method is tailored for verifying config file fields.
	 * 
	 * @param stringBool
	 *            - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	private static Boolean isValidConfigBoolean(String tag, String stringBool) {
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
