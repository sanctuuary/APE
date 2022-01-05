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
 * Structure used to model conjunction of terms in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATAndStatement extends SATFact {

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
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> constraints = new HashSet<CNFClause>();
		/* Add each element of the conjunction as a separate clause/case .*/
		for(SATFact fact : conjunctedFacts) {
			constraints.addAll(fact.getCNFEncoding(stateNo, synthesisEngine));
		}
		return constraints;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {

		List<CNFClause> allClauses = new ArrayList<CNFClause>();

		Iterator<SATFact> currConjFact = conjunctedFacts.iterator();
		/* Add the first elements of the disjunction to the list of clauses.. */
		if (currConjFact.hasNext()) {
			allClauses.addAll(currConjFact.next().getNegatedCNFEncoding(stateNo, synthesisEngine));
		  while (currConjFact.hasNext()) {
			  Set<CNFClause> newClauses = currConjFact.next().getNegatedCNFEncoding(stateNo, synthesisEngine);
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

}
