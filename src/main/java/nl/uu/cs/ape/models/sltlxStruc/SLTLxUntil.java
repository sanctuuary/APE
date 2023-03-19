package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Until (U) modal statement in SLTLx.
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
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<Set<String>> allClauses = new HashSet<Set<String>>();

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
