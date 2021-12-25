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
public class SATOrStatement implements SATFact {

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
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		List<SATClause> allClauses = new ArrayList<SATClause>();

		Iterator<SATFact> currDisjFact = disjointFacts.iterator();
		/* Add the first elements of the disjunction to the list of clauses.. */
		if (currDisjFact.hasNext()) {
			allClauses.addAll(currDisjFact.next().createCNFEncoding(synthesisEngine));
		  while (currDisjFact.hasNext()) {
			  Set<SATClause> newClauses = currDisjFact.next().createCNFEncoding(synthesisEngine);
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
		Set<SATClause> fullClauses = new HashSet<>();
		fullClauses.addAll(allClauses);
		return fullClauses;
	}

	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<SATClause> constraints = new HashSet<SATClause>();
		/* Add each element of the conjunction as a separate clause/case .*/
		for(SATFact fact : disjointFacts) {
			constraints.addAll(fact.createNegatedCNFEncoding(synthesisEngine));
		}
		return constraints;
	}
	
	
	
	
	

}
