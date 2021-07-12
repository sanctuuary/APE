package nl.uu.cs.ape.models.smtStruc.boolStatements;

import java.util.SortedSet;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model binary predicate - (predicate x y) statement in smt2lib.
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
	
	
	public BinarySMTPredicate(SMTFunctionName predicate, PredicateLabel argument1, PredicateLabel argument2) {
		super();
		this.predicate = predicate;
		this.argument1 = new SMTFunctionArgument(argument1);
		this.argument2 = new SMTFunctionArgument(argument2);
	}
	
	public BinarySMTPredicate(SMTFunctionName predicate, SMTFunctionArgument argument1, SMTFunctionArgument argument2) {
		super();
		this.predicate = predicate;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}
	

	public String toString(SMTPredicateMappings mapping) {
		if(this.argument2.getPredicate() != null) {
			return getComposedPredicate(mapping, this.argument2.getPredicate());
		} else {
			StringBuilder constraints = new StringBuilder();
			constraints
			.append("(")
				.append(predicate.toString(mapping)).append(" ")
					.append(argument1.toString(mapping)).append(" ")
					.append(argument2.toString(mapping))
			.append(")");
			return constraints.toString();
		}
		
	}
	
	/**
	 * Recursive function that creates the the composition of predicates based on the structure of the given 2nd argument.
	 * @param mapping - SMT2Lib mapping of the predicates
	 * @param newArgument2 - second argument of the predicate
	 * @return
	 */
	private String getComposedPredicate(SMTPredicateMappings mapping, PredicateLabel newArgument2) {
		StringBuilder constraints = new StringBuilder();
		if(newArgument2 instanceof AuxiliaryPredicate) {
			String sign = ((AuxiliaryPredicate) newArgument2).getLogicOp().toShortString();
			boolean first = true;
			for(TaxonomyPredicate predicate : ((AuxiliaryPredicate) newArgument2).getGeneralizedPredicates()) {
				if(first) {
					constraints.append(getComposedPredicate(mapping, predicate));
					first = false;
				} else {
					constraints.insert(0, "(" + sign + " ").append(" ").append(getComposedPredicate(mapping, predicate)).append(")");
				}
			}
		} else {
			constraints
			.append("(")
				.append(predicate.toString(mapping)).append(" ")
					.append(argument1.toString(mapping)).append(" ")
					.append(mapping.add(newArgument2))
			.append(")");
		}
		
//		if((newArgument2.getPredicateID() == argument2.getPredicate().getPredicateID()) & (argument2.getPredicate() instanceof AuxiliaryPredicate)) {
//			System.out.println("-------------------");
//			System.out.println("--!-------"+((AuxiliaryPredicate) argument2.getPredicate()).getPredicateLabel()+"----------");
//			System.out.println("-------------------");
//			System.out.println(constraints.toString());
//			}
		
		return constraints.toString();
	}
}
