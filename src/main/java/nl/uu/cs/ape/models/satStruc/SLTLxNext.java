package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Next (N) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxNext extends SLTLxFormula {

private SLTLxFormula formula;
	
	public SLTLxNext(SLTLxFormula formula) {
		super();
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		return formula.getCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		return formula.getNegatedCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
	}

}
