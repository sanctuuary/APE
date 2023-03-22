package nl.uu.cs.ape.constraints;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEDomainSetup;

import java.util.List;

/**
 * The {@code ConstraintTemplate} class is an abstract class used to represent a
 * general constraint template.
 *
 * @author Vedran Kasalica
 */
public abstract class ConstraintTemplate {

    /**
     * Identification String of the constraint template.
     */
    String constraintID;

    /**
     * Description of the constraint.
     */
    String description;

    /**
     * List of all the parameters of the constraint.
     */
    List<ConstraintTemplateParameter> parameters;

    /**
     * Instantiates a new Constraint template.
     *
     * @param id          Constraint ID.
     * @param parameters  Set of {@link ConstraintTemplateParameter} the constraint
     *                    requires.
     * @param description Description of the constraint.
     */
    public ConstraintTemplate(String id, List<ConstraintTemplateParameter> parameters, String description) {
        this.constraintID = id;
        this.parameters = parameters;
        this.description = description;
    }

    /**
     * Sets constraint id.
     *
     * @param constraintID the constraint id
     */
    public void setConstraintID(String constraintID) {
        this.constraintID = constraintID;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets no of parameters.
     *
     * @return the no of parameters
     */
    public int getNoOfParameters() {
        return this.parameters.size();
    }

    /**
     * Gets constraint id.
     *
     * @return the constraint id
     */
    public String getConstraintID() {
        return this.constraintID;
    }

    /**
     * Gets parameter templates that are expected to be provided. The template is
     *
     * @return The field {@link #parameters}.
     */
    public List<ConstraintTemplateParameter> getParameters() {
        return parameters;
    }

    /**
     * Returns the Constraint parameter at the specified position in this list.
     *
     * @param index Index of the element to return.
     * @return The Constraint parameter at the specified position in this list.
     */
    public ConstraintTemplateParameter getParameter(int index) {
        return parameters.get(index);
    }

    /**
     * Method will return a CNF representation of the constraint in DIMACS format.
     * It will use predefined mapping function and all the atoms will be mapped to
     * numbers accordingly.
     *
     * @param list            Array of input parameters.
     * @param domainSetup     Domain with all the modules.
     * @param moduleAutomaton Module automaton.
     * @param typeAutomaton   Type automaton.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the constraint. null in case of
     *         incorrect number of constraint parameters.
     */
    public abstract String getConstraint(List<TaxonomyPredicate> list, APEDomainSetup domainSetup,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings);

    /**
     * Print the template for encoding the constraint, containing the template ID,
     * description and required number of parameters.
     *
     * @return String representing the description.
     */
    public String printConstraintCode() {

        return toJSON().toString(3) + ",\n";
    }

    /**
     * Convert the constraint template to a JSONObject.
     *
     * @return the JSONObject representing the constraint template.
     */
    public JSONObject toJSON() {
        JSONObject currJson = new JSONObject();
        currJson.put("constraintID", constraintID);
        currJson.put("description", description);
        JSONArray params = new JSONArray();
        for (ConstraintTemplateParameter param : parameters) {
            params.put(param.toJSON());
        }
        currJson.put("parameters", params);
        return currJson;
    }

    /**
     * Throwing an error in case of having a wrong number of parameters in the
     * constraints file.
     *
     * @param givenParameters Provided number of parameters.
     */
    public void throwParametersError(int givenParameters) {
        System.err.println(
                "Error in the constraints file.\nConstraint: " + this.description + "\nExpected number of parameters: "
                        + parameters.size() + ".\nProvided number of parameters: " + givenParameters);

    }

}
