package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * A fact used for the SAT encoding. Represents a clause in cnf.
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
	 * Return a new clause that combines the two clauses. The method combines the 2 sets of disjoint elements.
	 * 
	 * @param clause1 - 1st clause that should be combined
	 * @param clause2 - 2nd clause that should be combined
	 * @return
	 */
	public static CNFClause combine2Clauses(CNFClause clause1, CNFClause clause2) {
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
