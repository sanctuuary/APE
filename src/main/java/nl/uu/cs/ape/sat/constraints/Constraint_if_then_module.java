package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.GeneralEncodingUtils;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.ModuleUtils;
import nl.uu.cs.ape.sat.models.enums.LogicOperation;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
 * subsequently using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_if_then_module extends ConstraintTemplate {


	public Constraint_if_then_module(String id, List<ConstraintParameter> parameterTypes, String description) {
		super(id, parameterTypes, description);
	}

	@Override
	public String getConstraint(List<ConstraintParameter> parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMappings mappings) {
		if (parameters.size() != 2) {
			super.throwParametersError(parameters.size());
			return null;
		}
		
		String constraint = "";
		
		/* working on first parameter */
		List<TaxonomyPredicate> seondInSeq = parameters.get(0).getParameterTypes();
		AbstractModule if_module  = (AbstractModule) ModuleUtils.generateAbstractmodule(seondInSeq, allModules, LogicOperation.AND);
		GeneralEncodingUtils.getConstraintGroupLogicallyPredicates(if_module, seondInSeq, mappings, moduleAutomaton, WorkflowElement.MODULE, LogicOperation.AND);
		/* working on second parameter */
		List<TaxonomyPredicate> firstdInSeq = parameters.get(1).getParameterTypes();
		AbstractModule then_module  = (AbstractModule) ModuleUtils.generateAbstractmodule(firstdInSeq, allModules, LogicOperation.AND);
		GeneralEncodingUtils.getConstraintGroupLogicallyPredicates(then_module, seondInSeq, mappings, moduleAutomaton, WorkflowElement.MODULE, LogicOperation.AND);

		if (if_module == null || then_module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint = SLTL_formula.ite_module(if_module, then_module, moduleAutomaton, mappings);

		return constraint;
	}

}
