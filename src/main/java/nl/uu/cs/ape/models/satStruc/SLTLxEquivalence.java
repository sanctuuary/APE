package nl.uu.cs.ape.models.satStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model equivalence (e.g., "a <=> b") in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxEquivalence extends SLTLxFormula {

private SLTLxFormula firstArg;
private SLTLxFormula secondArg;
	
	

	public SLTLxEquivalence(SLTLxFormula firstArg, SLTLxFormula secondArg) {
	super();
	this.firstArg = firstArg;
	this.secondArg = secondArg;
}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> allClauses = new HashSet<CNFClause>();

		/* Add the elements that represent the 2 way implication. */
		allClauses.addAll(new SLTLxImplication(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.addAll(new SLTLxImplication(secondArg, firstArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		
		return allClauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> allClauses = new HashSet<CNFClause>();

		/* Ensure that the 2 arguments are not the same. */
		allClauses.addAll(new SLTLxDisjunction(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.addAll(new SLTLxNegatedConjunction(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		
		return allClauses;
	}

}
