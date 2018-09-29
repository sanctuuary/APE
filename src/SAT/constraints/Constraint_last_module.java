package SAT.constraints;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;
import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;
import SAT.models.SLTL_formula;
import SAT.models.SLTL_formula_F;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * Use <b>parameters[0]</b> as last module in the solution.
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_last_module extends ConstraintTemplate {


	public Constraint_last_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 1) {
			return null;
		}
		String constraint = "";
		AbstractModule module = allModules.get(parameters[0]);
		constraint += SLTL_formula.useAsLastModule(module, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
