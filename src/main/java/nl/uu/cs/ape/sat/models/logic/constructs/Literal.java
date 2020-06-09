package nl.uu.cs.ape.sat.models.logic.constructs;

import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * The {@code Literal} class represents literals (atoms that can be negated) corresponding to the usage of the modules
 * and types in the solution. It is of the form {@code PredicateLabel(Attribute)} where
 * {@code PredicateLabel} represents a single predicate/label used to depict {@code AbstractModule, Module}
 * or {@code Type}, while {@code State} represents the state in the module/type automaton where the
 * module/type is used (or not used if the literal is negative). The second attribute (referredStateArgument) is optional and used when
 * the Literal represent the type that is used as tool input - referred state is the state when the type was created.
 * <p>
 * {@code Literals} can start with a negation.<br>
 * {@code Literals} are compared according to the state in which they are used ({@linkplain #getUsedInStateArgument}),
 * i.e. a literal that represents the n-th state in the workflow, comes before the literal that represents the (n+1)th state.
 *
 * @author Vedran Kasalica
 */
public class Literal implements Comparable<Literal> {

    /**
     * Integer value used to encode the atom into cnf form.
     */
    private Integer mappedAtom;

    /**
     * true if the atom is negated.
     */
    private Boolean negated;

    /**
     * The {@link Atom} class represents elements of the workflow, that can be true or not (depending of the truth value of the literal).
     */
    private Atom atom;

    /**
     * Generating an object from a mapped representation of the Literal.
     *
     * @param mappedLiteral Mapped literal.
     * @param atomMapping   Mapping of the atoms.
     */
    public Literal(String mappedLiteral, AtomMappings atomMapping) {
        super();
        if (mappedLiteral.startsWith("-")) {
            negated = true;
            mappedAtom = Integer.parseInt(mappedLiteral.substring(1));
        } else {
            negated = false;
            mappedAtom = Integer.parseInt(mappedLiteral);
        }

        this.atom = atomMapping.findOriginal(mappedAtom);
    }

    /**
     * To mapped string string.
     *
     * @return The value of the mapped literal.
     */
    public String toMappedString() {
        if (negated) {
            return "-" + mappedAtom;
        } else {
            return mappedAtom.toString();
        }
    }

    /**
     * To negated mapped string string.
     *
     * @return Negation of the value of the mapped literal.
     */
    public String toNegatedMappedString() {
        if (negated) {
            return mappedAtom.toString();
        } else {
            return "-" + mappedAtom;
        }
    }

    /**
     * To negated mapped int int.
     *
     * @return Negation of the value of the mapped literal.
     */
    public int toNegatedMappedInt() {
        if (negated) {
            return mappedAtom;
        } else {
            return -mappedAtom;
        }
    }

    /**
     * Return the type of the element in the workflow (tool, memory type, etc.)
     *
     * @return The {@link WorkflowElement} that corresponds to the Literal usage or not usage (in case of a negated literal).
     */
    public WorkflowElement getWorkflowElementType() {
        return atom.getWorkflowElementType();
    }

    /**
     * Return true if the current workflow element is of the given {@link WorkflowElement} type.
     *
     * @param workflowElemType Element type that is current literal is compared to.
     * @return true if the current workflow element corresponds to the given {@link WorkflowElement}, false otherwise.
     */
    public boolean isWorkflowElementType(WorkflowElement workflowElemType) {
        return atom.getWorkflowElementType() == workflowElemType;
    }

    /**
     * Returns true in case the literal is NEGATED, false otherwise.
     *
     * @return true if Literal is negated.
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * Returns the state in the automaton.
     *
     * @return String representation of the module/type automaton state.
     */
    public State getUsedInStateArgument() {
        return atom.getUsedInStateArgument();
    }

    /**
     * Returns the predicate/label used to depict {@code AbstractModule, Module, Type} or {@code State}.
     * Each of those refers to the element that is described by the Literal (e.g. PredicateLabel(State)).
     *
     * @return PredicateLabel object that is referred by the literal.
     */
    public PredicateLabel getPredicate() {
        return atom.getPredicate();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = atom.hashCode();
        result = prime * result + ((negated == null) ? 0 : negated.hashCode());
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
        Literal other = (Literal) obj;
        if (negated == null) {
            if (other.negated != null)
                return false;
        } else if (!negated.equals(other.negated))
            return false;
        return atom.equals(other.atom);
    }

    /**
     * Compare the two Literals according to the state they are used in.
     * Returns a negative integer, zero, or a positive integer as this
     * Literal's state comes before than, is equal to, or comes after
     * than the @otherLiteral's state.
     *
     * @param otherLiteral The Literal to be compared
     * @return The value 0 if the argument Literal's state is equal to this Literal's state;
     * a value less than 0 if this Literal's state comes before the @otherLiteral's state;
     * and a value greater than 0 if this Literal's state comes after the @otherLiteral's state.
     */
    public int compareTo(Literal otherLiteral) {

        int thisLiteralState = this.getUsedInStateArgument().getAbsoluteStateNumber();
        int otherLiteralState = otherLiteral.getUsedInStateArgument().getAbsoluteStateNumber();
        int diff = 0;
        if ((diff = Integer.compare(thisLiteralState, otherLiteralState)) != 0) {
            return diff;
        } else {
            return this.getPredicate().compareTo(otherLiteral.getPredicate());
        }
    }

    /**
     * Returns the Original (human readable) value of the literal. The atom of the
     * Literal is transformed using the {@link Atom#toString} function.
     *
     * @return The value of the original literal
     */
    public String toString() {
        if (negated) {
            return "-" + atom.toString();
        } else {
            return atom.toString();
        }
    }
}
