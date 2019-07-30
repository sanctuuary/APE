package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Block of Type states that comprise the Type automaton.
 * @author vedran
 *
 */
public class TypeBlock {

	/** States that comprise this block. Number of stater correpond to the max number of inputs or outputs. */
	private List<TypeState> typeStates;
	/** Order number of the block in the solution. */
	private int blockNumber;

	public TypeBlock(int blockNumber) {
		typeStates = new ArrayList<TypeState>();
		this.blockNumber = blockNumber;
	}

	public TypeBlock(List<TypeState> typeStates, int blockNumber) {
		super();
		this.typeStates = typeStates;
		this.blockNumber = blockNumber;
	}

	public List<TypeState> getTypeStates() {
		return typeStates;
	}

	public void setTypeStates(List<TypeState> typeStates) {
		this.typeStates = typeStates;
	}

	/**
	 * Return the ordering number of the block in the Type automaton.
	 * 
	 * @return Ordering number of the block
	 */
	public int getBlockNumber() {
		return blockNumber;
	}

	/**
	 * Returns the size for each block in the automaton.
	 * 
	 * @return {@code int} block size.
	 */
	public int getBlockSize() {
		return typeStates.size();
	}

	/**
	 * Add Type state to the Type Block
	 * @param state - Type State to be added
	 */
	public void addState(TypeState state) {
		typeStates.add(state);
	}
	
	/**
	 * Get @i-th state of Type states from the block.
	 * @param i - ordering number of the state to be returned
	 * @return A type state
	 */
	public TypeState getState(int i) {
		return typeStates.get(i);
	}

}
