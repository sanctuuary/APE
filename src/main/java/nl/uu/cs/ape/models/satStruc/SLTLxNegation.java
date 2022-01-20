package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model not statement in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxNegation extends SLTLxFormula {

private SLTLxFormula negatedFact;
	
	public SLTLxNegation(SLTLxFormula arg1) {
		super();
		this.negatedFact = arg1; 
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		return negatedFact.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		return negatedFact.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

}
