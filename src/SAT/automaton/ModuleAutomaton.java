package SAT.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class is used to represent the module automaton.
 * @author vedran
 *
 */
public class ModuleAutomaton {

	private List<ModuleState> moduleStates;

	public ModuleAutomaton(){
		moduleStates = new ArrayList<ModuleState>();
	}
	
	public ModuleAutomaton(List<ModuleState> moduleStates) {
		super();
		this.moduleStates = moduleStates;
	}

	/**
	 * Return all Module States from the Module automaton
	 * @return
	 */
	public List<ModuleState> getModuleStates() {
		return moduleStates;
	}
	
	/**
	 * Add @state to the Module automaton
	 * @param state - module state to be added
	 */
	public void addState(ModuleState state){
		moduleStates.add(state);
	}
	
	/**
	 * Return the size of the Module automaton
	 * @return
	 */
	public int size(){
		return moduleStates.size();
	}
	
	/**
	 * Return @i-th Module state from the automaton
	 * @param i - ordering number of the state to be returned
	 * @return - Module State
	 */
	public ModuleState get(int i){
		return moduleStates.get(i);
	}

}
