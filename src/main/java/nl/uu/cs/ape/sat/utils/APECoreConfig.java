package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.configuration.APEConfig;
import nl.uu.cs.ape.sat.configuration.APEConfigField;
import nl.uu.cs.ape.sat.configuration.APEConfigTag;
import nl.uu.cs.ape.sat.configuration.APEConfigTagFactory;
import nl.uu.cs.ape.sat.io.APEFiles;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link APECoreConfig} class is used to define the core configuration
 * variables required for the proper execution of the library.
 *
 * @author Vedran Kasalica
 */
public class APECoreConfig extends APEConfig {

    /**
     * The taxonomy (ontology) file
     */
    public final static APEConfigTag<File> ONTOLOGY_TAG = new APEConfigTagFactory.ExistingFile("ontology_path")
            .withTagDescription("This tag should be a path to an .owl file.");

    /**
     * The JSON file with all tool annotations.
     */
    public final static APEConfigTag<File> TOOL_ANNOTATIONS_TAG = new APEConfigTagFactory.ExistingFile("tool_annotations_path")
            .withTagDescription("This tag should be a path to an .json file.");

    /**
     * Prefix used to define OWL class IDs
     */
    public final static APEConfigTag<String> ONTOLOGY_PREFIX = new APEConfigTagFactory.StringTag("ontologyPrexifIRI")
            .withDefaultValue("")
            .addValidationFunction(APEFiles::isURI, "Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).");

    /**
     * Node in the ontology that corresponds to the root of the module taxonomy.
     */
    public final static APEConfigTag<String> TOOL_ONTOLOGY_TAG = new APEConfigTagFactory.StringTag("toolsTaxonomyRoot");

    /**
     * List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     */
    public final static APEConfigTag<List<String>> DIMENSIONSONTOLOGY_TAG = new APEConfigTagFactory.DataDimensions("dataDimensionsTaxonomyRoots");

    /**
     * Initialize the configuration of the project.
     *
     * @param ontology           A file containing the APE configuration.
     * @param ontologyPrefixURI  Prefix used to define OWL class IDs
     * @param toolTaxonomyRoot   Node in the ontology that corresponds to the root of the module taxonomy.
     * @param dataDimensionRoots List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     * @param toolAnnotations    The JSON file with all tool annotations.
     */
    public APECoreConfig(File ontology, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots, File toolAnnotations) {

        /* Path to the OWL file. */
        getField(ONTOLOGY_TAG).setValue(ontology);

        /* URI of the ontology classes. */
        getField(ONTOLOGY_PREFIX).setValue(ontologyPrefixURI);

        /* The root class of the tool taxonomy. */
        getField(TOOL_ONTOLOGY_TAG).setValue(APEUtils.createClassURI(toolTaxonomyRoot, getOntologyPrefixURI()));

        /* Dimension classes of the data taxonomy. */
        getField(DIMENSIONSONTOLOGY_TAG).setValue(
                dataDimensionRoots.stream()
                        .map(subTaxonomy -> APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()))
                        .collect(Collectors.toList()));

        /* Path to the tool annotations JSON file. */
        getField(TOOL_ANNOTATIONS_TAG).setValue(toolAnnotations);
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
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(JSONObject configObject) throws JSONException, APEConfigException {
        if (configObject == null)
            throw new NullPointerException("The provided JSONObject is null.");

        coreConfigSetup(configObject);
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

    /**
     * Setting up the core configuration of the library.
     *
     * @return true if the method successfully set-up the configuration, false otherwise.
     * //@throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     *                            //@throws OWLException       Error in setting up the ontology for the configuration.
     */
    private boolean coreConfigSetup(JSONObject coreConfiguration) throws JSONException, APEConfigException {

        /* JSONObject must have been parsed correctly. */
        if (coreConfiguration == null) {
            throw new APEConfigException("Cannot set up the core configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }

        for (APEConfigField<?> field : getFields()) {
            field.setValue(coreConfiguration);
        }

        return true;
    }

    /**
     * Gets ontology path.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS_TAG}
     */
    public File getOntologyFile() {
        return getField(ONTOLOGY_TAG).getValue();
    }

    /**
     * Gets ontology prefix uri.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS_TAG}
     */
    public String getOntologyPrefixURI() {
        return getField(ONTOLOGY_PREFIX).getValue();
    }

    /**
     * Gets tool taxonomy root.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS_TAG}
     */
    public String getToolTaxonomyRoot() {
        return getField(TOOL_ONTOLOGY_TAG).getValue();
    }

    /**
     * Gets data dimension roots.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS_TAG}
     */
    public List<String> getDataDimensionRoots() {
        return getField(DIMENSIONSONTOLOGY_TAG).getValue();
    }

    /**
     * Gets tool annotations path.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS_TAG}
     */
    public File getToolAnnotationsFile() {
        return getField(TOOL_ANNOTATIONS_TAG).getValue();
    }

    /**
     * Gets cwl format root.
     * TODO: Set real values.
     *
     * @return the cwl format root
     */
    public String getCWLFormatRoot() {
        return "format_1915";
    }
}
