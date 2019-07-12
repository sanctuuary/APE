package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The {@code ModuleAutomaton} class is used to represent the module automaton. Module automaton is represented as an array of {@link ModuleState ModuleStates}.
 * <br><br>
 * Labeling of the automaton is provided in /APE/res/WorkflowAutomaton_Implementation.png
 * 
 * @author Vedran Kasalica
 *
 */
public class ModuleAutomaton {

	private List<ModuleState> moduleStates;

	/**
	 * Generate the Module State automatons based on the defined length.
	 * 
	 * @param automata_bound   - length of the automaton
	 */
	public ModuleAutomaton(int automata_bound) {
		moduleStates = new ArrayList<ModuleState>();
		for (int i = 0; i <= automata_bound; i++) {
			String i_var;
			if (automata_bound > 10 && i < 10) {
				i_var = "0" + i;
			} else {
				i_var = "" + i;
			}
			if (i > 0) {
				ModuleState tmpModuleState = new ModuleState("M" + i_var, i);
				if (i == 1) {
					tmpModuleState.setFirst();
				} else if (i == automata_bound) {
					tmpModuleState.setLast();
				}
				addState(tmpModuleState);

			}
		}
	}
	
//	public ModuleAutomaton(List<ModuleState> moduleStates) {
//		super();
//		this.moduleStates = moduleStates;
//	}

	/**
	 * Return all Module States from the Module automaton
	 * @return {@link List} <{@link ModuleState}>
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
