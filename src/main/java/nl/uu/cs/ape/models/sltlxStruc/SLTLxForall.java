package nl.uu.cs.ape.models.sltlxStruc;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * Structure used to model forall statement in SLTLx.
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxForall extends SLTLxVarQuantification {

	public SLTLxForall(SLTLxVariable boundVariable, SLTLxFormula formula) {
		super(boundVariable, formula);
	}

	@Override
	public Set<String> getCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<>();

		/** Encode the possible substitutions for the given variable. */
		SLTLxVariable.getVariableDomain(stateNo, synthesisEngine).forEach(
				state -> {
					SLTLxVariableSubstitutionCollection newVarMapping = new SLTLxVariableSubstitutionCollection(
							curVarMapping);
					Set<State> domainState = new HashSet<>();
					domainState.add(state);
					SLTLxVariable flatBindedVariable = newVarMapping.addNewVariable(boundVariable, domainState);

					/* Encode the substitution. */
					clauses.addAll(
							flatBindedVariable.getUniversalCNFEncoding(stateNo, newVarMapping, synthesisEngine));
					/** Encode the variable substitution and the underlying formula. */
					clauses.addAll(super.getCNFEncoding(stateNo, newVarMapping, synthesisEngine));
				});

		return clauses;
	}

	@Override
	public Set<String> getNegatedCNFEncoding(int stateNo, SLTLxVariableSubstitutionCollection curVarMapping,
			SATSynthesisEngine synthesisEngine) {
		Set<String> clauses = new HashSet<>();
		SLTLxVariableSubstitutionCollection newVarMapping = new SLTLxVariableSubstitutionCollection(curVarMapping);
		SLTLxVariable flatBindedVariable = newVarMapping.addNewVariable(boundVariable,
				SLTLxVariable.getVariableDomain(stateNo, synthesisEngine));

		/** Encode the possible substitutions for the given variable. */
		clauses.addAll(flatBindedVariable.getExistentialCNFEncoding(stateNo, newVarMapping, synthesisEngine));

		/** Encode the variable substitution and the underlying formula. */
		clauses.addAll(super.getNegatedCNFEncoding(stateNo, newVarMapping, synthesisEngine));
		return clauses;
	}

}
