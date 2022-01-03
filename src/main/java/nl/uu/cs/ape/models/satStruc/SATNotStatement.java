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
public class SATNotStatement extends SATFact {

private SATFact negatedFact;
	
	public SATNotStatement(int stateNo, SATFact arg1) {
		super(stateNo);
		this.negatedFact = arg1; 
	}


	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return negatedFact.getNegatedCNFEncoding(synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return negatedFact.getCNFEncoding(synthesisEngine);
	}

}
