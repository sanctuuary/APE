/**
 * 
 */
package nl.uu.cs.ape.sat.models;

import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {#code ConstraintTemplateData} class is used to store the data describing each constrain.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintTemplateData {

	private String constraintID;
	private List<TaxonomyPredicate> parameters;
	
	public ConstraintTemplateData(String constraintID,  List<TaxonomyPredicate> parameters) {
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
	public List<TaxonomyPredicate> getParameters() {
		return parameters;
	}
	
}
