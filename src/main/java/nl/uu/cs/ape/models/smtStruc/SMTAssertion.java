package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFact;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class SMTAssertion implements SMTLib2Row {

	private SMTFact content;
	
	public SMTAssertion(SMTFact content) {
		this.content = content;
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(assert ")
				.append(content.getSMT2Encoding(synthesisEngine))
			.append(")");
		return constraints.append("\n").toString();
	}
}
