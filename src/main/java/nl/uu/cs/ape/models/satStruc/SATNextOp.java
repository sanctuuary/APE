package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

import nl.uu.cs.ape.automaton.TypeStateVar;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model Next (N) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATNextOp extends SATModalOp {

	private SATOperation operation;
	private SATFact formula;
	
	public SATNextOp(SATOperation operation, SATFact formula) {
		super();
		this.operation = operation;
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Conjunct the collection of clauses that encode the operation and the formula */
		allClauses.add(operation.getCNFEncoding(stateNo, synthesisEngine));
		allClauses.add(formula.getCNFEncoding(stateNo + 1, synthesisEngine));
		
		return CNFClause.conjunctClausesCollection(allClauses);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Disjunction the collection of clauses that encode the negation of the operation and of the formula */
		allClauses.add(operation.getNegatedCNFEncoding(stateNo, synthesisEngine));
		allClauses.add(formula.getNegatedCNFEncoding(stateNo + 1, synthesisEngine));
		
		return CNFClause.conjunctClausesCollection(allClauses);
	}

}
