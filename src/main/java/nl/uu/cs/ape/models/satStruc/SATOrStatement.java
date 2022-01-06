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

import javax.swing.SpringLayout.Constraints;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model disjunction of terms in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATOrStatement extends SATFact {

private Set<SATFact> disjointFacts;
	
	
	public SATOrStatement(SATFact arg1, SATFact arg2) {
		super();
		this.disjointFacts = new HashSet<SATFact>();
		this.disjointFacts.add(arg1);
		this.disjointFacts.add(arg2);
	}

	public SATOrStatement(Collection<? extends SATFact> conjunctedFacts) {
		super();
		this.disjointFacts = new HashSet<SATFact>();
		conjunctedFacts.forEach(fact -> this.disjointFacts.add(fact));
	}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Disjoint the collection of clauses that encode each of the disjoint elements. */
		for(SATFact formula : disjointFacts) {
			allClauses.add(formula.getCNFEncoding(stateNo, synthesisEngine));
		}
		return CNFClause.disjoinClausesCollection(allClauses);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Conjunct the collection of clauses that encode negations of each of the disjoint elements. */
		for(SATFact formula : disjointFacts) {
			allClauses.add(formula.getNegatedCNFEncoding(stateNo, synthesisEngine));
		}
		return CNFClause.conjunctClausesCollection(allClauses);
	}
	
	
	
	
	

}
