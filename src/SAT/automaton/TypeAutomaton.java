package SAT.automaton;

import java.util.ArrayList;
import java.util.List;

public class TypeAutomaton {

	private List<TypeBlock> typeAutomaton;

	public TypeAutomaton(){
		typeAutomaton = new ArrayList<TypeBlock>();
	}
	
	public TypeAutomaton(List<TypeBlock> typeAutomaton) {
		super();
		this.typeAutomaton = typeAutomaton;
	}

	public List<TypeBlock> getTypeBlocks() {
		return typeAutomaton;
	}

	public void addBlock(TypeBlock block){
		typeAutomaton.add(block);
	}
	
	public TypeBlock getBlock(int i){
		return typeAutomaton.get(i);
	}
	
}
