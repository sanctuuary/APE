package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.APEPredicate;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.SMT2LibElem;

/**
 * Interface used to present any predicate in the smt2lib structure.
 * @author Vedran Kasalica
 *
 */
public class SMTFunctionName implements SMT2LibElem {

	private WorkflowElement functionName; 
	
	
	public SMTFunctionName(WorkflowElement element) {
		this.functionName = element;
	}
	
	public String toString(SMTPredicateMappings mapping) {
			return functionName.toString(); 
	}

	
}
