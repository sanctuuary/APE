package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model not statement in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATNotStatement implements SATFact {

private SATFact negatedFact;
	
	public SATNotStatement(SATFact arg1) {
		super();
		this.negatedFact = arg1; 
	}


	@Override
	public Set<SATClause> createCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return negatedFact.createNegatedCNFEncoding(synthesisEngine);
	}

	@Override
	public Set<SATClause> createNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return negatedFact.createCNFEncoding(synthesisEngine);
	}

}
