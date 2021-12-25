package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model nand relation of terms in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATNorStatement implements SATFact {

private Set<SATFact> ndisjointFacts;
	
	
	public SATNorStatement(SATFact arg1, SATFact arg2) {
		super();
		this.ndisjointFacts = new HashSet<SATFact>();
		this.ndisjointFacts.add(arg1);
		this.ndisjointFacts.add(arg2);
	}

	public SATNorStatement(Collection<? extends SATFact> ndisjointFacts) {
		super();
		this.ndisjointFacts = new HashSet<SATFact>();
		ndisjointFacts.forEach(fact -> this.ndisjointFacts.add(fact));
	}


	@Override
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return new SATOrStatement(ndisjointFacts).createNegatedCNFEncoding(synthesisEngine);
	}

	
	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return new SATOrStatement(ndisjointFacts).createCNFEncoding(synthesisEngine);
	}

}
