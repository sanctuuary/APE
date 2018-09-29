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
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
 * consequently using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_if_then_module extends ConstraintTemplate {


	public Constraint_if_then_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			return null;
		}
		String constraint = "";
		AbstractModule if_module = allModules.get(parameters[0]);
		AbstractModule then_module = allModules.get(parameters[1]);
		constraint = SLTL_formula.ite(if_module, then_module, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
