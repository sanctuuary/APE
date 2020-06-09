package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code ConstraintParameterX} class is used to represent a parameter of a constraint.
 *
 * @author Vedran Kasalica
 */
public class ConstraintParameter {

    /**
     * List of all the taxonomy types that correspond to the current constraint parameter.
     */
    private List<TaxonomyPredicate> parameterTypes;

    /**
     * Instantiates a new Constraint parameter.
     *
     * @param parameterTypes the parameter types
     */
    public ConstraintParameter(List<TaxonomyPredicate> parameterTypes) {
        if (parameterTypes != null) {
            this.parameterTypes = parameterTypes;
        } else {
            this.parameterTypes = new ArrayList<TaxonomyPredicate>();
        }
    }

    /**
     * Instantiates a new Constraint parameter.
     */
    public ConstraintParameter() {
        this.parameterTypes = new ArrayList<TaxonomyPredicate>();
    }

    /**
     * Add parameter.
     *
     * @param newParam Add a new taxonomy predicates to describe the constraint parameter.
     */
    public void addParameter(TaxonomyPredicate newParam) {
        this.parameterTypes.add(newParam);
    }

    /**
     * Gets parameter types.
     *
     * @return All the taxonomy predicates that describe the constraint parameter.
     */
    public List<TaxonomyPredicate> getParameterTypes() {
        return this.parameterTypes;
    }

    public String toString() {
        String print = "&";
        for (TaxonomyPredicate param : parameterTypes) {
            print = print.concat(param.toShortString()).concat("&");
        }
        return print;
    }
}
