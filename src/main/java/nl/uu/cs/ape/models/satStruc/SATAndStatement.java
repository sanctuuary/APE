package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model conjunction of terms in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATAndStatement extends SATFact {

private Set<SATFact> conjunctedFacts;
	
	
	public SATAndStatement(SATFact arg1, SATFact arg2) {
		super();
		this.conjunctedFacts = new HashSet<SATFact>();
		this.conjunctedFacts.add(arg1);
		this.conjunctedFacts.add(arg2);
	}

	public SATAndStatement(Collection<? extends SATFact> conjunctedFacts) {
		super();
		this.conjunctedFacts = new HashSet<SATFact>();
		conjunctedFacts.forEach(fact -> this.conjunctedFacts.add(fact));
	}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Conjunct the collection of clauses that encode each of the conjucted elements. */
		for(SATFact formula : conjunctedFacts) {
			allClauses.add(formula.getCNFEncoding(stateNo, synthesisEngine));
		}
		return CNFClause.conjunctClausesCollection(allClauses);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Disjoint the collection of clauses that encode negatioNs of each of the disjoint elements. */
		for(SATFact formula : conjunctedFacts) {
			allClauses.add(formula.getNegatedCNFEncoding(stateNo, synthesisEngine));
		}
		return CNFClause.disjoinClausesCollection(allClauses);
	}

}
