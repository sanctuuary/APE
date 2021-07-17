package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.enums.SMTLogicFragment;

/**
 * Structure used to model the definition of the logic fragment used by the Z3 solver.
 * 
 * @author Vedran Kasalica
 *
 */
public class LogicFragmentDeclaration implements SMTLib2Row {

	private SMTLogicFragment fragment;
	
	public LogicFragmentDeclaration(SMTLogicFragment fragment) {
		this.fragment = fragment;
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(set-logic ")
				.append(fragment.toString()).append(")");
		return constraints.append("\n").toString();
	}
}
