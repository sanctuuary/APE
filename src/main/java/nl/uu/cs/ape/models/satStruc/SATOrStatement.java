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
	
	
	public SATOrStatement(int stateNo, SATFact arg1, SATFact arg2) {
		super(stateNo);
		this.disjointFacts = new HashSet<SATFact>();
		this.disjointFacts.add(arg1);
		this.disjointFacts.add(arg2);
	}

	public SATOrStatement(int stateNo, Collection<? extends SATFact> conjunctedFacts) {
		super(stateNo);
		this.disjointFacts = new HashSet<SATFact>();
		conjunctedFacts.forEach(fact -> this.disjointFacts.add(fact));
	}

	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		List<CNFClause> allClauses = new ArrayList<CNFClause>();

		Iterator<SATFact> currDisjFact = disjointFacts.iterator();
		/* Add the first elements of the disjunction to the list of clauses.. */
		if (currDisjFact.hasNext()) {
			allClauses.addAll(currDisjFact.next().getCNFEncoding(synthesisEngine));
		  while (currDisjFact.hasNext()) {
			  Set<CNFClause> newClauses = currDisjFact.next().getCNFEncoding(synthesisEngine);
			  /* .. and combine it with all the other elements. */
			  ListIterator<CNFClause> allClausesIt = allClauses.listIterator();
			  while (allClausesIt.hasNext()) {
				  /* Remove the existing element .. */
				  CNFClause existingClause = allClausesIt.next();
				  allClausesIt.remove();
				  
				  /* ... and add all the combinations of that elements and the new elements. */
				  for(CNFClause newClause : newClauses) {
					  allClausesIt.add(CNFClause.combine2Clauses(existingClause, newClause));
				  }
			  }
		  }
		}
		Set<CNFClause> fullClauses = new HashSet<>();
		fullClauses.addAll(allClauses);
		return fullClauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> constraints = new HashSet<CNFClause>();
		/* Add each element of the conjunction as a separate clause/case .*/
		for(SATFact fact : disjointFacts) {
			constraints.addAll(fact.getNegatedCNFEncoding(synthesisEngine));
		}
		return constraints;
	}
	
	
	
	
	

}
