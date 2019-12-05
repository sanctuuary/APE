/**
 * 
 */
package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;

/**
 * The {@code ConstraintTemplate} class is an abstract class used to represent a
 * general constraint template.
 * 
 * @author Vedran Kasalica
 *
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
	List<ConstraintParameter> parameters;

	/**
	 * 
	 * @param constraintID
	 *            - Constraint ID.
	 * @param parametersNo
	 *            - Number of parameters the constraint requires.
	 * @param description
	 *            - Description of the constraint.
	 */
	public ConstraintTemplate(String id, List<ConstraintParameter> parameters, String description) {
		this.constraintID = id;
		this.parameters = parameters;
		this.description = description;
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
	 * Method will return a CNF representation of the constraint in DIMACS format.
	 * It will use predefined mapping function and all the atoms will be mapped to
	 * numbers accordingly.
	 * 
	 * @param parameters
	 *            - array of input parameters
	 * @param allModules
	 *            - list of all the modules
	 * @param moduleAutomaton
	 *            - module automaton
	 * @param typeAutomaton
	 *            - type automaton
	 * @param mappings
	 *            - set of the mappings for the literals
	 * @return {@link String} CNF representation of the constraint. {@code NULL} in
	 *         case of incorrect number of constraint parameters.
	 */
	public abstract String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMappings mappings);

	/**
	 * Print the template for encoding the constraint, containing the template ID, description and required number of parameters.
	 * @return String representing the description.
	 */
	public String printConstraintCode() {
		JSONArray params = new JSONArray();
		for(int i = 1; i <= parameters.size(); i++) {
			params.put("${parameter_" + i + "}");
		}
		JSONObject constJson = new JSONObject();
		constJson.put("constraintid", constraintID);
		constJson.put("parameters", params);
		constJson.put("description", description);
		
		return constJson.toString(3) + ",\n";
	}

	/**
	 * Throwing an error in case of having a wrong number of parameters in the constraints file.
	 * @param givenParameters Provided number of parameters
	 */
	public void throwParametersError(int givenParameters) {
		System.err.println("Error in the constraints file.\nConstraint: " + this.description + "\nExpected number of parameters: " + parameters.size() + ".\nProvided number of parameters: " + givenParameters);
		
	}
	
	// TODO: define implementation of each constraint and a general template
	// constraint that will be modified

}
