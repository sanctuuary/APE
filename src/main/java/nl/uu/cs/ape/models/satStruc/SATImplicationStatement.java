package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model implication in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATImplicationStatement implements SATFact {

private List<SATFact> impliedFacts;
	
	
	public SATImplicationStatement(SATFact arg1, SATFact arg2) {
		super();
		this.impliedFacts = new ArrayList<SATFact>();
		this.impliedFacts.add(arg1);
		this.impliedFacts.add(arg2);
	}

	public SATImplicationStatement(List<? extends SATFact> conjunctedFacts) {
		super();
		this.impliedFacts = new ArrayList<SATFact>();
		conjunctedFacts.forEach(fact -> this.impliedFacts.add(fact));
	}

	@Override
	public String getPropositionalEncoding(SATSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(impliedFacts.size() == 1) {
			return impliedFacts.get(0).getPropositionalEncoding(synthesisEngine);
		}

		Iterator<SATFact> currFact = impliedFacts.iterator();
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
