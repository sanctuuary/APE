package nl.uu.cs.ape.sat.automaton;

/**
 * Class represents a single State (Module or Type) in the system.
 * @author vedran
 *
 */
public interface State {

	/**
	 * Returns the text representing the state (e.g. M04 or T02.31)
	 * @return String representation of the state.
	 */
	public String getStateName();
	
	/**
	 * Returns the order number of the state in the respective array of states.
	 * @return Order number of the state (within the block).
	 */
	public int getStateNumber();
	
	/**
	 * Returns the absolute order number of the state within the whole workflow. Unlike {@link getStateNumber}, this function returns number that can be used to compare ordering of any 2 states in the system,
	 * disregarding the block or their type (data type, tool, etc.).
	 * @return Absolute order number of the state.
	 */
	public int getAbsoluteStateNumber();
	
	public int hashCode();
	public boolean equals(Object obj);
}
