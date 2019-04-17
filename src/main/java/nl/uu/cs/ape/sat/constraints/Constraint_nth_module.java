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
 * Use <b>parameters[0]</b> as N-th module in the solution (where <b>parameters[0]</b> = N)
 * using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_nth_module extends ConstraintTemplate {


	public Constraint_nth_module(String id, int parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			super.throwParametersError(parameters.length);
			return null;
		}
		int n;
		try { 
	        n = Integer.parseInt(parameters[1]); 
	    } catch(NumberFormatException e) { 
	    	System.err.println("Constraint argument is not an intiger number.");
	        return null; 
	    } catch(NullPointerException e) {
	    	System.err.println("Constraint argument is not an intiger number.");
	        return null;
	    }
		String constraint = "";
		AbstractModule module = allModules.get(parameters[0]);
		if (module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint += SLTL_formula.useAsNthModule(module, n, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}
