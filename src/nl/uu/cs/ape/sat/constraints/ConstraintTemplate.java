/**
 * 
 */
package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;

/**
 * The {@code ConstraintTemplate} class is an abstract class used to represent a
 * general constraint template.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class ConstraintTemplate {

	/**
	 * Constraint ID.
	 */
	int constraintID;
	/**
	 * Number of parameters the constraint requires.
	 */
	int parametersNo;
	/**
	 * Description of the constraint.
	 */
	String description;

	/**
	 * 
	 * @param constraintID
	 *            - Constraint ID.
	 * @param parametersNo
	 *            - Number of parameters the constraint requires.
	 * @param description
	 *            - Description of the constraint.
	 */
	public ConstraintTemplate(int parametersNo, String description) {
		this.constraintID = 0;
		this.parametersNo = parametersNo;
		this.description = description;
	}

	public void setConstraintID(int constraintID) {
		this.constraintID = constraintID;
	}

	public String getDescription() {
		return this.description;
	}

	public int getNoOfParameters() {
		return this.parametersNo;
	}

	public int getConstraintID() {
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
			TypeAutomaton typeAutomaton, AtomMapping mappings);

	/**
	 * Print the template for encoding the constraint, containing the template ID, description and required number of parameters.
	 * @return String representing the description.
	 */
	public String printConstraintCode() {
		return constraintID + ";" + description + ";" + parametersNo + "\n";
	}
	
	// TODO: define implementation of each constraint and a general template
	// constraint that will be modified

}
