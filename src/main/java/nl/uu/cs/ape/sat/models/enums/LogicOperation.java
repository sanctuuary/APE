/**
 * 
 */
package nl.uu.cs.ape.sat.models.enums;

/**
 * The {@code LogicOperation} class is used to 
 *
 * @author Vedran Kasalica
 *
 */
public enum LogicOperation {

	OR,
	
	AND;
	
	/** Get a string corresponding to the logical operation. */
	public String toString() {
		if(this == LogicOperation.OR) {
			return "disjunction";
		} else {
			return "conjunction";
		}
	}
	
	/** Get a simple sign corresponding to the logical operation. */
	public String toStringSign() {
		if(this == LogicOperation.OR) {
			return "|";
		} else {
			return "&";
		}
	}
}
