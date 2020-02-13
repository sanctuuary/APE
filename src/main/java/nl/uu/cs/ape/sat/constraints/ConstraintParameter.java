/**
 * 
 */
package nl.uu.cs.ape.sat.constraints;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code ConstraintParameterX} class is used to represent a parameter of a constraint.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintParameter {

	/** List of all the taxonomy types that correspond to the current constraint parameter. */
	private List<TaxonomyPredicate> parameterTypes;
	
	public ConstraintParameter(List<TaxonomyPredicate> parameterTypes) {
		if(parameterTypes != null) {
		this.parameterTypes = parameterTypes;
		} else {
			this.parameterTypes = new ArrayList<TaxonomyPredicate>();
		}
	}
	
	public ConstraintParameter() {
		this.parameterTypes = new ArrayList<TaxonomyPredicate>();
	}
	
	/** Add a new taxonomy predicates to describe the constraint parameter. */
	public void addParameter(TaxonomyPredicate newParam) {
		this.parameterTypes.add(newParam);
	}
	
	/** Return all the taxonomy predicates that describe the constraint parameter. */
	public List<TaxonomyPredicate> getParameterTypes(){
		return this.parameterTypes;
	}
	
	public String toString() {
		String print = "&";
		for(TaxonomyPredicate param : parameterTypes) {
			print = print.concat(param.toShortString()).concat("&");
		}
		return print;
	}
	
}
