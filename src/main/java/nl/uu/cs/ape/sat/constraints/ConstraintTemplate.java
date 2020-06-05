package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * The {@code ConstraintTemplate} class is an abstract class used to represent a general constraint template.
 *
 * @author Vedran Kasalica
 */
public abstract class ConstraintTemplate {

    /** Identification String of the constraint template. */
    String constraintID;

    /** Description of the constraint. */
    String description;

    /** List of all the parameters of the constraint. */
    List<ConstraintParameter> parameters;

    /** Implementation function of the constraint.*/
    Runnable function;

    /**
     * @param id Constraint ID.
     * @param parameters Set of {@link ConstraintParameter} the constraint requires.
     * @param description Description of the constraint.
     */
    public ConstraintTemplate(String id, List<ConstraintParameter> parameters, String description) {
        this.constraintID = id;
        this.parameters = parameters;
        this.description = description;
        // this.function = function;
    }

    public void setConstraintID(String constraintID) {
        this.constraintID = constraintID;
    }

    public String getDescription() {
        return this.description;
    }

    public int getNoOfParameters() {
        return this.parameters.size();
    }

    public String getConstraintID() {
        return this.constraintID;
    }

    /**
     * @return The field {@link #parameters}.
     */
    public List<ConstraintParameter> getParameters() {
        return parameters;
    }

    /**
     * Returns the Constraint parameter at the specified position in this list.
     *
     * @param index Index of the element to return.
     * @return The Constraint parameter at the specified position in this list.
     */
    public ConstraintParameter getParameter(int index) {
        return parameters.get(index);
    }

    /**
     * Method will return a CNF representation of the constraint in DIMACS format.
     * It will use predefined mapping function and all the atoms will be mapped to
     * numbers accordingly.
     *
     * @param parameters Array of input parameters.
     * @param domainSetup Domain with all the modules.
     * @param moduleAutomaton Module automaton.
     * @param typeAutomaton Type automaton.
     * @param mappings Set of the mappings for the literals.
     * @return The {@link String} CNF representation of the constraint. {@code NULL} in case of incorrect number of constraint parameters.
     */
    public abstract String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                         TypeAutomaton typeAutomaton, AtomMappings mappings);

    /**
     * Print the template for encoding the constraint, containing the template ID, description and required number of parameters.
     *
     * @return {@link String} representing the description.
     */
    public String printConstraintCode() {

        return toJSON().toString(3) + ",\n";
    }

    public JSONObject toJSON() {
        JSONObject currJson = new JSONObject();
        currJson.put("constraintID", constraintID);
        currJson.put("description", description);
        JSONArray params = new JSONArray();
        for (ConstraintParameter param : parameters) {
            JSONArray oneParamDimensions = new JSONArray();
            for (TaxonomyPredicate pred : param.getParameterTypes()) {
                oneParamDimensions.put(pred.getPredicateID());
            }
            params.put(oneParamDimensions);
        }
        currJson.put("parameters", params);
        return currJson;
    }

    /**
     * Throwing an error in case of having a wrong number of parameters in the constraints file.
     *
     * @param givenParameters Provided number of parameters.
     */
    public void throwParametersError(int givenParameters) {
        System.err.println("Error in the constraints file.\nConstraint: " + this.description + "\nExpected number of parameters: " + parameters.size() + ".\nProvided number of parameters: " + givenParameters);

    }

    // TODO: define implementation of each constraint and a general template
    // constraint that will be modified
}
