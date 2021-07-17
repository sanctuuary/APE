package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model (and x y) statement in SMTLib2.
 * 
 * @author Vedran Kasalica
 *
 */
public class AndStatement implements Fact {

private List<Fact> conjunctedFacts;
	
	
	public AndStatement(Fact arg1, Fact arg2) {
		super();
		conjunctedFacts = new ArrayList<Fact>();
		conjunctedFacts.add(arg1);
		conjunctedFacts.add(arg2);
	}



	public AndStatement(List<Fact> conjunctedFacts) {
		this.conjunctedFacts = conjunctedFacts;
	}


	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(conjunctedFacts.size() == 1) {
			return conjunctedFacts.get(0).getSMT2Encoding(synthesisEngine);
		}
		constraints.append("(and");
//		add the statements to the conjunction
		for(Fact fact : conjunctedFacts) {
			constraints.append(" ").append(fact.getSMT2Encoding(synthesisEngine));
		}
		constraints.append(")");
		
		return constraints.toString();
	}
}
