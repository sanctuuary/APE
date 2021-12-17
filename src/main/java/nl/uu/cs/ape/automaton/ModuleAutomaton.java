package nl.uu.cs.ape.automaton;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;

/**
 * The {@code ModuleAutomaton} class is used to represent the module automaton. Module Automaton represents the structure that tools in the provided solutions will follow.
 * Module automaton is represented as an array of {@link State}.
 * <p>
 * Labeling of the automaton is provided in
 * <a href="https://github.com/sanctuuary/APE/blob/master/res/WorkflowAutomaton_Implementation.png">/APE/res/WorkflowAutomaton_Implementation.png</a>.
 *
 * @author Vedran Kasalica
 */
public class ModuleAutomaton implements Automaton {

    private List<State> moduleStates;

    /**
     * Generate the Module State automatons based on the defined length.
     *
     * @param automataBound  Length of the automaton.
     * @param inputBranching Input branching factor (max number of inputs for modules).
     * @param outputBranching Output branching factor (max number of inputs for modules).
     */
    public ModuleAutomaton(int automataBound, int inputBranching, int outputBranching) {
        moduleStates = new ArrayList<State>();
        automataBound = Math.max(automataBound, 1);

        for (int i = 1; i <= automataBound; i++) {
            State tmpState = new State(WorkflowElement.MODULE, null, i, inputBranching, outputBranching);
            moduleStates.add(tmpState);
        }
    }

    /**
     * Add {@link State} to the Module automaton.
     *
     * @param state Module state to be added.
     */
    public void addState(State state) {
        moduleStates.add(state);
    }

    /**
     * Return the size of the Module automaton.
     *
     * @return Current amount of module states.
     */
    public int size() {
        return moduleStates.size();
    }

    /**
     * Return {@code i}-th Module state from the automaton (where 0 is the first index).
     *
     * @param i Ordering number of the state to be returned.
     * @return Module State.
     */
    public State get(int i) {
        return moduleStates.get(i);
    }

    /* (non-Javadoc)
     * @see nl.uu.cs.ape.automaton.Automaton#getAllStates()
     */
    @Override
    public List<State> getAllStates() {
        return moduleStates;
    }

    /**
     * Print.
     */
    public void print() {
        System.out.println("-------------------------------------------------------------");
        System.out.println("\tModule automaton:");
        System.out.println("-------------------------------------------------------------");
        for (State state : moduleStates) {
            System.out.println("\tModule state: " + state.getPredicateID() + ", order number: " + state.getAbsoluteStateNumber());
        }
        System.out.println("-------------------------------------------------------------");

    }
    
    /**
     * Gets state object which corresponds to the type and order number (w.r.t. the type) of the state.
     * @param usedTypeState - type of the state
     * @param typeDependantStateNumber - order number of the state within the type
     * @return State no {@code typeDependantStateNumber} of the type {@code usedTypeState}
     */
	public State getState(int typeDependantStateNumber) {
		return get(typeDependantStateNumber);
	}
}