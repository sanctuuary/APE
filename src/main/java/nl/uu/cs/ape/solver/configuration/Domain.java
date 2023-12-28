package nl.uu.cs.ape.solver.configuration;

import java.io.IOException;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.ToolAnnotationTag;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.DomainModules;
import nl.uu.cs.ape.models.DomainTypes;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code APEDomainSetup} class is used to store the domain information and
 * initial constraints that have to be encoded.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class Domain {

    private static final String TOOLS_JSON_TAG = "functions";
    /**
     * All modules/operations used in the domain.
     */
    private DomainModules allModules;

    /**
     * All data types defined in the domain.
     */
    private DomainTypes allTypes;

    /**
     * Prefix used to define OWL class IDs
     */
    @Getter
    private String ontologyPrefixIRI;

    /**
     * Helper predicates defined within the domain model.
     */
    @Getter
    private final List<AuxiliaryPredicate> helperPredicates = new ArrayList<>();

    /**
     * Maximum number of inputs that a tool can have.
     */
    @Getter
    private int maxNoToolInputs = 0;

    /**
     * Maximum number of outputs that a tool can have.
     */
    @Getter
    private int maxNoToolOutputs = 0;

    /**
     * Holds information whether the domain was annotated under the strict rules of
     * the output dependency.
     */
    @Getter
    private boolean useStrictToolAnnotations;

    /* Helper objects used to keep track of the domain quality. */
    private Set<String> emptyTools = new HashSet<>();
    private Set<String> wrongToolIO = new HashSet<>();
    private Set<String> wrongToolTax = new HashSet<>();

    /**
     * Instantiates a new Ape domain setup.
     *
     * @param config the config
     */
    public Domain(APECoreConfig config) {
        this.allModules = new DomainModules(config);
        this.allTypes = new DomainTypes(config);
        this.ontologyPrefixIRI = config.getOntologyPrefixIRI();
        this.useStrictToolAnnotations = config.getUseStrictToolAnnotations();
    }

    /**
     * Gets all modules.
     *
     * @return The field {@link #allModules}.
     */
    public DomainModules getAllModules() {
        return allModules;
    }

    /**
     * Gets all types.
     *
     * @return the field {@link #allTypes}.
     */
    public DomainTypes getAllTypes() {
        return allTypes;
    }

    /**
     * Trim taxonomy boolean.
     *
     * @return the boolean
     */
    public boolean trimTaxonomy() {
        boolean succRun = true;

        succRun &= allModules.trimTaxonomy();
        succRun &= allTypes.trimTaxonomy();
        return succRun;
    }

    /**
     * Updates the list of All Modules by annotating the existing ones (or adding
     * non-existing) using the I/O DataInstance from the @file. Returns the list of
     * Updated Modules.
     *
     * @param toolAnnotationsFile JSON file containing tool annotations.
     * @return The list of all annotated Modules in the process (possibly empty
     *         list).
     * @throws IOException   Error in handling a JSON file containing tool
     *                       annotations.
     * @throws JSONException Error if the tool annotation JSON file, bad format
     */
    public boolean updateToolAnnotationsFromJson(JSONObject toolAnnotationsFile) throws IOException, JSONException {
        int currModule = 0;
        for (JSONObject jsonModule : APEUtils
                .safe(APEUtils.getListFromJson(toolAnnotationsFile, TOOLS_JSON_TAG, JSONObject.class))) {
            currModule++;
            updateModuleFromJson(jsonModule);
        }
        if (currModule == 0) {
            log.warn("No tools were annotated in the current domain.");
            return false;
        }
        return true;
    }

    /**
     * Creates/updates a module from a tool annotation instance from a JSON file and
     * updates the list of modules ({@link DomainModules}) in the domain
     * accordingly.
     *
     * @param jsonModule JSON representation of a module
     * @return {@code true} if the domain was updated, false otherwise.
     * @throws JSONException Error if the JSON file was not properly formatted.
     */
    private boolean updateModuleFromJson(JSONObject jsonModule)
            throws JSONException, APEDimensionsException {
        String moduleIRI = APEUtils.createClassIRI(jsonModule.getString(ToolAnnotationTag.ID.toString()),
                ontologyPrefixIRI);
        if (allModules.get(moduleIRI) != null) {
            moduleIRI = moduleIRI + "[tool]";
        }
        String moduleLabel = jsonModule.getString(ToolAnnotationTag.LABEL.toString());
        Set<String> taxonomyModules = new HashSet<>(
                APEUtils.getListFromJson(jsonModule, ToolAnnotationTag.TAXONOMY_OPERATIONS.toString(), String.class));
        taxonomyModules = APEUtils.createIRIsFromLabels(taxonomyModules, ontologyPrefixIRI);
        /* Check if the referenced module taxonomy classes exist. */
        List<String> toRemove = new ArrayList<>();
        for (String taxonomyModule : taxonomyModules) {
            String taxonomyModuleIRI = APEUtils.createClassIRI(taxonomyModule, ontologyPrefixIRI);
            if (allModules.get(taxonomyModuleIRI) == null) {
                log.warn("Tool '" + moduleIRI + "' annotation issue. "
                        + "Referenced '" + ToolAnnotationTag.TAXONOMY_OPERATIONS.toString() + "': '" + taxonomyModuleIRI
                        + "' cannot be found in the Tool Taxonomy.");
                wrongToolTax.add(moduleLabel);
                toRemove.add(taxonomyModuleIRI);
            }
        }
        taxonomyModules.removeAll(toRemove);

        /*
         * If the taxonomy terms were not properly specified the tool taxonomy root is
         * used as superclass of the tool.
         */
        if (taxonomyModules.isEmpty()) {
            log.warn("Tool '" + moduleIRI + "' annotation issue. "
                    + "None of the referenced '" + ToolAnnotationTag.TAXONOMY_OPERATIONS.toString()
                    + "' can be found in the Tool Taxonomy.");
            taxonomyModules.add(allModules.getRootModuleID());
        }

        String executionCode = null;
        try {
            executionCode = jsonModule.getJSONObject(ToolAnnotationTag.IMPLEMENTATION.toString())
                    .getString(ToolAnnotationTag.CODE.toString());
        } catch (JSONException e) {
            /* Skip the execution code */
        }

        List<JSONObject> jsonModuleInput = APEUtils.getListFromJson(jsonModule, ToolAnnotationTag.INPUTS.toString(),
                JSONObject.class);
        updateMaxNoToolInputs(jsonModuleInput.size());
        List<JSONObject> jsonModuleOutput = APEUtils.getListFromJson(jsonModule, ToolAnnotationTag.OUTPUTS.toString(),
                JSONObject.class);
        updateMaxNoToolOutputs(jsonModuleOutput.size());

        List<Type> inputs = new ArrayList<>();
        List<Type> outputs = new ArrayList<>();

        try {
            /* For each input and output, allocate the corresponding abstract types. */
            for (JSONObject jsonInput : jsonModuleInput) {
                if (!jsonInput.isEmpty()) {
                    inputs.add(Type.taxonomyInstanceFromJson(jsonInput, this, false));
                }
            }
            for (JSONObject jsonOutput : jsonModuleOutput) {
                if (!jsonOutput.isEmpty()) {
                    outputs.add(Type.taxonomyInstanceFromJson(jsonOutput, this, true));
                }
            }
        } catch (APEDimensionsException badDimension) {
            wrongToolIO.add(moduleLabel);
            log.warn("Operation '" + "' was not included." + badDimension.getMessage());
            return false;
        }

        String moduleExecutionImpl = null;
        if (executionCode != null && !executionCode.equals("")) {
            moduleExecutionImpl = executionCode;
        }
        if (inputs.isEmpty() && outputs.isEmpty()) {
            emptyTools.add(moduleLabel);
            log.debug("Operation '" + "' was not included as it has no (valid) inputs and outputs specified.");
            return false;
        }
        /*
         * Add the module and make it sub module of the currSuperModule (if it was not
         * previously defined)
         */
        Module currModule = (Module) allModules
                .addPredicate(new Module(moduleLabel, moduleIRI, allModules.getRootModuleID(), moduleExecutionImpl));

        /* For each supermodule add the current module as a subset and vice versa. */
        for (String superModuleID : taxonomyModules) {
            AbstractModule superModule = allModules.get(superModuleID);
            if (superModule != null) {
                superModule.addSubPredicate(currModule);
                currModule.addSuperPredicate(superModule);
            }
        }

        currModule.setModuleInput(inputs);
        currModule.setModuleOutput(outputs);
        currModule.setAsRelevantTaxonomyTerm(allModules);

        return currModule != null;
    }

    /**
     * Updates the list of All Modules to include the CWL annotations.
     * 
     * @param cwlAnnotations A Map of the content of the CWL annotations file.
     * @return Whether the update was successful.
     */
    public boolean updateCWLAnnotationsFromYaml(Map<String, Object> cwlAnnotations) {
        for (Map.Entry<String, Object> entry : cwlAnnotations.entrySet()) {
            Object[] ids = allModules.getModules().stream()
                    .filter(m -> m.getPredicateID().toLowerCase().contains(entry.getKey().toLowerCase())
                            && m.getType().equals("module"))
                    .toArray();
            String id;
            if (ids.length > 0) {
                TaxonomyPredicate predicate = (TaxonomyPredicate) ids[0];
                id = predicate.getPredicateID();
            } else {
                // Could not find module related to annotation entry, skip the entry.
                continue;
            }
            Module currModule = (Module) allModules.get(id);
            Map<String, Object> tool = (Map<String, Object>) cwlAnnotations.get(currModule.getPredicateLabel());

            ArrayList<LinkedHashMap<String, String>> cwlInputs = null;
            Map<String, Object> implementation = null;
            if (tool != null) {
                ArrayList<LinkedHashMap<String, String>> cwlInp = (ArrayList<LinkedHashMap<String, String>>) tool
                        .get("inputs");
                Map<String, Object> imp = (Map<String, Object>) tool.get("implementation");
                cwlInputs = cwlInp;
                implementation = imp;
            }
            currModule.setCwlInputs(cwlInputs);
            currModule.setCwlImplementation(implementation);
        }
        return true;
    }

    /**
     * Update the maximum number of inputs that a tool can have, i.e. increase the
     * number if the current max number is smaller than the new number of inputs.
     *
     * @param currNoInputs the number of inputs that a tool has
     */
    public void updateMaxNoToolInputs(int currNoInputs) {
        if (this.maxNoToolInputs < currNoInputs) {
            this.maxNoToolInputs = currNoInputs;
        }
    }

    /**
     * Update the maximum number of outputs that a tool can have, i.e. increase the
     * number if the current max number is smaller than the new number of outputs.
     *
     * @param currNoOutputs the number of outputs that the current tool has
     */
    public void updateMaxNoToolOutputs(int currNoOutputs) {
        if (this.maxNoToolOutputs < currNoOutputs) {
            this.maxNoToolOutputs = currNoOutputs;
        }
    }

    /**
     * Add predicate to the list of auxiliary predicates that should be encoded.
     * 
     * @param helperPredicate
     */
    protected void addHelperPredicate(AuxiliaryPredicate helperPredicate) {
        helperPredicates.add(helperPredicate);
    }

}
