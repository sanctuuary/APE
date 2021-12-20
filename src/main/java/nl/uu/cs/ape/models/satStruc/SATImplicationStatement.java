package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();

		constraints.append("(").append(ifFact.getPropositionalEncoding(synthesisEngine));
		constraints.append(" -> ").append(thenFact.getPropositionalEncoding(synthesisEngine));
		constraints.append(")");
		
		return constraints.toString();
	}

	@Override
	public List<SATClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		List<SATClause> allClauses = new ArrayList<SATClause>();

		/* Add the first elements of the disjunction to the list of clauses.. */
		if (currDisjFact.hasNext()) {
			allClauses.addAll(currDisjFact.next().getCNFEncoding(synthesisEngine));
		  while (currDisjFact.hasNext()) {
			  List<SATClause> newClauses = currDisjFact.next().getCNFEncoding(synthesisEngine);
			  /* .. and combine it with all the other elements. */
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
		  }
		}
		
		return allClauses;
	}

}
