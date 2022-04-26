package nl.uu.cs.ape.models.satStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Finally (F) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxFinally extends SLTLxFormula {

private SLTLxFormula formula;
	
	public SLTLxFinally(SLTLxFormula formula) {
		super();
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Disjoint the collection of clauses that encode the formula at each of the workflow steps. */
		for(int i = stateNo; i <= synthesisEngine.getSolutionSize(); i++) {
			allClauses.add(formula.getCNFEncoding(i, variableMapping, synthesisEngine));
		}
		return CNFClause.disjoinClausesCollection(allClauses);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Conjunct the collection of clauses that encode negation the formula at each of the workflow steps. */
		for(int i = stateNo; i <= synthesisEngine.getSolutionSize(); i++) {
			allClauses.add(formula.getNegatedCNFEncoding(i, variableMapping, synthesisEngine));
		}
		return CNFClause.conjunctClausesCollection(allClauses);
	}

}
