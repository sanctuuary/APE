package nl.uu.cs.ape.models.enums;

import nl.uu.cs.ape.models.logic.constructs.APEPredicate;

/**
 * Defines the values describing the states in the workflow.
 * <p>
 * Values: [{@code MODULE}, {@code MEMORY_TYPE}, {@code USED_TYPE}, {@code MEM_TYPE_REFERENCE}]
 * 
 * 
 *  @author Vedran Kasalica
 */
public enum WorkflowElement implements APEPredicate {

    /**
     * Depicts usage of a tool/module.
     */
    MODULE("module"),

    /**
     * Depicts the creation of a new type instance to the memory.
     */
    MEMORY_TYPE("memType"),

    /**
     * Depicts the usage of an already created type instance. Usually as an input for a tool.
     */
    USED_TYPE("usedType"),

    /**
     * Depicts the usage of an already created type instance, as an input for a tool. It references the created data type.
     */
    MEM_TYPE_REFERENCE("memRef"),
    
	/**
     * Depicts the dependency between two data instances (states). It depicts that a data instance is dependent (was derived from) on another data instance.
     */
    TYPE_DEPENDENCY("typeDep");

	
	 private final String text;

	 private WorkflowElement(String s) {
         this.text = s;
     }
	
	    
	 public String toString() {
	     return this.text;
	 }
	 
    /**
     * Gets string shortcut.
     *
     * @param elem        the element
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
        } else if(elem == null) {
        	return "nullMem";
        }
        return "NaN";
    }

}
