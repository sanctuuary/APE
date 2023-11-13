package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

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
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		return negatedFact.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		return negatedFact.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

}
