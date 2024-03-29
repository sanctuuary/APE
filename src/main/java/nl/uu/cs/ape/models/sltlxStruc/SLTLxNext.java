package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

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
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		if (synthesisEngine.getSolutionSize() <= stateNo) {
			return SLTLxAtom.getFalse().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		} else {
			return formula.getCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
		}
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		if (synthesisEngine.getSolutionSize() <= stateNo) {
			return SLTLxAtom.getTrue().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		} else {
			return formula.getNegatedCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
		}
	}

}
