package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model (or x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class OrStatement implements SMTFact {

	private List<SMTFact> disjointFacts;
	
	
	public OrStatement(SMTFact arg1, SMTFact arg2) {
		super();
		disjointFacts = new ArrayList<SMTFact>();
		disjointFacts.add(arg1);
		disjointFacts.add(arg2);
	}



	public OrStatement(List<SMTFact> disjointFacts) {
		this.disjointFacts = disjointFacts;
	}


	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(disjointFacts.size() == 1) {
			return disjointFacts.get(0).getSMT2Encoding(synthesisEngine);
		}
		constraints.append("(or");
//		add the statements to the dijunction
		for(SMTFact fact : disjointFacts) {
			constraints.append(" ").append(fact.getSMT2Encoding(synthesisEngine));
		}
		constraints.append(")");
		
		return constraints.toString();
	}
}
