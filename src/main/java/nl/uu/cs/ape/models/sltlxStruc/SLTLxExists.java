package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;

/**
 * Structure used to model exists statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxExists extends SLTLxVarQuantification {

	public SLTLxExists(SLTLxVariable boundVariable, SLTLxFormula formula) {
		super(boundVariable, formula);
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<>();
		SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(curVarMapping);
		SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(boundVariable,
				SLTLxVariable.getVariableDomain(stateNo, synthesisEngine));

		/** Encode the possible substitutions for the given variable. */
		clauses.addAll(flatBindedVariable.getExistentialCNFEncoding(stateNo, newVarMappping, synthesisEngine));

		/** Encode the variable substitution and the underlying formula. */
		clauses.addAll(super.getCNFEncoding(stateNo, newVarMappping, synthesisEngine));
		return clauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<>();

		/** Encode the possible substitutions for the given variable. */
		SLTLxVariable.getVariableDomain(stateNo, synthesisEngine).forEach(
				state -> {
					SLTLxVariableSubstitutionCollection newVarMappping = new SLTLxVariableSubstitutionCollection(
							curVarMapping);
					Set<State> domainState = new HashSet<>();
					domainState.add(state);
					SLTLxVariable flatBindedVariable = newVarMappping.addNewVariable(boundVariable, domainState);

					/* Encode the substitution. */
					clauses.addAll(
							flatBindedVariable.getUniversalCNFEncoding(stateNo, newVarMappping, synthesisEngine));
					/** Encode the variable substitution and the underlying formula. */
					clauses.addAll(super.getNegatedCNFEncoding(stateNo, newVarMappping, synthesisEngine));
				});

		return clauses;
	}

}
