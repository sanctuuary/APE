package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.SLTL_formula;
import nl.uu.cs.ape.sat.models.SLTL_formula_F;

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
