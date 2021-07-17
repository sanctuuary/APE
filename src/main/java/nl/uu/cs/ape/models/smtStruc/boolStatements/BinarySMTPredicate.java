package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model binary predicate - (predicate x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class BinarySMTPredicate implements SMTPredicate {

	/** Predicate type. */
	private SMTFunctionName predicate;
	/** 1st argument of the predicate. */
	private SMTFunctionArgument argument1;
	/** 2nd argument might be a complex predicate (disj/conj of multiple predicates).*/
	private SMTFunctionArgument argument2;
	
	
//	public BinarySMTPredicate(WorkflowElement predicate, PredicateLabel argument1, PredicateLabel argument2) {
//		super();
//		this.predicate = predicate;
//		this.argument1 = new SMTFunctionArgument(argument1);
//		this.argument2 = new SMTFunctionArgument(argument2);
//	}
	
	public BinarySMTPredicate(SMTFunctionName predicate, SMTFunctionArgument argument1, SMTFunctionArgument argument2) {
		super();
		this.predicate = predicate;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}
	

	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		if((this.argument2 instanceof SMTPredicateFunArg) && (((SMTPredicateFunArg) this.argument2).getPredicate() != null)) {
			return getComposedPredicate(synthesisEngine, ((SMTPredicateFunArg) this.argument2).getPredicate());
		} else {
			StringBuilder constraints = new StringBuilder();
			constraints
			.append("(")
				.append(predicate.toString()).append(" ")
					.append(argument1.getSMT2Encoding(synthesisEngine)).append(" ")
					.append(argument2.getSMT2Encoding(synthesisEngine))
			.append(")");
			return constraints.toString();
		}
		
	}
	
	/**
	 * Recursive function that creates the the composition of predicates based on the structure of the given 2nd argument.
	 * @param synthesisEngine - synthesis instance containing all information needed to map the encoding to SMTLib2
	 * @param newArgument2 - second argument of the predicate
	 * @return
	 */
	private String getComposedPredicate(SMTSynthesisEngine synthesisEngine, PredicateLabel newArgument2) {
		StringBuilder constraints = new StringBuilder();
		if(newArgument2 instanceof AuxiliaryPredicate) {
			String sign = ((AuxiliaryPredicate) newArgument2).getLogicOp().toShortString();
			boolean first = true;
			for(TaxonomyPredicate predicate : ((AuxiliaryPredicate) newArgument2).getGeneralizedPredicates()) {
				if(first) {
					constraints.append(getComposedPredicate(synthesisEngine, predicate));
					first = false;
				} else {
					constraints.insert(0, "(" + sign + " ").append(" ").append(getComposedPredicate(synthesisEngine, predicate)).append(")");
				}
			}
		} else {
			constraints
			.append("(")
				.append(predicate.toString()).append(" ")
					.append(argument1.getSMT2Encoding(synthesisEngine)).append(" ")
					.append(synthesisEngine.getMappings().add(newArgument2))
			.append(")");
		}
		
		return constraints.toString();
	}
}
