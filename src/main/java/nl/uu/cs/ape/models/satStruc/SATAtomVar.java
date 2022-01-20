package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;

/**
 * The {@code SATAtom} class represents elements of the workflow, that consists
 * of the operation or type used, state where it is used and potentially
 * a state that it refers to, i.e. input type elements refer to a state
 * when the type was created.
 *
 * @author Vedran Kasalica
 */
public class SATAtomVar extends SATFact implements SATAbstractAtom {

    /**
     * First argument is usually predicate that is referred (tool or type), or a variable representing a type state.
     */
    private final PredicateLabel firstArg;

    /**
     * Second argument is a variable representing a typeState.
     */
    private final SATVariable secondArg;

    /**
     * Defines the type of the element in the workflow that the atom describes (tool, memory type, etc.)
     */
    private AtomVarType elementType;
    
    
    /**
     * Clause that represents the SATAtom.
     */
    private CNFClause clause = null;
    

    /**
     * Creates an atom that can represent usage of the tool, creation or usage of a type,
     * or a reference between an input type and the state in which it was generated..
     *
     * @param elementType - Element that defines what the atom depicts.
     * @param firstArg  - Predicate used or a variable that is the referenced.
     * @param secondArg - Variable representing state in the automaton it was used/created in.
     */
    public SATAtomVar(AtomVarType elementType, PredicateLabel firstArg, SATVariable secondArg) {
    	super();
        this.firstArg = firstArg;
        this.secondArg = secondArg;
        this.elementType = elementType;
    }

    /**TODO: What is the point of this?
     * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
      
     * @param atom SATAtom that is being copied.
     */
    public SATAtomVar(SATAtomVar atom) {
    	super();
        this.firstArg = atom.firstArg;
        this.secondArg = atom.secondArg;
        this.elementType = atom.elementType;
    }

	/**
     * Gets firstArg.
     *
     * @return Field {@link #firstArg}.
     */
    public PredicateLabel getFirstArg() {
        return firstArg;
    }

    /* (non-Javadoc)
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
        SATAtomVar other = (SATAtomVar) obj;
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
    public SATVariable getSecondArg() {
        return secondArg;
    }

    /**
     * Return the type of the element in the workflow (tool, memory type, etc.)
     *
     * @return The {@link SMTDataType} that corresponds to the atom usage.
     */
    public AtomVarType getWorkflowElementType() {
        return elementType;
    }

    /**
     * Returns the string representation of the SMTDataType, used for the textual solution representation. In case of the atom depicting
     * a usage of a type in the workflow, the structure of the representation contains an additional attribute, state in which the type was initially added to the memory.
     *
     * @return String representing the workflow element in a textual form. {@code null} if the atom type is not correct.
     */
    public String toString() {
        if (this.elementType == AtomVarType.VAR_REF) {
            return "[" + firstArg.getPredicateID() + "] <- (" + secondArg.getPredicateID() + ")";
        } else if (this.elementType == AtomVarType.TYPE_DEPENDENCY_VAR) {
            return "R(" + firstArg.getPredicateID() + "," + secondArg.getPredicateID() + ")";
        } else if (this.elementType == AtomVarType.TYPE_VAR){
            return firstArg.getPredicateID() + "(" + secondArg.getPredicateID() + ")";
        } else if (this.elementType == AtomVarType.VAR_EQUIVALENCE){
            return "[" + firstArg.getPredicateID() + "=" + secondArg.getPredicateID() + "]";
        } else {
        	return null;
        }
    }

    /**
     * Return true if the current workflow element is of the given {@link SMTDataType} type.
     *
     * @param workflowElemType Element type that is current SATAtom is compared to.
     * @return true if the current workflow element corresponds to the given {@link SMTDataType}, false otherwise.
     */
    public boolean isWorkflowElementType(AtomVarType workflowElemType) {
        return getWorkflowElementType() == workflowElemType;
    }

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		if(this.elementType.isUnaryProperty()) {
			SATVariable var = variableMapping.getVarSabstitute(this.secondArg);
			synthesisEngine.getVariableUsage().addUnaryPair(var, this.firstArg);
		} else if(!this.elementType.equals(AtomVarType.VAR_REF) && this.elementType.isBinaryRel()) {
			SATVariable firstVaR = variableMapping.getVarSabstitute((SATVariable) this.firstArg);
			SATVariable secondVar = variableMapping.getVarSabstitute(this.secondArg);
			synthesisEngine.getVariableUsage().addBinaryPair(new Pair<SATVariable>(firstVaR, secondVar), this.elementType);
		}
		if(this.clause == null) {
			int encoding = synthesisEngine.getMappings().add(this);
			this.clause = new CNFClause(encoding);
		}
		return this.clause.createCNFEncoding(synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		if(this.clause == null) {
			int encoding = synthesisEngine.getMappings().add(this);
			this.clause = new CNFClause(encoding);
		}
		return this.clause.createNegatedCNFEncoding(synthesisEngine);
	}

}
