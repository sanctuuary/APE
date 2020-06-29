package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The {@link APERunConfig} class is used to define the run configuration
 * variables, required for the proper execution of the synthesis process.
 *
 * @author Vedran Kasalica
 */
public class APERunConfig {

    /**
     * Tags used in the JSON file.
     */
    private static final String CONSTRAINTS_TAG = "constraints_path";
    private static final String SHARED_MEMORY_TAG = "shared_memory";
    private static final String SOLUTION_PATH_TAG = "solutions_path";
    private static final String SOLUTION_MIN_LENGTH_TAG = "solution_min_length";
    private static final String SOLUTION_MAX_LENGTH_TAG = "solution_max_length";
    private static final String MAX_NOSOLUTIONS_TAG = "max_solutions";
    private static final String EXECUTIONSCRIPTS_FOLDER_TAG = "execution_scripts_folder";
    private static final String NOEXECUTIONS_TAG = "number_of_execution_scripts";
    private static final String SOLUTION_GRAPHS_FOLDER_TAG = "solution_graphs_folder";
    private static final String NO_GRAPHS_TAG = "number_of_generated_graphs";
    private static final String PROGRAM_INPUTS_TAG = "inputs";
    private static final String PROGRAM_OUTPUTS_TAG = "outputs";
    private static final String USEWORKFLOW_INPUT = "use_workflow_input";
    private static final String USE_ALL_GENERATED_DATA = "use_all_generated_data";
    private static final String DEBUG_MODE_TAG = "debug_mode";
    private static final String TOOL_SEQ_REPEAT = "tool_seq_repeat";

    /**
     * Tags separated in the categories: obligatory, optional, core and run.
     * The obligatory tags are used in the constructor to check the presence of tags.
     * Optional tags or All tags are mostly used by test cases.
     */
    private static final String[] obligatoryRunTags = new String[]{
            SOLUTION_MIN_LENGTH_TAG,
            SOLUTION_MAX_LENGTH_TAG,
            MAX_NOSOLUTIONS_TAG
           
    };
    private static final String[] optionalRunTags = new String[]{
    		PROGRAM_INPUTS_TAG,
            PROGRAM_OUTPUTS_TAG,
            CONSTRAINTS_TAG,
            SHARED_MEMORY_TAG,
            NOEXECUTIONS_TAG,
            NO_GRAPHS_TAG,
            USEWORKFLOW_INPUT,
            USE_ALL_GENERATED_DATA,
            DEBUG_MODE_TAG,
            TOOL_SEQ_REPEAT,
            SOLUTION_PATH_TAG,
            EXECUTIONSCRIPTS_FOLDER_TAG,
            SOLUTION_GRAPHS_FOLDER_TAG
    };

    /**
     * READ and WRITE enums used to verify paths.
     */
    private enum Permission {READ, WRITE}

    /**
     * Path to the file with all workflow constraints.
     */
    private String constraintsPath = null;
    /**
     * true if the shared memory structure should be used, false in
     * case of a restrictive message passing structure.
     */
    private Boolean sharedMemory = true;
    /**
     * false iff the provided solutions should be distinguished 
     * based on the tool sequences alone, i.e. tool sequences cannot repeat, 
     * ignoring the types in the solutions.
     */
    private Boolean toolSeqRepeat = true;
    /**
     * Path to the file that will contain all the solutions to the problem in human
     * readable representation.
     */
    private String solutionPath = null;
    /**
     * Min and Max possible length of the solutions (length of the automaton). For
     * no upper limit, max length should be set to 0.
     */
    private Integer solutionMinLength, solutionMaxLength;
    /**
     * Max number of solution that the solver will return.
     */
    private Integer maxNoSolutions;
    /**
     * Path to the folder that will contain all the scripts generated based on the
     * candidate workflows.
     */
    private String executionScriptsFolder = null;
    /**
     * Number of the workflow scripts that should be generated from candidate
     * workflows. Default is 0.
     */
    private Integer noExecutions = 0;
    /**
     * Path to the folder that will contain all the figures/graphs generated based
     * on the candidate workflows.
     */
    private String solutionGraphsFolder = null;
    /**
     * Number of the solution graphs that should be generated from candidate
     * workflows. Default is 0.
     */
    private Integer noGraphs = 0;
    /**
     * Input types of the workflow.
     */
    private List<DataInstance> programInputs = new ArrayList<>();
    /**
     * Output types of the workflow.
     */
    private List<DataInstance> programOutputs = new ArrayList<>();
    /**
     * Determines the required usage for the data instances that are given as
     * workflow input:<br>
     * {@link ConfigEnum#ALL} if all the workflow inputs have to be used,<br>
     * {@link ConfigEnum#ONE} if one of the workflow inputs should be used or <br>
     * {@link ConfigEnum#NONE} if none of the workflow inputs has to be used
     */
    private ConfigEnum useWorkflowInput = ConfigEnum.ALL;
    /**
     * Determines the required usage for the generated data instances:<br>
     * {@link ConfigEnum#ALL} if all the generated data has to be used,<br>
     * {@link ConfigEnum#ONE} if one of the data instances that are generated as
     * output, per tool, has to be used or <br>
     * {@link ConfigEnum#NONE} if none of the data instances is obligatory to use.
     */
    private ConfigEnum useAllGeneratedData  = ConfigEnum.ONE;
    /**
     * Mode is true if debug mode is turned on.
     */
    private Boolean debugMode = false;
    /**
     * Object containing domain information needed for the execution.
     */
    private final APEDomainSetup apeDomainSetup;
    
    
    
    
    public APERunConfig(APEDomainSetup apeDomainSetup, Integer solutionMinLength, Integer solutionMaxLength, Integer maxNoSolutions) {
    	this.apeDomainSetup = apeDomainSetup;
    	this.solutionMinLength = solutionMinLength;
		this.solutionMaxLength = solutionMaxLength;
		this.maxNoSolutions = maxNoSolutions;
		
	}


    /**
     * Setup the configuration for the current run of the synthesis.
     *
     * @param configObject   The APE configuration JSONObject{@link JSONObject}.
     * @param apeDomainSetup the ape domain setup
     * @return the run configuration
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APERunConfig(JSONObject runConfiguration, APEDomainSetup apeDomainSetup) throws IOException, JSONException, APEConfigException {
        this.apeDomainSetup = apeDomainSetup;

        /* JSONObject must have been parsed correctly. */
        if (runConfiguration == null) {
            throw new APEConfigException("Cannot set up the run configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }

        /* Make sure all required core tags are present. This way, the rest of the method does not have to check the presence of the tag. */
        for (String tag : getObligatoryRunTags()) {
            if (!runConfiguration.has(tag)) {
                throw APEConfigException.missingTag(tag);
            }
        }
        
        /* Path to the solution directory. */
        this.solutionPath = readFilesDirectoryPath(SOLUTION_PATH_TAG, runConfiguration, Permission.WRITE);

        /* Path to the output script directory. */
        this.executionScriptsFolder = readDirectoryPath(EXECUTIONSCRIPTS_FOLDER_TAG, runConfiguration, Permission.WRITE);

        /* Path to the output graph directory. */
        this.solutionGraphsFolder = readDirectoryPath(SOLUTION_GRAPHS_FOLDER_TAG, runConfiguration, Permission.WRITE);

        /* Path to the JSON constraints file. */
        if (runConfiguration.has(CONSTRAINTS_TAG)) {
            this.constraintsPath = readFilePath(CONSTRAINTS_TAG, runConfiguration, Permission.READ);
        } else {
            APEUtils.printWarning("Tag '" + CONSTRAINTS_TAG + "' in the configuration file is not provided. No constraints will be applied.");
        }

        /* Read shared memory tag. */
        this.sharedMemory = readBooleanOrDefault(SHARED_MEMORY_TAG, runConfiguration, true);

        /* Read solutions filtering tag. */
        this.toolSeqRepeat = readBooleanOrDefault(TOOL_SEQ_REPEAT, runConfiguration, true);
        
        /* Minimal length of the solution must be greater or equal to 1. */
        this.solutionMinLength = runConfiguration.getInt(SOLUTION_MIN_LENGTH_TAG);
        if (solutionMinLength < 1) {
            throw APEConfigException.invalidValue(SOLUTION_MIN_LENGTH_TAG, solutionMinLength, "use a numeric value greater or equal to 1.");
        }

        /* Maximum length of the solution must be greater or equal to 1. */
        this.solutionMaxLength = runConfiguration.getInt(SOLUTION_MAX_LENGTH_TAG);
        if (this.solutionMaxLength < 1) {
            throw APEConfigException.invalidValue(SOLUTION_MAX_LENGTH_TAG, solutionMaxLength, "use a numeric value greater or equal to 1.");
        }

        /* Check MIN and MAX solution length. */
        if (solutionMaxLength < solutionMinLength) {
            throw APEConfigException.invalidValue(SOLUTION_MAX_LENGTH_TAG, solutionMaxLength, String.format("MAX solution length cannot be smaller than MIN solution length (%s).", solutionMinLength));
        }

        /* Maximum number of generated solutions. */
        this.maxNoSolutions = runConfiguration.getInt(MAX_NOSOLUTIONS_TAG);
        if (this.maxNoSolutions < 0) {
            throw APEConfigException.invalidValue(MAX_NOSOLUTIONS_TAG, maxNoSolutions, "use a numeric value greater or equal to 0.");
        }

        /* Number of execution scripts generated from the solutions. */
        this.noExecutions = readIntegerOrDefault(NOEXECUTIONS_TAG, runConfiguration, 0);
        if (this.noExecutions < 0) {
            throw APEConfigException.invalidValue(NOEXECUTIONS_TAG, this.noExecutions, "use a numeric value greater or equal to 0.");
        }

        /* Number of graphs generated from the solutions. */
        this.noGraphs = readIntegerOrDefault(NO_GRAPHS_TAG, runConfiguration, 0);
        if (this.noGraphs < 0) {
            throw APEConfigException.invalidValue(NO_GRAPHS_TAG, this.noGraphs, "use a numeric value greater or equal to 0.");
        }

        /* Parse the input and output DataInstances of the program*/
        this.programInputs = getDataInstances(PROGRAM_INPUTS_TAG, runConfiguration);
        this.programOutputs = getDataInstances(PROGRAM_OUTPUTS_TAG, runConfiguration);

        /* Read the config enums. */
        this.useWorkflowInput = readConfigEnumOrDefault(USEWORKFLOW_INPUT, runConfiguration, ConfigEnum.ALL);
        this.useAllGeneratedData = readConfigEnumOrDefault(USE_ALL_GENERATED_DATA, runConfiguration, ConfigEnum.ONE);

        /* DEBUG_MODE_TAG */
        this.debugMode = readBooleanOrDefault(DEBUG_MODE_TAG, runConfiguration, false);
    }

    /**
     * Method checks whether the provided value represent a Boolean, and returns the Boolean if it does.
     * Method returns the param {@code default_value} if the specified tag is not present.
     *
     * @param tag           Corresponding tag from the config file.
     * @param config        Provided JSON configuration with values.
     * @param default_value This value will be returned if the specified tag is not present in the JSONObject.
     * @return Value represented in the JSON object, or the default value if the tag is not present.
     * @throws JSONException Error in parsing the value for specified tag.
     */
    private static Boolean readBooleanOrDefault(String tag, JSONObject config, boolean default_value) throws JSONException {

        if (!config.has(tag)) {
            APEUtils.printWarning(String.format("Tag '%s' in the configuration file is not provided. Default value is: %s.", tag, default_value));
            return default_value;
        }

        return config.getBoolean(tag);
    }

    /**
     * Method checks whether the provided value represent a Integer, and Integer the boolean if it does.
     * Method returns the param {@code default_value} if the specified tag is not present.
     *
     * @param tag           Corresponding tag from the config file.
     * @param config        Provided JSON configuration with values.
     * @param default_value This value will be returned if the specified tag is not present in the JSONObject.
     * @return Value represented in the JSON object, or the default value if the tag is not present.
     * @throws JSONException Error in parsing the value for specified tag.
     */
    private static Integer readIntegerOrDefault(String tag, JSONObject config, int default_value) throws JSONException {

        if (!config.has(tag)) {
            APEUtils.printWarning(String.format("Tag '%s' in the configuration file is not provided. Default value is: %s.", tag, default_value));
            return default_value;
        }

        return config.getInt(tag);
    }

    /**
     * Method checks whether the provided value represent a {@link ConfigEnum}, and returns the {@link ConfigEnum} if it does.
     * Method returns the param {@code default_value} if the specified tag is not present.
     *
     * @param tag           Corresponding tag from the config file.
     * @param config        Provided JSON configuration with values.
     * @param default_value This value will be returned if the specified tag is not present in the JSONObject.
     * @return Value represented in the JSON object, or the default value if the tag is not present.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private static ConfigEnum readConfigEnumOrDefault(String tag, JSONObject config, ConfigEnum default_value) throws JSONException, APEConfigException {

        if (!config.has(tag)) {
            APEUtils.printWarning(String.format("Tag '%s' in the configuration file is not provided. Default value is: %s.", tag, default_value));
            return default_value;
        }

        String stringEnum = config.getString(tag);

        if (stringEnum == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (stringEnum.equals("")) {
            throw APEConfigException.invalidValue(tag, stringEnum, "value is empty.");
        }

        try {
            return ConfigEnum.valueOf(stringEnum.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw APEConfigException.invalidValue(tag, stringEnum, String.format("could not parse value. Use one of the following values: %s", Arrays.toString(ConfigEnum.values())));
        }
    }

    /**
     * Method checks whether the provided value represent a correct path, and returns the path if it does.
     *
     * @param tag    Corresponding tag from the config file.
     * @param config Provided JSON configuration with values.
     * @return Path represented in the JSON object, or the default value if the tag is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private static String readFilePath(String tag, JSONObject config, Permission... requestedPermissions) throws IOException, JSONException, APEConfigException {

        // read path
        String stringPath = config.getString(tag);

        // check on empty values
        if (stringPath == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (stringPath.equals("")) {
            throw APEConfigException.invalidValue(tag, stringPath, "value is empty.");
        }

        // path should exist
        Path path = Paths.get(stringPath);
        if (Files.notExists(path)) {
            throw APEConfigException.pathNotFound(tag, stringPath);
        }

        if (!Files.isRegularFile(path)) {
            throw APEConfigException.notAFile(tag, stringPath);
        }

        // check permissions
        for (Permission permission : Arrays.stream(requestedPermissions).distinct().collect(Collectors.toList())) {

            if (permission == Permission.READ && !Files.isReadable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

            if (permission == Permission.WRITE && !Files.isWritable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

        }

        return stringPath;
    }

    /**
     * Method checks whether the provided value represent a correct path, and returns the path if it does.
     *
     * @param tag    Corresponding tag from the config file.
     * @param config Provided JSON configuration with values.
     * @return Path represented in the JSON object, or the default value if the tag is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private static String readFilesDirectoryPath(String tag, JSONObject config, Permission... requestedPermissions) throws IOException, JSONException, APEConfigException {

        // read path
        String stringPath = config.getString(tag);

        // check on empty values
        if (stringPath == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (stringPath.equals("")) {
            throw APEConfigException.invalidValue(tag, stringPath, "value is empty.");
        }

        // path should exist and should be a file path
        Path path = Paths.get(stringPath);
        // check if the proposed path represents a file and not a directory (it does not matter whether it exists or not)
        if(FilenameUtils.getExtension(path.toString()).equals("")){
            throw APEConfigException.notAFile(tag, stringPath);
        }

        // create parent directory if required
        File directory = new File(path.getParent().toString());
        if (!directory.exists()){
            APEUtils.printWarning("Directory '" + path.getParent().toString() + "' does not exist. The directory will be created.");
            if(directory.mkdirs()){
                APEUtils.printWarning("Successfully created directory '" + path.getParent().toString() + "'");
            }
        }

        // create file if required
        if (Files.notExists(path)){
            APEUtils.printWarning("File '" + stringPath + "' does not exist. The file will be created.");
            if(new File(path.toString()).createNewFile()){
                APEUtils.printWarning("Successfully created file '" + stringPath + "'");
            }
        }

        // check permissions
        for (Permission permission : Arrays.stream(requestedPermissions).distinct().collect(Collectors.toList())) {

            if (permission == Permission.READ && !Files.isReadable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

            if (permission == Permission.WRITE && !Files.isWritable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

        }

        return stringPath;
    }
    
    /**
     * Method checks whether the provided value represent a correct path, and returns the path if it does.
     *
     * @param tag    Corresponding tag from the config file.
     * @param config Provided JSON configuration with values.
     * @return Path represented in the JSON object, or the default value if the tag is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private static String readDirectoryPath(String tag, JSONObject config, Permission... requestedPermissions) throws IOException, JSONException, APEConfigException {

        // read path
        String stringPath = config.getString(tag);

        // check on empty values
        if (stringPath == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (stringPath.equals("")) {
            throw APEConfigException.invalidValue(tag, stringPath, "value is empty.");
        }

        if(!FilenameUtils.getExtension(stringPath).equals("")){
            throw APEConfigException.notADirectory(tag, stringPath);
        }

        // path should exist
        Path path = Paths.get(stringPath);
        if (Files.notExists(path)) {
            // create parent directory if required
            File directory = new File(path.toAbsolutePath().toString());
            APEUtils.printWarning("Directory '" + stringPath + "' does not exist. The directory will be created.");
            if(directory.mkdirs()){
                APEUtils.printWarning("Successfully created directory '" + stringPath + "'");
            }
            else{
                throw new APEConfigException("Could not create directory '" + stringPath + "'");
            }
        }

        if (!Files.isDirectory(path)) {
            throw APEConfigException.notADirectory(tag, stringPath);
        }

        // check permissions
        for (Permission permission : Arrays.stream(requestedPermissions).distinct().collect(Collectors.toList())) {

            if (permission == Permission.READ && !Files.isReadable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

            if (permission == Permission.WRITE && !Files.isWritable(path)) {
                throw APEConfigException.missingPermission(tag, stringPath, permission);
            }

        }

        return stringPath;
    }

    /**
     * Used to read the input and output data instances for the program. This method calls {@link #getDataInstance}.
     */
    private ArrayList<DataInstance> getDataInstances(String tag, JSONObject config) throws JSONException {

        ArrayList<DataInstance> instances = new ArrayList<>();

        try {
            for (JSONObject jsonModuleOutput : APEUtils.getListFromJson(config, tag, JSONObject.class)) {
                DataInstance output;
                if ((output = getDataInstance(jsonModuleOutput, this.apeDomainSetup.getAllTypes())) != null) {
                    instances.add(output);
                }
            }
        } catch (ClassCastException e) {
            instances.clear();
            throw APEConfigException.cannotParse(tag, config.get(tag).toString(), JSONObject[].class, "please provide the correct format.");
        }

        return instances;
    }

    /**
     * Used to read an input or output data instance for the program.
     */
    private DataInstance getDataInstance(JSONObject jsonModuleInput, AllTypes allTypes) {

        DataInstance dataInstances = new DataInstance();

        for (String typeSuperClassLabel : jsonModuleInput.keySet()) {

            String typeSuperClassURI = APEUtils.createClassURI(typeSuperClassLabel, this.apeDomainSetup.getOntologyPrefixURI());

            for (String currTypeLabel : APEUtils.getListFromJson(jsonModuleInput, typeSuperClassLabel, String.class)) {

                String currTypeURI = APEUtils.createClassURI(currTypeLabel, this.apeDomainSetup.getOntologyPrefixURI());

                Type currType = allTypes.get(currTypeURI, typeSuperClassURI);

                if (currType == null) {
                    System.err.println("Error in the configuration file. The data type '" + currTypeURI
                            + "' was not defined or does not belong to the dimension '" + typeSuperClassLabel + "'.");
                    return null;
                }

                dataInstances.addType(currType);
            }
        }

        if (!dataInstances.getTypes().isEmpty()) {
            return dataInstances;
        }

        return null;
    }

    /**
     * Get all obligatory JSON tags to execute the synthesis.
     *
     * @return All obligatory JSON tags to execute the synthesis.
     */
    public static String[] getObligatoryRunTags() {
        return obligatoryRunTags;
    }

    /**
     * Get all optional JSON tags to execute the synthesis.
     *
     * @return All optional JSON tags to execute the synthesis.
     */
    public static String[] getOptionalRunTags() {
        return optionalRunTags;
    }


    /**
     * Get all JSON tags that can be used to execute the synthesis.
     *
     * @return All JSON tags that can be used to execute the synthesis.
     */
    public static String[] getRunTags() {
        return ArrayUtils.addAll(getObligatoryRunTags(), getOptionalRunTags());
    }

    /**
     * Gets constraints path.
     *
     * @return the {@link #constraintsPath}
     */
    public String getConstraintsPath() {
        return constraintsPath;
    }

    /**
     * Returns true if the shared memory structure should be used, i.e. if the generated data is available in memory to all the tools used subsequently,
     * or false in case of a restrictive message passing structure, i.e. if the generated data is available only to the tool next in sequence.
     *
     * @return true if the shared memory structure should be used, false in case of a restrictive message passing structure.
     */
    public Boolean getSharedMemory() {
        return sharedMemory;
    }
    
    /**
     * Returns false if the provided solutions should be distinguished based on the tool sequences alone, i.e. tool sequences cannot repeat, ignoring the types in the solutions.
     *
     * @return true if tool sequences cannot repeat, ignoring the types in the solutions, or false in case that the tool sequences can repeat as long as the corresponding types differ.
     */
	public Boolean getToolSeqRepeat() {
		return toolSeqRepeat;
	}

    /**
     * Gets solution path.
     *
     * @return the {@link #solutionPath}
     */
    public String getSolutionPath() {
        return solutionPath;
    }

    /**
     * Gets solution min length.
     *
     * @return the {@link #solutionMinLength}
     */
    public Integer getSolutionMinLength() {
        return solutionMinLength;
    }

    /**
     * Gets solution max length.
     *
     * @return the {@link #solutionMaxLength}
     */
    public Integer getSolutionMaxLength() {
        return solutionMaxLength;
    }

    /**
     * Gets max no solutions.
     *
     * @return the {@link #maxNoSolutions}
     */
    public Integer getMaxNoSolutions() {
        return maxNoSolutions;
    }

    /**
     * Gets execution scripts folder.
     *
     * @return the {@link #executionScriptsFolder}
     */
    public String getExecutionScriptsFolder() {
        return executionScriptsFolder;
    }

    /**
     * Gets no executions.
     *
     * @return the {@link #noExecutions}
     */
    public Integer getNoExecutions() {
        return noExecutions;
    }

    /**
     * Gets solution graphs folder.
     *
     * @return the {@link #solutionGraphsFolder}
     */
    public String getSolutionGraphsFolder() {
        return solutionGraphsFolder;
    }

    /**
     * Gets no graphs.
     *
     * @return the {@link #noGraphs}
     */
    public Integer getNoGraphs() {
        return noGraphs;
    }

    /**
     * Gets program inputs.
     *
     * @return the {@link #programInputs}
     */
    public List<DataInstance> getProgramInputs() {
        return programInputs;
    }

    /**
     * Gets program outputs.
     *
     * @return the {@link #programOutputs}
     */
    public List<DataInstance> getProgramOutputs() {
        return programOutputs;
    }

    /**
     * Gets use workflow input.
     *
     * @return the {@link #useWorkflowInput}
     */
    public ConfigEnum getUseWorkflowInput() {
        return useWorkflowInput;
    }

    /**
     * Gets all generated data.
     *
     * @return the {@link #useAllGeneratedData}
     */
    public ConfigEnum getUseAllGeneratedData() {
        return useAllGeneratedData;
    }

    /**
     * Gets debug mode.
     *
     * @return the {@link #debugMode}
     */
    public Boolean getDebugMode() {
        return debugMode;
    }

    /**
     * Gets cwl format root.
     * TODO: Set real values.
     * @return the cwl format root
     */
    public String getCWLFormatRoot() {
        return "format_1915";
    }

    /**
     * Function that returns the tags that are used in the JSON files. Function
     * can be used to rename the tags.
     *
     * @param tag that is used
     * @return json tags
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
}
