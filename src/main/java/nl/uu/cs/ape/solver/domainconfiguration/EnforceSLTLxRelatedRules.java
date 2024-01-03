package nl.uu.cs.ape.solver.domainconfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.Automaton;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.AuxiliaryPredicate;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.LogicOperation;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxImplication;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegation;

/**
 * Class used to encode rules specific to SLTLx logic.
 * 
 * @author Vedran Kasalica
 *
 */
public class EnforceSLTLxRelatedRules {

    /** Hide the implicit public constructor. */
    private EnforceSLTLxRelatedRules() {
    }

    /**
     * Define the base cases for the SLTLx relations.
     * Ensure the truth value of:
     * <ul>
     * <li>{@code true} - SLTLx term</li>
     * <li>{@code false} - SLTLx term</li>
     * </ul>
     * 
     * @return A set of formulas that ensure the encoding.
     */
    public static Collection<SLTLxFormula> setTrueFalse() {
        Set<SLTLxFormula> cnfEncoding = new HashSet<>();

        /* Encode {@code true} and {@code false} SLTLx terms. */
        cnfEncoding.add(SLTLxAtom.getTrue());
        cnfEncoding.add(new SLTLxNegation(SLTLxAtom.getFalse()));

        return cnfEncoding;
    }

    /**
     * Encoding all the required constraints for the given program length, in order
     * to ensure that helper predicates are used properly.
     *
     * @param moduleAutomaton  Graph representing all the tool states in the current
     *                         workflow (one synthesis run might iterate though
     *                         workflows of different lengths).
     * @param typeAutomaton    Graph representing all the type states in the current
     *                         workflow (one synthesis run might iterate though
     *                         workflows of different lengths).
     * @param helperPredicates List of helper predicates that should be encoded
     * @return CNF encoding of that ensures the correctness of the helper
     *         predicates.
     */
    public static Set<SLTLxFormula> preserveAuxiliaryPredicateRules(ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, List<AuxiliaryPredicate> helperPredicates) {
        Set<SLTLxFormula> cnfEncoding = new HashSet<>();

        Automaton automaton = null;
        AtomType workflowElem = null;
        for (AuxiliaryPredicate helperPredicate : helperPredicates) {
            if (helperPredicate.getGeneralizedPredicates().first() instanceof Type) {
                automaton = typeAutomaton;
            } else {
                automaton = moduleAutomaton;
            }
            for (State currState : automaton.getAllStates()) {
                workflowElem = currState.getWorkflowStateType();
                if (helperPredicate.getLogicOp() == LogicOperation.OR) {
                    /*
                     * Ensures that if the abstract predicate is used, at least one of the
                     * disjointLabels has to be used.
                     */
                    Set<SLTLxFormula> allORPossibilities = new HashSet<>();
                    for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
                        allORPossibilities.add(
                                new SLTLxAtom(
                                        workflowElem,
                                        subLabel,
                                        currState));
                    }
                    cnfEncoding.add(new SLTLxImplication(
                            new SLTLxAtom(
                                    workflowElem,
                                    helperPredicate,
                                    currState),
                            new SLTLxDisjunction(allORPossibilities)));

                    /*
                     * Ensures that if at least one of the disjointLabels was used, the abstract
                     * predicate has to be used as well.
                     */
                    for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
                        cnfEncoding.add(
                                new SLTLxImplication(
                                        new SLTLxAtom(
                                                workflowElem,
                                                subLabel,
                                                currState),
                                        new SLTLxAtom(
                                                workflowElem,
                                                helperPredicate,
                                                currState)));
                    }
                } else if (helperPredicate.getLogicOp() == LogicOperation.AND) {

                    /*
                     * Ensures that if the abstract predicate is used, all of the conjointLabels
                     * have to be used.
                     */
                    for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
                        cnfEncoding.add(
                                new SLTLxImplication(
                                        new SLTLxAtom(
                                                workflowElem,
                                                helperPredicate,
                                                currState),
                                        new SLTLxAtom(
                                                workflowElem,
                                                subLabel,
                                                currState)));
                    }

                    /*
                     * Ensures that if all of the conjointLabels were used, the abstract predicate
                     * has to be used as well.
                     */
                    Set<SLTLxFormula> allANDPossibilities = new HashSet<>();

                    for (TaxonomyPredicate subLabel : helperPredicate.getGeneralizedPredicates()) {
                        allANDPossibilities.add(
                                new SLTLxNegation(
                                        new SLTLxAtom(
                                                workflowElem,
                                                subLabel,
                                                currState)));
                    }
                    allANDPossibilities.add(
                            new SLTLxAtom(
                                    workflowElem,
                                    helperPredicate,
                                    currState));

                    cnfEncoding.add(new SLTLxDisjunction(allANDPossibilities));
                }
            }

        }
        return cnfEncoding;
    }
}
