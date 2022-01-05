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

import nl.uu.cs.ape.automaton.TypeStateVar;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;

/**
 * Structure used to model not statement in cnf.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATForall extends SATQuantification {

private TypeStateVar boundBariable;
private SATFact formula;
	
	public SATForall(TypeStateVar boundBariable,  SATFact formula) {
		super();
		this.boundBariable = boundBariable; 
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		clauses.addAll(boundBariable.getUniversalCNFEncoding(stateNo, synthesisEngine));
		
		formula.addVariable(boundBariable);
		clauses.addAll(formula.getCNFEncoding(stateNo, synthesisEngine));
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		return new SATExists(boundBariable, formula).getNegatedCNFEncoding(stateNo, synthesisEngine);
	}

}
