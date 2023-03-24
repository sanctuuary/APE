package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model disjunction of terms in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxDisjunction extends SLTLxFormula {

	private Set<SLTLxFormula> disjointFacts;

	public SLTLxDisjunction(SLTLxFormula arg1, SLTLxFormula arg2) {
		super();
		this.disjointFacts = new HashSet<>();
		if (arg1 != null)
			this.disjointFacts.add(arg1);
		if (arg2 != null)
			this.disjointFacts.add(arg2);
	}

	public SLTLxDisjunction(Collection<? extends SLTLxFormula> conjunctedFacts) {
		super();
		this.disjointFacts = new HashSet<>();
		conjunctedFacts.forEach(fact -> {
			if (fact != null)
				this.disjointFacts.add(fact);
		});
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		/* Implement disjunction over an empty set rule. */
		if (disjointFacts.isEmpty()) {
			return SLTLxAtom.getFalse().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		}

		Set<Set<String>> allClauses = new HashSet<>();

		/*
		 * Disjoint the collection of clauses that encode each of the disjoint elements.
		 */
		for (SLTLxFormula formula : disjointFacts) {
			allClauses.add(formula.getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		}
		return CNFClause.disjoinClausesCollection(allClauses);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		/* Implement disjunction over an empty set rule. */
		if (disjointFacts.isEmpty()) {
			return SLTLxAtom.getTrue().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		}

		Set<Set<String>> allClauses = new HashSet<>();

		/*
		 * Conjunct the collection of clauses that encode negations of each of the
		 * disjoint elements.
		 */
		for (SLTLxFormula formula : disjointFacts) {
			allClauses.add(formula.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));
		}
		return CNFClause.conjunctClausesCollection(allClauses);
	}

}
