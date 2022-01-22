package nl.uu.cs.ape.automaton;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.satStruc.CNFClause;
import nl.uu.cs.ape.models.satStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxAtom;
import nl.uu.cs.ape.models.satStruc.SLTLxAtomVar;
import nl.uu.cs.ape.models.satStruc.SLTLxFormula;
import nl.uu.cs.ape.models.satStruc.SLTLxImplication;
import nl.uu.cs.ape.models.satStruc.SLTLxNegation;
import nl.uu.cs.ape.models.satStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxVariableFlattening;
import nl.uu.cs.ape.models.satStruc.SLTLxVariableOccurance;

/***
 * The {@code State} class is used to represent a variable for type states. The variable only represents states from the type automatons, excluding the module automaton states. 
 * <p>
 * Labeling of the automaton is provided in
 * <a href="https://github.com/sanctuuary/APE/blob/master/res/WorkflowAutomaton_Implementation.png">/APE/res/WorkflowAutomaton_Implementation.png</a>.
 *
 * @author Vedran Kasalica
 */
public class SLTLxVariable extends PredicateLabel implements StateInterface  {

	/** Unique name of the type state variable */
    private final String variableID;


    /**
     * Create new type state variable.
     * @param variableName - Unique variable name
     */
    public SLTLxVariable(String variableName) {
		super();
		this.variableID = variableName;
	}

	/**
	 * @return the variableID
	 */
	public String getVariableName() {
		return variableID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variableID == null) ? 0 : variableID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SLTLxVariable other = (SLTLxVariable) obj;
		if (variableID == null) {
			if (other.variableID != null)
				return false;
		} else if (!variableID.equals(other.variableID))
			return false;
		return true;
	}


	@Override
	public String getPredicateID() {
		return variableID;
	}

	public int compareTo(PredicateLabel other) {
        return this.getPredicateID().compareTo(other.getPredicateID());
    }

	@Override
	public String getPredicateLabel() {
		return variableID;
	}

	@Override
	public String getPredicateLongLabel() {
		return variableID;
	}

	/**
	 * Get the set of clauses that enforce substitution of the variable under the existential
	 * quantification for the given set of states.
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<CNFClause> getExistentialCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<SLTLxFormula> varRefs = new HashSet<SLTLxFormula>();
		for(State state :synthesisEngine.getTypeAutomaton().getAllStatesUntilBlockNo(stateNo)) {
			SLTLxAtomVar currAtomVar = new SLTLxAtomVar(AtomVarType.VAR_REF, state, this);
			SLTLxAtom emptyState = new SLTLxAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SLTLxDisjunction(currAtomVar, emptyState));
		}
		SLTLxDisjunction allVars = new SLTLxDisjunction(varRefs);
		
		return allVars.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}
	
	/**
	 * Get the set of clauses that enforce substitution of the variable under the universal
	 * quantification for the given set of states. In addition, the encoding ensures that the 
	 * usage of variables in atoms will imply usage of the corresponding states as well (mimicking substitution).<br/><br/>
	 * 
	 * <b>IMPORTANT: This method should be call after the binded subformula was visited (the corresponding CNF was generated),
	 * in order to ensure that all occurrences of the variable were taken into account.</b>
	 * 
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<CNFClause> getUniversalCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		/** Setting up the domain of the variable. */
		Set<SLTLxFormula> varRefs = new HashSet<SLTLxFormula>();
		for(State state : this.getVariableDomain(stateNo, synthesisEngine)) {
			SLTLxAtomVar currAtomVar = new SLTLxAtomVar(AtomVarType.VAR_REF, state, this);
			SLTLxAtom emptyState = new SLTLxAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SLTLxDisjunction(currAtomVar, emptyState));
		}
		SLTLxConjunction allVars = new SLTLxConjunction(varRefs);
		
		return allVars.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}
	
	
	/**
	 * The encoding ensures that the usage of variables in atoms will imply 
	 * usage of the corresponding states as well (mimicking substitution).<br/><br/>
	 * 
	 * <b>IMPORTANT: This method should be call after the binded subformula was visited (the corresponding CNF was generated),
	 * in order to ensure that all occurrences of the variable were taken into account.</b>
	 * 
	 * @param stateNo - current state in the SLTLx model
	 * @param synthesisEngine - synthesis engine
	 * @return Set of clauses that encode the possible variable substitution.
	 */
	public Set<CNFClause> getVariableSubstitutionEnforcingCNFEncoding(int stateNo, SLTLxVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<SLTLxFormula> allFacts = new HashSet<>();
		SLTLxVariableOccurance varUsage = synthesisEngine.getVariableUsage(); 
		
		/** Introduce rules to enforce substitution over unary predicates when needed. 
		 * e.g., x = T1 &  P(x) ->  P(T1)
		 * 		 x = T1 & !P(x) -> !P(T1)	
		 * */
		for(PredicateLabel usedPred : varUsage.getUnaryPredicates(this)) {
			for(State varState : variableMapping.getVariableDomain(this)) {
				allFacts.add(new SLTLxImplication(
										new SLTLxConjunction(
												new SLTLxAtomVar(
														AtomVarType.VAR_REF,
														varState,
														this),
												new SLTLxAtomVar(
														AtomVarType.TYPE_VAR,
														usedPred,
														this)),
										new SLTLxAtom(
												varState.getWorkflowStateType(),
												usedPred,
												varState)));
				
				/* Enforce negation of the predicate as well. */
				allFacts.add(new SLTLxImplication(
						new SLTLxConjunction(
								new SLTLxAtomVar(
										AtomVarType.VAR_REF,
										varState,
										this),
								new SLTLxNegation(
										new SLTLxAtomVar(
												AtomVarType.TYPE_VAR,
												usedPred,
												this))),
						new SLTLxNegation(
								new SLTLxAtom(
										varState.getWorkflowStateType(),
										usedPred,
										varState))
						)
				);
			}
		}
		
		/** Introduce rules to enforce substitution over unary predicates when needed. */
		for(Pair<SLTLxVariable> pairs : varUsage.getPairsContainingVarAsFirstArg(this)) {
			SLTLxVariable var1 = this;
			SLTLxVariable var2 = pairs.getSecond();
			for(AtomVarType atomVarType : varUsage.getBinaryPredicates(pairs)) {
				AtomType atomType;
				if(atomVarType.equals(AtomVarType.VAR_EQUIVALENCE)) {
					atomType = AtomType.TYPE_EQUIVALENCE;
				} else if(atomVarType.equals(AtomVarType.TYPE_DEPENDENCY_VAR)) {
					atomType = AtomType.TYPE_DEPENDENCY; 
				} else {
					continue;
				}
				for(State var1State : variableMapping.getVariableDomain(var1)) {
					for(State var2State : variableMapping.getVariableDomain(var2)) {
					allFacts.add(new SLTLxImplication(
											new SLTLxConjunction(
													new SLTLxConjunction(
															new SLTLxAtomVar(
																	AtomVarType.VAR_REF,
																	var1State,
																	var1),
															new SLTLxAtomVar(
																	AtomVarType.VAR_REF,
																	var2State,
																	var2)),
													new SLTLxAtomVar(
															atomVarType,
															var1,
															var2)),
											new SLTLxAtom(
													atomType,
													var1State,
													var2State)
											));
					
					/* Enforce negation of the predicate as well. */
					allFacts.add(new SLTLxImplication(
							new SLTLxConjunction(
									new SLTLxConjunction(
											new SLTLxAtomVar(
													AtomVarType.VAR_REF,
													var1State,
													var1),
											new SLTLxAtomVar(
													AtomVarType.VAR_REF,
													var2State,
													var2)),
									new SLTLxNegation(
											new SLTLxAtomVar(
													atomVarType,
													var1,
													var2))),
							new SLTLxNegation(
									new SLTLxAtom(
											atomType,
											var1State,
											var2State))
							));
					}
				}
			}
		}
		SLTLxConjunction andFacts = new SLTLxConjunction(allFacts);
		
		return andFacts.getCNFEncoding(stateNo, variableMapping, synthesisEngine);
	}

	/**
	 * Get set of states that correspond to the domain of the variable
	 * @param stateNo
	 * @param synthesisEngine
	 * @return
	 */
	public Set<State> getVariableDomain(int stateNo, SATSynthesisEngine synthesisEngine) {
		Set<State> variableDomain = new HashSet<State>();
		for(State state :synthesisEngine.getTypeAutomaton().getAllStatesUntilBlockNo(stateNo)) {
			variableDomain.add(state);
		}
		return variableDomain;
	}

}
