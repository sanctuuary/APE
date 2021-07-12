package nl.uu.cs.ape.models.smtStruc;

import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.enums.SMTLogicFragment;

/**
 * Structure used to model the definition of the logic fragment used by the Z3 solver.
 * 
 * @author Vedran Kasalica
 *
 */
public class LogicFragmentDeclaration implements SMT2LibRow {

	private SMTLogicFragment fragment;
	
	public LogicFragmentDeclaration(SMTLogicFragment fragment) {
		this.fragment = fragment;
	}
	
	public String toString(SMTPredicateMappings mapping) {
		StringBuilder constraints = new StringBuilder();
		constraints
			.append("(set-logic ")
				.append(fragment.toString()).append(")");
		return constraints.append("\n").toString();
	}
}
