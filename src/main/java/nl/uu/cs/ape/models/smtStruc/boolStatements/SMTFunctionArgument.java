package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.SMT2LibElem;

/**
 * Interface used to model function arguments in smt2lib.
 * @author Vedran Kasalica
 *
 */
public class SMTFunctionArgument implements SMT2LibElem {

	// TODO: group into one
	private PredicateLabel predicate;
	private SMTBoundedVar boundedVariable;
	
	public SMTFunctionArgument(PredicateLabel predicate) {
		this.predicate = predicate;
	}

	public SMTFunctionArgument(SMTBoundedVar boundedVariable) {
		this.boundedVariable = boundedVariable;
	}
	
	/**
	 * Get {@link PredicateLabel} if it exists, otherwise return null. 
	 * @return {@link PredicateLabel} or null.
	 */
	public PredicateLabel getPredicate() {
			return predicate;
	}
	
	@Override
	public String toString(SMTPredicateMappings mapping) {
		if(predicate != null) {
			return mapping.add(predicate);
		} else {
			return boundedVariable.toString(mapping);
		}
	}

}
