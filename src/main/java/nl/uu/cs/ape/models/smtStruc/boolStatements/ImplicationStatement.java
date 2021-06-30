package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model (=> x y) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class ImplicationStatement implements Fact {

	private Fact ifStatement;
	private Fact thanStatement;
	
	
	
	public ImplicationStatement(Fact ifStatement, Fact thanStatement) {
		super();
		this.ifStatement = ifStatement;
		this.thanStatement = thanStatement;
	}



	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(=> ")
				.append(ifStatement.toString(mapping)).append(" ")
				.append(thanStatement.toString(mapping))
			.append(")");
		
		return constraints.toString();
	}
}
