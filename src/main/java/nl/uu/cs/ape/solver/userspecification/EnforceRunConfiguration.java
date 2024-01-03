package nl.uu.cs.ape.solver.userspecification;

import java.util.HashSet;
import java.util.Set;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.ConfigEnum;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.solver.domainconfiguration.Domain;

public class EnforceRunConfiguration {

    /**
     * Function returns the encoding that ensures that tool outputs are used
     * according to the configuration, e.g. if the config specifies that all
     * workflow inputs have to be used, then each of them has to be referenced at
     * least once.
     * 
     * @param synthesisInstance - instance of the synthesis engine
     *
     * @return Set of SLTLx formulas that represent the constraints.
    private static Set<SLTLxFormula> usageOfGeneratedTypes(Domain domain, ModuleAutomaton moduleAutomaton,
            TypeAutomaton typeAutomaton) {

        Type emptyType = synthesisInstance.getEmptyType();
        Set<SLTLxFormula> fullEncoding = new HashSet<>();
        /*
         * Setting up the constraints that ensure usage of the generated types in the
         * memory, (e.g. all workflow inputs and at least one of each of the tool
         * outputs needs to be used in the program, unless they are empty.)
         */
        for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
            int blockNumber = currBlock.getBlockNumber();
            /* If the memory is provided as input */
            if (blockNumber == 0) {
                /* In case that all workflow inputs need to be used */
                if (synthesisInstance.getRunConfig().getUseWorkflowInput() == ConfigEnum.ALL) {
                    for (State currMemoryState : currBlock.getStates()) {
                        Set<SLTLxFormula> allPossibilities = new HashSet<>();

                        allPossibilities.add(
                                new SLTLxAtom(
                                        AtomType.MEMORY_TYPE,
                                        emptyType,
                                        currMemoryState));

                        for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEM_TYPE_REFERENCE,
                                            currMemoryState,
                                            inputState));
                        }
                        fullEncoding.add(new SLTLxDisjunction(allPossibilities));
                    }
                    /* In case that at least one workflow input need to be used */
                } else if (synthesisInstance.getRunConfig().getUseWorkflowInput() == ConfigEnum.ONE) {
                    Set<SLTLxFormula> allPossibilities = new HashSet<>();
                    for (State currMemoryState : currBlock.getStates()) {
                        if (currMemoryState.getLocalStateNumber() == 0) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEMORY_TYPE,
                                            emptyType,
                                            currMemoryState));
                        }
                        for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEM_TYPE_REFERENCE,
                                            currMemoryState,
                                            inputState));
                        }
                    }
                    fullEncoding.add(new SLTLxDisjunction(allPossibilities));
                }
                /* In case that none of the workflow input has to be used, do nothing. */
            } else {
                /* In case that all generated data need to be used. */
                if (synthesisInstance.getRunConfig().getUseAllGeneratedData() == ConfigEnum.ALL) {
                    for (State currMemoryState : currBlock.getStates()) {
                        Set<SLTLxFormula> allPossibilities = new HashSet<>();
                        allPossibilities.add(
                                new SLTLxAtom(
                                        AtomType.MEMORY_TYPE,
                                        emptyType,
                                        currMemoryState));
                        for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEM_TYPE_REFERENCE,
                                            currMemoryState,
                                            inputState));
                        }
                        fullEncoding.add(new SLTLxDisjunction(allPossibilities));
                    }
                    /*
                     * In case that at least one of the generated data instances per tool need to be
                     * used.
                     */
                } else if (synthesisInstance.getRunConfig().getUseAllGeneratedData() == ConfigEnum.ONE) {
                    Set<SLTLxFormula> allPossibilities = new HashSet<>();
                    for (State currMemoryState : currBlock.getStates()) {
                        if (currMemoryState.getLocalStateNumber() == 0) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEMORY_TYPE,
                                            emptyType,
                                            currMemoryState));
                        }
                        for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
                            allPossibilities.add(
                                    new SLTLxAtom(
                                            AtomType.MEM_TYPE_REFERENCE,
                                            currMemoryState,
                                            inputState));
                        }
                    }
                    fullEncoding.add(new SLTLxDisjunction(allPossibilities));
                }
                /* In case that none generated data has to be used do nothing. */

            }
        }

        return fullEncoding;
    }}
