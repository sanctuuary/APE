package nl.uu.cs.ape.sat.core.solutionStructure;

import nl.uu.cs.ape.sat.automaton.State;

/**
 * The {@code SolutionWorkflowNode} class is used to represent a node in the actual solution workflow. 
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class SolutionWorkflowNode implements Comparable<SolutionWorkflowNode> {
	
	/** State in the automaton that the Node refers to. */
	private State automatonState;
	
	
	public SolutionWorkflowNode(State automatonState) {
		this.automatonState = automatonState;
	}
	
	public State getAutomatonState() {
		return this.automatonState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((automatonState == null) ? 0 : automatonState.hashCode());
		return result;
	}
	

	@Override
	/**
	 * Two SolutionWorkflowNodes are equal if they represent the same state in the workflow.
	 * @return true if the state that the describe is the same.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolutionWorkflowNode other = (SolutionWorkflowNode) obj;
		if (automatonState == null) {
			if (other.automatonState != null)
				return false;
		} else if (!automatonState.equals(other.automatonState))
			return false;
		return true;
	}
	
	/**
	 * Compares the two SolutionWorkflowNodes based on their order in the solution. {@link State} is used to evaluate the absolute position of the node in the workflow.
	 * @return a negative integer, zero, or a positive integer as this object is before than, equal to, or after than the specified SolutionWorkflowNode.
	 */
	@Override
	public int compareTo(SolutionWorkflowNode otherNode) {
		return this.getAutomatonState().compareTo(otherNode.getAutomatonState());
	}
	
	/** Get the unique label that describes the node. */
	public abstract String getNodeID();
	
	/** Get a label that describes the node. */
	public abstract String getNodeLabel();
	
	
}
