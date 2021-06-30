package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class Assertion implements SMT2LibRow {

	private Fact content;
	
	public Assertion(Fact content) {
		this.content = content;
	}
	
	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(assert ")
				.append(content.toString(mapping))
			.append(")");
		return constraints.append("\n").toString();
	}
}
