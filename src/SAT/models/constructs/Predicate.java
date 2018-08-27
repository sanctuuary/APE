package SAT.models.constructs;

/**
 * The {@code Predicate} class (interface) represents a single predicate/label. It is not a whole atom. In order to be an atom relation needs to be added.

 * @author Vedran Kasalica
 *
 */
public interface Predicate {

	/**
	 * Function is used to return the predicate defined.
	 * @return
	 */
	public String getPredicate();
	
	/**
	 * The function is used to determine the type of the predicate ["type", "module" or "abstract module"].
	 * @return
	 */
	public String getType();
}
