package nl.uu.cs.ape.sat.models.formulas;

import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;

import java.util.List;

public class SLTL_formula_F extends SLTL_formula {

    public SLTL_formula_F(TaxonomyPredicate formula) {
        super(formula);
    }

    public SLTL_formula_F(boolean sign, TaxonomyPredicate formula) {
        super(sign, formula);
    }

    /**
     * Generate String representation of the CNF formula for defined @moduleAutomaton and @typeAutomaton.
     * However, the function only considers the memory states of type automaton (not the tool input/"used" states).
     *
     * @param moduleAutomaton Automaton of all the module states.
     * @param typeStateBlocks Automaton of all the type states.
     * @param workflowElement TODO
     * @param mappings Set of the mappings for the literals.
     * @return The CNF representation of the SLTL formula.
     */
    @Override
    public String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks, WorkflowElement workflowElement, AtomMappings mappings) {

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
            for (State moduleState : moduleAutomaton.getModuleStates()) {
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