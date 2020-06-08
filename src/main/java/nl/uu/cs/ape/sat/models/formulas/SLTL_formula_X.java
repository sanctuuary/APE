package nl.uu.cs.ape.sat.models.formulas;

import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The type Sltl formula x.
 */
public abstract class SLTL_formula_X extends SLTL_formula {

    /**
     * Instantiates a new Sltl formula x.
     *
     * @param predicate the predicate
     */
    public SLTL_formula_X(TaxonomyPredicate predicate) {
        super(predicate);
    }

    /**
     * Instantiates a new Sltl formula x.
     *
     * @param sign    the sign
     * @param formula the formula
     */
    public SLTL_formula_X(boolean sign, TaxonomyPredicate formula) {
        super(sign, formula);
    }
}
