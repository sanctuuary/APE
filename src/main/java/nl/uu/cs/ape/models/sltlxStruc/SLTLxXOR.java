package nl.uu.cs.ape.models.sltlxStruc;

import java.util.Set;

import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * Structure used to model XOR relation ("a xor b" is the same as "not a
 * &lt;=&gt; b") in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxXOR extends SLTLxFormula {

	private SLTLxFormula firstArg;
	private SLTLxFormula secondArg;

	public SLTLxXOR(SLTLxFormula firstArg, SLTLxFormula secondArg) {
		super();
		this.firstArg = firstArg;
		this.secondArg = secondArg;
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		return new SLTLxEquivalence(firstArg, secondArg).getNegatedCNFEncoding(stateNo, variableMapping,
				synthesisEngine);
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection variableMapping,
			SATSynthesisEngine synthesisEngine) {
		return new SLTLxEquivalence(firstArg, secondArg).getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

}
