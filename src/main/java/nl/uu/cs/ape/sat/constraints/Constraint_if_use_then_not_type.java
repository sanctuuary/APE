package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.GeneralEncodingUtils;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.TypeUtils;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we have used data module <b>parameters[0]</b>,  then do not use type <b>parameters[1]</b> subsequently
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_if_use_then_not_type extends ConstraintTemplate {


	public Constraint_if_use_then_not_type(String id, List<ConstraintParameter> parametersNo, String description) {
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
		List<TaxonomyPredicate> parameterDimensions1 = parameters.get(0).getParameterTypes();
		Type ifType  = (Type) TypeUtils.getConjunctType(parameterDimensions1, allTypes);
		GeneralEncodingUtils.getConjunctConstraints(ifType, parameterDimensions1, mappings, typeAutomaton, WorkflowElement.USED_TYPE);
		
		/* working on second parameter */
		List<TaxonomyPredicate> parameterDimensions2 = parameters.get(1).getParameterTypes();
		Type thenNotType  = (Type) TypeUtils.getConjunctType(parameterDimensions2, allTypes);
		GeneralEncodingUtils.getConjunctConstraints(thenNotType, parameterDimensions2, mappings, typeAutomaton, WorkflowElement.USED_TYPE);

		if (ifType == null || thenNotType == null) {
			System.err.println("Constraint argument does not exist in the type taxonomy.");
			return null;
		}
		constraint = SLTL_formula.itn_type(ifType, thenNotType, WorkflowElement.USED_TYPE, moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);

		return constraint;
	}

}
