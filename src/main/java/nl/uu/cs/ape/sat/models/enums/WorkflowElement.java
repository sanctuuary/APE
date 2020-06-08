package nl.uu.cs.ape.sat.models.enums;

/**
 * Defines the values describing the states in the workflow.
 * <p>
 * Values: [{@code TOOL}, {@code MEMORY_TYPE}, {@code USED_TYPE}, {@code MEM_TYPE_REFERENCE}]
 */
public enum WorkflowElement {

    /**
     * Depicts usage of a tool/module.
     */
    MODULE,

    /**
     * Depicts the creation of a new type instance to the memory.
     */
    MEMORY_TYPE,

    /**
     * Depicts the usage of an already created type instance. Usually as an input for a tool.
     */
    USED_TYPE,

    /**
     * Depicts the usage of an already created type instance, as an input for a tool. It references the created data type.
     */
    MEM_TYPE_REFERENCE;

    /**
     * Gets string shortcut.
     *
     * @param elem        the elem
     * @param blockNumber the block number
     * @param stateNumber the state number
     * @return the string shortcut
     */
    public static String getStringShortcut(WorkflowElement elem, Integer blockNumber, int stateNumber) {

        if (elem == MODULE) {
            return "Tool" + stateNumber;
        } else if (elem == MEMORY_TYPE) {
            return "MemT" + blockNumber + "." + stateNumber;
        } else if (elem == USED_TYPE) {
            return "UsedT" + blockNumber + "." + stateNumber;
        }
        return "NaN";
    }
}
