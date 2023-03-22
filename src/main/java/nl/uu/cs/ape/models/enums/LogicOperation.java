package nl.uu.cs.ape.models.enums;

/**
 * The {@code LogicOperation} class is used to depict logical OR and AND
 * operators.
 *
 * @author Vedran Kasalica
 */
public enum LogicOperation {

    /**
     * Or logic operation.
     */
    OR("disjunction", "or", "|"),

    /**
     * And logic operation.
     */
    AND("conjunction", "and", "&");

    private final String longString;
    private final String shortString;
    private final String symbol;

    private LogicOperation(String longString, String shortString, String symbol) {
        this.longString = longString;
        this.shortString = shortString;
        this.symbol = symbol;
    }

    /**
     * @return A string corresponding to the logical operation.
     */
    public String toString() {
        return longString;
    }

    /**
     * Get sign as a 1 char string - '|' or '&amp;'.
     *
     * @return A simple sign corresponding to the logical operation.
     */
    public String toStringSign() {
        return symbol;
    }

    /**
     * Get a short string representing the sign - 'or' or 'and'.
     *
     * @return A short string corresponding to the logical operation.
     */
    public String toShortString() {
        return shortString;
    }
}
