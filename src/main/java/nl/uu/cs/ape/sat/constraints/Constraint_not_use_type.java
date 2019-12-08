package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.GeneralEncodingUtils;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.TypeUtils;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * Do not use type <b>parameters[0]</b> in the solution
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_not_use_type extends ConstraintTemplate {


	public Constraint_not_use_type(String id,  List<ConstraintParameter> parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(List<ConstraintParameter> parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMappings mappings) {
		if (parameters.size() != 1) {
			super.throwParametersError(parameters.size());
			return null;
		}
		String constraint = "";

		/* working on first parameter */
		List<TaxonomyPredicate> parameterDimensions = parameters.get(0).getParameterTypes();
		Type type  = (Type) TypeUtils.getConjunctType(parameterDimensions, allTypes);
		GeneralEncodingUtils.getConjunctConstraints(type, parameterDimensions, mappings, typeAutomaton, WorkflowElement.USED_TYPE);
		

		if (type == null) {
			System.err.println("Constraint argument does not exist in the type taxonomy.");
			return null;
		}
		SLTL_formula_G formula = new SLTL_formula_G(false, type);
		constraint = formula.getCNF(null, typeAutomaton.getUsedTypesBlocks(), WorkflowElement.USED_TYPE, mappings);

		return constraint;
	}

}
