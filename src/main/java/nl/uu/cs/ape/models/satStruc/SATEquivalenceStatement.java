package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model implication in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATEquivalenceStatement implements SATFact {

private SATFact firstArg;
private SATFact secondArg;
	
	

	public SATEquivalenceStatement(SATFact firstArg, SATFact secondArg) {
	super();
	this.firstArg = firstArg;
	this.secondArg = secondArg;
}

	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> allClauses = new HashSet<CNFClause>();

		/* Add the elements that represent the 2 way implication. */
		allClauses.addAll(new SATImplicationStatement(firstArg, secondArg).getCNFEncoding(synthesisEngine));
		allClauses.addAll(new SATImplicationStatement(secondArg, firstArg).getCNFEncoding(synthesisEngine));
		
		return allClauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> allClauses = new HashSet<CNFClause>();

		/* Ensure that the 2 arguments are not the same. */
		allClauses.addAll(new SATOrStatement(firstArg, secondArg).getCNFEncoding(synthesisEngine));
		allClauses.addAll(new SATNandStatement(firstArg, secondArg).getCNFEncoding(synthesisEngine));
		
		return allClauses;
	}

}
