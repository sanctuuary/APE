package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model exists statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATExists extends SATQuantification {

private SATVariable bindedVariable;
private SATFact formula;
	
	public SATExists(SATVariable boundBariable,  SATFact formula) {
		super();
		this.bindedVariable = boundBariable; 
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATVariableFlattening curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		SATVariableFlattening newVarMappping = new SATVariableFlattening(curVarMapping); 
		SATVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, bindedVariable.getVariableDomain(stateNo, synthesisEngine));
		
		clauses.addAll(flatBindedVariable.getExistentialCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(formula.getCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionEnforcingCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATVariableFlattening curVarMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		SATVariableFlattening newVarMappping = new SATVariableFlattening(curVarMapping); 
		SATVariable flatBindedVariable = newVarMappping.addNewVariable(bindedVariable, bindedVariable.getVariableDomain(stateNo, synthesisEngine));
		
		clauses.addAll(flatBindedVariable.getUniversalCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		clauses.addAll(formula.getNegatedCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		/** The rules have to be applied after visiting the bound formula (as done in the previous step). */
		clauses.addAll(flatBindedVariable.getVariableSubstitutionEnforcingCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		
		return clauses;
	}

}
