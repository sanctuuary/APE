package SAT.automaton;

import java.util.ArrayList;
import java.util.List;

public class TypeBlock {

	private List<TypeState> typeStates;
	private int blockNumber;
	
	
	
	public TypeBlock() {
		typeStates = new ArrayList<TypeState>();
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


	public int getBlockNumber() {
		return blockNumber;
	}

	public void addState(TypeState state){
		typeStates.add(state);
	}
	
	
}
