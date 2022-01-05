package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Elem;

/**
 * Class Predicate Function Argument is used to model concrete property predicates given as function arguments in SMTLib2 .
 * @author Vedran Kasalica
 *
 */
public class SMTPredicateFunArg implements SMTFunctionArgument {

	private PredicateLabel predicate;
	
	public SMTPredicateFunArg(PredicateLabel predicate) {
		this.predicate = predicate;
	}
	
	/**
	 * Get {@link StateInterface} if it exists, otherwise return null. 
	 * @return {@link StateInterface} or null.
	 */
	public PredicateLabel getPredicate() {
			return predicate;
	}
	
	@Override
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
			return  synthesisEngine.getMappings().add(predicate);
	}

}
