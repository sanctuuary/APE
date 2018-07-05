package SAT.models;

/**
 * Class currently represents a single predicate/label. It is not a whole atom. In order to be an atom relation needs to be added.
 * TODO Should be renamed to PREDICATE or something similar.
 * @author VedranPC
 *
 */
public interface Atom {

	/**
	 * Function is used to return the formula (usually a single predicate) defined.
	 * @return
	 */
	public String getAtom();
	
	/**
	 * The function is used to determine the type of the element ["type", "module" or "abstract module"].
	 * @return
	 */
	public String getType();
}
