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

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model conjunction of terms in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATAndStatement implements SATFact {

private Set<SATFact> conjunctedFacts;
	
	
	public SATAndStatement(SATFact arg1, SATFact arg2) {
		super();
		this.conjunctedFacts = new HashSet<SATFact>();
		this.conjunctedFacts.add(arg1);
		this.conjunctedFacts.add(arg2);
	}

	public SATAndStatement(Collection<? extends SATFact> conjunctedFacts) {
		super();
		this.conjunctedFacts = new HashSet<SATFact>();
		conjunctedFacts.forEach(fact -> this.conjunctedFacts.add(fact));
	}

	@Override
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<SATClause> constraints = new HashSet<SATClause>();
		/* Add each element of the conjunction as a separate clause/case .*/
		for(SATFact fact : conjunctedFacts) {
			constraints.addAll(fact.createCNFEncoding(synthesisEngine));
		}
		return constraints;
	}

	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {

		List<SATClause> allClauses = new ArrayList<SATClause>();

		Iterator<SATFact> currDisjFact = conjunctedFacts.iterator();
		/* Add the first elements of the disjunction to the list of clauses.. */
		if (currDisjFact.hasNext()) {
			allClauses.addAll(currDisjFact.next().createNegatedCNFEncoding(synthesisEngine));
		  while (currDisjFact.hasNext()) {
			  Set<SATClause> newClauses = currDisjFact.next().createNegatedCNFEncoding(synthesisEngine);
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

}
