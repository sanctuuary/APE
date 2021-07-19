package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.Collection;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Row;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * Structure used to model declaration of new datatypes (declare-datatypes () ((A x y)))  in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class DeclareSimplifiedFunction implements SMTLib2Row {

	private WorkflowElement predicate;
	private SMTDataType dataTypeName;
	private SMTDataType automatonType;
	private int orderNumber;
	
	
	public DeclareSimplifiedFunction(WorkflowElement predicate, int orderNumber, SMTDataType dataTypeName, SMTDataType automatonType) {
		this.predicate = predicate;
		this.orderNumber = orderNumber;
		this.dataTypeName = dataTypeName;
		this.automatonType = automatonType;
	}

	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(declare-fun ")
				.append(predicate.toString()).append(orderNumber)
				.append(" (")
					.append(dataTypeName.toString())
				.append(") Bool)\n");
		
		constraints
			.append("(assert (forall ((x ")
				.append(dataTypeName.toString())
				.append(")) (=> (")
				.append(predicate.toString())
				.append(" (_ bv" + orderNumber + " ")
				.append(SMTUtils.countBits(synthesisEngine.getAutomatonSize(automatonType)))
				.append(") x) (")
				.append(predicate.toString()).append(orderNumber)
				.append(" x))))");
			
			
//			String addition = "(declare-fun module" + i + " (module) Bool)\n"
//        			+ "(assert (forall ((x module)) (=> (module (_ bv" + i + " 3) x) (module" + i + " x))))\n";
		return constraints.append("\n").toString();
	}
}
