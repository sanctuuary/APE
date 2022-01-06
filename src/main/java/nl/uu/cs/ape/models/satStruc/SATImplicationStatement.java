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
public class SATImplicationStatement extends SATFact {

private SATFact ifFact;
private SATFact thenFact;
	
	

	public SATImplicationStatement(SATFact ifFact, SATFact thenFact) {
		super();
		this.ifFact = ifFact;
		this.thenFact = thenFact;
	}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		
		List<CNFClause> allClauses = new ArrayList<CNFClause>();

		/* Add the elements of the if element of the implication.. */
		allClauses.addAll(ifFact.getNegatedCNFEncoding(stateNo, synthesisEngine));
		Set<CNFClause> newClauses = thenFact.getCNFEncoding(stateNo, synthesisEngine);
		/* .. and combine it with all the other elements of the then term. */
		ListIterator<CNFClause> allClausesIt = allClauses.listIterator();
		while (allClausesIt.hasNext()) {
			  /* Remove the existing element .. */
			  CNFClause existingClause = allClausesIt.next();
			  allClausesIt.remove();
			  
			  /* ... and add all the combinations of that elements and the new elements. */
			  for(CNFClause newClause : newClauses) {
				  allClausesIt.add(CNFClause.disjoin2Clauses(existingClause, newClause));
			  }
		  }
		
		Set<CNFClause> fullClauses = new HashSet<>();
		fullClauses.addAll(allClauses);
		return fullClauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> constraints = new HashSet<CNFClause>();
		constraints.addAll(ifFact.getCNFEncoding(stateNo, synthesisEngine));
		constraints.addAll(thenFact.getNegatedCNFEncoding(stateNo, synthesisEngine));
		return constraints;
	}

}
