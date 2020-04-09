package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * Class is used to represent the type automaton. Type Automaton represents the structure that data instances in the provided solutions will follow.
 * It comprises blocks of data types that are added to the memory and those that are being used by tools, as
 * input.
 * <br><br>
 * Labeling of the automaton is provided in {@code /APE/res/WorkflowAutomaton_Implementation.png}
 * 
 * @author Vedran Kasalica
 *
 */
public class TypeAutomaton implements Automaton {

	/**
	 * Blocks of data types that are being added to the memory (usually outputs from
	 * the tools, apart from the initial workflow input)
	 */
	private List<Block> memoryTypesAutomaton;
	/**
	 * Blocks of data types that are being used by tools from the memory (inputs to
	 * the tools)
	 */
	private List<Block> usedTypesAutomaton;
	
	/** State is used in order to represent no state. */
	private State nullState;
	
	/**
	 * Generate the Type State automatons based on the defined length
	 * and branching factor.
	 * <br><br>
	 * Labeling of the automaton is provided in /APE/res/WorkflowAutomaton_Implementation.png
	 * 
	 * @param automata_bound   - length of the automaton
	 * @param input_branching  - input branching factor (max number of inputs for modules)
	 * @param output_branching - output branching factor (max number of outputs for modules)
	 */
	public TypeAutomaton(int automata_bound, int input_branching, int output_branching) {
		memoryTypesAutomaton = new ArrayList<Block>();
		usedTypesAutomaton = new ArrayList<Block>();
		nullState = new State(null, null, -1, input_branching);
	
		automata_bound =  automata_bound < 1 ? 1 : automata_bound;
			
		for (int i = 0; i <= automata_bound; i++) { 
			
			Block tmpMemoryTypeBlock = new Block(i);
			
			for (int j = 0; j < input_branching; j++) {
				State tmpMemoryState = new State(WorkflowElement.MEMORY_TYPE, i, j, input_branching);
				tmpMemoryTypeBlock.addState(tmpMemoryState);
			}
			memoryTypesAutomaton.add(tmpMemoryTypeBlock);
			
			Block tmpUsedTypesBlock = new Block(i);
			for (int j = 0; j < input_branching; j++) {
				State tmpUsedState = new State(WorkflowElement.USED_TYPE, i, j, input_branching);
				tmpUsedTypesBlock.addState(tmpUsedState);
			}
			usedTypesAutomaton.add(tmpUsedTypesBlock);
		}
	}

	/**
	 * Return from the automaton all the Type Blocks that contain types used by
	 * tools.
	 * 
	 * @return Blocks of data types used by tools.
	 */
	public List<Block> getUsedTypesBlocks() {
		return usedTypesAutomaton;
	}

	/**
	 * Return from the automaton all the Type Blocks that contain types added to the
	 * memory.
	 * 
	 * @return Blocks of data types added to the memory.
	 */
	public List<Block> getMemoryTypesBlocks() {
		return memoryTypesAutomaton;
	}
	
	/**
	 * Return from the automaton all the Type Blocks.
	 * 
	 * @return Blocks of data types used by tools.
	 */
	public List<Block> getAllBlocks() {
		List<Block> x = new ArrayList<Block> (usedTypesAutomaton);
		x.addAll(memoryTypesAutomaton);
		return x;
	}
	
	/**
	 * Returns the null state. 
	 * @return State representing a null state.
	 */
	public State getNullState() {
		return nullState;
	}

	/**
	 * Add to the automaton the Type Block that contains types used by tools.
	 * 
	 * @return true (as specified by {@link Collection#add(E)}
	 */
	public boolean addUsedTypesBlock(Block block) {
		return usedTypesAutomaton.add(block);
	}

	/**
	 * Add to the automaton the Type Block that contains types added to the memory.
	 * 
	 * @return true (as specified by {@link Collection#add(E)}
	 */
	public boolean addMemoryTypesBlock(Block block) {
		return memoryTypesAutomaton.add(block);
	}

	/**
	 * Get the first memory Type state blocks from the automaton. The first memory
	 * block represent the initial input to the workflow.
	 * 
	 * @return Memory Type Block that represents workflow input.
	 */
	public Block getWorkflowInputBlock() {
		Block tmp;
		try {
			tmp = memoryTypesAutomaton.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return tmp;
	}
	
	/**
	 * Get the last used Type state blocks from the automaton. The last used
	 * block represent The last's tool output.
	 * 
	 * @return The workflow output.
	 */
	public Block getWorkflowOutputBlock() {
		Block tmp;
		try {
			tmp = usedTypesAutomaton.get(usedTypesAutomaton.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return tmp;
	}

	/**
	 * Get the last memory Type state blocks from the automaton. The last memory
	 * block represent The last's tool output.
	 * 
	 * @return The last's tool output Block.
	 */
	public Block getLastToolOutputBlock() {
		Block tmp;
		try {
			tmp = memoryTypesAutomaton.get(memoryTypesAutomaton.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		return tmp;
	}

	/**
	 * Get from the automaton all the @i-th Type Block that contain types used by
	 * tools.
	 * 
	 * @param i - ordering number of the used type block to be returned.
	 * @return Block of Type states that are used by tools.
	 */
	public Block getUsedTypesBlock(int i) {
		return usedTypesAutomaton.get(i);
	}

	/**
	 * Get from the automaton all the @i-th Type Block that contain types added to
	 * the memory.
	 * 
	 * @param i - ordering number of the memory type block to be returned
	 * @return lock of Type states that are added to the memory.
	 */
	public Block getMemoryTypesBlock(int i) {
		return memoryTypesAutomaton.get(i);
	}
	
	/**
	 * Return all the memory type states that are generated until a certain block, i.e. all the slots of memory are generated until a certain block/tool.
	 * @param maxBlockNo - memory block prior to which we are looking into memory (this block is included).
	 * @return List of memory States.
	 */
	public List<State> getMemoryStatesUntilBlockNo(int maxBlockNo) {
		 List<State> untilStates= new ArrayList<State>();
		for(int i = 0; i <= maxBlockNo; i++) {
			Block currBlock = this.memoryTypesAutomaton.get(i);
			for(State currState : currBlock.getStates()) {
				untilStates.add(currState);
			}
		}
		return untilStates;
	}
	
	/**
	 * Return all the memory type states that are generated after a certain block, i.e. all the slots of memory are generated prior to a certain block/tool.
	 * @param minBlockNo - memory block after which we are looking into memory (this block is not included).
	 * @return List of memory States.
	 */
	public List<State> getMemoryStatesAfterBlockNo(int minBlockNo) {
		 List<State> untilStates= new ArrayList<State>();
		for(int i = minBlockNo + 1; i < this.memoryTypesAutomaton.size(); i++) {
			Block currBlock = this.memoryTypesAutomaton.get(i);
			for(State currState : currBlock.getStates()) {
				untilStates.add(currState);
			}
		}
		return untilStates;
	}
	
	/**
	 * Return all the type states that are used after a certain block, i.e. all the slots of tool inputs that are used after current types were added to the memory.
	 * @param minBlockNo - memory block after which we are looking into tool inputs (this block is not included).
	 * @return List of Used States.
	 */
	public List<State> getUsedStatesAfterBlockNo(int minBlockNo) {
		 List<State> untilStates= new ArrayList<State>();
		for(int i = minBlockNo + 1; i < this.usedTypesAutomaton.size(); i++) {
			Block currBlock = this.usedTypesAutomaton.get(i);
			for(State currState : currBlock.getStates()) {
				untilStates.add(currState);
			}
		}
		return untilStates;
	}
	
	public void print() {
		System.out.println("-------------------------------------------------------------");
		System.out.println("\tType automaton:");
		System.out.println("-------------------------------------------------------------");
		for(Block memBlock : memoryTypesAutomaton) {
			for(State memState : memBlock.getStates()) {
				System.out.println("\tType state: " + memState.getPredicateID() + ", order number: " + memState.getAbsoluteStateNumber());
			}
		}
		System.out.println("-------------------------------------------------------------");
		for(Block usedBlock : usedTypesAutomaton) {
			for(State usedState : usedBlock.getStates()) {
				System.out.println("\tType state: " + usedState.getPredicateID() + ", order number: " + usedState.getAbsoluteStateNumber());
			}
		}
			
		System.out.println("-------------------------------------------------------------");
		
	}

	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.automaton.Automaton#getAllStates()
	 */
	@Override
	public List<State> getAllStates() {
		List<State> allStates = new ArrayList<State>();
		for(Block currBlock : getAllBlocks()) {
			for(State currState : currBlock.getStates()) {
				allStates.add(currState);
			}
		}
		return allStates;
	}

}
