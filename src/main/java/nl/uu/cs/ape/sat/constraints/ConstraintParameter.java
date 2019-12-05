/**
 * 
 */
package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;

/**
 * The {@code ConstraintParameter} class is used to represent a parameter of a constraint.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintParameter {

	private List<PredicateLabel> parameterTypes;
	
	public ConstraintParameter(List<PredicateLabel> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	
	public List<PredicateLabel> getParameterTypes(){
		return this.parameterTypes;
	}
	
}
