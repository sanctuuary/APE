package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;

/**
 * Structure used to model binary predicate - (predicate x y) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class SMTBinaryBoolFuncDeclaration implements SMTLib2Row {

	private AtomType predicate;
	private SMTDataType argument1;
	private SMTDataType argument2;
	
	
	
	public SMTBinaryBoolFuncDeclaration(AtomType predicate, SMTDataType argument1, SMTDataType argument2) {
		super();
		this.predicate = predicate;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}

	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();

		constraints
		.append("(declare-fun ")
			.append(predicate.toString())
			.append(" (")
				.append(argument1.toString()).append(" ")
				.append(argument2.toString())
			.append(") Bool")
		.append(")");
		
		return constraints.append("\n").toString();
	}
}
