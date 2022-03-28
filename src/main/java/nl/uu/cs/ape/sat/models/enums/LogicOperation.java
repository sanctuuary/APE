package nl.uu.cs.ape.sat.models.enums;

/**
 * The {@code LogicOperation} class is used to depict logical OR and AND operators.
 *
 * @author Vedran Kasalica
 */
public enum LogicOperation {

    /**
     * Or logic operation.
     */
    OR,

    /**
     * And logic operation.
     */
    AND;

    /**
     * @return A string corresponding to the logical operation.
     */
    public String toString() {
        if (this == LogicOperation.OR) {
            return "disjunction";
        } else {
            return "conjunction";
        }
    }

    /**
     * To string sign string.
     *
     * @return A simple sign corresponding to the logical operation.
     */
    public String toStringSign() {
        if (this == LogicOperation.OR) {
            return "|";
        } else {
            return "&";
        }
    }
}
