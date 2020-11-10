package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.configuration.tags.APEConfigTag;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTagFactory;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTagFactory.TAGS.*;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTags;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
     * The taxonomy (ontology) file
     */
    public final APEConfigTag<Path> ONTOLOGY = new APEConfigTagFactory.TAGS.ONTOLOGY();
    /**
     * Prefix used to define OWL class IDs
     */
    public final APEConfigTag<String> ONTOLOGY_PREFIX = new APEConfigTagFactory.TAGS.ONTOLOGY_PREFIX();
    /**
     * Node in the ontology that corresponds to the root of the module taxonomy.
     */
    public final APEConfigTag<String> TOOL_ONTOLOGY_ROOT = new APEConfigTagFactory.TAGS.TOOL_ONTOLOGY_ROOT(ONTOLOGY_PREFIX::getValue);
    /**
     * List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     */
    public final APEConfigTag<List<String>> DIMENSIONS_ONTOLOGY = new APEConfigTagFactory.TAGS.DIMENSIONS_ONTOLOGY(ONTOLOGY_PREFIX::getValue);
    /**
     * The JSON file with all tool annotations.
     */
    public final APEConfigTag<Path> TOOL_ANNOTATIONS = new APEConfigTagFactory.TAGS.TOOL_ANNOTATIONS();
    /**
     * {@code true} if the domain expects strict tool annotations, where, {@code false} in case of a
     * restrictive message passing structure.
     */
    public final APEConfigTag<Boolean> STRICT_TOOL_ANNOTATIONS = new APEConfigTagFactory.TAGS.STRICT_TOOL_ANNOTATIONS();

    /**
     * All the Tags specified in this class. Should be in correct order of dependencies.
     */
    private final APEConfigTag<?>[] all_tags = new APEConfigTag[]{
            this.ONTOLOGY_PREFIX,
            this.ONTOLOGY,
            this.TOOL_ONTOLOGY_ROOT,
            this.DIMENSIONS_ONTOLOGY,
            this.TOOL_ANNOTATIONS,
            this.STRICT_TOOL_ANNOTATIONS
    };

    /**
     * Static versions of the Tags specified in this class.
     * Should be in correct order for the Web API.
     */
    public static final APEConfigTags TAGS = new APEConfigTags(
            new ONTOLOGY_PREFIX(),
            new ONTOLOGY(),
            new TOOL_ONTOLOGY_ROOT(null),
            new DIMENSIONS_ONTOLOGY(null),
            new TOOL_ANNOTATIONS(),
            new STRICT_TOOL_ANNOTATIONS()
    );

    /**
     * Initialize the configuration of the project.
     *
     * @param ontology           A file containing the APE configuration.
     * @param ontologyPrefixURI  Prefix used to define OWL class IDs
     * @param toolTaxonomyRoot   Node in the ontology that corresponds to the root of the module taxonomy.
     * @param dataDimensionRoots List of nodes in the ontology that correspond to the roots of disjoint sub-taxonomies, where each represents a data dimension (e.g. data type, data format, etc.).
     * @param toolAnnotations    The JSON file with all tool annotations.
     */
    public APECoreConfig(File ontology, String ontologyPrefixURI, String toolTaxonomyRoot, List<String> dataDimensionRoots, File toolAnnotations, boolean strictToolAnnotations) {

        /* Path to the OWL file. */
        this.ONTOLOGY.setValue(ontology.toPath());

        /* URI of the ontology classes. */
        this.ONTOLOGY_PREFIX.setValue(ontologyPrefixURI);

        /* The root class of the tool taxonomy. */
        this.TOOL_ONTOLOGY_ROOT.setValue(APEUtils.createClassURI(toolTaxonomyRoot, getOntologyPrefixURI()));

        /* Dimension classes of the data taxonomy. */
        this.DIMENSIONS_ONTOLOGY.setValue(
                dataDimensionRoots.stream()
                        .map(subTaxonomy -> APEUtils.createClassURI(subTaxonomy, getOntologyPrefixURI()))
                        .collect(Collectors.toList()));

        /* Path to the tool annotations JSON file. */
        this.TOOL_ANNOTATIONS.setValue(toolAnnotations.toPath());
        /* Set the tool annotation model for the domain. */
        this.STRICT_TOOL_ANNOTATIONS.setValue(strictToolAnnotations);
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

        coreConfigSetup(new JSONObject(FileUtils.readFileToString(new File(configPath), "utf-8")));
    }
    
    /**
     * Initialize the configuration of the project.
     *
     * @param configPath Path to the APE configuration file.
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(File config) throws IOException, JSONException, APEConfigException {

        coreConfigSetup(new JSONObject(FileUtils.readFileToString(config, "utf-8")));
    }

    /**
     * Initialize the configuration of the project.
     *
     * @param configObject The APE configuration JSONObject{@link JSONObject}.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APECoreConfig(JSONObject configObject) throws JSONException, APEConfigException {
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
     * <p>
     * //@throws IOException        Error in reading the configuration file.
     *
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     *                            //@throws OWLException       Error in setting up the ontology for the configuration.
     */
    private void coreConfigSetup(JSONObject coreConfiguration) throws JSONException, APEConfigException {

        /* JSONObject must have been parsed correctly. */
        if (coreConfiguration == null) {
            throw new APEConfigException("Cannot set up the core configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }

        // set the value for each tag
        for (APEConfigTag<?> tag : all_tags) {
            tag.setValueFromConfig(coreConfiguration);
        }
    }

    /**
     * Private constructor used by {@link APECoreConfig#validate(JSONObject config)}
     * to create an empty instance.
     */
    private APECoreConfig(){}

    /**
     * Validate tje JSONObject for each CORE tag.
     * If {@link ValidationResults#success()} ()} returns true,
     * the configuration object can be safely used to create
     * an APECoreConfig object.
     *
     * @param json the json
     * @return the validation results
     */
    public static ValidationResults validate(JSONObject json){
        APECoreConfig dummy = new APECoreConfig();
        ValidationResults results = new ValidationResults();
        for(APEConfigTag<?> tag : dummy.all_tags){
            results.add(tag.validateConfig(json));
            if(results.hasFails()){
                return results;
            }
            else{
                tag.setValueFromConfig(json); // for dependencies
            }
        }
        return results;
    }

    /**
     * Gets ontology.
     *
     * @return the value of tag {@link #ONTOLOGY}
     */
    public File getOntologyFile() {
        return ONTOLOGY.getValue().toFile();
    }
    
    /**
     * Set ontology annotation.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS}
     */
    public void setOntologyFile(File ontology) {
    	ONTOLOGY.setValue(ontology.toPath());
    }

    /**
     * Gets ontology prefix uri.
     *
     * @return the value of tag {@link #ONTOLOGY_PREFIX}
     */
    public String getOntologyPrefixURI() {
        return ONTOLOGY_PREFIX.getValue();
    }

    /**
     * Gets tool taxonomy root.
     *
     * @return the value of tag {@link #TOOL_ONTOLOGY_ROOT}
     */
    public String getToolTaxonomyRoot() {
        return TOOL_ONTOLOGY_ROOT.getValue();
    }
    
    /**
     * Gets data dimension roots.
     *
     * @return the value of tag {@link #DIMENSIONS_ONTOLOGY}
     */
    public List<String> getDataDimensionRoots() {
        return DIMENSIONS_ONTOLOGY.getValue();
    }

    /**
     * Gets tool annotations path.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS}
     */
    public File getToolAnnotationsFile() {
        return TOOL_ANNOTATIONS.getValue().toFile();
    }
    
    /**
     * Set tool annotations.
     *
     * @return the value of tag {@link #TOOL_ANNOTATIONS}
     */
    public void setToolAnnotationsFile(File toolAnnotations) {
        TOOL_ANNOTATIONS.setValue(toolAnnotations.toPath());
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
    
    /**
     * Get information whether the domain was annotated under the strict rules of the output dependency.
     * @return {@code true} if the strict rules apply, {@code false} otherwise.
     */
    public boolean getUseStrictToolAnnotations() {
        return STRICT_TOOL_ANNOTATIONS.getValue();
    }
    
}
