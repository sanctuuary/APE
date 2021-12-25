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
public class SATImplicationStatement implements SATFact {

private SATFact ifFact;
private SATFact thenFact;
	
	

	public SATImplicationStatement(SATFact ifFact, SATFact thenFact) {
	super();
	this.ifFact = ifFact;
	this.thenFact = thenFact;
}

	@Override
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		
		List<SATClause> allClauses = new ArrayList<SATClause>();

		/* Add the elements of the if element of the implication.. */
		allClauses.addAll(ifFact.createNegatedCNFEncoding(synthesisEngine));
		Set<SATClause> newClauses = thenFact.createCNFEncoding(synthesisEngine);
		/* .. and combine it with all the other elements of the then term. */
		ListIterator<SATClause> allClausesIt = allClauses.listIterator();
		while (allClausesIt.hasNext()) {
			  /* Remove the existing element .. */
			  SATClause existingClause = allClausesIt.next();
			  allClausesIt.remove();
			  
			  /* ... and add all the combinations of that elements and the new elements. */
			  for(SATClause newClause : newClauses) {
				  allClausesIt.add(SATClause.combine2Clauses(existingClause, newClause));
			  }
		  }
		
		Set<SATClause> fullClauses = new HashSet<>();
		fullClauses.addAll(allClauses);
		return fullClauses;
	}

	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<SATClause> constraints = new HashSet<SATClause>();
		constraints.addAll(ifFact.createCNFEncoding(synthesisEngine));
		constraints.addAll(thenFact.createNegatedCNFEncoding(synthesisEngine));
		return constraints;
	}

}
