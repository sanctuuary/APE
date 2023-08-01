package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model conjunction of terms in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxConjunction extends SLTLxFormula {

	private Set<SLTLxFormula> conjunctedFacts;

	public SLTLxConjunction(SLTLxFormula arg1, SLTLxFormula arg2) {
		super();
		this.conjunctedFacts = new HashSet<>();
		if (arg1 != null)
			this.conjunctedFacts.add(arg1);
		if (arg2 != null)
			this.conjunctedFacts.add(arg2);
	}

	public SLTLxConjunction(SLTLxFormula arg1, SLTLxFormula arg2, SLTLxFormula arg3) {
		super();
		this.conjunctedFacts = new HashSet<>();
		if (arg1 != null)
			this.conjunctedFacts.add(arg1);
		if (arg2 != null)
			this.conjunctedFacts.add(arg2);
		if (arg3 != null)
			this.conjunctedFacts.add(arg3);
	}

	public SLTLxConjunction(Collection<? extends SLTLxFormula> conjunctedFacts) {
		super();
		this.conjunctedFacts = new HashSet<>();
		conjunctedFacts.forEach(fact -> {
			if (fact != null)
				this.conjunctedFacts.add(fact);
		});
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		/* Implement conjunction over an empty set rule. */
		if (conjunctedFacts.isEmpty()) {
			return SLTLxAtom.getTrue().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		}

		Set<Set<String>> allClauses = new HashSet<>();

		/*
		 * Conjunct the collection of clauses that encode each of the conjuncted
		 * elements.
		 */
		for (SLTLxFormula formula : conjunctedFacts) {
			allClauses.add(formula.getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		}
		return CNFClause.conjunctClausesCollection(allClauses);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		/* Implement conjunction over an empty set rule. */
		if (conjunctedFacts.isEmpty()) {
			return SLTLxAtom.getFalse().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
		}

		Set<Set<String>> allClauses = new HashSet<>();

		/*
		 * Disjoint the collection of clauses that encode negatioNs of each of the
		 * disjoint elements.
		 */
		for (SLTLxFormula formula : conjunctedFacts) {
			allClauses.add(formula.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));
		}
		return CNFClause.disjoinClausesCollection(allClauses);
	}

}
