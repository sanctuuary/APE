package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.sat.StaticFunctions;

/**
 * Class is used to represent the type automaton. It comprises blocks of data
 * types that are added to the memory and those that are being used by tools, as
 * input.
 * <br><br>
 * Labeling of the automaton is provided in /APE/res/WorkflowAutomaton_Implementation.png
 * 
 * @author Vedran Kasalica
 *
 */
public class TypeAutomaton {

	/**
	 * Blocks of data types that are being added to the memory (usually outputs from
	 * the tools, apart from the initial workflow input)
	 */
	private List<TypeBlock> memoryTypesAutomaton;
	/**
	 * Blocks of data types that are being used by tools from the memory (inputs to
	 * the tools)
	 */
	private List<TypeBlock> usedTypesAutomaton;
	
	
	/** Workflow length */
	private int workflowLength;

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
		memoryTypesAutomaton = new ArrayList<TypeBlock>();
		usedTypesAutomaton = new ArrayList<TypeBlock>();
	
		workflowLength =  automata_bound < 1 ? 1 : automata_bound;
			
		
		for (int i = 0; i <= workflowLength; i++) {
			String i_var;
			if (workflowLength > 10 && i < 10) {
				i_var = "0" + i;
			} else {
				i_var = "" + i;
			}
			
			
			TypeBlock tmpMemoryTypeBlock = new TypeBlock(i);
			for (int j = 0; j < input_branching; j++) {
				TypeState tmpMemoryTypeState = new TypeState("MemT" + i_var + "." + j, j, StaticFunctions.calculateAbsStateNumber(i, j, input_branching, WorkflowElement.MEMORY_TYPE));
				tmpMemoryTypeBlock.addState(tmpMemoryTypeState);
			}
			addMemoryTypesBlock(tmpMemoryTypeBlock);
			
			TypeBlock tmpUsedTypesBlock = new TypeBlock(i);
			for (int j = 0; j < input_branching; j++) {
				TypeState tmpUsedTypeState = new TypeState("UsedT" + i_var + "." + j, j,StaticFunctions.calculateAbsStateNumber(i, j, input_branching, WorkflowElement.USED_TYPE));
				tmpUsedTypesBlock.addState(tmpUsedTypeState);
			}
			addUsedTypesBlock(tmpUsedTypesBlock);
		}
	}

//	public TypeAutomaton(List<TypeBlock> typeAutomaton) {
//		super();
//		this.typeAutomaton = typeAutomaton;
//	}

	/**
	 * Return from the automaton all the Type Blocks that contain types used by
	 * tools.
	 * 
	 * @return Blocks of data types used by tools.
	 */
	public List<TypeBlock> getUsedTypesBlocks() {
		return usedTypesAutomaton;
	}

	/**
	 * Return from the automaton all the Type Blocks that contain types added to the
	 * memory.
	 * 
	 * @return Blocks of data types added to the memory.
	 */
	public List<TypeBlock> getMemoryTypesBlocks() {
		return memoryTypesAutomaton;
	}
	
	/**
	 * Return from the automaton all the Type Blocks.
	 * 
	 * @return Blocks of data types used by tools.
	 */
	public List<TypeBlock> getAllBlocks() {
		List<TypeBlock> x = new ArrayList<TypeBlock> (usedTypesAutomaton);
		x.addAll(memoryTypesAutomaton);
		return x;
	}

	/**
	 * Add to the automaton the Type Block that contains types used by tools.
	 * 
	 * @return true (as specified by {@link Collection#add(E)}
	 */
	public boolean addUsedTypesBlock(TypeBlock block) {
		return usedTypesAutomaton.add(block);
	}

	/**
	 * Add to the automaton the Type Block that contains types added to the memory.
	 * 
	 * @return true (as specified by {@link Collection#add(E)}
	 */
	public boolean addMemoryTypesBlock(TypeBlock block) {
		return memoryTypesAutomaton.add(block);
	}

	/**
	 * Get the first memory Type state blocks from the automaton. The first memory
	 * block represent the initial input to the workflow.
	 * 
	 * @return Memory Type Block that represents workflow input.
	 */
	public TypeBlock getWorkflowInputBlock() {
		TypeBlock tmp;
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
	public TypeBlock getWorkflowOutputBlock() {
		TypeBlock tmp;
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
	public TypeBlock getLastToolOutputBlock() {
		TypeBlock tmp;
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
	public TypeBlock getUsedTypesBlock(int i) {
		return usedTypesAutomaton.get(i);
	}

	/**
	 * Get from the automaton all the @i-th Type Block that contain types added to
	 * the memory.
	 * 
	 * @param i - ordering number of the memory type block to be returned
	 * @return lock of Type states that are added to the memory.
	 */
	public TypeBlock getMemoryTypesBlock(int i) {
		return memoryTypesAutomaton.get(i);
	}
	
	/**
	 * Get workflow length.
	 * @return
	 */
	public int getWorkflowLength() {
		return workflowLength;
	}

	/**
	 * Return all the memory type states that are generated prior to a certain block, i.e. all the slots of memory are generated prior to a certain block/tool.
	 * @param maxBlockNo - memory block prior to which we are looking into memory (this block is not included).
	 * @return List of memory TypeStates.
	 */
	public List<TypeState> getMemoryStatesUntilBlockNo(int maxBlockNo) {
		 List<TypeState> untilTypeStates= new ArrayList<TypeState>();
		for(int i = 0; i < maxBlockNo; i++) {
			TypeBlock currBlock = this.memoryTypesAutomaton.get(i);
			for(TypeState currState : currBlock.getTypeStates()) {
				untilTypeStates.add(currState);
			}
		}
		return untilTypeStates;
	}

}
