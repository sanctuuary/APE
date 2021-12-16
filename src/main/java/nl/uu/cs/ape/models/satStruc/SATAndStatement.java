package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model conjunction of terms in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATAndStatement implements SATFact {

private List<SATFact> conjunctedFacts;
	
	
	public SATAndStatement(SATFact arg1, SATFact arg2) {
		super();
		this.conjunctedFacts = new ArrayList<SATFact>();
		this.conjunctedFacts.add(arg1);
		this.conjunctedFacts.add(arg2);
	}

	public SATAndStatement(List<? extends SATFact> conjunctedFacts) {
		super();
		this.conjunctedFacts = new ArrayList<SATFact>();
		conjunctedFacts.forEach(fact -> this.conjunctedFacts.add(fact));
	}

	@Override
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(conjunctedFacts.size() == 1) {
			return conjunctedFacts.get(0).getPropositionalEncoding(synthesisEngine);
		}

		Iterator<SATFact> currFact = conjunctedFacts.iterator();
		if (currFact.hasNext()) {
			constraints.append("(").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  while (currFact.hasNext()) {
			  constraints.append(" & ").append(currFact.next().getPropositionalEncoding(synthesisEngine));
		  }
		  constraints.append(")");
		}
		
		return constraints.toString();
	}

}
