package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model not statement in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATExists extends SATQuantification {

private SATBoundVar boundBariable;
private SATFact formula;
	
	public SATExists(int stateNo, SATBoundVar boundBariable,  SATFact formula) {
		super(stateNo);
		this.boundBariable = boundBariable; 
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		clauses.addAll(boundBariable.getExistentialCNFEncoding(this.getStateNo(), synthesisEngine));
		
		formula.addVariable(boundBariable);
		clauses.addAll(formula.getCNFEncoding(synthesisEngine));
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(SATSynthesisEngine synthesisEngine) {
		return new SATForall(this.getStateNo(), boundBariable, formula).getNegatedCNFEncoding(synthesisEngine);
	}

}
