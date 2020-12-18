package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDimensionsException;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The {@code ConstraintTemplateParameter} class is used to represent a parameter of a
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
	 * Generate a taxonomy instance (tool or type) that is defined based on one or
	 * more dimensions that describe it. Based on the constraint, it will either generate a Type or Module object.
	 * 
	 * @param jsonParam
	 * @param domainSetup
	 * @return A {@link Type} or {@link AbstractModule} object that represent the data instance given as the parameter.
	 * @throws JSONException if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types/modules are not well defined
	 */
	public TaxonomyPredicate readConstraintParameterFromJson(JSONObject jsonParam, APEDomainSetup domainSetup) throws JSONException, APEDimensionsException {
		if(parameterTypes.get(0) instanceof Type) {
			return Type.taxonomyInstanceFromJson(jsonParam, domainSetup, false);
		} else {
			return Module.taxonomyInstanceFromJson(jsonParam, domainSetup);
		}
	}


}
