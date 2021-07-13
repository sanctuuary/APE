package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.models.SMTPredicateMappings;

/**
 * Structure used to model (and x y) statement in smt2lib.
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


	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		if(conjunctedFacts.size() == 1) {
			return conjunctedFacts.get(0).toString(mapping);
		}
		constraints.append("(and");
//		add the statements to the conjunction
		for(Fact fact : conjunctedFacts) {
			constraints.append(" ").append(fact.toString(mapping));
		}
		constraints.append(")");
		
		return constraints.toString();
	}
}
