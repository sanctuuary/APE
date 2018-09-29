package SAT.constraints;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;
import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;
import SAT.models.SLTL_formula;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we use module <b>parameters[0]</b>, then we must have used <b>parameters[1]</b> prior to it
 *  using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_depend_module extends ConstraintTemplate {


	public Constraint_depend_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			return null;
		}

		String constraint = "";
		AbstractModule second_module_in_sequence = allModules.get(parameters[0]);
		AbstractModule first_module_in_sequence = allModules.get(parameters[1]);
		constraint = SLTL_formula.depend(first_module_in_sequence, second_module_in_sequence, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	
	}

}
