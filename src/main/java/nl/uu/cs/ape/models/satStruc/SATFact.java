package nl.uu.cs.ape.models.satStruc;

import java.util.List;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

public interface SATFact extends SATElem {

	/**
	 * Get the CNF encoding of the statement.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return a list of clauses that represent cnf clauses.
	 */
	public List<SATClause> getCNFEncoding(SATSynthesisEngine synthesisEngine);
	
	/**
	 * Get negated version of the term.
	 * @return a fact that is negation of the original fact.
	 */
	public SATFact getNegated();
}
