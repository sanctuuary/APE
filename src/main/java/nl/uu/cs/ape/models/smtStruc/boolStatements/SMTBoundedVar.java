package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.MappingsException;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Elem;

/**
 * Interface used to present any predicate in the SMTLib2 structure.
 * @author Vedran Kasalica
 *
 */
public class SMTBoundedVar implements SMTFunctionArgument {

	private String variable; 
	
	
	public SMTBoundedVar(int variableNo) {
		this.variable = SMTUtils.removeUnsupportedCharacters("var_" + variableNo);
	}
	
	public SMTBoundedVar(String variable) {
		this.variable = SMTUtils.removeUnsupportedCharacters("var_" + variable);
	}
	
	public String getSMT2Encoding(SMTSynthesisEngine synthesisEngine) {
		if(synthesisEngine.getMappings().findOriginal(variable) == null) {
			return variable;
		} else {
			throw new MappingsException("A taxonomy term is using the variable name: '" + variable + "'.");
		}
			
	}
	
}
