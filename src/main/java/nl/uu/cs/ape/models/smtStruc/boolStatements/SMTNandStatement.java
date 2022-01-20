package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;

/**
 * Structure used to model nand - (not (and x y)) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class SMTNandStatement implements SMTFact {

	private SMTFact arg1;
	private SMTFact arg2;
	
	
	
	public SMTNandStatement(SMTFact ifStatement, SMTFact thanStatement) {
		super();
		this.arg1 = ifStatement;
		this.arg2 = thanStatement;
	}



	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
		.append("(not ")
			.append("(and ")
			.append(arg1.getSMT2Encoding(synthesisEngine)).append(" ")
			.append(arg2.getSMT2Encoding(synthesisEngine))
			.append(")")
		.append(")");
		
		return constraints.toString();
	}
}
