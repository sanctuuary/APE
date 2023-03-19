package nl.uu.cs.ape.utils;

import java.io.IOException;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.constraints.ConstraintFactory;
import nl.uu.cs.ape.constraints.ConstraintFormatException;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.constraints.ConstraintTemplateParameter;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code APEDomainSetup} class is used to store the domain information and
 * initial constraints that have to be encoded.
 *
 * @author Vedran Kasalica
 */
public class APEDomainSetup {

    /* Helper objects used to keep track of the domain quality. */
    public Set<String> emptyTools = new HashSet<String>();
    public Set<String> wrongToolIO = new HashSet<String>();
    public Set<String> wrongToolTax = new HashSet<String>();
    /**
     * All modules/operations used in the domain.
     */
    private AllModules allModules;

    /**
     * All data types defined in the domain.
     */
    private AllTypes allTypes;

    /**
     * Prefix used to define OWL class IDs
     */
    private String ontologyPrefixIRI;

    /**
     * Object used to create temporal constraints.
     */
    private ConstraintFactory constraintFactory;

    /**
     * List of data gathered from the constraint file.
     */
    private List<ConstraintTemplateData> unformattedConstr;
    private List<AuxiliaryPredicate> helperPredicates;
    private List<String> constraintsSLTLx;

    /**
     * Maximum number of inputs that a tool can have.
     */
    private int maxNoToolInputs = 0;

    /**
     * Maximum number of outputs that a tool can have.
     */
    private int maxNoToolOutputs = 0;

    /**
     * Holds information whether the domain was annotated under the strict rules of
     * the output dependency.
     */
    private boolean useStrictToolAnnotations;

    private static final String CONSTR_JSON_TAG = "constraints";
    private static final String CONSTR_ID_TAG = "constraintid";
    private static final String CONSTR_SLTLx = "formula";
    private static final String CONSTR_PARAM_JSON_TAG = "parameters";
    private static final String TOOLS_JSOM_TAG = "functions";

    /**
     * Instantiates a new Ape domain setup.
     *
     * @param config the config
     */
    public APEDomainSetup(APECoreConfig config) {
        this.unformattedConstr = new ArrayList<ConstraintTemplateData>();
        this.allModules = new AllModules(config);
        this.allTypes = new AllTypes(config);
        this.constraintFactory = new ConstraintFactory();
        this.helperPredicates = new ArrayList<AuxiliaryPredicate>();
        this.constraintsSLTLx = new ArrayList<String>();
        this.ontologyPrefixIRI = config.getOntologyPrefixIRI();
        this.useStrictToolAnnotations = config.getUseStrictToolAnnotations();
    }

    /**
     * Gets all modules.
     *
     * @return The field {@link #allModules}.
     */
    public AllModules getAllModules() {
        return allModules;
    }

    /**
     * Add constraint data.
     *
     * @param constr Add a constraint to the list of constraints, that should be
     *               encoded during the execution of the synthesis.
     */
    public void addConstraintData(ConstraintTemplateData constr) {
        this.unformattedConstr.add(constr);
    }

    /**
     * Add the String that corresponds to an SLTLx formula that should be parsed to
     * the list of constraints.
     * 
     * @param formulaSLTLx - String that corresponds to an SLTLx formula that should
     *                     be parsed
     */
    public void addSLTLxConstraint(String formulaSLTLx) {
        this.constraintsSLTLx.add(formulaSLTLx);
    }

    /**
     * Gets unformatted constraints.
     *
     * @return the field {@link #unformattedConstr}.
     */
    public List<ConstraintTemplateData> getUnformattedConstr() {
        return unformattedConstr;
    }

    /**
     * Gets all SLTLx constraints specified by the user in SLTLx as text.
     * 
     * @return Set of string representations of the constraints.
     */
    public List<String> getSLTLxConstraints() {
        return constraintsSLTLx;
    }

    /**
     * Removes all of the unformatted constraints, in order to start a new synthesis
     * run.
     */
    public void clearConstraints() {
        this.unformattedConstr.clear();
        this.constraintsSLTLx.clear();
    }

    /**
     * Gets all types.
     *
     * @return the field {@link #allTypes}.
     */
    public AllTypes getAllTypes() {
        return allTypes;
    }

    /**
     * Gets constraint factory.
     *
     * @return the field {@link #constraintFactory}.
     */
    public ConstraintFactory getConstraintFactory() {
        return constraintFactory;
    }

    /**
     * Adding each constraint format in the set of all cons. formats. method
     * should be called only once all the data types and modules have been
     * initialized.
     */
    public void initializeConstraints() {
        constraintFactory.initializeConstraints(allModules, allTypes);
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
     * Return the {@link ConstraintTemplate} that corresponds to the given ID, or
     * null if the constraint with the given ID does not exist.
     *
     * @param constraintID ID of the {@code ConstraintTemplate}.
     * @return The {@code ConstraintTemplate} that corresponds to the given ID, or
     *         null if the ID is not mapped to any constraint.
     */
    public ConstraintTemplate getConstraintTamplate(String constraintID) {
        return constraintFactory.getConstraintTemplate(constraintID);
    }

    /**
     * Method read the constraints from a JSON object and updates the
     * {@link APEDomainSetup} object accordingly.
     *
     * @param constraintsJSON JSON object containing the constraints
     * @throws ConstraintFormatException exception in case of bad constraint json
     *                                   formatting
     */
    public void updateConstraints(JSONObject constraintsJSON) throws ConstraintFormatException {
        if (constraintsJSON == null) {
            return;
        }
        String constraintID = null;
        int currNode = 0;

        List<JSONObject> constraints = APEUtils.getListFromJson(constraintsJSON, CONSTR_JSON_TAG, JSONObject.class);

        /* Iterate through each constraint in the list */
        for (JSONObject jsonConstraint : APEUtils.safe(constraints)) {
            currNode++;
            /* READ THE CONSTRAINT */
            try {
                constraintID = jsonConstraint.getString(CONSTR_ID_TAG);
                ConstraintTemplate currConstrTemplate = getConstraintFactory()
                        .getConstraintTemplate(constraintID);
                if (currConstrTemplate == null) {
                    if (constraintID.equals("SLTLx")) {
                        String formulaSLTLx = jsonConstraint.getString(CONSTR_SLTLx);
                        if (formulaSLTLx == null) {
                            throw ConstraintFormatException.wrongNumberOfParameters(String
                                    .format("Error at constraint no: %d, constraint ID: %d", currNode, constraintID));
                        }
                        this.addSLTLxConstraint(formulaSLTLx);
                        continue;
                    } else {
                        throw ConstraintFormatException.wrongConstraintID(
                                String.format("Error at constraint no: %d, constraint ID: %d", currNode, constraintID));
                    }
                }

                List<ConstraintTemplateParameter> currTemplateParameters = currConstrTemplate.getParameters();

                List<JSONObject> jsonConstParam = APEUtils.getListFromJson(jsonConstraint, CONSTR_PARAM_JSON_TAG,
                        JSONObject.class);
                if (currTemplateParameters.size() != jsonConstParam.size()) {
                    throw ConstraintFormatException.wrongNumberOfParameters(
                            String.format("Error at constraint no: %d, constraint ID: %d", currNode, constraintID));
                }
                int paramNo = 0;
                List<TaxonomyPredicate> constraintParametes = new ArrayList<TaxonomyPredicate>();
                /* for each constraint parameter */
                for (JSONObject jsonParam : jsonConstParam) {
                    ConstraintTemplateParameter taxInstanceFromJson = currTemplateParameters.get(paramNo++);
                    TaxonomyPredicate currParameter = taxInstanceFromJson.readConstraintParameterFromJson(jsonParam,
                            this);
                    constraintParametes.add(currParameter);
                }

                ConstraintTemplateData currConstr = getConstraintFactory()
                        .generateConstraintTemplateData(constraintID, constraintParametes);
                if (constraintParametes.stream().anyMatch(Objects::isNull)) {
                    throw ConstraintFormatException.wrongParameter(
                            String.format("Error at constraint no: %d, constraint ID: %d", currNode, constraintID));
                } else {
                    this.addConstraintData(currConstr);
                }

            } catch (JSONException e) {
                throw ConstraintFormatException.badFormat(
                        String.format("Error at constraint no: %d, constraint ID: %d", currNode, constraintID));
            }

        }
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
                .safe(APEUtils.getListFromJson(toolAnnotationsFile, TOOLS_JSOM_TAG, JSONObject.class))) {
            currModule++;
            updateModuleFromJson(jsonModule);
        }
        if (currModule == 0) {
            System.err.println("No tools were annotated.");
            return false;
        }
        return true;
    }

    /**
     * Creates/updates a module from a tool annotation instance from a JSON file and
     * updates the list of modules ({@link AllModules}) in the domain accordingly.
     *
     * @param jsonModule JSON representation of a module
     * @return {@code true} if the domain was updated, false otherwise.
     * @throws JSONException Error if the JSON file was not properly formatted.
     */
    private boolean updateModuleFromJson(JSONObject jsonModule)
            throws JSONException, APEDimensionsException {
        String moduleIRI = APEUtils.createClassIRI(jsonModule.getString(APECoreConfig.getJsonTags("id")),
                ontologyPrefixIRI);
        if (allModules.get(moduleIRI) != null) {
            moduleIRI = moduleIRI + "[tool]";
        }
        String moduleLabel = jsonModule.getString(APECoreConfig.getJsonTags("label"));
        Set<String> taxonomyModules = new HashSet<String>(
                APEUtils.getListFromJson(jsonModule, APECoreConfig.getJsonTags("taxonomyOperations"), String.class));
        taxonomyModules = APEUtils.createIRIsFromLabels(taxonomyModules, ontologyPrefixIRI);
        /* Check if the referenced module taxonomy classes exist. */
        List<String> toRemove = new ArrayList<String>();
        for (String taxonomyModule : taxonomyModules) {
            String taxonomyModuleIRI = APEUtils.createClassIRI(taxonomyModule, ontologyPrefixIRI);
            if (allModules.get(taxonomyModuleIRI) == null) {
                System.err.println("Tool '" + moduleIRI + "' annotation issue. "
                        + "Referenced '" + APECoreConfig.getJsonTags("taxonomyOperations") + "': '" + taxonomyModuleIRI
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
            System.err.println("Tool '" + moduleIRI + "' annotation issue. "
                    + "None of the referenced '" + APECoreConfig.getJsonTags("taxonomyOperations")
                    + "' can be found in the Tool Taxonomy.");
            taxonomyModules.add(allModules.getRootModuleID());
        }

        String executionCode = null;
        try {
            executionCode = jsonModule.getJSONObject(APECoreConfig.getJsonTags("implementation"))
                    .getString(APECoreConfig.getJsonTags("code"));
        } catch (JSONException e) {
            /* Skip the execution code */
        }

        List<JSONObject> jsonModuleInput = APEUtils.getListFromJson(jsonModule, APECoreConfig.getJsonTags("inputs"),
                JSONObject.class);
        updateMaxNoToolInputs(jsonModuleInput.size());
        List<JSONObject> jsonModuleOutput = APEUtils.getListFromJson(jsonModule, APECoreConfig.getJsonTags("outputs"),
                JSONObject.class);
        updateMaxNoToolOutputs(jsonModuleOutput.size());

        List<Type> inputs = new ArrayList<Type>();
        List<Type> outputs = new ArrayList<Type>();

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
            System.err.println("Operation '" + "' was not included." + badDimension.getMessage());
            // System.out.println("Skipped " + (counterErrors ++) + " tool annotations.");
            return false;
        }

        String moduleExecutionImpl = null;
        if (executionCode != null && !executionCode.equals("")) {
            moduleExecutionImpl = executionCode;
        }
        if (inputs.isEmpty() && outputs.isEmpty()) {
            emptyTools.add(moduleLabel);
            System.out.println("Operation '" + "' was not included as it has no (valid) inputs and outputs specified.");
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
     * Gets ontology prefix IRI.
     *
     * @return the ontology prefix IRI
     */
    public String getOntologyPrefixIRI() {
        return ontologyPrefixIRI;
    }

    /**
     * Get the maximum number of inputs that a tool can have.
     *
     * @return the field {@link #maxNoToolInputs}.
     */
    public int getMaxNoToolInputs() {
        return maxNoToolInputs;
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
     * Get the maximum number of outputs that a tool can have.
     *
     * @return the field {@link #maxNoToolOutputs}.
     */
    public int getMaxNoToolOutputs() {
        return maxNoToolOutputs;
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
    public void addHelperPredicate(AuxiliaryPredicate helperPredicate) {
        helperPredicates.add(helperPredicate);

    }

    /**
     * Get information whether the domain was annotated under the strict rules of
     * the output dependency.
     * 
     * @return {@code true} if the strict rules apply, {@code false} otherwise.
     */
    public boolean getUseStrictToolAnnotations() {
        return useStrictToolAnnotations;
    }

    /**
     * Get the list of helper predicates used in the domain.
     * 
     * @return List of the auxiliary helper predicates.
     */
    public List<AuxiliaryPredicate> getHelperPredicates() {
        return helperPredicates;
    }

}
