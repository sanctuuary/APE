package nl.uu.cs.ape.models.smtStruc.boolStatements;

import nl.uu.cs.ape.core.implSMT.SMTUtils;
import nl.uu.cs.ape.models.MappingsException;
import nl.uu.cs.ape.models.SMTPredicateMappings;

/**
 * Interface used to present any predicate in the smt2lib structure.
 * @author Vedran Kasalica
 *
 */
public class SMTBoundedVar {

	private String variable; 
	
	
	public SMTBoundedVar(int variableNo) {
		this.variable = SMTUtils.removeUnsupportedCharacters("var_" + variableNo);
	}
	
	public SMTBoundedVar(String variable) {
		this.variable = SMTUtils.removeUnsupportedCharacters("var_" + variable);
	}
	
	public String toString(SMTPredicateMappings mapping) {
		if(mapping.findOriginal(variable) == null) {
			return variable;
		} else {
			throw new MappingsException("A taxonomy term is using the variable name: '" + variable + "'.");
		}
			
	}

	
}
