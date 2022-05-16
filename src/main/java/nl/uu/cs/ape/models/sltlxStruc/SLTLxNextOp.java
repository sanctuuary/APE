package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model Next operation (&lt;Op&gt;) modal statement in SLTLx.
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
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		if(synthesisEngine.getSolutionSize() <= stateNo)  {
			return SLTLxAtom.getFalse().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
 		}
		
		Set<Set<String>> allClauses = new HashSet<Set<String>>();

		/* Conjunct the collection of clauses that encode the operation and the formula */
		allClauses.add(operation.getCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(formula.getCNFEncoding(stateNo + 1, variableMapping, synthesisEngine));
		
		return CNFClause.conjunctClausesCollection(allClauses);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		if(synthesisEngine.getSolutionSize() <= stateNo)  {
			return SLTLxAtom.getTrue().getCNFEncoding(stateNo, variableMapping, synthesisEngine);
 		}
		Set<Set<String>> allClauses = new HashSet<Set<String>>();

		/* Disjunction the collection of clauses that encode the negation of the operation and of the formula */
		allClauses.add(operation.getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine));
		allClauses.add(formula.getNegatedCNFEncoding(stateNo + 1, variableMapping, synthesisEngine));
		
		return CNFClause.disjoinClausesCollection(allClauses);
	}

}
