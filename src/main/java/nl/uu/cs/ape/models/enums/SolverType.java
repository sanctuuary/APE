package nl.uu.cs.ape.models.enums;

/**
 * Defines the values describing the quantity of covered cases.
 * <p>
 * Values: [{@code NONE}, {@code ONE}, {@code ALL}]
 */
public enum SolverType {

    /**
     * SAT solver should be used.
     */
    SAT,

    /**
     * SMT solver should be used.
     */
    SMT
}
