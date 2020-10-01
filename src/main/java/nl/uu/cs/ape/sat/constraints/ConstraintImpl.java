package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.formulas.SLTL_formula;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

import java.util.List;

/**
 * Implements constraints of the form:<br>
 * If we use module <b>parameters[0]</b>, then do not use <b>parameters[1]</b>
 * subsequently using the function {@link #getConstraint}.
 *
 * @author Vedran Kasalica
 */
public class ConstraintImpl extends ConstraintTemplate {

    /**
     * Instantiates a new Constraint.
     *
     * @param id           the id
     * @param parametersNo the parameters no
     * @param description  the description
     * @param function     the function
     */
    public ConstraintImpl(String id, List<TaxonomyPredicate> parametersNo, String description, Runnable function) {
        super(id, parametersNo, description);
    }

    @Override
    public String getConstraint(List<TaxonomyPredicate> parameters, APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
                                TypeAutomaton typeAutomaton, AtomMappings mappings) {
        if (parameters.size() != this.getNoOfParameters()) {
            super.throwParametersError(parameters.size());
            return null;
        }

        return SLTL_formula.itn_module(parameters.get(0), parameters.get(1), moduleAutomaton, mappings);
    }

}
