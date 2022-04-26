package nl.uu.cs.ape.models.satStruc;

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
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(curVarMapping); 
		SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, bindedVariable.getVariableDomain(stateNo, synthesisEngine));
		
		clauses.addAll(flatBindedVariable.getExistentialCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(formula.getCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionEnforcingCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(curVarMapping); 
		SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, bindedVariable.getVariableDomain(stateNo, synthesisEngine));
		
		clauses.addAll(flatBindedVariable.getUniversalCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(formula.getNegatedCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionEnforcingCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

}
