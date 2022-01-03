package nl.uu.cs.ape.models.satStruc;

import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

public interface SATElem {

	
	/**
	 * Create the CNF encoding of the statement and return the list of clauses.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return a list of clauses that represent cnf clauses.
	 */
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine);
	
	/**
	 * CreatE the CNF encoding of the negation of the statement and return the list of clauses.
	 * 
	 * @param synthesisEngine - synthesis engine used to encode the problem.
	 * @return a list of clauses that represent the negated cnf clauses.
	 */
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine);
	
}
