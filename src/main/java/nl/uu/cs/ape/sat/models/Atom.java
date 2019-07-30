package nl.uu.cs.ape.sat.models;

import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.WorkflowElement;
import nl.uu.cs.ape.sat.models.constructs.Predicate;

/**
 *  The {@code Atom} class represents elements of the workflow, that consists of the tool or type used, state where it is used
 *  and potentially a state that it refers to, i.e. input type elements refer to a state when the type was created.
 * 
 * @author Vedran Kasalica
 *
 */
public class Atom {

	/**  Predicate that is referred (tool or type). */
	private final Predicate predicate;
	/**  State in which the type/tool was used. */
	private final State usedInStateArgument;
	/**  Referred state in which the type was created. This argument exists only if the atom represent a tool input. */
	private State referedStateArgument;
	/**  Defines the type of the element in the workflow that the atom describes (tool, memory type, etc.) */
	private WorkflowElement elementType;
	
	
	/**
	 * Creates a regular state in the automaton that corresponds to a usage of a tool or type that is added to the memory.
	 * @param predicate - tool used or data type created (added to memory)
	 * @param usedInState - state in the automaton it was used/created in
	 */
	public Atom(Predicate predicate, State usedInState) {
		this.predicate = predicate;
		this.usedInStateArgument = usedInState;
		if(predicate.getClass().equals(Type.class)) {
			this.elementType = WorkflowElement.MEMORY_TYPE;
		} else {
			this.elementType = WorkflowElement.MODULE;
		}
	}
	
	/**
	 * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
	 * @param predicate - used data type 
	 * @param usedInState - state in the automaton the types is used as input
	 * @param usedInState - state in the automaton when the type was created
	 */
	public Atom(Predicate predicate, State usedInState, State referedState) {
		this.predicate = predicate;
		this.usedInStateArgument = usedInState;
		this.referedStateArgument = referedState;
		this.elementType = WorkflowElement.USED_TYPE;
	}
	
	/**
	 * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
	 * @param atom - atom that is being copied.
	 */
	public Atom(Atom atom) {
		this.predicate = atom.predicate;
		this.usedInStateArgument = atom.usedInStateArgument;
		this.referedStateArgument = atom.referedStateArgument;
		this.elementType = atom.elementType;
	}
	
    

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((referedStateArgument == null) ? 0 : referedStateArgument.hashCode());
		result = prime * result + ((usedInStateArgument == null) ? 0 : usedInStateArgument.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (referedStateArgument == null) {
			if (other.referedStateArgument != null)
				return false;
		} else if (!referedStateArgument.equals(other.referedStateArgument))
			return false;
		if (usedInStateArgument == null) {
			if (other.usedInStateArgument != null)
				return false;
		} else if (!usedInStateArgument.equals(other.usedInStateArgument))
			return false;
		return true;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public State getUsedInStateArgument() {
		return usedInStateArgument;
	}

	public State getReferedStateArgument() {
		return referedStateArgument;
	}
	
	/**
	 * Return the type of the element in the workflow (tool, memory type, etc.)
	 * @return The {@link WorkflowElement} that corresponds to the atom usage.
	 */
	public WorkflowElement getWorkflowElementType() {
		return elementType;
	}


	/**
	 * Returns the string representation of the WorkflowElement, used for the textual solution representation. In case of the atom depicting
	 * a usage of a type in the workflow, the structure of the representation contains an additional attribute, state in which the type was initially added to the memory.
	 * @return String representing the workflow element in a textual form.
	 */
	public String toString() {
		if(this.elementType == WorkflowElement.USED_TYPE) {
			return predicate.getPredicate() + "[" + referedStateArgument.getStateName() + "]("+ usedInStateArgument.getStateName() + ")";
		} else {
			return predicate.getPredicate() + "(" + usedInStateArgument.getStateName() + ")";
		}
	}
	
 
	
}
