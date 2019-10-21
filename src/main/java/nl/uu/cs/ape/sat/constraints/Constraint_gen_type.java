package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * Generate type <b>parameters[0]</b> in the solution
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_gen_type extends ConstraintTemplate {


	public Constraint_gen_type(String id, int parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 1) {
			super.throwParametersError(parameters.length);
			return null;
		}
		String constraint = "";

		Type type = allTypes.get(parameters[0]);
		if (type == null) {
			System.err.println("Constraint argument does not exist in the type taxonomy.");
			return null;
		}
		SLTL_formula_F formula = new SLTL_formula_F(type);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton.getMemoryTypesBlocks(), WorkflowElement.MEMORY_TYPE, mappings);

		return constraint;
	}

}
