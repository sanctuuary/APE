package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints.append(negatedFact.getPropositionalEncoding(synthesisEngine).toString());
		return constraints.toString();
	}

}
