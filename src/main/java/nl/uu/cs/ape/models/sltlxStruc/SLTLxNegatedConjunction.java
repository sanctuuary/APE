package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model NAND (not (and x y)) operation in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxNegatedConjunction extends SLTLxFormula {

private Set<SLTLxFormula> nconjunctedFacts;
	
		
	public SLTLxNegatedConjunction(SLTLxFormula arg1, SLTLxFormula arg2) {
		super();
		this.nconjunctedFacts = new HashSet<SLTLxFormula>();
		this.nconjunctedFacts.add(arg1);
		this.nconjunctedFacts.add(arg2);
	}

	public SLTLxNegatedConjunction(Collection<? extends SLTLxFormula> nconjunctedFacts) {
		super();
		this.nconjunctedFacts = new HashSet<SLTLxFormula>();
		nconjunctedFacts.forEach(fact -> this.nconjunctedFacts.add(fact));
	}


	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		return new SLTLxConjunction(nconjunctedFacts).getNegatedCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	
	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping, SATSynthesisEngine synthesisEngine) {
		return new SLTLxConjunction(nconjunctedFacts).getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

}
