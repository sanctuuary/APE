package nl.uu.cs.ape.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.constraints.ConstraintFactory;
import nl.uu.cs.ape.constraints.ConstraintFormatException;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.constraints.ConstraintTemplateParameter;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.ConstraintTemplateData;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEUtils;

public class UserSpecification {

    private static final String CONSTR_JSON_TAG = "constraints";
    private static final String CONSTR_ID_TAG = "constraintid";
    private static final String CONSTR_SLTLx = "formula";
    private static final String CONSTR_PARAM_JSON_TAG = "parameters";

    /**
     * Object used to create temporal constraints.
     */
    private ConstraintFactory constraintFactory;

    /**
     * List of data gathered from the constraint file.
     */
    private List<ConstraintTemplateData> unformattedConstr;

    /**
     * List of helper predicates that are used to encode the constraints.
     */
    private List<AuxiliaryPredicate> helperPredicates;

    /**
     * List of constraints provided as Strings in SLTLx format.
     */
    private List<String> constraintsSLTLx;

    public UserSpecification() {
        this.unformattedConstr = new ArrayList<>();
        this.constraintsSLTLx = new ArrayList<>();
        this.helperPredicates = new ArrayList<>();
        this.constraintFactory = new ConstraintFactory();
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
     * Method reads the constraints from a JSON object and updates the
     * {@link Domain} object accordingly.
     *
     * @param constraintsJSONArray JSON array containing the constraints
     * @throws ConstraintFormatException exception in case of bad constraint json
     *                                   formatting
     */
    public void updateConstraints(JSONArray constraintsJSONArray) throws ConstraintFormatException {
        if (constraintsJSONArray == null) {
            return;
        }
        String constraintID = null;
        int currNode = 0;

        List<JSONObject> constraints = APEUtils.getListFromJsonList(constraintsJSONArray, JSONObject.class);

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
                            throw ConstraintFormatException.wrongNumberOfParameters(
                                    getConstrErrorMsg(currNode, constraintID));
                        }
                        this.addSLTLxConstraint(formulaSLTLx);
                        continue;
                    } else {
                        throw ConstraintFormatException.wrongConstraintID(
                                getConstrErrorMsg(currNode, constraintID));
                    }
                }

                List<ConstraintTemplateParameter> currTemplateParameters = currConstrTemplate.getParameters();

                List<JSONObject> jsonConstParam = APEUtils.getListFromJson(jsonConstraint, CONSTR_PARAM_JSON_TAG,
                        JSONObject.class);
                if (currTemplateParameters.size() != jsonConstParam.size()) {
                    throw ConstraintFormatException.wrongNumberOfParameters(
                            getConstrErrorMsg(currNode, constraintID));
                }
                int paramNo = 0;
                List<TaxonomyPredicate> constraintParameters = new ArrayList<>();
                /* for each constraint parameter */
                for (JSONObject jsonParam : jsonConstParam) {
                    ConstraintTemplateParameter taxInstanceFromJson = currTemplateParameters.get(paramNo++);
                    TaxonomyPredicate currParameter = taxInstanceFromJson.readConstraintParameterFromJson(jsonParam,
                            this);
                    constraintParameters.add(currParameter);
                }

                ConstraintTemplateData currConstr = getConstraintFactory()
                        .generateConstraintTemplateData(constraintID, constraintParameters);
                if (constraintParameters.stream().anyMatch(Objects::isNull)) {
                    throw ConstraintFormatException.wrongParameter(
                            getConstrErrorMsg(currNode, constraintID));
                } else {
                    this.addConstraintData(currConstr);
                }

            } catch (JSONException e) {
                throw ConstraintFormatException.badFormat(
                        getConstrErrorMsg(currNode, constraintID));
            }

        }
    }

    private String getConstrErrorMsg(int currNode, String constraintID) {
        return String.format("Error at constraint no: %d, constraint ID: %s", currNode, constraintID);
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
    public void initializeConstraints(AllModules allModules, AllTypes allTypes) {
        constraintFactory.initializeConstraints(allModules, allTypes);
    }

    /**
     * Return the {@link ConstraintTemplate} that corresponds to the given ID, or
     * null if the constraint with the given ID does not exist.
     *
     * @param constraintID ID of the {@code ConstraintTemplate}.
     * @return The {@code ConstraintTemplate} that corresponds to the given ID, or
     *         null if the ID is not mapped to any constraint.
     */
    public ConstraintTemplate getConstraintTemplate(String constraintID) {
        return constraintFactory.getConstraintTemplate(constraintID);
    }
}
