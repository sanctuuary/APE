package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model (= x y) statement in SMTLib2.
 * 
 * @author Vedran Kasalica
 *
 */
public class SMTEqualStatement implements SMTFact {

	private SMTFact arg1;
	private SMTFact arg2;
	
	
	
	public SMTEqualStatement(SMTFact arg1, SMTFact arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}



	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(= ")
				.append(arg1.getSMT2Encoding(synthesisEngine)).append(" ")
				.append(arg2.getSMT2Encoding(synthesisEngine))
			.append(")");
		
		return constraints.toString();
	}
}
