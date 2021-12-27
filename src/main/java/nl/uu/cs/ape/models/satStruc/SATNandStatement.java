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
public class SATNandStatement implements SATFact {

private Set<SATFact> nconjunctedFacts;
	
	
	public SATNandStatement(SATFact arg1, SATFact arg2) {
		super();
		this.nconjunctedFacts = new HashSet<SATFact>();
		this.nconjunctedFacts.add(arg1);
		this.nconjunctedFacts.add(arg2);
	}

	public SATNandStatement(Collection<? extends SATFact> nconjunctedFacts) {
		super();
		this.nconjunctedFacts = new HashSet<SATFact>();
		nconjunctedFacts.forEach(fact -> this.nconjunctedFacts.add(fact));
	}


	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return new SATAndStatement(nconjunctedFacts).getNegatedCNFEncoding(synthesisEngine);
	}

	
	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return new SATAndStatement(nconjunctedFacts).getCNFEncoding(synthesisEngine);
	}

}
