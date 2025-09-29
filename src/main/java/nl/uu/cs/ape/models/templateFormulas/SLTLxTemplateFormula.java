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
import nl.uu.cs.ape.domain.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.AbstractModule;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;

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
     * If <b>ifPredicate</b> is used, tool <b>thenPredicate</b> has to be used
     * subsequently.
     *
     * @param ifPredicate     Predicate that enforce the usage of
     *                        <b>thenPredicate</b>.
     * @param thenPredicate   Predicate that is enforced by <b>ifPredicate</b>.
     * @param moduleAutomaton Module automaton.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String iteModule(TaxonomyPredicate ifPredicate, TaxonomyPredicate thenPredicate,
            ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-"
                    + mappings.add(ifPredicate, moduleAutomaton.getAllStates().get(i), AtomType.MODULE)
                    + " ");
            for (int j = i + 1; j < automatonSize; j++) {
                constraints.append(mappings.add(thenPredicate, moduleAutomaton.get(j), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If type <b>ifPredicate</b> is used/generated (depending on the @typeBlocks),
     * then type <b>thenPredicate</b> has to be used/generated subsequently.
     *
     * @param ifPredicate   Predicate that enforce the usage of
     *                      <b>thenPredicate</b>.
     * @param thenPredicate Predicate that is enforced by <b>ifPredicate</b>.
     * @param typeElement   Workflow element type.
     * @param typeBlocks    Type blocks (corresponding to the memory or used type
     *                      states).
     * @param mappings      Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String iteType(TaxonomyPredicate ifPredicate, TaxonomyPredicate thenPredicate,
            AtomType typeElement,
            List<Block> typeBlocks, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int numberOfBlocks = typeBlocks.size();
        int numberOfStates = typeBlocks.get(0).getBlockSize();
        for (int i_block = 0; i_block < numberOfBlocks; i_block++) {
            for (int i_state = 0; i_state < numberOfStates; i_state++) {

                /* If ifPredicate is used in any state of a certain block */
                constraints.append("-").append(mappings.add(ifPredicate,
                        typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");

                /* then thenPredicate must be used in a state of the subsequent blocks. */
                for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
                    for (int j_state = i_state + 1; j_state < numberOfBlocks; j_state++) {
                        constraints.append(mappings.add(thenPredicate,
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
     * If <b>ifPredicate</b> is used, tool <b>thenPredicate</b> cannot be used
     * subsequently.
     *
     * @param ifPredicate      Predicate that forbids the usage of
     *                         <b>thenNotPredicate</b>.
     * @param thenNotPredicate Module that is forbidden by <b>ifPredicate</b>.
     * @param moduleAutomaton  Module automaton.
     * @param mappings         Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String itnModule(TaxonomyPredicate ifPredicate, TaxonomyPredicate thenNotPredicate,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize - 1; i++) {
            State currModuleState = moduleAutomaton.getAllStates().get(i);
            for (int j = i + 1; j < automatonSize; j++) {
                constraints.append("-").append(mappings.add(ifPredicate, currModuleState, AtomType.MODULE))
                        .append(" ");
                constraints.append("-"
                        + mappings.add(thenNotPredicate, moduleAutomaton.get(j), AtomType.MODULE)
                        + " 0\n");
            }
        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If type <b>ifPredicate</b> is used/generated (depending on the @typeBlocks),
     * then do not use/generate type <b>thenNotPredicate</b> subsequently.
     *
     * @param ifPredicate      Predicate that forbids the usage of
     *                         <b>thenNotPredicate</b>.
     * @param thenNotPredicate Predicate that is forbidden by <b>ifPredicate</b>.
     * @param typeElement      Workflow element type.
     * @param typeBlocks       Type blocks (corresponding to the memory or used
     *                         type states).
     * @param mappings         Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String itnType(TaxonomyPredicate ifPredicate, TaxonomyPredicate thenNotPredicate,
            AtomType typeElement,
            List<Block> typeBlocks, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int numberOfBlocks = typeBlocks.size();
        int numberOfStates = typeBlocks.get(0).getBlockSize();
        for (int i_block = 0; i_block < numberOfBlocks - 1; i_block++) {
            for (int i_state = 0; i_state < numberOfStates; i_state++) {
                for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
                    for (int j_state = 0; j_state < numberOfStates; j_state++) {
                        /*
                         * If ifPredicate is used in any state of a certain block
                         */
                        constraints.append("-").append(mappings.add(ifPredicate,
                                typeBlocks.get(i_block).getState(i_state), typeElement)).append(" ");
                        /*
                         * then thenPredicate cannot be used in a state of the subsequent blocks.
                         */
                        constraints.append("-").append(mappings.add(thenNotPredicate,
                                typeBlocks.get(j_block).getState(j_state), typeElement)).append(" 0\n");
                    }

                }
            }
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use module <b>secondModuleInSequence</b>,
     * then we must have used <b>firstModuleInSequence</b> prior to it.
     *
     * @param secondModuleInSequence Predicate that enforces the usage of
     *                               <b>firstPredicate</b>.
     * @param firstModuleInSequence  Predicate that is enforced by
     *                               <b>secondPredicate</b>.
     * @param moduleAutomaton        Module automaton.
     * @param mappings               Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String dependModule(TaxonomyPredicate secondModuleInSequence,
            TaxonomyPredicate firstModuleInSequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(secondModuleInSequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");
            for (int j = 0; j < i; j++) {
                constraints.append(mappings.add(firstModuleInSequence,
                        moduleAutomaton.get(j), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use predicate <b>firstModuleInSequence</b>, then use
     * <b>secondModuleInSequence</b> as a next predicate in the sequence.
     *
     * @param firstModuleInSequence  Predicate that enforce the usage of
     *                               <b>secondPredicate</b>.
     * @param secondModuleInSequence Predicate that is enforced by
     *                               <b>firstPredicate</b>.
     * @param moduleAutomaton        Module automaton.
     * @param mappings               Set of the mappings for the literals.
     * @return The String CNF representation of the constraint.
     */
    public static String nextModule(TaxonomyPredicate firstModuleInSequence,
            TaxonomyPredicate secondModuleInSequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(firstModuleInSequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");

            /* Clause that forbids using firstPredicate as the last in the sequence */
            if (i < automatonSize - 1) {
                constraints.append(mappings.add(secondModuleInSequence,
                        moduleAutomaton.get(i + 1), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * If we use predicate <b>firstPredicate</b>, then use
     * <b>secondPredicate</b> as a next predicate in the sequence.
     *
     * @param secondModuleInSequence Predicate that is enforced by
     *                               <b>firstPredicate</b>.
     * @param firstModuleInSequence  Predicate that enforce the usage of
     *                               <b>secondPredicate</b>.
     * @param moduleAutomaton        Module automaton.
     * @param mappings               Set of the mappings for the literals.
     * @return The String CNF representation of the constraint.
     */
    public static String prevModule(TaxonomyPredicate secondModuleInSequence,
            TaxonomyPredicate firstModuleInSequence,
            ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (int i = 0; i < automatonSize; i++) {
            constraints.append("-").append(mappings.add(secondModuleInSequence,
                    moduleAutomaton.getAllStates().get(i), AtomType.MODULE)).append(" ");

            /*
             * Clause that forbids using secondModuleInSequence as the first tool in the
             * sequence
             */
            if (i > 0) {
                constraints.append(mappings.add(firstModuleInSequence,
                        moduleAutomaton.get(i - 1), AtomType.MODULE)).append(" ");
            }
            constraints.append("0\n");
        }
        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Use <b>lastModule</b> as last module in the solution.
     *
     * @param lastModule      The module.
     * @param moduleAutomaton Automaton of all the module states.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String useAsLastModule(TaxonomyPredicate lastModule, ModuleAutomaton moduleAutomaton,
            SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        List<State> moduleAutomatonStates = moduleAutomaton.getAllStates();
        State lastModuleState = moduleAutomatonStates.get(moduleAutomatonStates.size() - 1);
        constraints.append(mappings.add(lastModule, lastModuleState, AtomType.MODULE)).append(" 0\n");

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
    public static String useModuleInput(TaxonomyPredicate module, TaxonomyPredicate inputType,
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
    public static String useModuleOutput(TaxonomyPredicate module, TaxonomyPredicate outputType,
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
     * @param firstPredicate  Module type that generates the data as output
     * @param secondPredicate Module type that uses the generated data as input
     * @param moduleAutomaton module automaton.
     * @param typeAutomaton   type automaton
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String connectedModules(TaxonomyPredicate firstPredicate, TaxonomyPredicate secondPredicate,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        Set<Integer> allCombinations = new HashSet<>();
        Map<Integer, Pair<State>> opOrderStates = new HashMap<>();
        Map<Integer, Set<Pair<State>>> opOutInPairs = new HashMap<>();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (Pair<Integer> operations : APEUtils.generateDistinctPairs(automatonSize)) {
            int op1 = operations.getFirst();
            int op2 = operations.getSecond();
            int currComb = mappings.getNextAuxNum();
            allCombinations.add(currComb);

            opOrderStates.put(currComb, new Pair<>(moduleAutomaton.getAllStates().get(op1),
                    moduleAutomaton.getAllStates().get(op2)));

            List<State> op1outputs = typeAutomaton.getMemoryTypesBlock(op1 + 1).getStates();
            List<State> op2inputs = typeAutomaton.getUsedTypesBlock(op2).getStates();

            Set<Pair<State>> statePairs = APEUtils.getUniquePairs(op1outputs, op2inputs);
            opOutInPairs.put(currComb, statePairs);

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
                    .append(mappings.add(firstPredicate, opOrderStates.get(currComb).getFirst(), AtomType.MODULE))
                    .append(" 0\n");
            constraints.append("-" + currComb + " ")
                    .append(mappings.add(secondPredicate, opOrderStates.get(currComb).getSecond(), AtomType.MODULE))
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
     * @param firstPredicate  Module type that generates the data as output
     * @param secondPredicate Module type that uses the generated data as input
     * @param typeAutomaton   type automaton
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String notConnectedModules(TaxonomyPredicate firstPredicate, TaxonomyPredicate secondPredicate,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (Pair<Integer> operations : APEUtils.generateDistinctPairs(automatonSize)) {
            int op1 = operations.getFirst();
            int op2 = operations.getSecond();

            State firstModuleState = moduleAutomaton.get(op1);
            State secondModuleState = moduleAutomaton.get(op2);

            List<State> op1outputs = typeAutomaton.getMemoryTypesBlock(op1 + 1).getStates();
            List<State> op2inputs = typeAutomaton.getUsedTypesBlock(op2).getStates();

            // Ensure that either the 2 operations are not used consequently, or that they
            // are not connected
            Set<Pair<State>> statePairs = APEUtils.getUniquePairs(op1outputs, op2inputs);
            for (Pair<State> currIOpair : statePairs) {
                constraints.append("-").append(mappings.add(firstPredicate, firstModuleState, AtomType.MODULE))
                        .append(" ");
                constraints.append("-"
                        + mappings.add(currIOpair.getFirst(), currIOpair.getSecond(), AtomType.MEM_TYPE_REFERENCE)
                        + " ");
                constraints.append("-"
                        + mappings.add(secondPredicate, secondModuleState, AtomType.MODULE)
                        + " 0\n");
            }

        }

        return constraints.toString();

    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * Do not repeat using the same tool.
     * 
     * @param predicate
     * @param domainSetup
     * @param moduleAutomaton
     * @param typeAutomaton
     * @param mappings
     * @return
     */
    public static String notRepeatModules(TaxonomyPredicate predicate, APEDomainSetup domainSetup,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();

        int automatonSize = moduleAutomaton.getAllStates().size();
        for (Pair<Integer> operations : APEUtils.generateDistinctPairs(automatonSize)) {
            int op1 = operations.getFirst();
            int op2 = operations.getSecond();

            State firstModuleState = moduleAutomaton.get(op1);
            State secondModuleState = moduleAutomaton.get(op2);

            // filter all operations
            domainSetup.getAllModules().getElementsFromSubTaxonomy(predicate).stream()
                    .filter(x -> x.isSimplePredicate()).forEach(operation -> {

                        constraints.append("-")
                                .append(mappings.add(operation, firstModuleState, AtomType.MODULE)).append(" ");
                        constraints.append("-"
                                + mappings.add(operation, secondModuleState, AtomType.MODULE)
                                + " 0\n");
                    });

        }

        return constraints.toString();
    }

    /**
     * Creates a CNF representation of the Constraint:<br>
     * The same tools that belong to the sub-taxonomy should not be connected.
     * 
     * @param predicate       Root predicate of the sub-taxonomy.
     * @param domainSetup     Current domain setup.
     * @param moduleAutomaton Current module automaton modeling the workflow control
     *                        flow.
     * @param typeAutomaton   Current type automaton modeling the workflow data
     *                        flow.
     * @param mappings        Set of the mappings for the literals.
     * @return The String CNF representation of the SLTLx formula.
     */
    public static String notConnectModules(TaxonomyPredicate predicate, APEDomainSetup domainSetup,
            ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
        StringBuilder constraints = new StringBuilder();
        int automatonSize = moduleAutomaton.getAllStates().size();
        for (Pair<Integer> operations : APEUtils.generateDistinctPairs(automatonSize)) {
            int op1 = operations.getFirst();
            int op2 = operations.getSecond();

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

        return constraints.toString();
    }

    /**
     * 
     * Creates a CNF representation of the Constraint:<br>
     * Tools (that belong to the sub-taxonomy) should have all inputs unique.
     * 
     * @param moduleAutomaton
     * @param typeAutomaton
     * @param mappings
     * @return
     */
    public static String useUniqueInputs(
            ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

        StringBuilder constraints = new StringBuilder();
        for (int blockNo = 0; blockNo < typeAutomaton.getLength(); blockNo++) {
            Block inputs = typeAutomaton.getUsedTypesBlock(blockNo);
            Set<Pair<State>> inputPairs = new HashSet<>();
            inputPairs.addAll(APEUtils.getUniquePairs(inputs.getStates()));
            for (State memoryState : typeAutomaton.getAllMemoryStatesUntilBlockNo(blockNo)) {
                for (Pair<State> inputPair : inputPairs) {
                    constraints.append("-")
                            .append(mappings.add(memoryState, inputPair.getFirst(), AtomType.MEM_TYPE_REFERENCE))
                            .append(" ");
                    constraints.append("-")
                            .append(mappings.add(memoryState, inputPair.getSecond(), AtomType.MEM_TYPE_REFERENCE))
                            .append(" 0\n");
                }
            }

        }
        return constraints.toString();
    }

}

// pairs of inputs X1 X2 not ref(x1, O) or not ref(x2, O)