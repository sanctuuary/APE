package nl.uu.cs.ape.sat.utils;


import org.apache.commons.io.FileUtils;
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
import java.util.stream.Collectors;

/**
 * The {@link APECoreConfig} class is used to define the core configuration
 * variables required for the proper execution of the library.
 *
 * @author Vedran Kasalica
 */
public class APECoreConfig {

    /**
     * Tags used in the JSON file.
     */
    private final static String ONTOLOGY_TAG = "ontology_path";
    private final static String ONTOLOGY_PREFIX = "ontologyPrexifIRI";
    private final static String TOOL_ONTOLOGY_TAG = "toolsTaxonomyRoot";
    private final static String DIMENSIONSONTOLOGY_TAG = "dataDimensionsTaxonomyRoots";
    private final static String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";

    /**
     * Tags separated in the categories: obligatory, optional, core and run.
     * The obligatory tags are used in the constructor to check the presence of tags.
     * Optional tags or All tags are mostly used by test cases.
     */
    private final static String[] obligatoryCoreTags = new String[]{
            ONTOLOGY_TAG,
            ONTOLOGY_PREFIX,
            TOOL_ONTOLOGY_TAG,
            DIMENSIONSONTOLOGY_TAG,
            TOOL_ANNOTATIONS_TAG
    };
    private final static String[] optionalCoreTags = new String[]{};

    /**
     * READ and WRITE enums used to verify paths.
     */
    private enum Permission {READ, WRITE}

    /**
     * The taxonomy (ontology) file
     */
    private File ontology;
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
     * The JSON file with all tool annotations.
     */
    private File toolAnnotations;

    
    /**
     * Initialize the configuration of the project.
     *
     * @param configPath Path to the APE configuration file.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(File ontology, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots, File toolAnnotations) throws IOException, JSONException {
    	/* Path to the OWL file. */
        this.ontology = ontology;

        /* URI of the ontology classes. */
        this.ontologyPrefixURI = ontologyPrefixURI;

        /* The root class of the tool taxonomy. */
        this.toolTaxonomyRoot = APEUtils.createClassURI(toolTaxonomyRoot, getOntologyPrefixURI());

        /* Dimension classes of the data taxonomy. */
            for (String subTaxonomy : dataDimensionRoots) {
                this.dataDimensionRoots.add(APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()));
            }

        /* Path to the tool annotations JSON file. */
        this.toolAnnotations = toolAnnotations;
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

        coreConfigSetup(new JSONObject(content));
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

        coreConfigSetup(configObject);
    }


    /**
     * Setting up the core configuration of the library.
     *
     * @return true if the method successfully set-up the configuration, false otherwise.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     * @throws OWLException Error in setting up the the configuration.
     */
    private boolean coreConfigSetup(JSONObject coreConfiguration) throws IOException, JSONException, APEConfigException {

        /* JSONObject must have been parsed correctly. */
        if (coreConfiguration == null) {
            throw new APEConfigException("Cannot set up the core configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }

        /* Make sure all required core tags are present. This way, the parser does not have to check presence of the tag */
        for (String requiredTag : getObligatoryCoreTags()) {
            if (!coreConfiguration.has(requiredTag)) {
                throw APEConfigException.missingTag(requiredTag);
            }
        }

        /* Path to the OWL file. */
        this.ontology = readFileFromPath(ONTOLOGY_TAG, coreConfiguration, Permission.READ);

        /* URI of the ontology classes. */
        this.ontologyPrefixURI = coreConfiguration.getString(ONTOLOGY_PREFIX);

        /* The root class of the tool taxonomy. */
        try{
        this.toolTaxonomyRoot = APEUtils.createClassURI(coreConfiguration.getString(TOOL_ONTOLOGY_TAG), getOntologyPrefixURI());
        }
        catch (IllegalArgumentException e){
            throw APEConfigException.invalidValue(TOOL_ONTOLOGY_TAG, coreConfiguration, "expected tag cannot be empty.");
        }

        /* Dimension classes of the data taxonomy. */
        try{
            for (String subTaxonomy : APEUtils.getListFromJson(coreConfiguration, DIMENSIONSONTOLOGY_TAG, String.class)) {
                this.dataDimensionRoots.add(APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()));
            }
        }
        catch (ClassCastException e){
            throw APEConfigException.invalidValue(DIMENSIONSONTOLOGY_TAG, coreConfiguration, "expected a list in correct format.");
        } catch (IllegalArgumentException e) {
        	throw APEConfigException.invalidValue(DIMENSIONSONTOLOGY_TAG, coreConfiguration, "elements of the list cannot be empty.");
		}

        /* Path to the tool annotations JSON file. */
        this.toolAnnotations = readFileFromPath(TOOL_ANNOTATIONS_TAG, coreConfiguration, Permission.READ);

        return true;
    }
    
    /**
     * Method checks whether the provided value represent a correct path to a file, and returns the corresponding file if it does.
     *
     * @param tag    Corresponding tag from the config file.
     * @param config Provided JSON configuration with values.
     * @return File represented by the path in the JSON object, or the default value if the tag is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    private static File readFileFromPath(String tag, JSONObject config, Permission... requestedPermissions) throws IOException, JSONException, APEConfigException {

        // read path
        String filePath = config.getString(tag);

        // check on empty values
        if (filePath == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (filePath.equals("")) {
            throw APEConfigException.invalidValue(tag, filePath, "value is empty.");
        }

        // path should exist
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            throw APEConfigException.pathNotFound(tag, filePath);
        }

        if (!Files.isRegularFile(path)) {
            throw APEConfigException.notAFile(tag, filePath);
        }

        // check permissions
        for (Permission permission : Arrays.stream(requestedPermissions).distinct().collect(Collectors.toList())) {

            if (permission == Permission.READ && !Files.isReadable(path)) {
                throw APEConfigException.missingPermission(tag, filePath, permission);
            }

            if (permission == Permission.WRITE && !Files.isWritable(path)) {
                throw APEConfigException.missingPermission(tag, filePath, permission);
            }

        }
        
        File file = new File(filePath);

        return file;
    }


    /**
     * Get all obligatory JSON tags to set up the framework.
     *
     * @return All obligatory JSON tags to set up the framework.
     */
    public static String[] getObligatoryCoreTags() {
        return obligatoryCoreTags;
    }

    /**
     * Get all optional JSON tags to set up the framework.
     *
     * @return All optional JSON tags to set up the framework.
     */
    public static String[] getOptionalCoreTags() {
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
    public File getOntologyFile() {
        return ontology;
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
    public File getToolAnnotationsFile() {
        return toolAnnotations;
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
