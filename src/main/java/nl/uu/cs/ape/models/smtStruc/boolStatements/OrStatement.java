package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model (or x y) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class OrStatement implements Fact {

	private List<Fact> disjointFacts;
	
	
	public OrStatement(Fact ifStatement, Fact thanStatement) {
		super();
		disjointFacts = new ArrayList<Fact>();
		disjointFacts.add(ifStatement);
		disjointFacts.add(thanStatement);
	}



	public OrStatement(List<Fact> disjointFacts) {
		this.disjointFacts = disjointFacts;
	}


	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		if(disjointFacts.size() == 1) {
			return disjointFacts.get(0).toString(mapping);
		}
		constraints.append("(or");
//		add the statements to the dijunction
		for(Fact fact : disjointFacts) {
			constraints.append(" ").append(fact.toString(mapping));
		}
		constraints.append(")");
		
		return constraints.toString();
	}
}
