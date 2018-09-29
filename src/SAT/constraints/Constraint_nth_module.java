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
 * Use <b>parameters[0]</b> as N-th module in the solution (where <b>parameters[0]</b> = N)
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_nth_module extends ConstraintTemplate {


	public Constraint_nth_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			return null;
		}
		int n;
		try { 
	        n = Integer.parseInt(parameters[1]); 
	    } catch(NumberFormatException e) { 
	        return null; 
	    } catch(NullPointerException e) {
	        return null;
	    }
		String constraint = "";
		AbstractModule module = allModules.get(parameters[0]);
		constraint += SLTL_formula.useAsNthModule(module, n, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
