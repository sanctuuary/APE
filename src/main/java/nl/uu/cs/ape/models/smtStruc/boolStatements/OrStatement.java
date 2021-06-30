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
//		dijoint the first two statements
		constraints
			.append("(or ")
				.append(disjointFacts.get(0).toString(mapping)).append(" ")
				.append(disjointFacts.get(1).toString(mapping))
			.append(")");
//		add the rest of the statements to the dijunction
		for(int i = 2; i < disjointFacts.size(); i++) {
			constraints
			.insert(0, "(or ")
				.append(disjointFacts.get(i).toString(mapping))
			.append(")");
		}
		
		return constraints.toString();
	}
}
