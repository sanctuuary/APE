package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

private List<SATFact> disjointFacts;
	
	
	public SATOrStatement(SATFact arg1, SATFact arg2) {
		super();
		this.disjointFacts = new ArrayList<SATFact>();
		this.disjointFacts.add(arg1);
		this.disjointFacts.add(arg2);
	}

	public SATOrStatement(List<? extends SATFact> conjunctedFacts) {
		super();
		this.disjointFacts = new ArrayList<SATFact>();
		conjunctedFacts.forEach(fact -> this.disjointFacts.add(fact));
	}

	@Override
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(disjointFacts.size() == 1) {
			return disjointFacts.get(0).getPropositionalEncoding(synthesisEngine);
		}

		Iterator<SATFact> currFact = disjointFacts.iterator();
		if (currFact.hasNext()) {
			constraints.append("(").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  while (currFact.hasNext()) {
			  constraints.append(" & ").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  }
		  constraints.append(")");
		}
		
		return constraints.toString();
	}

	@Override
	public List<SATClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		List<SATClause> allClauses = new ArrayList<SATClause>();

		Iterator<SATFact> currDisjFact = disjointFacts.iterator();
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
