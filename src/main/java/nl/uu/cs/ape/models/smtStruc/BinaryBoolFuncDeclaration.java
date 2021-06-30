package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionName;

/**
 * Structure used to model binary predicate - (predicate x y) statement in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class BinaryBoolFuncDeclaration implements SMT2LibRow {

	private SMTFunctionName predicate;
	private SMTDataType argument1;
	private SMTDataType argument2;
	
	
	
	public BinaryBoolFuncDeclaration(SMTFunctionName predicate, SMTDataType argument1, SMTDataType argument2) {
		super();
		this.predicate = predicate;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}

	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();

		constraints
		.append("(declare-fun ")
			.append(predicate.toString(mapping))
			.append(" (")
				.append(argument1.toString(mapping)).append(" ")
				.append(argument2.toString(mapping))
			.append(") Bool")
		.append(")");
		
		return constraints.append("\n").toString();
	}
}
