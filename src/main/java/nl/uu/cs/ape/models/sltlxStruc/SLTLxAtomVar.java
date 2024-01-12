package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.Predicate;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * The {@code SLTLxAtomVar} class represents {@link SLTLxAtom}s in SLTLx that
 * contain variables instead of states.
 *
 * @author Vedran Kasalica
 */
public class SLTLxAtomVar extends SLTLxFormula {

    /**
     * First argument is usually predicate that is referred (tool or type), or a
     * variable representing a type state.
     */
    private Predicate firstArg;

    /**
     * Second argument is a variable representing a typeState.
     */
    private SLTLxVariable secondArg;

    /**
     * Defines the type of the element in the workflow that the atom describes
     * (tool, memory type, etc.)
     */
    private AtomVarType elementType;

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
     * @param elementType - Element that defines what the atom depicts.
     * @param firstArg    - Predicate used or a variable that is the referenced.
     * @param secondArg   - Variable representing state in the automaton it was
     *                    used/created in.
     */
    public SLTLxAtomVar(AtomVarType elementType, Predicate firstArg, SLTLxVariable secondArg) {
        super();
        this.firstArg = firstArg;
        this.secondArg = secondArg;
        this.elementType = elementType;
    }

    /**
     * Gets firstArg.
     *
     * @return Field {@link #firstArg}.
     */
    public Predicate getFirstArg() {
        return firstArg;
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
        result = prime * result + ((secondArg == null) ? 0 : secondArg.hashCode());
        result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
        result = prime * result + ((firstArg == null) ? 0 : firstArg.hashCode());
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
        SLTLxAtomVar other = (SLTLxAtomVar) obj;
        if (secondArg == null) {
            if (other.secondArg != null)
                return false;
        } else if (!secondArg.equals(other.secondArg))
            return false;
        if (elementType != other.elementType)
            return false;
        if (firstArg == null) {
            if (other.firstArg != null)
                return false;
        } else if (!firstArg.equals(other.firstArg))
            return false;
        return true;
    }

    /**
     * Gets used in state argument.
     *
     * @return Field {@link #secondArg}.
     */
    public SLTLxVariable getSecondArg() {
        return secondArg;
    }

    /**
     * Return the type of the element in the workflow (tool, memory type, etc.)
     *
     * @return The {@link AtomType} that corresponds to the atom usage.
     */
    public AtomVarType getWorkflowElementType() {
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
        if (this.elementType.isVarDataType()) {
            return firstArg.getPredicateID() + "(" + secondArg.getPredicateID() + ")";

        } else if (this.elementType.isVarMemReference()) {
            return "[" + firstArg.getPredicateID() + "->" + secondArg.getPredicateID() + "]";

        } else if (this.elementType.isBinaryRel()) {
            return elementType.toString() + "(" + firstArg.getPredicateID() + "," + secondArg.getPredicateID() + ")";
        } else {
            return "SLTLxAtomVar_ERROR";
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
    public boolean isWorkflowElementType(AtomVarType workflowElemType) {
        return getWorkflowElementType() == workflowElemType;
    }

    @Override
    public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
            SATSynthesisEngine synthesisEngine) {
        if (this.clause == null) {
            this.substituteVariables(variableMapping, synthesisEngine);

            int encoding = synthesisEngine.getMappings().add(this);
            this.clause = new CNFClause(encoding);
        }
        return this.clause.toCNF();
    }

    @Override
    public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
            SATSynthesisEngine synthesisEngine) {
        if (this.clause == null) {
            this.substituteVariables(variableMapping, synthesisEngine);

            int encoding = synthesisEngine.getMappings().add(this);
            this.clause = new CNFClause(encoding);
        }
        return this.clause.toNegatedCNF();
    }

    /**
     * Method is used to substitute the variable occurrences to the unique ones.
     * It is used to ensure that nesting of quantifications over the same variable
     * works as intended
     * (e.g. "Exists (?x) Q(?x) Forall (?x) P(?x)")
     * 
     * @param variableMapping
     * @param synthesisEngine
     */
    private void substituteVariables(SLTLxVariableSubstitutionCollection variableMapping,
            SATSynthesisEngine synthesisEngine) {
        if (this.elementType.isVarDataType()) {
            this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
            synthesisEngine.getVariableUsage().addDataType(firstArg, secondArg);

        } else if (this.elementType.isVarMemReference()) {
            this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
            synthesisEngine.getVariableUsage().addMemoryReference((State) firstArg, secondArg);
        }

        else if (this.elementType.isBinaryRel() && !(this.elementType.equals(AtomVarType.VAR_VALUE))) {
            this.firstArg = variableMapping.getVarSabstitute((SLTLxVariable) this.firstArg);
            this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
            synthesisEngine.getVariableUsage().addBinaryPred(
                    new Pair<>((SLTLxVariable) this.firstArg, this.secondArg), this.elementType);
        }

        else if (this.elementType.equals(AtomVarType.VAR_VALUE)) {
            this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
            /* These predicates are not added to the set. */
        }
    }

}
