package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class SMTComment implements SMTLib2Row {

	private String content;
	
	public SMTComment(String content) {
		this.content = content;
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append(";; ")
				.append(content);
		return constraints.append("\n").toString();
	}
}
