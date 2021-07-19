package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model (assert x) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class ExistsStatement implements Fact {

	private SMTBoundedVar boundedVar;
	private SMTDataType dataType;
	private Fact content;
	
	public ExistsStatement(SMTBoundedVar boundedVar, SMTDataType dataType, Fact content) {
		this.boundedVar = boundedVar;
		this.dataType = dataType;
		this.content = content;
	}
	

	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if(synthesisEngine.getAutomatonSize(dataType) == -1) {
			constraints
			.append("(exists ")
				.append("((")
					.append(boundedVar.getSMT2Encoding(synthesisEngine))
					.append(" ")
					.append(this.dataType.toString())
				.append(")) ")
				.append(content.getSMT2Encoding(synthesisEngine))
			.append(")");
		} else {
		constraints
			.append("(exists ")
				.append("((")
					.append(boundedVar.getSMT2Encoding(synthesisEngine))
					.append(" ")
					.append(this.dataType.toBitVector(synthesisEngine))
				.append(")) ")
				.append("(and ")
					.append(boundedVarIsInBounds(synthesisEngine))
					.append(" ")
					.append(content.getSMT2Encoding(synthesisEngine))
				.append(")")
			.append(")");
		}
		
		return constraints.toString();
	}
	
	private String boundedVarIsInBounds(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		
		constraints.append("(")
						.append(SMTBitVectorOp.LESS_THAN.toString())
						.append(" ")
						.append(boundedVar.getSMT2Encoding(synthesisEngine))
						.append(" ")
						.append(synthesisEngine.getAutomatonSize(dataType))
					.append(")");
		
		return constraints.toString();
	}
}
