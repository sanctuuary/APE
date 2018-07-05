package SAT.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModuleAutomaton {

	private List<ModuleState> moduleStates;

	public ModuleAutomaton(){
		moduleStates = new ArrayList<ModuleState>();
	}
	
	public ModuleAutomaton(List<ModuleState> moduleStates) {
		super();
		this.moduleStates = moduleStates;
	}

	public List<ModuleState> getModuleStates() {
		return moduleStates;
	}
	
	public void addState(ModuleState state){
		moduleStates.add(state);
	}
	
	public int size(){
		return moduleStates.size();
	}
	
	public ModuleState get(int i){
		return moduleStates.get(i);
	}

}
