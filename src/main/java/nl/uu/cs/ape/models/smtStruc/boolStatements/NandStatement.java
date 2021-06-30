package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model nand - (not (and x y)) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class NandStatement implements Fact {

	private Fact arg1;
	private Fact arg2;
	
	
	
	public NandStatement(Fact ifStatement, Fact thanStatement) {
		super();
		this.arg1 = ifStatement;
		this.arg2 = thanStatement;
	}



	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
		.append("(not ")
			.append("(and ")
			.append(arg1.toString(mapping)).append(" ")
			.append(arg2.toString(mapping))
			.append(")")
		.append(")");
		
		return constraints.toString();
	}
}
