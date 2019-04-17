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
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_next_module extends ConstraintTemplate {


	public Constraint_next_module(String id, int parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			super.throwParametersError(parameters.length);
			return null;
		}

		String constraint = "";
		AbstractModule first_module = allModules.get(parameters[0]);
		AbstractModule second_module = allModules.get(parameters[1]);
		if (first_module == null || second_module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint = SLTL_formula.next_module(first_module, second_module, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
