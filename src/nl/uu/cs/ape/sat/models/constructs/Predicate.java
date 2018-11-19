package nl.uu.cs.ape.sat.models.constructs;

/**
 * The {@code Predicate} class (interface) represents a single predicate/label. It is not a whole atom. In order to be an atom relation needs to be added.

 * @author Vedran Kasalica
 *
 */
public interface Predicate {

	/**
	 * Function is used to return the predicate defined as String.
	 * @return String representation of the predicate.
	 */
	public String getPredicate();
	
	/**
	 * The function is used to determine the type of the predicate [<b>type</b>,<b>module</b> or <b>abstract module</b>].
	 * @return String [<b>type</b>,<b>module</b> or <b>abstract module</b>]
	 */
	public String getType();
}
