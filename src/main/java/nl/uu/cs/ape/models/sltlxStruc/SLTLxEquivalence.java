package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model equivalence (e.g., "a &lt;=&gt; b") in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxEquivalence extends SLTLxFormula {

	private SLTLxFormula firstArg;
	private SLTLxFormula secondArg;

	public SLTLxEquivalence(SLTLxFormula firstArg, SLTLxFormula secondArg) {
		super();
		this.firstArg = firstArg;
		this.secondArg = secondArg;
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> allClauses = new HashSet<String>();

		/* Add the elements that represent the 2 way implication. */
		allClauses.addAll(
				new SLTLxImplication(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.addAll(
				new SLTLxImplication(secondArg, firstArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));

		return allClauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> allClauses = new HashSet<String>();

		/* Ensure that the 2 arguments are not the same. */
		allClauses.addAll(
				new SLTLxDisjunction(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.addAll(new SLTLxNegatedConjunction(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping,
				synthesisEngine));

		return allClauses;
	}

}
