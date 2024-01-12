package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.Predicate;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * The {@code SLTLxAtom} class represents Atoms supported by the SLTLx syntax.
 *
 * @author Vedran Kasalica
 */
public class SLTLxAtom extends SLTLxFormula implements Comparable<SLTLxAtom> {

    /**
     * StateInterface that is referred (tool or type).
     */
    private final Predicate predicate;

    /**
     * State in which the type/operation was used.
     */
    private final State argumentState;

    /**
     * Defines the type of the element in the workflow that the atom describes
     * (tool, memory type, etc.)
     */
    private AtomType elementType;

    /**
     * Clause that represents the SLTLxAtom.
     */
    private CNFClause clause = null;

    /**
     * Creates an atom that can represent usage of the tool, creation or usage of a
     * type,
     * or a reference between an input type and the state in which it was
     * generated..
     *
     * @param elementType Element that defines what type of a predicate is described
     *                    (such as {@link AtomType#MODULE}.
     * @param predicate   Predicate used.
     * @param usedInState State in the automaton it was used/created in.
     */
    public SLTLxAtom(AtomType elementType, Predicate predicate, State usedInState) {
        super();
        this.predicate = predicate;
        this.argumentState = usedInState;
        this.elementType = elementType;
    }

    /**
     * Private constructor used to create auxiliary {@code true} and {@code false}
     * atoms.
     * 
     * @param mapping integer that corresponds to the mapped Atom.
     */
    private SLTLxAtom(int mapping) {
        super();
        this.predicate = null;
        this.argumentState = null;
        this.clause = new CNFClause(mapping);
    }

    /**
     * Gets predicate.
     *
     * @return Field {@link #predicate}.
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /*
     * (non-Javadoc)
     * 
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

    /*
     * (non-Javadoc)
     * 
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
        SLTLxAtom other = (SLTLxAtom) obj;
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
     * @return The {@link AtomType} that corresponds to the atom usage.
     */
    public AtomType getWorkflowElementType() {
        return elementType;
    }

    /**
     * Returns the string representation of the {@link SLTLxAtom} used for the
     * textual solution representation. In case of the atom depicting
     * a usage of a type in the workflow, the structure of the representation
     * contains an additional attribute, state in which the type was initially added
     * to the memory.
     *
     * @return String representing the workflow element in a textual form.
     */
    public String toString() {
        if (this.elementType.isUnaryProperty()) {
            return predicate.getPredicateID() + "(" + argumentState.getPredicateID() + ")";
        } else if (this.elementType.isBinaryRel()) {
            return elementType.toString() + "(" + predicate.getPredicateID() + "," + argumentState.getPredicateID()
                    + ")";
        } else {
            return "SLTLxAtom_ERROR";
        }
    }

    /**
     * Return true if the current workflow element is of the given {@link AtomType}
     * type.
     *
     * @param workflowElemType Element type that is current SLTLxAtom is compared
     *                         to.
     * @return true if the current workflow element corresponds to the given
     *         {@link AtomType}, false otherwise.
     */
    public boolean isWorkflowElementType(AtomType workflowElemType) {
        return getWorkflowElementType() == workflowElemType;
    }

    /**
     * Compare the two Atoms according to the state they are used in.
     * Returns a negative integer, zero, or a positive integer as this
     * SLTLxAtom's state comes before than, is equal to, or comes after
     * than the @otherAtom's state.
     *
     * @param otherAtom The SLTLxAtom to be compared
     * @return The value 0 if the argument SLTLxAtom's state is equal to this
     *         SLTLxAtom's state;
     *         a value less than 0 if this SLTLxAtom's state comes before
     *         the @otherAtom's state;
     *         and a value greater than 0 if this SLTLxAtom's state comes after
     *         the @otherAtom's state.
     */
    public int compareTo(SLTLxAtom otherAtom) {

        int thisAtomState = this.getUsedInStateArgument().getAbsoluteStateNumber();
        int otherAtomState = otherAtom.getUsedInStateArgument().getAbsoluteStateNumber();
        int diff = 0;
        if ((diff = Integer.compare(thisAtomState, otherAtomState)) != 0) {
            return diff;
        } else {
            return this.getPredicate().compareTo(otherAtom.getPredicate());
        }
    }

    /**
     * Method returns atom that corresponds to the {@code true} statement.
     * 
     * @return
     */
    public static SLTLxAtom getTrue() {
        return new SLTLxAtom(1);
    }

    /**
     * Method returns atom that corresponds to the {@code false} statement.
     * 
     * @return
     */
    public static SLTLxAtom getFalse() {
        return new SLTLxAtom(2);
    }

    @Override
    public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
            SATSynthesisEngine synthesisEngine) {
        if (this.clause == null) {
            int encoding = synthesisEngine.getMappings().add(this);
            this.clause = new CNFClause(encoding);
        }
        return this.clause.toCNF();
    }

    @Override
    public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
            SATSynthesisEngine synthesisEngine) {
        if (this.clause == null) {
            int encoding = synthesisEngine.getMappings().add(this);
            this.clause = new CNFClause(encoding);
        }
        return this.clause.toNegatedCNF();
    }

}
