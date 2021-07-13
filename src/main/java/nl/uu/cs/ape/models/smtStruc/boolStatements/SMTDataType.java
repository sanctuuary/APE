package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.logic.constructs.APEPredicate;

/**
 * Defines the values describing the states in the workflow.
 * <p>
 * Values: [{@code MODULE}, {@code MEMORY_TYPE}, {@code USED_TYPE}, {@code MEM_TYPE_REFERENCE}]
 * 
 * 
 *  @author Vedran Kasalica
 */
public enum SMTDataType implements APEPredicate {

    /**
     * Depicts a tool/module.
     */
    MODULE("module"),

    /**
     * Depicts a data type.
     */
    TYPE("type"),

    /**
     * State that represents tools used in the workflow.
     */
    MODULE_STATE("moduleState"),

    /**
     * State that represents data types available in memory.
     */
    MEMORY_TYPE_STATE("memTypeState"),
    
	/**
     * State that represents data types used by tools.
     */
    USED_TYPE_STATE("usedTypeState");

	
	 private final String text;

	 private SMTDataType(String s) {
         this.text = s;
     }
	
	    
	 public String toString() {
	     return this.text;
	 }
	 

}
