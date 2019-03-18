package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.Type;

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


	public Constraint_not_use_type(int parametersNo, String description) {
		super(parametersNo, description);
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
		SLTL_formula_G formula = new SLTL_formula_G(false, type);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
