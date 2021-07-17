package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class Assertion implements SMTLib2Row {

	private Fact content;
	
	public Assertion(Fact content) {
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
