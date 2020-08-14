package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.configuration.APEConfigField;
import nl.uu.cs.ape.sat.configuration.APEConfigTag;
import nl.uu.cs.ape.sat.configuration.APEConfigTagFactory;
import nl.uu.cs.ape.sat.configuration.ValidationRule;
import nl.uu.cs.ape.sat.io.APEFiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nl.uu.cs.ape.sat.io.APEFiles.readFileFromPath;

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
    public final static APEConfigTag<File> ONTOLOGY_TAG = new APEConfigTagFactory.ExistingFile("ontology_path")
            .withTagDescription("This tag should be a path to an .owl file.");

    public final static APEConfigTag<File> TOOL_ANNOTATIONS_TAG = new APEConfigTagFactory.ExistingFile("tool_annotations_path")
            .withTagDescription("This tag should be a path to an .json file.");

    public final static APEConfigTag<String> ONTOLOGY_PREFIX = new APEConfigTagFactory.StringTag("ontologyPrexifIRI")
            .addValidationFunction(APEFiles::isURI, "Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).");

    public final static APEConfigTag<String> TOOL_ONTOLOGY_TAG = new APEConfigTagFactory.StringTag("toolsTaxonomyRoot");

    public final static APEConfigTag<List<String>> DIMENSIONSONTOLOGY_TAG = new APEConfigTagFactory.DataDimensions("dataDimensionsTaxonomyRoots");

    /**
     * The taxonomy (ontology) file
     */
    private APEConfigField<File> ontology = ONTOLOGY_TAG.createField();
    /**
     * The JSON file with all tool annotations.
     */
    private APEConfigField<File> toolAnnotations = TOOL_ANNOTATIONS_TAG.createField();
    /**
     * Prefix used to define OWL class IDs
     */
    private APEConfigField<String> ontologyPrefixURI = ONTOLOGY_PREFIX.createField();
    /**
     * Node in the ontology that corresponds to the root of the module taxonomy.
     */
    private APEConfigField<String> toolTaxonomyRoot = TOOL_ONTOLOGY_TAG.createField();
    /**
     * List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     */
    private APEConfigField<List<String>> dataDimensionRoots = DIMENSIONSONTOLOGY_TAG.createField();

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
     * Initialize the configuration of the project.
     *
     * @param ontology A file containing the APE configuration.
     * @param ontologyPrefixURI TODO
     * @param toolTaxonomyRoot TODO
     * @param dataDimensionRoots TODO
     * @param toolAnnotations TODO
     */
    public APECoreConfig(File ontology, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots, File toolAnnotations) {
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
     * @throws OWLException       Error in setting up the ontology for the configuration.
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
        this.ontology = readFileFromPath(ONTOLOGY_TAG, coreConfiguration.getString(ONTOLOGY_TAG), APEFiles.Permission.READ);

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
        this.toolAnnotations = readFileFromPath(TOOL_ANNOTATIONS_TAG, coreConfiguration.getString(TOOL_ANNOTATIONS_TAG), APEFiles.Permission.READ);

        return true;
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
     * @return the {@link #ontology}
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
     * @return the {@link #toolAnnotations}
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
