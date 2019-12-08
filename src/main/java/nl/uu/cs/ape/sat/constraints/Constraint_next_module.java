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
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_next_module extends ConstraintTemplate {


	public Constraint_next_module(String id, List<ConstraintParameter> parametersNo, String description) {
		super(id, parametersNo, description);
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
		AbstractModule first_module  = (AbstractModule) ModuleUtils.getConjunctModule(seondInSeq, allModules);
		GeneralEncodingUtils.getConjunctConstraints(first_module, seondInSeq, mappings, moduleAutomaton, WorkflowElement.MODULE);
		/* working on second parameter */
		List<TaxonomyPredicate> firstdInSeq = parameters.get(1).getParameterTypes();
		AbstractModule second_module  = (AbstractModule) ModuleUtils.getConjunctModule(firstdInSeq, allModules);
		GeneralEncodingUtils.getConjunctConstraints(second_module, seondInSeq, mappings, moduleAutomaton, WorkflowElement.MODULE);

		
		if (first_module == null || second_module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint = SLTL_formula.next_module(first_module, second_module, moduleAutomaton, mappings);

		return constraint;
	}

}
