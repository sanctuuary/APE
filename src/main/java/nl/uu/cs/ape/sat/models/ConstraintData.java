/**
 * 
 */
package nl.uu.cs.ape.sat.models;

import java.util.List;

/**
 * The {#code ConstraintData} class is used to store the data describing each constrain.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintData {

	private String constraintID;
	private String[] parameters;
	
	public ConstraintData(String constraintID,  String[] parameters) {
		this.constraintID = constraintID;
		this.parameters = new String[parameters.length];
		for(int i=0; i < parameters.length; i++) {
			this.parameters[i] = parameters[i];
		}
	}
	
	public ConstraintData(String constraintID,  List<String> parameters) {
		this.constraintID = constraintID;
		this.parameters = new String[parameters.size()];
		for(int i=0; i < parameters.size(); i++) {
			this.parameters[i] = parameters.get(i);
		}
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
	public String[] getParameters() {
		return parameters;
	}
	
}
