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
 * Use module <b>parameters[0]</b> in the solution
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_use_module extends ConstraintTemplate {


	public Constraint_use_module(int parametersNo, String description) {
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
		SLTL_formula_F formula = new SLTL_formula_F(module);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
