package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model nand - (not x)) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class NotStatement implements Fact {

	private Fact negatedStatement;
	
	
	
	public NotStatement(Fact negatedStatement) {
		super();
		this.negatedStatement = negatedStatement;
	}



	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(not ")
				.append(negatedStatement.getSMT2Encoding(synthesisEngine))
			.append(")");
		
		return constraints.toString();
	}
}
