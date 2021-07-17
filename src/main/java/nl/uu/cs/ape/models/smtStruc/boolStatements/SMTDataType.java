package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.logic.constructs.APEPredicate;

/**
 * Defines the values describing the states in the workflow.
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
    USED_TYPE_STATE("usedTypeState"),
    
    /**
     * Integers represent the type states in the system.
     */
    INTEGER("Int"),
	
	/**
     * BitVectors represent the states in the system. 
     */
    BITVECTOR1("(_ BitVec 1)"),
    BITVECTOR2("(_ BitVec 2)"),
    BITVECTOR3("(_ BitVec 3)"),
    BITVECTOR4("(_ BitVec 4)"),
    BITVECTOR5("(_ BitVec 5)"),
    BITVECTOR6("(_ BitVec 6)"),
    BITVECTOR7("(_ BitVec 7)"),
    BITVECTOR8("(_ BitVec 8)");

	/**
	 * SMT2 representation of the data type.
	 */
	private String text;
	
	 /**
	  * Private constructor.
	  * @param s
	  */
	 private SMTDataType(String s) {
         this.text = s;
     }
	
	 /**
	  * Get string representing the data type in SMTLib2.
	  */
	 public String toString() {
	     return this.text;
	 }
	 
	 /**
	  * Define BitVector of a specific length.
	  * @param maxNumber - a maximal decimal number that should be represented (used to calculate length of the BitVector)
	  * @return BitVector of length that support numbers until maxNumber.
	  */
	public static SMTDataType BITVECTOR(int maxNumber) {
		switch (SMTUtils.countBits(maxNumber)) {
		case 1:
			return BITVECTOR1;
		case 2:
			return BITVECTOR2;
		case 3:
			return BITVECTOR3;
		case 4:
			return BITVECTOR4;
		case 5:
			return BITVECTOR5;
		case 6:
			return BITVECTOR6;
		case 7:
			return BITVECTOR7;
		default:
			return BITVECTOR8;
		}
	}
	 

}
