/**
 * 
 */
package nl.uu.cs.ape.sat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import nl.uu.cs.ape.sat.automaton.Automaton;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.ConstraintTemplateData;
import nl.uu.cs.ape.sat.models.AuxTaxonomyPredicate;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * The {@code APEDomainSetup} class is used to store the domain information and initial constraints that have to be encoded.
 *
 * @author Vedran Kasalica
 *
 */
public class APEDomainSetup {

	/** All modules/operations used in the domain. */
	private AllModules allModules;
	/** All data types defined in the domain. */
	private AllTypes allTypes;
	/** Prefix used to define OWL class IDs */
	private String ontologyPrexifURI;
	/** Object used to create temporal constraints. */ 
	private ConstraintFactory constraintFactory;
	/** List of data gathered from the constraint file. */
	private List<ConstraintTemplateData> unformattedConstr;
	private List<AuxTaxonomyPredicate> helperPredicates;
	
	
	public APEDomainSetup(APEConfig config) {
		unformattedConstr = new ArrayList<ConstraintTemplateData>();
		allModules = new AllModules(config);
		allTypes = new AllTypes(config);
		constraintFactory = new ConstraintFactory();
		helperPredicates = new ArrayList<AuxTaxonomyPredicate>();
		ontologyPrexifURI = config.getOntologyPrefixURI();
	}


	/** @return the field {@link allModules}. */
	public AllModules getAllModules() {
		return allModules;
	}

	/**
	 * Add a constraint to the list of constraints, that should be encoded during the execution of the synthesis.
	 */
	public void addConstraintData(ConstraintTemplateData constr) {
		this.unformattedConstr.add(constr);
	}
	
	/** @return the field {@link unformattedConstr}. */
	public List<ConstraintTemplateData> getUnformattedConstr() {
		return unformattedConstr;
	}
	
	/** Removes all of the unformatted constraints, in order to start a new synthesis run. */
	public void clearConstraints() {
		this.unformattedConstr.clear();
	}


	/** @return the field {@link allTypes}. */
	public AllTypes getAllTypes() {
		return allTypes;
	}

	/** @return the field {@link constraintFactory}. */
	public ConstraintFactory getConstraintFactory() {
		return constraintFactory;
	}
	
	/**
	 * Adding each constraint format in the set of all cons. formats. method 
	 * should be called only once all the data types and modules have been initialized.
	 */
	public void initializeConstraints() {
		constraintFactory.initializeConstraints(allModules, allTypes);
	}

	/**
	 * @return
	 */
	public boolean trimTaxonomy() {
		boolean succRun = true;
		
		succRun &= allModules.trimTaxonomy();
		succRun &= allTypes.trimTaxonomy();
		return succRun;
	}


	/**
	 * Return the {@code ConstraintTemplate} that corresponds to the given ID, or {@code null} if the constraint with the given ID doesn not exist.
	 * @param constraintID - ID of the {@code ConstraintTemplate}.
	 * @return the {@code ConstraintTemplate} that corresponds to the given ID, or {@code null} if the ID is not mapped to any constraint.
	 */
	public ConstraintTemplate getConstraintTamplate(String constraintID) {
		return constraintFactory.getConstraintTamplate(constraintID);
	}
	
//	TaxonomyPredicate generateAbstractType(List<TaxonomyPredicate> relatedTypes, AllTypes allTypes, LogicOperation logicOp) {
//		if(relatedTypes.isEmpty()) {
//			return null;
//		}
//		if(relatedTypes.size() == 1) {
//			return relatedTypes.get(0);
//		}
//		StringBuilder abstractLabel = new StringBuilder(logicOp.toString());
//		for(TaxonomyPredicate label : relatedTypes) {
//			abstractLabel = abstractLabel.append(label.getPredicateID());
//		}
//		
//		TaxonomyPredicate newAbsType = allTypes.addPredicate(new Type(abstractLabel, abstractLabel, relatedTypes.get(0).getRootNode(), NodeType.ABSTRACT));
//		return newAbsType;
//	}
	
	/**
	 * Method used to generate a new predicate that should provide an interface for handling multiple predicates.
	 * New predicated is used to simplify interaction with a set of related tools/types.<br><br>
	 * 
	 * The original predicates are available as consumed predicates(see {@link AuxTaxonomyPredicate#getGeneralizedPredicates()}) of the new {@link TaxonomyPredicate}
	 * @param relatedPredicates - set of sorted type that are logically related to the new abstract type (label of the equivalent sets is always the same due to its ordering)
	 * @param logicOp -logical operation that describes the relation between the types
	 * @return an abstract predicate that provides abstraction over a disjunction/conjunction of the labels
	 */
	public TaxonomyPredicate generateAuxiliaryPredicate(SortedSet<TaxonomyPredicate> relatedPredicates, LogicOperation logicOp) {
		if(relatedPredicates.isEmpty()) {
			return null;
		}
		if(relatedPredicates.size() == 1) {
			return relatedPredicates.first();
		}
		String abstractLabel = APEUtils.getLabelFromList(relatedPredicates, logicOp);
		
		TaxonomyPredicate newAbsType; 
		if(relatedPredicates.first() instanceof Type) {
			newAbsType = allTypes.addPredicate(new Type(abstractLabel, abstractLabel, relatedPredicates.first().getRootNode(), NodeType.ABSTRACT));
		} else {
			newAbsType = allModules.addPredicate(new AbstractModule(abstractLabel, abstractLabel, relatedPredicates.first().getRootNode(), NodeType.ABSTRACT));
		}
		AuxTaxonomyPredicate helperPredicate = new AuxTaxonomyPredicate(newAbsType, logicOp);
		
		for(TaxonomyPredicate predicate : relatedPredicates) {
			helperPredicate.addConcretePredicate(predicate);
		}
		if(helperPredicate != null) {
			helperPredicates.add(helperPredicate);
		}
		return helperPredicate.getTaxonomyPredicate();
	}
	
	/**
	 * Encoding all the required constraints for the given program length, in order to ensure that helper predicates are used properly.
	 * 
	 * @param mappings - current atom mappings
	 * @param moduleAutomaton - graph representing all the tool states in the current workflow (one synthesis run might iterate though workflows of different lengths)
	 * @param typeAutomaton - graph representing all the type states in the current workflow (one synthesis run might iterate though workflows of different lengths)
	 * @return CNF encoding of that ensures the correctness of the helper predicates.
	 */
	public String getConstraintsForAuxiliaryPredicates(AtomMappings mappings, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		StringBuilder constraints = new StringBuilder();
		Automaton automaton = null;
		WorkflowElement workflowElem = null;
		for (AuxTaxonomyPredicate helperPredicate : helperPredicates) {
			if(helperPredicate.getGeneralizedPredicates().first() instanceof Type) {
				automaton = typeAutomaton;
				workflowElem = WorkflowElement.MEMORY_TYPE;
			} else {
				automaton = moduleAutomaton;
				workflowElem = WorkflowElement.MODULE;
			}
			for (State currState : automaton.getAllStates()) {
				if (helperPredicate.getLogicOp() == LogicOperation.OR) {
					/*
					 * Ensures that if the abstract predicate is used, at least one of the
					 * disjointLabels has to be used.
					 */
					constraints = constraints.append("-")
							.append(mappings.add(helperPredicate, currState, workflowElem)).append(" ");

					for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
						constraints = constraints.append(mappings.add(subLabel, currState, workflowElem)).append(" ");
					}
					constraints = constraints.append(" 0\n");

					/*
					 * Ensures that if at least one of the disjointLabels was used, the abstract
					 * predicate has to be used as well.
					 */
					for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
						constraints = constraints.append("-").append(mappings.add(subLabel, currState, workflowElem))
								.append(" ");
						constraints = constraints.append(mappings.add(helperPredicate, currState, workflowElem))
								.append(" 0\n");
					}
				} else if (helperPredicate.getLogicOp() == LogicOperation.AND) {

					/*
					 * Ensures that if the abstract predicate is used, all of the disjointLabels
					 * have to be used.
					 */
					for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
						constraints = constraints.append("-")
								.append(mappings.add(helperPredicate, currState, workflowElem)).append(" ");

						constraints = constraints.append(mappings.add(subLabel, currState, workflowElem))
								.append(" 0\n");
					}

					/*
					 * Ensures that if all of the disjointLabels were used, the abstract predicate
					 * has to be used as well.
					 */
					for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
						constraints = constraints.append("-").append(mappings.add(subLabel, currState, workflowElem))
								.append(" ");
					}
					constraints = constraints.append(mappings.add(helperPredicate, currState, workflowElem))
							.append(" 0\n");
				}
			}

		}
		return constraints.toString();
	}


	/**
	 * @return
	 */
	public String getOntologyPrefixURI() {
		return ontologyPrexifURI;
	}
	
}
