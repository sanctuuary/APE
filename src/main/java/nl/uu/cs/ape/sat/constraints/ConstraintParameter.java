/**
 * 
 */
package nl.uu.cs.ape.sat.constraints;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code ConstraintParameter} class is used to represent a parameter of a constraint.
 *
 * @author Vedran Kasalica
 *
 */
public class ConstraintParameter {

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
	
	public void addParameter(TaxonomyPredicate newParam) {
		this.parameterTypes.add(newParam);
	}
	
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
