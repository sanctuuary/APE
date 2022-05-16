package nl.uu.cs.ape.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Block of states that comprise a type automaton.
 *
 * @author Vedran Kasalica
 */
public class Block {

    /**
     * States that comprise this block. Number of stater correspond to the max number of inputs or outputs.
     */
    private List<State> typeStates;

    /**
     * Order number of the block in the solution.
     */
    private int blockNumber;

    /**
     * Instantiates a new Block.
     *
     * @param blockNumber The block number.
     */
    public Block(int blockNumber) {
        typeStates = new ArrayList<State>();
        this.blockNumber = blockNumber;
    }

    /**
     * Instantiates a new Block.
     *
     * @param typeStates  The type states.
     * @param blockNumber The block number.
     */
    public Block(List<State> typeStates, int blockNumber) {
        super();
        this.typeStates = typeStates;
        this.blockNumber = blockNumber;
    }

    /**
     * Return the states that are part of the block. Those are usually Type States.
     *
     * @return List of states (usually type states).
     */
    public List<State> getStates() {
        return typeStates;
    }

    /**
     * Return the ordering number of the block in the Type automaton.
     *
     * @return Ordering number of the block.
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
     * Add Type state to the Type Block.
     *
     * @param state Type State to be added.
     */
    public void addState(State state) {
        typeStates.add(state);
    }

    /**
     * Get @i-th state of Type states from the block.
     *
     * @param i Ordering number of the state to be returned.
     * @return A type state.
     */
    public State getState(int i) {
        return typeStates.get(i);
    }
}
