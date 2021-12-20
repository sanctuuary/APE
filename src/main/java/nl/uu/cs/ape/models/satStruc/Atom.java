package nl.uu.cs.ape.models.satStruc;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * The {@code Atom} class represents elements of the workflow, that consists
 * of the operation or type used, state where it is used and potentially
 * a state that it refers to, i.e. input type elements refer to a state
 * when the type was created.
 *
 * @author Vedran Kasalica
 */
public class Atom implements Comparable<Atom> {

    /**
     * PredicateLabel that is referred (tool or type).
     */
    private final PredicateLabel predicate;

    /**
     * State in which the type/operation was used.
     */
    private final State argumentState;

    /**
     * Defines the type of the element in the workflow that the atom describes (tool, memory type, etc.)
     */
    private WorkflowElement elementType;

    /**
     * Creates an atom that can represent usage of the tool, creation or usage of a type,
     * or a reference between an input type and the state in which it was generated..
     *
     * @param predicate   Predicate used.
     * @param usedInState State in the automaton it was used/created in.
     * @param elementType Element that defines what type of a predicate is described (such as {@link SMTDataType#MODULE}.
     */
    public Atom(PredicateLabel predicate, State usedInState, WorkflowElement elementType) {
        this.predicate = predicate;
        this.argumentState = usedInState;
        this.elementType = elementType;
    }

    /**
     * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
     *
     * @param atom Atom that is being copied.
     */
    public Atom(Atom atom) {
        this.predicate = atom.predicate;
        this.argumentState = atom.argumentState;
        this.elementType = atom.elementType;
    }

    /**
     * Gets predicate.
     *
     * @return Field {@link #predicate}.
     */
    public PredicateLabel getPredicate() {
        return predicate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((argumentState == null) ? 0 : argumentState.hashCode());
        result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Atom other = (Atom) obj;
        if (argumentState == null) {
            if (other.argumentState != null)
                return false;
        } else if (!argumentState.equals(other.argumentState))
            return false;
        if (elementType != other.elementType)
            return false;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        return true;
    }

    /**
     * Gets used in state argument.
     *
     * @return Field {@link #argumentState}.
     */
    public State getUsedInStateArgument() {
        return argumentState;
    }

    /**
     * Return the type of the element in the workflow (tool, memory type, etc.)
     *
     * @return The {@link SMTDataType} that corresponds to the atom usage.
     */
    public WorkflowElement getWorkflowElementType() {
        return elementType;
    }

    /**
     * Returns the string representation of the SMTDataType, used for the textual solution representation. In case of the atom depicting
     * a usage of a type in the workflow, the structure of the representation contains an additional attribute, state in which the type was initially added to the memory.
     *
     * @return String representing the workflow element in a textual form.
     */
    public String toString() {
        if (this.elementType == WorkflowElement.MEM_TYPE_REFERENCE) {
            return "[" + predicate.getPredicateID() + "] <- (" + argumentState.getPredicateID() + ")";
        } else if (this.elementType == WorkflowElement.TYPE_DEPENDENCY) {
            return "R(" + predicate.getPredicateID() + "," + argumentState.getPredicateID() + ")";
        } else {
            return predicate.getPredicateID() + "(" + argumentState.getPredicateID() + ")";
        }
    }

    /**
     * Return true if the current workflow element is of the given {@link SMTDataType} type.
     *
     * @param workflowElemType Element type that is current Atom is compared to.
     * @return true if the current workflow element corresponds to the given {@link SMTDataType}, false otherwise.
     */
    public boolean isWorkflowElementType(WorkflowElement workflowElemType) {
        return getWorkflowElementType() == workflowElemType;
    }

    /**
     * Compare the two Atoms according to the state they are used in.
     * Returns a negative integer, zero, or a positive integer as this
     * Atom's state comes before than, is equal to, or comes after
     * than the @otherAtom's state.
     *
     * @param otherAtom The Atom to be compared
     * @return The value 0 if the argument Atom's state is equal to this Atom's state;
     * a value less than 0 if this Atom's state comes before the @otherAtom's state;
     * and a value greater than 0 if this Atom's state comes after the @otherAtom's state.
     */
	public int compareTo(Atom otherAtom) {

        int thisAtomState = this.getUsedInStateArgument().getAbsoluteStateNumber();
        int otherAtomState = otherAtom.getUsedInStateArgument().getAbsoluteStateNumber();
        int diff = 0;
        if ((diff = Integer.compare(thisAtomState, otherAtomState)) != 0) {
            return diff;
        } else {
            return this.getPredicate().compareTo(otherAtom.getPredicate());
        }
    }

}
