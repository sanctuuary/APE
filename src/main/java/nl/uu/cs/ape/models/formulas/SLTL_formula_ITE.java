package nl.uu.cs.ape.models.formulas;

import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * The type Sltl formula ite.
 */
public abstract class SLTL_formula_ITE extends SLTL_formula {

    /**
     * Instantiates a new Sltl formula ite.
     *
     * @param predicate the predicate
     */
    public SLTL_formula_ITE(TaxonomyPredicate predicate) {
        super(predicate);
    }

    /**
     * Instantiates a new Sltl formula ite.
     *
     * @param sign    the sign
     * @param formula the formula
     */
    public SLTL_formula_ITE(boolean sign, TaxonomyPredicate formula) {
        super(sign, formula);
    }
}
