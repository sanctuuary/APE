package nl.uu.cs.ape.models.satStruc;

import java.util.List;

import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.core.implSAT.SATSynthesisEngine;
import nl.uu.cs.ape.core.implSMT.SMTSynthesisEngine;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Structure used to model binary relationType - (relationType x y) statement in SMTLib2.
 * @author Vedran Kasalica
 *
 */
public class BinarySATPredicate {

	/** Type of the relation. */
	private WorkflowElement relationType;
	/** 1st argument of the relation, usually a taxonomy predicate, state or a variable. */
	private PredicateLabel predicate;
	/** 2nd argument of the relation, which is always a workflow state. */
	private State referencedState;
	
	

	public BinarySATPredicate(WorkflowElement relType, PredicateLabel predicate, State state) {
		super();
		this.relationType = relType;
		this.predicate = predicate;
		this.referencedState = state;
	}

	/**
	 * Get relation type.
	 * @return the relationType
	 */
	public WorkflowElement getRelationType() {
		return relationType;
	}

	/**
	 * Get the 1st argument, i.e., the predicate.
	 * @return the predicate
	 */
	public PredicateLabel getPredicate() {
		return predicate;
	}

	/**
	 * Get the 2nd argument, i.e., the referenced state.
	 * @return the referencedState
	 */
	public State getReferencedState() {
		return referencedState;
	}


}
