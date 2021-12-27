package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model (=> x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class ImplicationStatement implements SMTFact {

	private SMTFact ifStatement;
	private SMTFact thanStatement;
	
	
	
	public ImplicationStatement(SMTFact ifStatement, SMTFact thanStatement) {
		super();
		this.ifStatement = ifStatement;
		this.thanStatement = thanStatement;
	}



	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(=> ")
				.append(ifStatement.getSMT2Encoding(synthesisEngine)).append(" ")
				.append(thanStatement.getSMT2Encoding(synthesisEngine))
			.append(")");
		
		return constraints.toString();
	}
}
