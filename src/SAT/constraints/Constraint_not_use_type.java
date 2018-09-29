package SAT.constraints;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;
import SAT.models.AbstractModule;
import SAT.models.AllModules;
import SAT.models.AllTypes;
import SAT.models.AtomMapping;
import SAT.models.SLTL_formula;
import SAT.models.SLTL_formula_F;
import SAT.models.SLTL_formula_G;
import SAT.models.Type;

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
			return null;
		}
		String constraint = "";

		Type type = allTypes.get(parameters[0]);
		SLTL_formula_G formula = new SLTL_formula_G(false, type);
		constraint = formula.getCNF(moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
