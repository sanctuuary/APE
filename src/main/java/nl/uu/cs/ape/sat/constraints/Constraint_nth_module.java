package nl.uu.cs.ape.sat.constraints;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.GeneralEncodingUtils;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.ModuleUtils;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.formulas.*;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

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


	public Constraint_nth_module(String id,  List<ConstraintParameter> parametersNo, String description) {
		super(id, parametersNo, description);
	}

	@Override
	public String getConstraint(List<ConstraintParameter> parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMappings mappings) {
		if (parameters.size() != 2) {
			super.throwParametersError(parameters.size());
			return null;
		}
		int n;
		try { 
	        n = Integer.parseInt("not_working"); 
	    } catch(NumberFormatException e) { 
	    	System.err.println("Constraint argument is not an intiger number.");
	        return null; 
	    } catch(NullPointerException e) {
	    	System.err.println("Constraint argument is not an intiger number.");
	        return null;
	    }
		String constraint = "";
		/* working on first parameter */
		List<TaxonomyPredicate> seondInSeq = parameters.get(0).getParameterTypes();
		AbstractModule module  = (AbstractModule) ModuleUtils.getConjunctModule(seondInSeq, allModules);
		GeneralEncodingUtils.getConjunctConstraints(module, seondInSeq, mappings, moduleAutomaton, WorkflowElement.MODULE);
		
		if (module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint += SLTL_formula.useAsNthModule(module, n, moduleAutomaton, mappings);

		return constraint;
	}

}
