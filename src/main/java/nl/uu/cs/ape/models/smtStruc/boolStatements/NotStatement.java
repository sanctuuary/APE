package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model nand - (not x)) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class NotStatement implements Fact {

	private Fact negatedStatement;
	
	
	
	public NotStatement(Fact negatedStatement) {
		super();
		this.negatedStatement = negatedStatement;
	}



	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(not ")
				.append(negatedStatement.toString(mapping))
			.append(")");
		
		return constraints.toString();
	}
}
