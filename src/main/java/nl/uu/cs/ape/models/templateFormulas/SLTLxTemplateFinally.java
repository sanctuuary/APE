package nl.uu.cs.ape.models.templateFormulas;

import java.util.List;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Template for the formulas including the modal Finally operator in its simplest format "G A(x)" or "G &lt;T(;)&gt; true".
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxTemplateFinally extends SLTLxTemplateFormula {

    /**
     * Instantiates a new Sltl formula f.
     *
     * @param formula the formula
     */
    public SLTLxTemplateFinally(TaxonomyPredicate formula) {
        super(formula);
    }

    /**
     * Instantiates a new Sltl formula f.
     *
     * @param sign    the sign
     * @param formula the formula
     */
    public SLTLxTemplateFinally(boolean sign, TaxonomyPredicate formula) {
        super(sign, formula);
    }

    /**
     * Generate String representation of the CNF formula for defined @moduleAutomaton and @typeAutomaton.
     * However, the function only considers the memory states of type automaton (not the tool input/"used" states).
     *
     * @param moduleAutomaton Automaton of all the module states.
     * @param typeStateBlocks Automaton of all the type states.
     * @param workflowElement type of the workflow element ({@link AtomType#MODULE}, {@link AtomType#MEM_TYPE_REFERENCE} etc.)
     * @param mappings        Set of the mappings for the literals.
     * @return The CNF representation of the SLTL formula.
     */
    @Override
    public String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks, AtomType workflowElement, SATAtomMappings mappings) {

        String constraints = "";

        String negSign;
        /* Check whether the atom is expected to be negated or not */
        if (super.getSign()) {
            negSign = "";
        } else {
            negSign = "-";
        }

        /* Distinguishing whether the atom under the modal operator is type or module. */
//		if (super.getSubFormula() instanceof Type) {
        if (super.getSubFormula().getType().matches("type")) {
            for (Block typeBlock : typeStateBlocks) {
                for (State typeState : typeBlock.getStates()) {
                    constraints += negSign
                            + mappings.add(super.getSubFormula(), typeState, workflowElement) + " ";
                }
            }
            constraints += "0\n";
        } else {
            for (State moduleState : moduleAutomaton.getAllStates()) {
                constraints += negSign + mappings.add(super.getSubFormula(), moduleState, workflowElement)
                        + " ";
            }
            constraints += "0\n";
        }
        return constraints;
    }

    /**
     * Returns the type of the SLTL formula [F, G or X].
     *
     * @return [F, G or X] depending on the type of SLTL formula.
     */
    @Override
    public String getType() {
        return "F";
    }
}