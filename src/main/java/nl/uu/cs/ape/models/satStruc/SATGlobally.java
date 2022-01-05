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
 * Structure used to model Globally (G) modal statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATGlobally extends SATModalOp {

private SATFact formula;
	
	public SATGlobally(SATFact formula) {
		super();
		this.formula = formula;
	}


	@Override
	public Set<CNFClause> getCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<CNFClause> clauses = new HashSet<CNFClause>();
		
		for(int i = stateNo; i < synthesisEngine.getSolutionSize(); i++) {
			clauses.addAll(formula.getCNFEncoding(i, synthesisEngine));
		}
		return clauses;
	}

	@Override
	public Set<CNFClause> getNegatedCNFEncoding(int stateNo, SATSynthesisEngine synthesisEngine) {
		return new SATForall(this.stateNo, boundBariable, formula).getNegatedCNFEncoding(synthesisEngine);
	}

}
