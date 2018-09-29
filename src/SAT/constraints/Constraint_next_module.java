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
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b> as a next module in the sequence
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_next_module extends ConstraintTemplate {


	public Constraint_next_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			return null;
		}

		String constraint = "";
		AbstractModule first_module = allModules.get(parameters[0]);
		AbstractModule second_module = allModules.get(parameters[1]);
		constraint = SLTL_formula.next(first_module, second_module, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
