package nl.uu.cs.ape.models.satStruc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.units.qual.s;

import nl.uu.cs.ape.automaton.SATVariable;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Class is used to represent substitutions of the existing variables with a new one that will uniquely identify the variable bindings.
 * 
 * @author Vedran Kasalica
 *
 */
public class SATVariableFlattening {
	
	/** Variable mapping to unique IDs. */
	private Map<SATVariable, SATVariable> mappedVariables;
	/** Variable mapping to its domain. */
	private Map<SATVariable, Set<State>> variableDomain;
	/** Number of variables. */
	private int variableNo;
	

	/**
	 * Create a new variable mappping class.
	 */
	public SATVariableFlattening() {
		super();
		this.mappedVariables = new HashMap<>();
		this.variableDomain = new HashMap<>();
		this.variableNo = 1;
	}
	
	/**
	 * Create a new variable mapping, based on the existing one.
	 * @param existing - existing variable mapping.
	 */
	public SATVariableFlattening(SATVariableFlattening existing) {
		super();
		this.mappedVariables = new HashMap<>();
		for(SATVariable key : existing.mappedVariables.keySet()) {
			this.mappedVariables.put(key, existing.mappedVariables.get(key));
		}
		this.variableDomain = new HashMap<>();
		for(SATVariable key : existing.variableDomain.keySet()) {
			this.variableDomain.put(key, existing.variableDomain.get(key));
		}
		this.variableNo = existing.variableNo;
	}


	/**
	 * Add the new variable to the mapping.<br/>
	 * Create a new and unique variable to substitute the current binding of the variable. <br/>
	 * In case that the substitution for a variable with the same name exists, it will be overwritten.
	 * @param existingVar - variable used in the formula
	 * @return Unique variable that corresponds to the current variable binding. 
	 */
	public SATVariable addNewVariable(SATVariable existingVar, Set<State> varDomain) {
		SATVariable newVar = new SATVariable("uniqVar_" + this.variableNo++);
		this.mappedVariables.put(existingVar, newVar);
		this.mappedVariables.put(newVar, newVar);
		this.variableDomain.put(newVar, varDomain);
		return newVar;
	}
	

	/**
	 * Get the unique variable used to substitute the current binding of the variable.
	 * @param existingVar - variable used in the formula
	 * @return Unique variable that corresponds to the current variable binding. 
	 */
	public SATVariable getVarSabstitute(SATVariable existingVar) {
		return mappedVariables.get(existingVar);
	}
	
	public Set<State> getVariableDomain(SATVariable var) {
		return this.variableDomain.get(var);
	}
	
}
