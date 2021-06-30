package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class SMTComment implements SMT2LibRow {

	private String content;
	
	public SMTComment(String content) {
		this.content = content;
	}
	
	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append(";; ")
				.append(content);
		return constraints.append("\n").toString();
	}
}
