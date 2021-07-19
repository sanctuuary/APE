package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTUtils;

/**
 * Structure used to model (assert x) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class ForallStatement implements Fact {

	private SMTBoundedVar boundedVar;
	private SMTDataType dataType;
	private Fact content;
	private boolean explicit;
	
	public ForallStatement(SMTBoundedVar boundedVar, SMTDataType dataType, Fact content) {
		this.boundedVar = boundedVar;
		this.dataType = dataType;
		this.content = content;
	}
	
	public ForallStatement(boolean explicit, SMTBoundedVar boundedVar, SMTDataType dataType, Fact content) {
		this.boundedVar = boundedVar;
		this.dataType = dataType;
		this.content = content;
		this.explicit = explicit;
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		if((synthesisEngine.getAutomatonSize(dataType) == -1) || explicit) {
			constraints
			.append("(forall ")
				.append("((")
					.append(boundedVar.getSMT2Encoding(synthesisEngine))
					.append(" ")
					.append(this.dataType.toString())
				.append(")) ")
				.append(content.getSMT2Encoding(synthesisEngine))
			.append(")");
		} else {
		constraints
			.append("(forall ")
				.append("((")
					.append(boundedVar.getSMT2Encoding(synthesisEngine))
					.append(" ")
					.append(this.dataType.toBitVector(synthesisEngine))
				.append(")) ")
				.append("(or ")
					.append(boundedVarIsOutOfBounds(synthesisEngine))
					.append(" ")
					.append(content.getSMT2Encoding(synthesisEngine))
				.append(")")
			.append(")");
		}
		return constraints.toString();
	}
	
	private String boundedVarIsOutOfBounds(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		
		constraints.append("(")
						.append(SMTBitVectorOp.GREATER_OR_EQUAL.toString())
						.append(" ")
						.append(boundedVar.getSMT2Encoding(synthesisEngine))
						.append(" (_ bv")
							.append(synthesisEngine.getAutomatonSize(dataType))
							.append(" ")
							.append(SMTUtils.countBits(synthesisEngine.getAutomatonSize(dataType)))
						.append(") ")
					.append(")");
		
		return constraints.toString();
	}
}
