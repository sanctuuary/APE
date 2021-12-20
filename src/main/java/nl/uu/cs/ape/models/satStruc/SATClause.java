package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * A fact used for the SAT encoding. Represents a clause in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATClause implements SATFact {

	List<String> atoms;
	
	public SATClause(List<String> atoms) {
		super();
		this.atoms = new ArrayList<>();
		atoms.forEach(atom -> this.atoms.add(atom));
	}

	/** 
	 * Return a new clause that combines the two clauses. The method combines the 2 sets of disjoint elements.
	 * 
	 * @param clause1 - 1st clause that should be combined
	 * @param clause2 - 2nd clause that should be combined
	 * @return
	 */
	public static SATClause combine2Clauses(SATClause clause1, SATClause clause2) {
		List<String> combinedAtoms = new ArrayList<>();
		clause1.atoms.forEach(existingAtom -> combinedAtoms.add(existingAtom));
		clause2.atoms.forEach(newAtom -> combinedAtoms.add(newAtom));
		return new SATClause(combinedAtoms);
	}

	@Override
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SATClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
