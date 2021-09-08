package nl.uu.cs.ape.models.satStruc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model (or x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class OrStatement implements Fact {

	private List<Fact> disjointFacts;
	
	
	public OrStatement(Fact arg1, Fact arg2) {
		super();
		disjointFacts = new ArrayList<Fact>();
		disjointFacts.add(arg1);
		disjointFacts.add(arg2);
	}



	public OrStatement(List<Fact> disjointFacts) {
		this.disjointFacts = disjointFacts;
	}


	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(disjointFacts.size() == 1) {
			return disjointFacts.get(0).getSMT2Encoding(synthesisEngine);
		}
		constraints.append("(or");
//		add the statements to the dijunction
		for(Fact fact : disjointFacts) {
			constraints.append(" ").append(fact.getSMT2Encoding(synthesisEngine));
		}
		constraints.append(")");
		
		return constraints.toString();
	}
}
