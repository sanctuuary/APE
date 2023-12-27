package nl.uu.cs.ape.constraints;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.domain.APEDimensionsException;
import nl.uu.cs.ape.domain.Domain;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code ConstraintTemplateParameter} class is used to represent a
 * parameter of a
 * constraint.
 *
 * @author Vedran Kasalica
 */
@Slf4j
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
		if (parameterTypes != null) {
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
			log.warn("Cannot add null as a dimension that characterises a Taxonomy Instance.");
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
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		for (TaxonomyPredicate predicate : parameterTypes) {
			json.put(predicate.getRootNodeID(), predicate.getPredicateID());
		}
		return json;
	}

	/**
	 * Generate a taxonomy instance (tool or type) that is defined based on one or
	 * more dimensions that describe it. Based on the constraint, it will either
	 * generate a Type or Module object.
	 * 
	 * @param jsonParam   json object
	 * @param domainSetup - domain model
	 * @return A {@link Type} or {@link AbstractModule} object that represent the
	 *         data instance given as the parameter.
	 * @throws JSONException          if the given JSON is not well formatted
	 * @throws APEDimensionsException if the referenced types/modules are not well
	 *                                defined
	 */
	public TaxonomyPredicate readConstraintParameterFromJson(JSONObject jsonParam, Domain domainSetup)
			throws JSONException, APEDimensionsException {
		if (parameterTypes.get(0) instanceof Type) {
			return Type.taxonomyInstanceFromJson(jsonParam, domainSetup, false);
		} else {
			return Module.taxonomyInstanceFromJson(jsonParam, domainSetup);
		}
	}

}
