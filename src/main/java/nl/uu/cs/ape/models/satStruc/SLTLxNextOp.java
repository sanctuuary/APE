package nl.uu.cs.ape.models.satStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Next operation (<Op>) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxNextOp extends SLTLxFormula {

	private SLTLxFormula operation;
	private SLTLxFormula formula;
	
	public SLTLxNextOp(SLTLxFormula operation, SLTLxFormula formula) {
		super();
		this.operation = operation;
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		if(synthesisEngine.getSolutionSize() <= stateNo)  {
			return SLTLxAtom.getFalse().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
 		}
		
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Conjunct the collection of clauses that encode the operation and the formula */
		allClauses.add(operation.getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(formula.getCNFEncoding(stateNo + 1, variableMapping, synthesisEngine));
		
		return CNFClause.conjunctClausesCollection(allClauses);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		if(synthesisEngine.getSolutionSize() <= stateNo)  {
			return SLTLxAtom.getTrue().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
 		}
		Set<Collection<CNFClause>> allClauses = new HashSet<Collection<CNFClause>>();

		/* Disjunction the collection of clauses that encode the negation of the operation and of the formula */
		allClauses.add(operation.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(formula.getNegatedCNFEncoding(stateNo + 1, variableMapping, synthesisEngine));
		
		return CNFClause.disjoinClausesCollection(allClauses);
	}

}