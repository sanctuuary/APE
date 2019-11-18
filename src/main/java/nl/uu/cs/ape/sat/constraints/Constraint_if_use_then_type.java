package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.formulas.*;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we have used data module <b>parameters[0]</b>, then use <b>parameters[1]</b>
 * subsequently using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_if_use_then_type extends ConstraintTemplate {


	public Constraint_if_use_then_type(String id, int parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMappings mappings) {
		if (parameters.length != 2) {
			super.throwParametersError(parameters.length);
			return null;
		}
		String constraint = "";
		Type if_type = allTypes.get(parameters[0]);
		Type then_type = allTypes.get(parameters[1]);
		if (if_type == null || then_type == null) {
			System.err.println("Constraint argument does not exist in the type taxonomy.");
			return null;
		}
		constraint = SLTL_formula.ite_type(if_type, then_type, WorkflowElement.USED_TYPE, moduleAutomaton, typeAutomaton.getUsedTypesBlocks(), mappings);

		return constraint;
	}

}
