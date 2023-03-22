package nl.uu.cs.ape.models.enums;

/**
 * Defines the values describing the states in the workflow.
 * 
 * 
 * @author Vedran Kasalica
 */
public enum AtomType {

    /**
     * Depicts usage of a tool/module.
     */
    MODULE("module"),

    /**
     * Depicts the creation of a new type instance to the memory.
     */
    MEMORY_TYPE("memType"),

    /**
     * Depicts the usage of an already created type instance. Usually as an input
     * for a tool.
     */
    USED_TYPE("usedType"),

    /**
     * Depicts the usage of an already created type instance, as an input for a
     * tool. It references the created data type.
     */
    MEM_TYPE_REFERENCE("memRef"),

    /**
     * Depicts the dependency between two data instances (states), i.e., 'R'
     * relation in SLTLx. It depicts that a data instance is dependent (was derived
     * from) on another data instance.
     */
    R_RELATON("r_rel"),

    /**
     * This is a reflexive function, where X and Y are in relation IFF they are the
     * same data object
     */
    IDENTITI_RELATION("is_rel");

    private final String text;

    private AtomType(String s) {
        this.text = s;
    }

    public String toString() {
        return this.text;
    }

    /**
     * Check if the Atom represent a unary property.
     * 
     * @return {@code true} if it is a unary property, {@code false} otherwise.
     */
    public boolean isUnaryProperty() {
        if (this.equals(MODULE) | this.equals(MEMORY_TYPE) | this.equals(USED_TYPE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the Atom represent a binary relation.
     * 
     * @return {@code true} if it is a unary property, {@code false} otherwise.
     */
    public boolean isBinaryRel() {
        if (this.equals(R_RELATON) | this.equals(MEM_TYPE_REFERENCE) | this.equals(IDENTITI_RELATION)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets string shortcut.
     *
     * @param elem        the element
     * @param blockNumber the block number
     * @param stateNumber the state number
     * @return the string shortcut
     */
    public static String getStringShortcut(AtomType elem, Integer blockNumber, int stateNumber) {

        if (elem == MODULE) {
            return "Tool" + stateNumber;
        } else if (elem == MEMORY_TYPE) {
            return "MemT" + blockNumber + "." + stateNumber;
        } else if (elem == USED_TYPE) {
            return "UsedT" + blockNumber + "." + stateNumber;
        } else if (elem == null) {
            return "nullMem";
        }
        /** In case it is a element that should not have short string representation. */
        return "AtomType error.";
    }

}
