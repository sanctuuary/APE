package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.automaton.State;

/**
 * Class is used to collect substitutions of the existing variables with a new
 * one that will uniquely identify the variable bindings.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxVariableSubstitutionCollection {

	/** Variable mapping to unique IDs. */
	private Map<SLTLxVariable, SLTLxVariable> mappedVariables;
	/** Variable mapping to its domain. */
	private Map<SLTLxVariable, Set<State>> variableDomain;
	/** Number of variables. */
	private static int variableNo = 1;

	/**
	 * Create a new variable mapping class.
	 */
	public SLTLxVariableSubstitutionCollection() {
		super();
		this.mappedVariables = new HashMap<>();
		this.variableDomain = new HashMap<>();
	}

	/**
	 * Create a new variable mapping, based on the existing one.
	 * 
	 * @param existing existing variable mapping.
	 */
	public SLTLxVariableSubstitutionCollection(SLTLxVariableSubstitutionCollection existing) {
		super();
		this.mappedVariables = new HashMap<>();
		for (SLTLxVariable key : existing.mappedVariables.keySet()) {
			this.mappedVariables.put(key, existing.mappedVariables.get(key));
		}
		this.variableDomain = new HashMap<>();
		for (SLTLxVariable key : existing.variableDomain.keySet()) {
			this.variableDomain.put(key, existing.variableDomain.get(key));
		}
	}

	/**
	 * Add the new variable to the mapping.<br>
	 * Create a new and unique variable to substitute the current binding of the
	 * variable. <br>
	 * In case that the substitution for a variable with the same name exists, it
	 * will be overwritten.
	 * 
	 * @param existingVar variable used in the formula
	 * @return Unique variable that corresponds to the current variable binding.
	 */
	public SLTLxVariable addNewVariable(SLTLxVariable existingVar, Set<State> varDomain) {
		SLTLxVariable newVar = new SLTLxVariable("uniqVar_" + (this.variableNo++));
		this.mappedVariables.put(existingVar, newVar);
		this.mappedVariables.put(newVar, newVar);
		this.variableDomain.put(newVar, varDomain);
		return newVar;
	}

	/**
	 * Get the unique variable used to substitute the current binding of the
	 * variable.
	 * 
	 * @param existingVar variable used in the formula
	 * @return Unique variable that corresponds to the current variable binding.
	 * @throws SLTLxParsingAnnotationException in case that the variable does not
	 *                                         exist.
	 */
	public SLTLxVariable getVarSubstitute(SLTLxVariable existingVar) throws SLTLxParsingAnnotationException {
		SLTLxVariable variable = mappedVariables.get(existingVar);
		if (variable == null) {
			throw SLTLxParsingAnnotationException
					.variableNotBound("Variable " + existingVar.getPredicateID() + " must be bound.");
		}
		return variable;
	}

	/**
	 * Get the domain for the given variable.
	 * 
	 * @param variable variable in question
	 * @return Set of states that represent the domain of the variable.
	 */
	public Set<State> getVariableDomain(SLTLxVariable variable) {
		return this.variableDomain.get(variable);
	}
}
