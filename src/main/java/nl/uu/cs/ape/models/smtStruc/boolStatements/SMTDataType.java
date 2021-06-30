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
public class SMTDataType implements SMT2LibElem {

	private APEPredicate dataType; 
	
	public SMTDataType(APEPredicate element) {
		this.dataType = element;
	}
	
	
	public String toString(SMTPredicateMappings mapping) {
		if(dataType instanceof WorkflowElement) {
			return "data_" + ((WorkflowElement) dataType).toString(); 
		} else {
			return "data_" + mapping.add((PredicateLabel) dataType);
		}
	}

	
}
