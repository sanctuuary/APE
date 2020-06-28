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
 * The {@link APECoreConfig} (singleton) class is used to define the configuration
 * variables required for the proper execution of the library.
 *
 * @author Vedran Kasalica
 */
public class APECoreConfig {

    /**
     * Tags used in the JSON file.
     */
    private final String ONTOLOGY_TAG = "ontology_path";
    private final String ONTOLOGY_PREFIX = "ontologyPrexifIRI";
    private final String TOOL_ONTOLOGY_TAG = "toolsTaxonomyRoot";
    private final String DIMENSIONSONTOLOGY_TAG = "dataDimensionsTaxonomyRoots";
    private final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";

    /**
     * Tags separated in the categories: obligatory, optional, core and run.
     * The obligatory tags are used in the constructor to check the presence of tags.
     * Optional tags or All tags are mostly used by test cases.
     */
    private final String[] obligatoryCoreTags = new String[]{
            ONTOLOGY_TAG,
            ONTOLOGY_PREFIX,
            TOOL_ONTOLOGY_TAG,
            DIMENSIONSONTOLOGY_TAG,
            TOOL_ANNOTATIONS_TAG
    };
    private final String[] optionalCoreTags = new String[]{};

    /**
     * READ and WRITE enums used to verify paths.
     */
    private enum Permission {READ, WRITE}

    /**
     * Path to the taxonomy file
     */
    private String ontologyPath;
    /**
     * Prefix used to define OWL class IDs
     */
    private String ontologyPrefixURI;
    /**
     * Node in the ontology that corresponds to the root of the module taxonomy.
     */
    private String toolTaxonomyRoot;
    /**
     * List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     */
    private List<String> dataDimensionRoots = new ArrayList<>();
    /**
     * Path to the XML file with all tool annotations.
     */
    private String toolAnnotationsPath;
    /**
     * Configurations used to read "ape.configuration" file.
     */
    private JSONObject coreConfiguration;

    
    /**
     * Initialize the configuration of the project.
     *
     * @param configPath Path to the APE configuration file.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(String ontologyPath, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots, String toolAnnotationsPath) throws IOException, JSONException, APEConfigException {
        coreConfigSetup();
    }
    
    
    /**
     * Initialize the configuration of the project.
     *
     * @param configPath Path to the APE configuration file.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(String configPath) throws IOException, JSONException, APEConfigException {
        if (configPath == null) {
            throw new NullPointerException("The provided core configuration file path is null.");
        }
        File file = new File(configPath);
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        coreConfiguration = new JSONObject(content);

        coreConfigSetup();
    }

    /**
     * Initialize the configuration of the project.
     *
     * @param configObject The APE configuration JSONObject{@link JSONObject}.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(JSONObject configObject) throws IOException, JSONException, APEConfigException {
        if (configObject == null)
            throw new NullPointerException("The provided JSONObject is null.");

        // Set JSONObject as core configuration
        coreConfiguration = configObject;

        coreConfigSetup();
    }


    /**
     * Setting up the core configuration of the library.
     *
     * @return true if the method successfully set-up the configuration, false otherwise.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private boolean coreConfigSetup() throws IOException, JSONException, APEConfigException {

        /* JSONObject must have been parsed correctly. */
        if (coreConfiguration == null) {
            throw new APEConfigException("Cannot set up the core configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }

        /* Make sure all required core tags are present. This way, teh parser does not have to check presence of the tag */
        for (String requiredTag : getObligatoryCoreTags()) {
            if (!coreConfiguration.has(requiredTag)) {
                throw APEConfigException.missingTag(requiredTag);
            }
        }

        /* Path to the OWL file. */
        this.ontologyPath = readFilePath(ONTOLOGY_TAG, coreConfiguration, Permission.READ);

        /* URI of the ontology classes. */
        this.ontologyPrefixURI = coreConfiguration.getString(ONTOLOGY_PREFIX);

        /* The root class of the tool taxonomy. */
        /* TODO: should throw an exception if the root is not present in the OWL file. */
        this.toolTaxonomyRoot = APEUtils.createClassURI(coreConfiguration.getString(TOOL_ONTOLOGY_TAG), getOntologyPrefixURI());
        if (this.toolTaxonomyRoot.equals("")) {
            throw APEConfigException.invalidValue(TOOL_ONTOLOGY_TAG, coreConfiguration, "incorrect format.");
        }

        /* Dimension classes of the data taxonomy. */
        /* TODO: should throw an exception if a dimension is not present in the OWL file. */
        try{
            for (String subTaxonomy : APEUtils.getListFromJson(coreConfiguration, DIMENSIONSONTOLOGY_TAG, String.class)) {
                this.dataDimensionRoots.add(APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()));
            }
        }
        catch (ClassCastException e){
            throw APEConfigException.invalidValue(DIMENSIONSONTOLOGY_TAG, coreConfiguration, "expected a list in correct format.");
        }

        /* Path to the tool annotations JSON file. */
        this.toolAnnotationsPath = readFilePath(TOOL_ANNOTATIONS_TAG, coreConfiguration, Permission.READ);

        return true;
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
    private String readFilePath(String tag, JSONObject config, Permission... requestedPermissions) throws IOException, JSONException, APEConfigException {

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

        // path should exist
        Path path = Paths.get(stringPath);
        if (Files.notExists(path) || !Files.isDirectory(path)) {
            throw APEConfigException.pathNotFound(tag, stringPath);
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
     * Get all obligatory JSON tags to set up the framework.
     *
     * @return All obligatory JSON tags to set up the framework.
     */
    public String[] getObligatoryCoreTags() {
        return obligatoryCoreTags;
    }

    /**
     * Get all optional JSON tags to set up the framework.
     *
     * @return All optional JSON tags to set up the framework.
     */
    public String[] getOptionalCoreTags() {
        return optionalCoreTags;
    }

    /**
     * Get all JSON tags that can be used to set up the framework.
     *
     * @return All JSON tags that can be used to set up the framework.
     */
    public String[] getCoreTags() {
        return ArrayUtils.addAll(getObligatoryCoreTags(), getOptionalCoreTags());
    }

    /**
     * Gets ontology path.
     *
     * @return the {@link #ontologyPath}
     */
    public String getOntologyPath() {
        return ontologyPath;
    }

    /**
     * Gets ontology prefix uri.
     *
     * @return the {@link #ontologyPrefixURI}
     */
    public String getOntologyPrefixURI() {
        return (ontologyPrefixURI != null) ? ontologyPrefixURI : "";
    }

    /**
     * Gets tool taxonomy root.
     *
     * @return the {@link #toolTaxonomyRoot}
     */
    public String getToolTaxonomyRoot() {
        return toolTaxonomyRoot;
    }

    /**
     * Gets data dimension roots.
     *
     * @return the {@link #dataDimensionRoots}
     */
    public List<String> getDataDimensionRoots() {
        return dataDimensionRoots;
    }

    /**
     * Gets tool annotations path.
     *
     * @return the {@link #toolAnnotationsPath}
     */
    public String getToolAnnotationsPath() {
        return toolAnnotationsPath;
    }

    /**
     * Gets core config json obj.
     *
     * @return the {@link #coreConfiguration}
     */
    public JSONObject getCoreConfigJsonObj() {
        return coreConfiguration;
    }
    

    /**
	 * @param ontologyPath the ontologyPath to set
	 */
	private void setOntologyPath(String ontologyPath) {
		this.ontologyPath = ontologyPath;
	}

	/**
	 * @param ontologyPrefixURI the ontologyPrefixURI to set
	 */
	private void setOntologyPrefixURI(String ontologyPrefixURI) {
		this.ontologyPrefixURI = ontologyPrefixURI;
	}

	/**
	 * @param toolTaxonomyRoot the toolTaxonomyRoot to set
	 */
	private void setToolTaxonomyRoot(String toolTaxonomyRoot) {
		this.toolTaxonomyRoot = toolTaxonomyRoot;
	}

	/**
	 * @param dataDimensionRoots the dataDimensionRoots to set
	 */
	private void setDataDimensionRoots(List<String> dataDimensionRoots) {
		this.dataDimensionRoots = dataDimensionRoots;
	}

	/**
	 * @param toolAnnotationsPath the toolAnnotationsPath to set
	 */
	private void setToolAnnotationsPath(String toolAnnotationsPath) {
		this.toolAnnotationsPath = toolAnnotationsPath;
	}

	/**
	 * @param coreConfiguration the coreConfiguration to set
	 */
	private void setCoreConfiguration(JSONObject coreConfiguration) {
		this.coreConfiguration = coreConfiguration;
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
