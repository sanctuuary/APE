package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * The class represents a clause/fact used in the SAT encoding (CNF). 
 * 
 * @author Vedran Kasalica
 *
 */
public class CNFClause {

	List<Integer> atoms;
	
	/**
	 * Create clause based on the list of elements (integers, > 0)
	 * @param atoms
	 */
	public CNFClause(List<Integer> atoms) {
		super();
		this.atoms = new ArrayList<Integer>();
		atoms.forEach(atom -> this.atoms.add(atom));
	}
	
	/**
	 * Create a clause that has only one element.
	 * 
	 * @param atom
	 */
	public CNFClause(Integer atom) {
		super();
		this.atoms = new ArrayList<Integer>();
		this.atoms.add(atom);
	}

	/**
	 * Return conjunction of the collectors of clauses. Take a set/list of collections of {@link CNFClause}s and combine them under the AND logic operator.
	 * @param facts - collections of 'collections of clauses' that are conjunct
	 * @return Set of {@link CNFClause}s that represent conjunction of the given collections of clauses.
	 */
	public static Set<CNFClause> conjunctClausesCollection(Collection<Collection<CNFClause>> facts) {
		Set<CNFClause> allClauses = new HashSet<CNFClause>();
		facts.forEach(col -> allClauses.addAll(col));
		
		return allClauses;
	}
	
	/**
	 * Return disjunction of the collectors of clauses. Take a set/list of collections of {@link CNFClause}s and combine them under the OR logic operator.
	 * @param facts - collections of 'collections of clauses' that are disjoint.
	 * @return Set of {@link CNFClause}s that represent disjunction of the given collections of clauses.
	 */
	public static Set<CNFClause> disjoinClausesCollection(Collection<Collection<CNFClause>> facts) {
		List<CNFClause> clausesList = new ArrayList<CNFClause>();
		Iterator<Collection<CNFClause>> currDisjFact = facts.iterator();
		
		if (currDisjFact.hasNext()) {
			clausesList.addAll(currDisjFact.next());
		  while (currDisjFact.hasNext()) {
			  Collection<CNFClause> newClauses = currDisjFact.next();
			  /* .. and combine it with all the other elements. */
			  ListIterator<CNFClause> allClausesIt = clausesList.listIterator();
			  while (allClausesIt.hasNext()) {
				  /* Remove the existing element .. */
				  CNFClause existingClause = allClausesIt.next();
				  allClausesIt.remove();
				  
				  /* ... and add all the combinations of that elements and the new elements. */
				  for(CNFClause newClause : newClauses) {
					  allClausesIt.add(CNFClause.disjoin2Clauses(existingClause, newClause));
				  }
			  }
		  }
		}
		Set<CNFClause> allClauses = new HashSet<CNFClause>();
		allClauses.addAll(clausesList);
		return allClauses;
	}
	
	/** 
	 * Return a new clause that combines the two clauses. The method combines the 2 sets of disjoint elements.
	 * 
	 * @param clause1 - 1st clause that should be combined
	 * @param clause2 - 2nd clause that should be combined
	 * @return
	 */
	public static CNFClause disjoin2Clauses(CNFClause clause1, CNFClause clause2) {
		List<Integer> combinedAtoms = new ArrayList<Integer>();
		clause1.atoms.forEach(existingAtom -> combinedAtoms.add(existingAtom));
		clause2.atoms.forEach(newAtom -> combinedAtoms.add(newAtom));
		return new CNFClause(combinedAtoms);
	}
	

	public Set<CNFClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clause = new HashSet<>();
		clause.add(this);
		return clause;
	}

	public Set<CNFClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<>();
		for(int element : this.atoms) {
			clauses.add(new CNFClause(-element));
		}
		
		return clauses;
	}
	
	
	public String toCNF() {
		StringBuilder cnf = new StringBuilder();
		atoms.forEach(elem -> cnf.append(elem + " "));
		cnf.append("0\n");
		
		return cnf.toString();
	}
	
}
