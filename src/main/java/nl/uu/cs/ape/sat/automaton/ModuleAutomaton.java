package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * The {@code ModuleAutomaton} class is used to represent the module automaton. Module Automaton represents the structure that tools in the provided solutions will follow.
 * Module automaton is represented as an array of {@link State
 * States}. <br>
 * <br>
 * Labeling of the automaton is provided in
 * /APE/res/WorkflowAutomaton_Implementation.png
 * 
 * @author Vedran Kasalica
 *
 */
public class ModuleAutomaton implements Automaton {

	private List<State> moduleStates;

	/**
	 * Generate the Module State automatons based on the defined length.
	 * 
	 * @param automata_bound - length of the automaton
	 * @param input_branching  - input branching factor (max number of inputs for modules)
	 */
	public ModuleAutomaton(int automata_bound, int input_branching) {
		moduleStates = new ArrayList<State>();
		automata_bound =  automata_bound < 1 ? 1 : automata_bound;
		
		for (int i = 1; i <= automata_bound; i++) {
			State tmpState = new State(WorkflowElement.MODULE, null, i, input_branching);
			moduleStates.add(tmpState);

		}
	}

//	public ModuleAutomaton(List<State> moduleStates) {
//		super();
//		this.moduleStates = moduleStates;
//	}

	/**TODO: REMOVE-duplicate
	 * Return all Module States from the Module automaton
	 * 
	 * @return {@link List} <{@link State}>
	 */
	public List<State> getModuleStates() {
		return moduleStates;
	}

	/**
	 * Add @state to the Module automaton
	 * 
	 * @param state - module state to be added
	 */
	public void addState(State state) {
		moduleStates.add(state);
	}

	/**
	 * Return the size of the Module automaton
	 * 
	 * @return
	 */
	public int size() {
		return moduleStates.size();
	}

	/**
	 * Return @i-th Module state from the automaton
	 * 
	 * @param i - ordering number of the state to be returned
	 * @return - Module State
	 */
	public State get(int i) {
		return moduleStates.get(i);
	}

	/* (non-Javadoc)
	 * @see nl.uu.cs.ape.sat.automaton.Automaton#getAllStates()
	 */
	@Override
	public List<State> getAllStates() {
		return moduleStates;
	}
	
	public void print() {
		System.out.println("-------------------------------------------------------------");
		System.out.println("\tModule automaton:");
		System.out.println("-------------------------------------------------------------");
		for(State state : moduleStates) {
			System.out.println("\tModule state: " + state.getPredicateID() + ", order number: " + state.getAbsoluteStateNumber());
		}
		System.out.println("-------------------------------------------------------------");
		
	}

}
