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
 * Structure used to model Next (N) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATOperation extends SATModalOp {

private SATFact formula;
	
	public SATNextOp(SATOperation operation, SATFact formula) {
		super();
		this.formula
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATVariableMapping variableMapping, SATSynthesisEngine synthesisEngine) {
		return formula.getCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATVariableMapping variableMapping, SATSynthesisEngine synthesisEngine) {
		return formula.getNegatedCNFEncoding(stateNo + 1, variableMapping, synthesisEngine);
	}

}
