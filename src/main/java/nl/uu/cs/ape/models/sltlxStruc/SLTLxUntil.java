package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * Structure used to model Until (U) modal statement in SLTLx.
 * TODO: Implement operator.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxUntil extends SLTLxFormula {

	private SLTLxFormula formulaFrom;
	private SLTLxFormula formulaUntil;

	public SLTLxUntil(SLTLxFormula formulaFrom, SLTLxFormula formulaUntil) {
		super();
		this.formulaFrom = formulaFrom;
		this.formulaUntil = formulaUntil;
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<Set<String>> allClauses = new HashSet<>();

		Set<String> clauses = new HashSet<>();
		// for(int i = stateNo; i < synthesisEngine.getSolutionSize(); i++) {
		// clauses.addAll(formula.getCNFEncoding(i, variableMapping, synthesisEngine));
		// }
		return clauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<>();
		return clauses;
	}

}
