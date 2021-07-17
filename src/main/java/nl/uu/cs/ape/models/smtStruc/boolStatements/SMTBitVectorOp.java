package nl.uu.cs.ape.models.smtStruc.boolStatements;

/**
 * Defines the values describing the states in the workflow.
 * <p>
 * Values: [{@code MODULE}, {@code MEMORY_TYPE}, {@code USED_TYPE}, {@code MEM_TYPE_REFERENCE}]
 * 
 * 
 *  @author Vedran Kasalica
 */
public enum SMTBitVectorOp implements SMTFunctionName {

    LESS_THAN("bvult"),

    LESS_OR_EQUAL("bvule"),

    GREATER_THAN("bvugt"),

    GREATER_OR_EQUAL("bvuge");

	/**
	 * SMT2 representation of the operation.
	 */
	private String text;

	 /**
	  * Private constructor.
	  * @param s
	  */
	 private SMTBitVectorOp(String s) {
         this.text = s;
     }
	
	 /**
	  * Get string representing the Bit Vector operation in SMTLib2.
	  */
	 public String toString() {
	     return this.text;
	 }
	 
}
