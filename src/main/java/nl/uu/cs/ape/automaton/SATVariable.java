package nl.uu.cs.ape.automaton;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.AtomVarType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.CNFClause;
import nl.uu.cs.ape.models.satStruc.SATAndStatement;
import nl.uu.cs.ape.models.satStruc.SATAtom;
import nl.uu.cs.ape.models.satStruc.SATAtomVar;
import nl.uu.cs.ape.models.satStruc.SATFact;
import nl.uu.cs.ape.models.satStruc.SATImplicationStatement;
import nl.uu.cs.ape.models.satStruc.SATNotStatement;
import nl.uu.cs.ape.models.satStruc.SATOrStatement;
import nl.uu.cs.ape.models.satStruc.SATVariableFlattening;
import nl.uu.cs.ape.models.satStruc.SATVariableOccurance;
import uk.ac.manchester.cs.atomicdecomposition.Atom;

/***
 * The {@code State} class is used to represent a variable for type states. The variable only represents states from the type automatons, excluding the module automaton states. 
 * <p>
 * Labeling of the automaton is provided in
 * <a href="https://github.com/sanctuuary/APE/blob/master/res/WorkflowAutomaton_Implementation.png">/APE/res/WorkflowAutomaton_Implementation.png</a>.
 *
 * @author Vedran Kasalica
 */
public class SATVariable implements StateInterface, PredicateLabel {

	/** Unique name of the type state variable */
    private final String variableID;


    /**
     * Create new type state variable.
     * @param variableName - Unique variable name
     */
    public SATVariable(String variableName) {
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
		SATVariable other = (SATVariable) obj;
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
	public Set<CNFClause> getExistentialCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<SATFact> varRefs = new HashSet<SATFact>();
		for(State state :synthesisEngine.getTypeAutomaton().getAllStatesUntilBlockNo(stateNo)) {
			SATAtomVar currAtomVar = new SATAtomVar(AtomVarType.VAR_REF, state, this);
			SATAtom emptyState = new SATAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SATOrStatement(currAtomVar, emptyState));
		}
		SATOrStatement allVars = new SATOrStatement(varRefs);
		
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
	public Set<CNFClause> getUniversalCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		/** Setting up the domain of the variable. */
		Set<SATFact> varRefs = new HashSet<SATFact>();
		for(State state : this.getVariableDomain(stateNo, synthesisEngine)) {
			SATAtomVar currAtomVar = new SATAtomVar(AtomVarType.VAR_REF, state, this);
			SATAtom emptyState = new SATAtom(state.getWorkflowStateType(), synthesisEngine.getEmptyType(), state);
			varRefs.add(new SATOrStatement(currAtomVar, emptyState));
		}
		SATAndStatement allVars = new SATAndStatement(varRefs);
		
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
	public Set<CNFClause> getVariableSubstitutionEnforcingCNFEncoding(int stateNo, SATVariableFlattening variableMapping, SATSynthesisEngine synthesisEngine) {
		Set<SATFact> allFacts = new HashSet<>();
		SATVariableOccurance varUsage = synthesisEngine.getVariableUsage(); 
		
		/** Introduce rules to enforce substitution over unary predicates when needed. 
		 * e.g., x = T1 &  P(x) ->  P(T1)
		 * 		 x = T1 & !P(x) -> !P(T1)	
		 * */
		for(PredicateLabel usedPred : varUsage.getUnaryPredicates(this)) {
			for(State varState : variableMapping.getVariableDomain(this)) {
				allFacts.add(new SATImplicationStatement(
										new SATAndStatement(
												new SATAtomVar(
														AtomVarType.VAR_REF,
														varState,
														this),
												new SATAtomVar(
														AtomVarType.TYPE_VAR,
														usedPred,
														this)),
										new SATAtom(
												varState.getWorkflowStateType(),
												usedPred,
												varState)));
				
				/* Enforce negation of the predicate as well. */
				allFacts.add(new SATImplicationStatement(
						new SATAndStatement(
								new SATAtomVar(
										AtomVarType.VAR_REF,
										varState,
										this),
								new SATNotStatement(
										new SATAtomVar(
												AtomVarType.TYPE_VAR,
												usedPred,
												this))),
						new SATNotStatement(
								new SATAtom(
										varState.getWorkflowStateType(),
										usedPred,
										varState))
						)
				);
			}
		}
		
		/** Introduce rules to enforce substitution over unary predicates when needed. */
		for(Pair<SATVariable> pairs : varUsage.getPairsContainingVarAsFirstArg(this)) {
			SATVariable var1 = this;
			SATVariable var2 = pairs.getSecond();
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
					allFacts.add(new SATImplicationStatement(
											new SATAndStatement(
													new SATAndStatement(
															new SATAtomVar(
																	AtomVarType.VAR_REF,
																	var1State,
																	var1),
															new SATAtomVar(
																	AtomVarType.VAR_REF,
																	var2State,
																	var2)),
													new SATAtomVar(
															atomVarType,
															var1,
															var2)),
											new SATAtom(
													atomType,
													var1State,
													var2State)
											));
					
					/* Enforce negation of the predicate as well. */
					allFacts.add(new SATImplicationStatement(
							new SATAndStatement(
									new SATAndStatement(
											new SATAtomVar(
													AtomVarType.VAR_REF,
													var1State,
													var1),
											new SATAtomVar(
													AtomVarType.VAR_REF,
													var2State,
													var2)),
									new SATNotStatement(
											new SATAtomVar(
													atomVarType,
													var1,
													var2))),
							new SATNotStatement(
									new SATAtom(
											atomType,
											var1State,
											var2State))
							));
					}
				}
			}
		}
		SATAndStatement andFacts = new SATAndStatement(allFacts);
		
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
