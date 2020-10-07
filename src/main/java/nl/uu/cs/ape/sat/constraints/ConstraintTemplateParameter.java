package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import org.json.JSONObject;

/**
 * The {@code ConstraintParameterX} class is used to represent a parameter of a
 * constraint.
 *
 * @author Vedran Kasalica
 */
public class ConstraintTemplateParameter {

	/**
	 * List of all the taxonomy types that correspond to the current constraint
	 * parameter.
	 */
	private List<TaxonomyPredicate> parameterTypes;

	/**
	 * Instantiates a new Constraint parameter.
	 *
	 * @param parameterTypes the parameter types
	 */
	public ConstraintTemplateParameter(List<TaxonomyPredicate> parameterTypes) {
		if(parameterTypes != null) {
			this.parameterTypes = parameterTypes;
		} else {
			this.parameterTypes = new ArrayList<TaxonomyPredicate>();
		}
	}

	/**
	 * Instantiates a new Constraint parameter.
	 */
	public ConstraintTemplateParameter() {
		this.parameterTypes = new ArrayList<TaxonomyPredicate>();
	}

	/**
	 * Add a parameter that is not NULL.
	 *
	 * @param newParam Add a new taxonomy predicates to describe the constraint
	 *                 parameter.
	 */
	public void addParameter(TaxonomyPredicate newParam) {
		if (newParam == null) {
			System.err.println("Cannot add null as a dimension that characterises a Taxonomy Instance.");
		} else {
			this.parameterTypes.add(newParam);
		}
	}

	/**
	 * Gets parameter types.
	 *
	 * @return All the taxonomy predicates that describe the constraint parameter.
	 */
	public List<TaxonomyPredicate> getParameterTemplateTypes() {
		return this.parameterTypes;
	}
	

	public String toString() {
		String print = "{";
		for (TaxonomyPredicate param : parameterTypes) {
			print = print.concat(param.getRootNodeID()).concat(":").concat(param.toShortString()).concat(",");
		}
		return APEUtils.removeLastChar(print) + "}";
	}

	/**
	 * Returns a JSONObject in format that it should have been provided.
	 * @return JSONObject
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		for(TaxonomyPredicate predicate : parameterTypes) {
			json.put(predicate.getRootNodeID(), predicate.getPredicateID());
		}
		return json;
	}

	/**
	 * Calls Type or Module creator, based on the parameter dependencies.
	 * @param jsonParam
	 * @param domainSetup
	 * @return
	 */
	public TaxonomyPredicate taxonomyInstanceFromJson(JSONObject jsonParam, APEDomainSetup domainSetup) {
		if(parameterTypes.get(0) instanceof Type) {
			return Type.taxonomyInstanceFromJson(jsonParam, domainSetup);
		} else {
			return Module.taxonomyInstanceFromJson(jsonParam, domainSetup);
		}
	}


}
