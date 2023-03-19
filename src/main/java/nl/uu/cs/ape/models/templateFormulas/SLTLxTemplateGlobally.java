package nl.uu.cs.ape.models.templateFormulas;

import java.util.List;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

/**
 * Template for the SLTLx formulas that include modal Globally operator in its
 * simplest format "G A(x)" or "G &lt;T(;)&gt; true"
 * 
 * @author Vedran Kasalica
 *
 */
public class SLTLxTemplateGlobally extends SLTLxTemplateFormula {

    /**
     * Instantiates a new Sltl formula g.
     *
     * @param predicate the predicate
     */
    public SLTLxTemplateGlobally(TaxonomyPredicate predicate) {
        super(predicate);
    }

    /**
     * Instantiates a new Sltl formula g.
     *
     * @param sign      the sign
     * @param predicate the predicate
     */
    public SLTLxTemplateGlobally(boolean sign, TaxonomyPredicate predicate) {
        super(sign, predicate);
    }

    /**
     * Generate String representation of the CNF formula for defined
     * {@link ModuleAutomaton} and
     * {@link nl.uu.cs.ape.automaton.TypeAutomaton}. However, the function only
     * considers
     * the memory states of type automaton (not the tool input/"used" states).
     *
     * @param moduleAutomaton Automaton of all the module states.
     * @param typeStateBlocks Automaton of all the type states.
     * @param workflowElement type of the workflow element ({@link AtomType#MODULE},
     *                        {@link AtomType#MEM_TYPE_REFERENCE} etc.)
     * @param mappings        Set of the mappings for the literals.
     * @return The CNF representation of the SLTL formula.
     */
    @Override
    public String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks, AtomType workflowElement,
            SATAtomMappings mappings) {

        String constraints = "";
        String negSign;
        /* check whether the sub-formula is negated or not */
        if (super.getSign()) {
            negSign = "";
        } else {
            negSign = "-";
        }
        /*
         * Distinguishing whether the formula under the modal operator is type or
         * module.
         */
        if (super.getSubFormula().getType().equals("type")) {
            for (Block typeBlock : typeStateBlocks) {
                for (State typeState : typeBlock.getStates()) {
                    constraints += negSign
                            + mappings.add(super.getSubFormula(), typeState, workflowElement) + " 0\n";
                }
            }
        } else {
            for (State moduleState : moduleAutomaton.getAllStates()) {
                constraints += negSign + mappings.add(super.getSubFormula(), moduleState, workflowElement)
                        + " 0\n";
            }
        }
        return constraints;
    }

    /**
     * Returns the type of the SLTL formula [F, G or X].
     *
     * @return [F, G or X] depending on the type of SLTL formula
     */
    @Override
    public String getType() {
        return "G";
    }
}
