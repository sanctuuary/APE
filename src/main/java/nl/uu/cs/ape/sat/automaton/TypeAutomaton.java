package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Class is used to represent the type automaton.
 * @author vedran
 *
 */
public class TypeAutomaton {

	private List<TypeBlock> typeAutomaton;

	public TypeAutomaton(){
		typeAutomaton = new ArrayList<TypeBlock>();
	}
	
	public TypeAutomaton(List<TypeBlock> typeAutomaton) {
		super();
		this.typeAutomaton = typeAutomaton;
	}

	/**
	 * Return all the Type Block from the automaton.
	 * @return
	 */
	public List<TypeBlock> getTypeBlocks() {
		return typeAutomaton;
	}

	/**
	 * Add block of Type states to the automaton.
	 * @param block - block to be added
	 */
	public void addBlock(TypeBlock block){
		typeAutomaton.add(block);
	}
	
	/**
	 * Get @i-th block of Type states from the automaton.
	 * @param i - ordering number of the block to be returned
	 * @return Block of Type states
	 */
	public TypeBlock getBlock(int i){
		return typeAutomaton.get(i);
	}
	
}
