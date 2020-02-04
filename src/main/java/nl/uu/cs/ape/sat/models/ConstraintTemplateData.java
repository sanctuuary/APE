/**
 * 
 */
package nl.uu.cs.ape.sat.models;

import java.util.List;

import nl.uu.cs.ape.sat.constraints.ConstraintParameter;

/**
 * The {#code ConstraintTemplateData} class is used to store the data describing each constrain.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintTemplateData {

	private String constraintID;
	private List<ConstraintParameter> parameters;
	
	public ConstraintTemplateData(String constraintID,  List<ConstraintParameter> parameters) {
		this.constraintID = constraintID;
		this.parameters = parameters;
	}

	/**
	 * @return the constraintID
	 */
	public String getConstraintID() {
		return constraintID;
	}

	/**
	 * @return the parameters
	 */
	public List<ConstraintParameter> getParameters() {
		return parameters;
	}
	
}
