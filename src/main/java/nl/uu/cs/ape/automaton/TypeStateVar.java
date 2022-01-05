package nl.uu.cs.ape.automaton;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.satStruc.CNFClause;

/***
 * The {@code State} class is used to represent a variable for type states. The variable only represents states from the type automatons, excluding the module automaton states. 
 * <p>
 * Labeling of the automaton is provided in
 * <a href="https://github.com/sanctuuary/APE/blob/master/res/WorkflowAutomaton_Implementation.png">/APE/res/WorkflowAutomaton_Implementation.png</a>.
 *
 * @author Vedran Kasalica
 */
public class TypeStateVar implements StateInterface {

	/** Unique name of the type state variable */
    private final String variableID;


    /**
     * Create new type state variable.
     * @param variableName - Unique variable name
     */
    public TypeStateVar(String variableName) {
		super();
		this.variableID = variableName;
	}


	/**
	 * @return the variableID
	 */
	public String getVariableName() {
		return variableID;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variableID == null) ? 0 : variableID.hashCode());
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
		TypeStateVar other = (TypeStateVar) obj;
		if (variableID == null) {
			if (other.variableID != null)
				return false;
		} else if (!variableID.equals(other.variableID))
			return false;
		return true;
	}


	@Override
	public String getPredicateID() {
		return variableID;
	}


	public Set<CNFClause> getExistentialCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Set<CNFClause> getUniversalCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

}
