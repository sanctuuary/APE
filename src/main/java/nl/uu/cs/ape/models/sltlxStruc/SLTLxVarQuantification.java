package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model exists statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public abstract class SLTLxVarQuantification extends SLTLxFormula {

protected SLTLxVariable boundVariable;
protected SLTLxFormula formula;
	
	public SLTLxVarQuantification(SLTLxVariable boundVariable,  SLTLxFormula formula) {
		super();
		this.boundVariable = boundVariable; 
		this.formula = formula;
	}


	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection newVarMappping, SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<String>();
		SLTLxVariable flatBindedVariable = newVarMappping.getVarSabstitute(boundVariable);
		/** Encode the underlying formula. */
		clauses.addAll(formula.getCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Ensure that the variables and states they substitute satisfy the same properties. 
		 * The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionToPreserveProperties(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(flatBindedVariable.getVariableMutualExclusion(stateNo, newVarMappping, synthesisEngine));
		return clauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection newVarMappping, SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<String>();
		SLTLxVariable flatBindedVariable = newVarMappping.getVarSabstitute(boundVariable);
		/** Encode the underlying formula. */
		clauses.addAll(formula.getNegatedCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Ensure that the variables and states they substitute satisfy the same properties. 
		 * The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionToPreserveProperties(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(flatBindedVariable.getVariableMutualExclusion(stateNo, newVarMappping, synthesisEngine));
		return clauses;
	}

}
