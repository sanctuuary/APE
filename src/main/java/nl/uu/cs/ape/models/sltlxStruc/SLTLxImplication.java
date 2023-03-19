package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model implication in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxImplication extends SLTLxFormula {

	private SLTLxFormula ifFact;
	private SLTLxFormula thenFact;

	public SLTLxImplication(SLTLxFormula ifFact, SLTLxFormula thenFact) {
		super();
		this.ifFact = ifFact;
		this.thenFact = thenFact;
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {

		Set<Set<String>> allClauses = new HashSet<Set<String>>();

		/* Add the elements of the if element of the implication.. */
		allClauses.add(ifFact.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(thenFact.getCNFEncoding(stateNo, variableMapping, synthesisEngine));

		return CNFClause.disjoinClausesCollection(allClauses);

	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<Set<String>> allClauses = new HashSet<Set<String>>();

		/* Add the elements of the if element of the implication.. */
		allClauses.add(ifFact.getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(thenFact.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));

		return CNFClause.conjunctClausesCollection(allClauses);
	}

}
