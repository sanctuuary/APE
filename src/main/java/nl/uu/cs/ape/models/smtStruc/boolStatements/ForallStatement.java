package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;

/**
 * Structure used to model (assert x) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class ForallStatement implements Fact {

	private SMTBoundedVar boundedVar;
	private SMTDataType dataType;
	private Fact content;
	
	public ForallStatement(SMTBoundedVar boundedVar, SMTDataType dataType, Fact content) {
		this.boundedVar = boundedVar;
		this.dataType = dataType;
		this.content = content;
	}
	
	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(forall ")
				.append("((")
					.append(boundedVar.toString(mapping)).append(" ")
					.append(this.dataType.toString(mapping))
				.append(")) ")
				.append(content.toString(mapping))
			.append(")");
		return constraints.toString();
	}
}
