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
	
	public String toString() {
		if(this == LogicOperation.OR) {
			return "disjunction";
		} else {
			return "conjunction";
		}
	}
}
