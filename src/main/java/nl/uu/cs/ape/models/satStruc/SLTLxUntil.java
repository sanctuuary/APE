package nl.uu.cs.ape.models.satStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Until (U) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxUntil extends SLTLxFormula {

private SLTLxFormula formulaFrom;
private SLTLxFormula formulaUntil;
	


	public SLTLxUntil(SLTLxFormula formulaFrom, SLTLxFormula formulaUntil) {
	super();
	this.formulaFrom = formulaFrom;
	this.formulaUntil = formulaUntil;
}

	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();
		
		
		Set<CNFClause> clauses = new HashSet<>();
//		for(int i = stateNo; i < synthesisEngine.getSolutionSize(); i++) {
//			clauses.addAll(formula.getCNFEncoding(i, variableMapping, synthesisEngine));
//		}
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<>();
		return clauses;
	}

}
