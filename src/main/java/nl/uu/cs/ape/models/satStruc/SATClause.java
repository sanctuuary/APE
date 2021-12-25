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
public class SATClause implements SATFact {

	List<Integer> atoms;
	
	/**
	 * Create clause based on the list of elements (integers, > 0)
	 * @param atoms
	 */
	public SATClause(List<Integer> atoms) {
		super();
		this.atoms = new ArrayList<Integer>();
		atoms.forEach(atom -> this.atoms.add(atom));
	}
	
	/**
	 * Create a clause that has only one element.
	 * 
	 * @param atom
	 */
	public SATClause(Integer atom) {
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
	public static SATClause combine2Clauses(SATClause clause1, SATClause clause2) {
		List<Integer> combinedAtoms = new ArrayList<Integer>();
		clause1.atoms.forEach(existingAtom -> combinedAtoms.add(existingAtom));
		clause2.atoms.forEach(newAtom -> combinedAtoms.add(newAtom));
		return new SATClause(combinedAtoms);
	}
	
//	public String get

	@Override
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<SATClause> clause = new HashSet<>();
		clause.add(this);
		return clause;
	}

	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<SATClause> clauses = new HashSet<>();
		for(int element : this.atoms) {
			clauses.add(new SATClause(-element));
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
