package nl.uu.cs.ape.models.templateFormulas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The class is used to represent a predefined SLTLx constraints according to a
 * template, and to generate the
 * corresponding CNF representation. The form supports a limited set of commonly
 * used formulas, and unlike arbitrary
 * SLTLx formulas, encoding of templates is optimized.
 *
 * @author Vedran Kasalica
 */
public abstract class SLTLxTemplateFormula {

    private TaxonomyPredicate predicate;

    /**
     * Sign of the predicate, false if the predicate is negated, true otherwise.
     */
    private boolean sign;

    /**
     * Instantiates a new SLTLx formula.
     *
     * @param predicate the predicate
     */
    protected SLTLxTemplateFormula(TaxonomyPredicate predicate) {
        this.predicate = predicate;
        sign = true;
    }

    /**
     * Modal operators are performed over <b>formulas</b>. In case of value of
     * <b>sign</b> being true, <b>formula</b> is positive, otherwise the
     * formula is negative (<b>predicate</b> is negated).
     *
     * @param sign      Sign of the predicate, false if the predicate is negated,
     *                  true otherwise.
     * @param predicate A {@link TaxonomyPredicate}.
     */
    protected SLTLxTemplateFormula(boolean sign, TaxonomyPredicate predicate) {
        this.predicate = predicate;
        this.sign = sign;
    }

    /**
     * Setting whether the predicate is negated or not.
     * If sign is <b>false</b> the predicate will be negated.
     *
     * @param sign Sign of the predicate, false if the predicate is negated, true
     *             otherwise.
     */
    public void setSign(boolean sign) {
        this.sign = sign;
    }

    /**
     * Gets sign.
     *
     * @return <b>false</b> if the predicate is negated, <b>true</b> otherwise.
     */
    public boolean getSign() {
        return sign;
    }

    /**
     * Function returns the formula that was specified under the SLTLx operator.<br>
     * Currently only predicates.
     *
     * @return StateInterface ({@link AbstractModule}, {@link Module} or
     *         {@link Type}).
     */
    public TaxonomyPredicate getSubFormula() {
        return predicate;
    }

    /**
     * Returns the type of the SLTLx formula [<b>F</b>, <b>G</b> or <b>X</b>].
     *
     * @return String [<b>F</b>, <b>G</b> or <b>X</b>], depending on the type of
     *         SLTLx formula.
     */
    public abstract String getType();

    /**
     * Generate String representation of the CNF formula for
     * defined @moduleAutomaton and @typeAutomaton.
     *
     * @param moduleAutomaton Automaton of all the module states.
     * @param typeStateBlocks Automaton of all the type states.
     * @param workflowElement type of the workflow element ({@link AtomType#MODULE},
     *                        {@link AtomType#MEM_TYPE_REFERENCE} etc.)
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public abstract String getCNF(ModuleAutomaton moduleAutomaton, List<Block> typeStateBlocks,
            AtomType workflowElement, SATAtomMappings mappings);

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If <b>if_predicate</b> is used, tool <b>then_predicate</b> has to be used
     * subsequently.
     *
     * @param if_predicate    Predicate that enforce the usage of
     *                        <b>then_predicate</b>.
     * @param then_predicate  Predicate that is enforced by <b>if_predicate</b>.
     * @param moduleAutomaton Module automaton.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String ite_module(TaxonomyPredicate if_predicate, TaxonomyPredicate then_predicate,
            ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-"
                    + mappings.add(if_predicate, moduleAutomaton.getAllStates().get(i), AtomType.MODULE)
                    + " ");
            for (int j = i + 1; j < automatonSize; j++) {
                constraints.append(mappings.add(then_predicate, moduleAutomaton.get(j), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If type <b>if_predicate</b> is used/generated (depending on the @typeBlocks),
     * then type <b>then_predicate</b> has to be used/generated subsequently.
     *
     * @param if_predicate    Predicate that enforce the usage of
     *                        <b>then_predicate</b>.
     * @param then_predicate  Predicate that is enforced by <b>if_predicate</b>.
     * @param typeElement     Workflow element type.
     * @param moduleAutomaton Module automaton.
     * @param typeBlocks      Type blocks (corresponding to the memory or used type
     *                        states).
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String ite_type(TaxonomyPredicate if_predicate, TaxonomyPredicate then_predicate,
            AtomType typeElement, ModuleAutomaton moduleAutomaton,
            List<Block> typeBlocks, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int numberOfBlocks = typeBlocks.size();
        int numberOfStates = typeBlocks.get(0).getBlockSize();
        for (int i_block = 0; i_block < numberOfBlocks; i_block++) {
            for (int i_state = 0; i_state < numberOfStates; i_state++) {

                /* If if_predicate is used in any state of a certain block */
                constraints.append("-").append(mappings.add(if_predicate,
                        typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");

                /* then then_predicate must be used in a state of the subsequent blocks. */
                for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
                    for (int j_state = i_state + 1; j_state < numberOfBlocks; j_state++) {
                        constraints.append(mappings.add(then_predicate,
                                typeBlocks.get(j_block).getState(j_state), typeElement)).append(" ");
                    }

                }
                constraints.append("0\n");
            }
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If <b>if_predicate</b> is used, tool <b>then_predicate</b> cannot be used
     * subsequently.
     *
     * @param if_predicate       Predicate that forbids the usage of
     *                           <b>then_not_predicate</b>.
     * @param then_not_predicate Module that is forbidden by <b>if_predicate</b>.
     * @param moduleAutomaton    Module automaton.
     * @param mappings           Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String itn_module(TaxonomyPredicate if_predicate, TaxonomyPredicate then_not_predicate,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize - 1; i++) {
            State currModuleState = moduleAutomaton.getAllStates().get(i);
            for (int j = i + 1; j < automatonSize; j++) {
                constraints.append("-").append(mappings.add(if_predicate, currModuleState, AtomType.MODULE))
                        .append(" ");
                constraints.append("-"
                        + mappings.add(then_not_predicate, moduleAutomaton.get(j), AtomType.MODULE)
                        + " 0\n");
            }
        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If type <b>if_predicate</b> is used/generated (depending on the @typeBlocks),
     * then do not use/generate type <b>then_not_predicate</b> subsequently.
     *
     * @param if_predicate       Predicate that forbids the usage of
     *                           <b>then_not_predicate</b>.
     * @param then_not_predicate Predicate that is forbidden by <b>if_predicate</b>.
     * @param typeElement        Workflow element type.
     * @param moduleAutomaton    Module automaton.
     * @param typeBlocks         Type blocks (corresponding to the memory or used
     *                           type states).
     * @param mappings           Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String itn_type(TaxonomyPredicate if_predicate, TaxonomyPredicate then_not_predicate,
            AtomType typeElement, ModuleAutomaton moduleAutomaton,
            List<Block> typeBlocks, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int numberOfBlocks = typeBlocks.size();
        int numberOfStates = typeBlocks.get(0).getBlockSize();
        for (int i_block = 0; i_block < numberOfBlocks - 1; i_block++) {
            for (int i_state = 0; i_state < numberOfStates; i_state++) {
                for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
                    for (int j_state = 0; j_state < numberOfStates; j_state++) {
                        /*
                         * If if_predicate is used in any state of a certain block
                         */
                        constraints.append("-").append(mappings.add(if_predicate,
                                typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");
                        /*
                         * then then_predicate cannot be used in a state of the subsequent blocks.
                         */
                        constraints.append("-").append(mappings.add(then_not_predicate,
                                typeBlocks.get(j_block).getState(j_state), typeElement)).append(" 0\n");
                    }

                }
            }
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use module <b>second_module_in_sequence</b>,
     * then we must have used <b>first_module_in_sequence</b> prior to it.
     *
     * @param second_module_in_sequence Predicate that enforces the usage of
     *                                  <b>first_predicate</b>.
     * @param first_module_in_sequence  Predicate that is enforced by
     *                                  <b>second_predicate</b>.
     * @param moduleAutomaton           Module automaton.
     * @param mappings                  Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String depend_module(TaxonomyPredicate second_module_in_sequence,
            TaxonomyPredicate first_module_in_sequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(second_module_in_sequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");
            for (int j = 0; j < i; j++) {
                constraints.append(mappings.add(first_module_in_sequence,
                        moduleAutomaton.get(j), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use predicate <b>first_module_in_sequence</b>, then use
     * <b>second_module_in_sequence</b> as a next predicate in the sequence.
     *
     * @param first_module_in_sequence  Predicate that enforce the usage of
     *                                  <b>second_predicate</b>.
     * @param second_module_in_sequence Predicate that is enforced by
     *                                  <b>first_predicate</b>.
     * @param moduleAutomaton           Module automaton.
     * @param mappings                  Set of the mappings for the literals.
     * @return The String CNF representation of the constraint.
     */
    public static String next_module(TaxonomyPredicate first_module_in_sequence,
            TaxonomyPredicate second_module_in_sequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(first_module_in_sequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");

            /* Clause that forbids using first_predicate as the last in the sequence */
            if (i < automatonSize - 1) {
                constraints.append(mappings.add(second_module_in_sequence,
                        moduleAutomaton.get(i + 1), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use predicate <b>first_predicate</b>, then use
     * <b>second_predicate</b> as a next predicate in the sequence.
     *
     * @param second_module_in_sequence Predicate that is enforced by
     *                                  <b>first_predicate</b>.
     * @param first_module_in_sequence  Predicate that enforce the usage of
     *                                  <b>second_predicate</b>.
     * @param moduleAutomaton           Module automaton.
     * @param mappings                  Set of the mappings for the literals.
     * @return The String CNF representation of the constraint.
     */
    public static String prev_module(TaxonomyPredicate second_module_in_sequence,
            TaxonomyPredicate first_module_in_sequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(second_module_in_sequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");

            /*
             * Clause that forbids using second_module_in_sequence as the first tool in the
             * sequence
             */
            if (i > 0) {
                constraints.append(mappings.add(first_module_in_sequence,
                        moduleAutomaton.get(i - 1), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Use <b>last_module</b> as last module in the solution.
     *
     * @param last_module     The module.
     * @param moduleAutomaton Automaton of all the module states.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String useAsLastModule(TaxonomyPredicate last_module, ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        List<State> moduleAutomatonStates = moduleAutomaton.getAllStates();
        State lastModuleState = moduleAutomatonStates.get(moduleAutomatonStates.size() - 1);
        constraints.append(mappings.add(last_module, lastModuleState, AtomType.MODULE)).append(" 0\n");

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Use <b>module</b> as the <b>n</b>-th module in the solution.
     *
     * @param module          The module.
     * @param n               The absolute position in the solution.
     * @param moduleAutomaton Automaton of all the module states.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String useAsNthModule(TaxonomyPredicate module, int n, ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        List<State> moduleAutomatonStates = moduleAutomaton.getAllStates();
        State nthModuleState = moduleAutomatonStates.get(n - 1);
        constraints.append(mappings.add(module, nthModuleState, AtomType.MODULE)).append(" 0\n");

        return constraints.toString();
    }

    /**
     * TODO: Creates a CNF representation of the Constraint:<br>
     * Use <b>module</b> in the solution exactly <b>n</b> times.
     *
     * @param module          Module to be used.
     * @param n               Number of repetitions.
     * @param moduleAutomaton Automaton of all the module states.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String useModuleNtimes(TaxonomyPredicate module, int n, ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        // StringBuilder constraints = new StringBuilder();
        //
        // List<State> moduleAutomatonStates = moduleAutomaton.getModuleStates();
        // ModuleState nthModuleState = moduleAutomatonStates.get(index - 1);
        // constraints.append(mappings.add(module, nthModuleState)).append(" 0\n";

        return null;
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Use <b>module</b> with data <b>inputType</b> as one of the inputs.
     * 
     * @param module          Module to be used.
     * @param inputType       Type of one of the input types.
     * @param moduleAutomaton The module automaton
     * @param typeAutomaton   The type automaton.
     * @param mappings        Set of the mappings for the literals.
     * @return
     */
    public static String use_module_input(TaxonomyPredicate module, TaxonomyPredicate inputType,
            ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

        StringBuilder constraints = new StringBuilder();
        Set<Integer> allCombinations = new HashSet<>();
        Map<Integer, State> opOrderStates = new HashMap<>();
        Map<Integer, List<State>> opInputs = new HashMap<>();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int opNo = 0; opNo < automatonSize; opNo++) {

            int currOp = mappings.getNextAuxNum();
            allCombinations.add(currOp);

            opOrderStates.put(currOp, moduleAutomaton.getAllStates().get(opNo));

            opInputs.put(currOp, typeAutomaton.getUsedTypesBlock(opNo).getStates());

        }
        // at least one of the states must be valid
        for (Integer currComb : allCombinations) {
            constraints.append(currComb + " ");
        }
        constraints.append("0\n");

        // each state enforces usage of the corresponding tools and input
        for (Integer currComb : allCombinations) {
            // enforce tools
            constraints.append("-" + currComb + " ")
                    .append(mappings.add(module, opOrderStates.get(currComb), AtomType.MODULE))
                    .append(" 0\n");
            // enforce output/input dependencies
            constraints.append("-" + currComb + " ");
            for (State currState : opInputs.get(currComb)) {
                constraints.append(mappings.add(inputType, currState, AtomType.USED_TYPE))
                        .append(" ");
            }
            constraints.append("0\n");

        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Use <b>module</b> with data <b>outputType</b> as one of the outputs.
     * 
     * @param module          Module to be used.
     * @param outputType      Type of one of the input types.
     * @param moduleAutomaton The module automaton
     * @param typeAutomaton   The type automaton.
     * @param mappings        Set of the mappings for the literals.
     * @return
     */
    public static String use_module_output(TaxonomyPredicate module, TaxonomyPredicate outputType,
            ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

        StringBuilder constraints = new StringBuilder();
        Set<Integer> allCombinations = new HashSet<>();
        Map<Integer, State> opOrderStates = new HashMap<>();
        Map<Integer, List<State>> onOutputs = new HashMap<>();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int opNo = 0; opNo < automatonSize; opNo++) {

            int currOp = mappings.getNextAuxNum();
            allCombinations.add(currOp);

            opOrderStates.put(currOp, moduleAutomaton.getAllStates().get(opNo));

            onOutputs.put(currOp, typeAutomaton.getMemoryTypesBlock(opNo + 1).getStates());

        }
        // at least one of the states must be valid
        for (Integer currComb : allCombinations) {
            constraints.append(currComb + " ");
        }
        constraints.append("0\n");

        // each state enforces usage of the corresponding tools and input
        for (Integer currComb : allCombinations) {
            // enforce tools
            constraints.append("-" + currComb + " ")
                    .append(mappings.add(module, opOrderStates.get(currComb), AtomType.MODULE))
                    .append(" 0\n");
            // enforce output/input dependencies
            constraints.append("-" + currComb + " ");
            for (State currState : onOutputs.get(currComb)) {
                constraints.append(mappings.add(outputType, currState, AtomType.MEM_TYPE_REFERENCE))
                        .append(" ");
            }
            constraints.append("0\n");

        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * 1st operation should generate an output used by the 2nd operation.
     *
     * @param first_predicate  - Module type that generates the data as output
     * @param second_predicate - Module type that uses the generated data as input
     * @param domainSetup      - setup of the domain
     * @param moduleAutomaton  - module automaton.
     * @param typeAutomaton    - type automaton
     * @param mappings         - Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String connected_modules(TaxonomyPredicate first_predicate, TaxonomyPredicate second_predicate,
            APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        Set<Integer> allCombinations = new HashSet<>();
        Map<Integer, Pair<State>> opOrderStates = new HashMap<>();
        Map<Integer, Set<Pair<State>>> opOutInPairs = new HashMap<>();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int op1 = 0; op1 < automatonSize - 1; op1++) {
            for (int op2 = op1 + 1; op2 < automatonSize; op2++) {

                int currComb = mappings.getNextAuxNum();
                allCombinations.add(currComb);

                opOrderStates.put(currComb, new Pair<State>(moduleAutomaton.getAllStates().get(op1),
                        moduleAutomaton.getAllStates().get(op2)));

                List<State> op1outputs = typeAutomaton.getMemoryTypesBlock(op1 + 1).getStates();
                List<State> op2inputs = typeAutomaton.getUsedTypesBlock(op2).getStates();

                Set<Pair<State>> statePairs = APEUtils.getUniquePairs(op1outputs, op2inputs);
                opOutInPairs.put(currComb, statePairs);

            }
        }
        // at least one of the combinations must be valid
        for (Integer currComb : allCombinations) {
            constraints.append(currComb + " ");
        }
        constraints.append("0\n");

        // each combination enforces usage of the corresponding tools and output/inputs
        for (Integer currComb : allCombinations) {
            // enforce tools
            constraints.append("-" + currComb + " ")
                    .append(mappings.add(first_predicate, opOrderStates.get(currComb).getFirst(), AtomType.MODULE))
                    .append(" 0\n");
            constraints.append("-" + currComb + " ")
                    .append(mappings.add(second_predicate, opOrderStates.get(currComb).getSecond(), AtomType.MODULE))
                    .append(" 0\n");
            // enforce output/input dependencies
            constraints.append("-" + currComb + " ");
            for (Pair<State> currPair : opOutInPairs.get(currComb)) {
                constraints.append(mappings.add(currPair.getFirst(), currPair.getSecond(), AtomType.MEM_TYPE_REFERENCE))
                        .append(" ");
            }
            constraints.append("0\n");

        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * 1st operation should not generate an output used by the 2nd operation.
     *
     * @param first_predicate  - Module type that generates the data as output
     * @param second_predicate - Module type that uses the generated data as input
     * @param domainSetup      - setup of the domain
     * @param moduleAutomaton  - module automaton.
     * @param typeAutomaton    - type automaton
     * @param mappings         - Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String not_connected_modules(TaxonomyPredicate first_predicate, TaxonomyPredicate second_predicate,
            APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int op1 = 0; op1 < automatonSize - 1; op1++) {
            for (int op2 = op1 + 1; op2 < automatonSize; op2++) {

                State firstModuleState = moduleAutomaton.get(op1);
                State secondModuleState = moduleAutomaton.get(op2);

                List<State> op1outputs = typeAutomaton.getMemoryTypesBlock(op1 + 1).getStates();
                List<State> op2inputs = typeAutomaton.getUsedTypesBlock(op2).getStates();

                // Ensure that either the 2 operations are not used consequently, or that they
                // are not connected
                Set<Pair<State>> statePairs = APEUtils.getUniquePairs(op1outputs, op2inputs);
                for (Pair<State> currIOpair : statePairs) {
                    constraints.append("-").append(mappings.add(first_predicate, firstModuleState, AtomType.MODULE))
                            .append(" ");
                    constraints.append("-"
                            + mappings.add(currIOpair.getFirst(), currIOpair.getSecond(), AtomType.MEM_TYPE_REFERENCE)
                            + " ");
                    constraints.append("-"
                            + mappings.add(second_predicate, secondModuleState, AtomType.MODULE)
                            + " 0\n");
                }

            }
        }

        return constraints.toString();
    }

    public static String notRepeatModules(TaxonomyPredicate predicate, APEDomainSetup domainSetup,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int op1 = 0; op1 < automatonSize - 1; op1++) {
            for (int op2 = op1 + 1; op2 < automatonSize; op2++) {

                State firstModuleState = moduleAutomaton.get(op1);
                State secondModuleState = moduleAutomaton.get(op2);

                List<State> op1outputs = typeAutomaton.getMemoryTypesBlock(op1 + 1).getStates();
                List<State> op2inputs = typeAutomaton.getUsedTypesBlock(op2).getStates();

                // Ensure that either each operation is not used consequently, or that they are
                // not connected
                Set<Pair<State>> statePairs = APEUtils.getUniquePairs(op1outputs, op2inputs);
                for (Pair<State> currIOpair : statePairs) {
                    // filter all operations
                    domainSetup.getAllModules().getElementsFromSubTaxonomy(predicate).stream()
                            .filter(x -> x.isSimplePredicate()).forEach(operation -> {

                                constraints.append("-")
                                        .append(mappings.add(operation, firstModuleState, AtomType.MODULE)).append(" ");
                                constraints.append("-"
                                        + mappings.add(currIOpair.getFirst(), currIOpair.getSecond(),
                                                AtomType.MEM_TYPE_REFERENCE)
                                        + " ");
                                constraints.append("-"
                                        + mappings.add(operation, secondModuleState, AtomType.MODULE)
                                        + " 0\n");
                            });
                }

            }
        }

        return constraints.toString();
    }

    /**
     * Simple method that combines a pair of integers into a unique String.
     * 
     * @param int1 - first integer
     * @param int2 - second integer
     * @return Unique combination of the pair, as String.
     */
    private static String combine(int int1, int int2) {
        return int1 + "_" + int2;
    }

}
