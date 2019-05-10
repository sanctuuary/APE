package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.formulas.*;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * Use module <b>parameters[0]</b> in the solution
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_use_module extends Constraint {


	public Constraint_use_module(String id, int parametersNo, String description) {
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
		AbstractModule module = allModules.get(parameters[0]);
		if (module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		SLTL_formula_F formula = new SLTL_formula_F(module);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
