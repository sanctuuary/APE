package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.automaton.SLTLxVariable;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * The {@code SLTLxAtomVar} class represents {@link SLTLxAtom}s in SLTLx that contain variables instead of states.
 *
 * @author Vedran Kasalica
 */
public class SLTLxAtomVar extends SLTLxFormula {

    /**
     * First argument is usually predicate that is referred (tool or type), or a variable representing a type state.
     */
    private PredicateLabel firstArg;

    /**
     * Second argument is a variable representing a typeState.
     */
    private SLTLxVariable secondArg;

    /**
     * Defines the type of the element in the workflow that the atom describes (tool, memory type, etc.)
     */
    private AtomVarType elementType;
    
    
    /**
     * Clause that represents the SLTLxAtom.
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
    public SLTLxAtomVar(AtomVarType elementType, PredicateLabel firstArg, SLTLxVariable secondArg) {
    	super();
        this.firstArg = firstArg;
        this.secondArg = secondArg;
        this.elementType = elementType;
    }

    /**TODO: What is the point of this?
     * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
      
     * @param atom SLTLxAtom that is being copied.
     */
    public SLTLxAtomVar(SLTLxAtomVar atom) {
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
        if (this.elementType == AtomVarType.VAR_VALUE) {
            return "[" + firstArg.getPredicateID() + "] <- (" + secondArg.getPredicateID() + ")";
        } else if (this.elementType == AtomVarType.R_RELATION_V) {
            return "R(" + firstArg.getPredicateID() + "," + secondArg.getPredicateID() + ")";
        } else if (this.elementType == AtomVarType.TYPE_V){
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
     * @param workflowElemType Element type that is current SLTLxAtom is compared to.
     * @return true if the current workflow element corresponds to the given {@link SMTDataType}, false otherwise.
     */
    public boolean isWorkflowElementType(AtomVarType workflowElemType) {
        return getWorkflowElementType() == workflowElemType;
    }

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		if(this.clause == null) {
			this.substituteVariables(variableMapping, synthesisEngine);
			
			int encoding = synthesisEngine.getMappings().add(this);
			this.clause = new CNFClause(encoding);
		}
		return this.clause.createCNFEncoding(synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		if(this.clause == null) {
			this.substituteVariables(variableMapping, synthesisEngine);
			
			int encoding = synthesisEngine.getMappings().add(this);
			this.clause = new CNFClause(encoding);
		}
		return this.clause.createNegatedCNFEncoding(synthesisEngine);
	}
	
	/**
	 * Method is used to substitute the variable occurrences to the unique ones. 
	 * It is used to ensure that nesting of quantifications over the same variable works as intended 
	 * (e.g. "Exists (?x) Q(?x) Forall (?x) P(?x)")
	 * @param variableMapping
	 * @param synthesisEngine
	 */
	private void substituteVariables(SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		if (this.elementType.isUnaryProperty()) {
			this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
			synthesisEngine.getVariableUsage().addUnaryPair(this.secondArg, this.firstArg);
		} 
		
		else if (!this.elementType.equals(AtomVarType.VAR_VALUE) && this.elementType.isBinaryRel()) {
			this.firstArg = variableMapping.getVarSabstitute((SLTLxVariable) this.firstArg);
			this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
			synthesisEngine.getVariableUsage().addBinaryPair(new Pair<SLTLxVariable>((SLTLxVariable) this.firstArg, this.secondArg), this.elementType);
		} 
		
		else if (this.elementType.equals(AtomVarType.VAR_VALUE)) {
			this.secondArg = variableMapping.getVarSabstitute(this.secondArg);
		}
		
	}

}
