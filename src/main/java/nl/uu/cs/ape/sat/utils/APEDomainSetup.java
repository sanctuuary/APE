/**
 * 
 */
package nl.uu.cs.ape.sat.utils;

import java.util.ArrayList;
import java.util.List;

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
import nl.uu.cs.ape.sat.models.ConstraintData;
import nl.uu.cs.ape.sat.models.TaxonomyPredicateHelper;
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
	/** Object used to create temporal constraints. */ 
	private ConstraintFactory constraintFactory;
	/** List of data gathered from the constraint file. */
	private List<ConstraintData> unformattedConstr;
	private List<TaxonomyPredicateHelper> helperPredicates;
	
	
	public APEDomainSetup(APEConfig config) {
		unformattedConstr = new ArrayList<ConstraintData>();
		allModules = new AllModules(config);
		allTypes = new AllTypes(config);
		constraintFactory = new ConstraintFactory();
		helperPredicates =new ArrayList<TaxonomyPredicateHelper>();
	}


	/** @return the field {@link allModules}. */
	public AllModules getAllModules() {
		return allModules;
	}

	/**
	 * Add a constraint to the list of constraints, that should be encoded during the execution of the synthesis.
	 */
	public void addConstraintData(ConstraintData constr) {
		this.unformattedConstr.add(constr);
	}
	
	/** @return the field {@link unformattedConstr}. */
	public List<ConstraintData> getUnformattedConstr() {
		return unformattedConstr;
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
	
	TaxonomyPredicate generateAbstractType(List<TaxonomyPredicate> relatedTypes, AllTypes allTypes, LogicOperation logicOp) {
		if(relatedTypes.isEmpty()) {
			return null;
		}
		if(relatedTypes.size() == 1) {
			return relatedTypes.get(0);
		}
		StringBuilder abstractLabel = new StringBuilder(logicOp.toString());
		for(TaxonomyPredicate label : relatedTypes) {
			abstractLabel = abstractLabel.append(label.getPredicateID());
		}
		
		TaxonomyPredicate newAbsType = allTypes.addPredicate(new Type(abstractLabel.toString(), abstractLabel.toString(), relatedTypes.get(0).getRootNode(), NodeType.ABSTRACT));
		return newAbsType;
	}
	
	
	public TaxonomyPredicate generateHelperPredicate(List<TaxonomyPredicate> relatedTypes, LogicOperation logicOp) {
		if(relatedTypes.isEmpty()) {
			return null;
		}
		if(relatedTypes.size() == 1) {
			return relatedTypes.get(0);
		}
		StringBuilder abstractLabel = new StringBuilder(logicOp.toString());
		for(TaxonomyPredicate label : relatedTypes) {
			abstractLabel = abstractLabel.append(label.getPredicateID());
		}
		TaxonomyPredicate newAbsType; 
		if(relatedTypes.get(0) instanceof Type) {
			newAbsType = allTypes.addPredicate(new Type(abstractLabel.toString(), abstractLabel.toString(), relatedTypes.get(0).getRootNode(), NodeType.ABSTRACT));
		} else {
			newAbsType = allModules.addPredicate(new AbstractModule(abstractLabel.toString(), abstractLabel.toString(), relatedTypes.get(0).getRootNode(), NodeType.ABSTRACT));
		}
		TaxonomyPredicateHelper helperPredicate = new TaxonomyPredicateHelper(newAbsType, logicOp);
		
		for(TaxonomyPredicate predicate : relatedTypes) {
			helperPredicate.addSubPredicate(predicate);
		}
		helperPredicates.add(helperPredicate);
		return newAbsType;
	}
	
	
	public String getConstraintsForHelperPredicates(AtomMappings mappings, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		StringBuilder constraints = new StringBuilder();
		Automaton automaton;
		WorkflowElement workflowElem;
		for (TaxonomyPredicateHelper helperPredicate : helperPredicates) {
			if(helperPredicate.getSubPredicates().get(0) instanceof Type) {
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

					for (TaxonomyPredicate subLabel : helperPredicate.getSubPredicates()) {
						constraints = constraints.append(mappings.add(subLabel, currState, workflowElem)).append(" ");
					}
					constraints = constraints.append(" 0\n");

					/*
					 * Ensures that if at least one of the disjointLabels was used, the abstract
					 * predicate has to be used as well.
					 */
					for (TaxonomyPredicate subLabel : helperPredicate.getSubPredicates()) {
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
					for (TaxonomyPredicate subLabel : helperPredicate.getSubPredicates()) {
						constraints = constraints.append("-")
								.append(mappings.add(helperPredicate, currState, workflowElem)).append(" ");

						constraints = constraints.append(mappings.add(subLabel, currState, workflowElem))
								.append(" 0\n");
					}

					/*
					 * Ensures that if all of the disjointLabels were used, the abstract predicate
					 * has to be used as well.
					 */
					for (TaxonomyPredicate subLabel : helperPredicate.getSubPredicates()) {
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
	
}
