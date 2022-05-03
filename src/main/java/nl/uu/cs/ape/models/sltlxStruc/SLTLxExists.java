package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.automaton.SLTLxVariable;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model exists statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxExists extends SLTLxFormula {

private SLTLxVariable bindedVariable;
private SLTLxFormula formula;
	
	public SLTLxExists(SLTLxVariable boundBariable,  SLTLxFormula formula) {
		super();
		this.bindedVariable = boundBariable; 
		this.formula = formula;
	}


	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<String>();
		SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(curVarMapping); 
		SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, SLTLxVariable.getVariableDomain(stateNo, synthesisEngine));
		
		/** Encode the possible substitutions for the given variable. */
		clauses.addAll(flatBindedVariable.getExistentialCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Encode the underlying formula. */
		clauses.addAll(formula.getCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Ensure that the variables and states they substitute satisfy the same properties. 
		 * The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionToPresereProperties(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<String>();
		SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(curVarMapping); 
		SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, SLTLxVariable.getVariableDomain(stateNo, synthesisEngine));
		
		/** Encode the possible substitutions for the given variable. */
		clauses.addAll(flatBindedVariable.getUniversalCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Encode the underlying formula. */
		clauses.addAll(formula.getNegatedCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** Ensure that the variables and states they substitute satisfy the same properties. 
		 * The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionToPresereProperties(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

}
