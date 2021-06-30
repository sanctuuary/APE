package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model (= x y) statement in smt2lib.
 * 
 * @author Vedran Kasalica
 *
 */
public class EqualStatement implements Fact {

	private Fact arg1;
	private Fact arg2;
	
	
	
	public EqualStatement(Fact arg1, Fact arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}



	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(= ")
				.append(arg1.toString(mapping)).append(" ")
				.append(arg2.toString(mapping))
			.append(")");
		
		return constraints.toString();
	}
}
