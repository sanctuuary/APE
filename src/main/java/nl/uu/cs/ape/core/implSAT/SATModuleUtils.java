package nl.uu.cs.ape.core.implSAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.ConfigEnum;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxAtom;
import nl.uu.cs.ape.models.satStruc.SLTLxEquivalence;
import nl.uu.cs.ape.models.satStruc.SLTLxFormula;
import nl.uu.cs.ape.models.satStruc.SLTLxNegatedConjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxNegation;
import nl.uu.cs.ape.models.satStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.satStruc.SLTLxImplication;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code ModuleUtils} class is used to encode SAT constraints based on the
 * module annotations.
 *
 * @author Vedran Kasalica
 */
public final class SATModuleUtils {

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private SATModuleUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of CNF constraints regarding the required INPUT
	 *         and OUTPUT types of the modules.
	 */
	public static Set<SLTLxFormula> encodeModuleAnnotations(SATSynthesisEngine synthesisInstance) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		cnfEncoding.addAll(inputCons(synthesisInstance));

		cnfEncoding.addAll(outputCons(synthesisInstance));
		return cnfEncoding;
	}

	/**
	 * Return a CNF formula that preserves the memory structure that is being used
	 * (e.g. 'shared memory'), i.e. ensures that the referenced items are available
	 * according to the mem. structure and that the input type and the referenced
	 * type from the memory represent the same data.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of CNF constraints regarding the required
	 *         memory structure implementation.
	 */
	public static Set<SLTLxFormula> encodeMemoryStructure(SATSynthesisEngine synthesisInstance) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();

		cnfEncoding.addAll(allowDataReferencingCons(synthesisInstance.getTypeAutomaton()));
		cnfEncoding.addAll(enforcingUsageOfGeneratedTypesCons(synthesisInstance));
		cnfEncoding.addAll(enforceDataReferenceRules(synthesisInstance.getDomainSetup(),
				synthesisInstance.getTypeAutomaton()));
		return cnfEncoding;
	}

	
	/**
	 * Return a CNF formula that preserves the dependency between data instances. Instance A depends on Instance B if it was 
	 * computed by using Instance B, directly or indirectly.
	 * 
	 * @param typeAutomaton
	 * @param mappings
	 * @return
	 */
	public static Set<SLTLxFormula> encodeDataInstanceDependencyCons(TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		
		cnfEncoding.addAll(allowDataDependencyCons(typeAutomaton));
		cnfEncoding.addAll(enforceDataDependencyOverModules(typeAutomaton));
		cnfEncoding.addAll(enforceDataDependencyOverDataReferencing(typeAutomaton));
		
		return cnfEncoding;
	}

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Returns the CNF representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 *
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> inputCons(SATSynthesisEngine synthesisInstance) {

		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		/* For each module.. */
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			/* ..which is a Tool.. */
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				/* ..iterate through all the states.. */
				for (State moduleState : synthesisInstance.getModuleAutomaton().getAllStates()) {
					int moduleNo = moduleState.getLocalStateNumber();
					/* ..and for each state and input state of that module state.. */
					List<State> currInputStates = synthesisInstance.getTypeAutomaton().getUsedTypesBlock(moduleNo - 1)
							.getStates();
					List<Type> moduleInputs = module.getModuleInput();
					for (State currInputState : currInputStates) {
						int currInputStateNo = currInputState.getLocalStateNumber();
						/*
						 * ..require data type and/or format to be used in one of the directly preceding
						 * input states, if the data type/format it exists, otherwise use empty type.
						 */
						if (currInputStateNo < moduleInputs.size()) {
							/* Get input type and/or format that are/is required by the tool */
							TaxonomyPredicate currInputType = moduleInputs.get(currInputStateNo);
							/* Encode: if module was used in the module state
							 * the corresponding data and format types need to be provided in input
							 * states */
							cnfEncoding.add(
									new SLTLxImplication(
												new SLTLxAtom(
														AtomType.MODULE, 
														module, 
														moduleState),
												new SLTLxAtom(
														AtomType.USED_TYPE, 
														currInputType, 
														currInputState)));
						} else {
							cnfEncoding.add(
									new SLTLxImplication(
												new SLTLxAtom(
														AtomType.MODULE, 
														module, 
														moduleState),
												new SLTLxAtom(
														AtomType.USED_TYPE, 
														synthesisInstance.getEmptyType(), 
														currInputState)));
						}
					}
				}
			}
		}

		return cnfEncoding;
	}

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link SMTDataType#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 *
	 * @return String representing the constraints required to ensure that the
	 *         {@link SMTDataType#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private static Set<SLTLxFormula> enforceDataReferenceRules(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();

		/* For each type instance */
		for (TaxonomyPredicate currType : domainSetup.getAllTypes().getTypes()) {
			if (currType.isSimplePredicate() || currType.isEmptyPredicate()) {
				/* ..for each state in which type can be used .. */
				for (Block currUsedBlock : typeAutomaton.getUsedTypesBlocks()) {
					for (State currUsedTypeState : currUsedBlock.getStates()) {
						if (!currType.isEmptyPredicate()) {
							/* ..the referenced memory state cannot be null.. */
							cnfEncoding.add(
									new SLTLxNegatedConjunction(
												new SLTLxAtom(
														AtomType.USED_TYPE, 
														currType, 
														currUsedTypeState),
												new SLTLxAtom(
														AtomType.MEM_TYPE_REFERENCE, 
														typeAutomaton.getNullState(), 
														currUsedTypeState)));

							/* ..and for each state in which type can be created in memory .. */
							for (Block memoryBlock : typeAutomaton.getMemoryTypesBlocks()) {
								for (State refMemoryTypeState : memoryBlock.getStates()) {
									/*
									 * Pairs of referenced states have to be of the same types.
									 */
									
									cnfEncoding.add(
											new SLTLxImplication(
													new SLTLxAtom(
															AtomType.MEM_TYPE_REFERENCE, 
															refMemoryTypeState,
															currUsedTypeState),
													new SLTLxEquivalence(
														new SLTLxAtom(
																AtomType.USED_TYPE, 
																currType, 
																currUsedTypeState),
														new SLTLxAtom(
																AtomType.MEMORY_TYPE, 
																currType, 
																refMemoryTypeState)
														)));
								}
							}
							/* If the type is empty the referenced state has to be null. */
						} else {
							
							cnfEncoding.add(
									new SLTLxImplication(
												new SLTLxAtom(
														AtomType.USED_TYPE, 
														currType, 
														currUsedTypeState),
												new SLTLxAtom(
														AtomType.MEM_TYPE_REFERENCE, 
														typeAutomaton.getNullState(), 
														currUsedTypeState)));
							
						}
					}
				}
			}
		}

		return cnfEncoding;
	}

	/**
	 * Generate constraints that ensure that the all tool inputs can reference data
	 * that is available in memory at the time.
	 *
	 * <br>
	 * Return the CNF representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Shared Memory Approach.
	 *
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> allowDataReferencingCons(TypeAutomaton typeAutomaton) {

		// setting up input constraints (Shared Memory Approach)
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		/** For each input state... */
		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/*
				 * Used state can reference states that are currently in the shared memory, i.e.
				 * already created.
				 */
				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber);
				possibleMemStates.add(typeAutomaton.getNullState());
				Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();
				for (State exictingMemState : possibleMemStates) {
					allPossibilities.add(new SLTLxAtom(AtomType.MEM_TYPE_REFERENCE, exictingMemState, currInputState));
				}
				cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
				

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<PredicateLabel> pair : getPredicatePairs(possibleMemStates)) {
					cnfEncoding.add(
							new SLTLxNegatedConjunction(
										new SLTLxAtom(
												AtomType.MEM_TYPE_REFERENCE, 
												pair.getFirst(), 
												currInputState),
										new SLTLxAtom(
												AtomType.MEM_TYPE_REFERENCE, 
												pair.getSecond(), 
												currInputState)));
					
				}

				/*
				 * Used state cannot reference states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					
					cnfEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
										AtomType.MEM_TYPE_REFERENCE, 
										nonExictingMemState,
										currInputState)));
				}
			}
		}

		return cnfEncoding;
	}

	/**
	 * Generate constraints that ensure that the data instances can depend on
	 * instances that are available in memory, and that each data instance depends
	 * on itself.
	 * 
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> allowDataDependencyCons(TypeAutomaton typeAutomaton) {

		// setting up dependency constraints
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		/** For each input state... */
		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/* Input state does not depend on the null state */
				cnfEncoding.add(
						new SLTLxNegation(
								new SLTLxAtom(
									AtomType.R_RELATON, 
									currInputState,
									typeAutomaton.getNullState())));
				/*
				 * Tool input state can depend on states that are currently in the shared
				 * memory, i.e. already created.
				 */
//				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber);
//				for (State exictingMemState : possibleMemStates) {
//					constraints = constraints
//							.append(mappings.add(exictingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//							.append(" ");
//				}
//				if(possibleMemStates.size() > 0) {
//					constraints.append(" 0\n");
//				}

				/*
				 * Used state cannot depend on states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					cnfEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
										AtomType.R_RELATON, 
										nonExictingMemState,
										currInputState)));
				}
			}
		}
		/** For each memory state... */
		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currMemState : currBlock.getStates()) {
				/* Memory state depends on itself */
				cnfEncoding.add(
							new SLTLxAtom(
								AtomType.R_RELATON, 
								currMemState,
								currMemState));
				/* ..and does not depend on the null state */ 
			cnfEncoding.add(
						new SLTLxNegation(
								new SLTLxAtom(
									AtomType.R_RELATON, 
									currMemState,
									typeAutomaton.getNullState())));
				/*
				 * and the memory state can depend on states that are currently in the shared
				 * memory, i.e. already created.
				 */
//				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber - 1);
//				for (State exictingMemState : possibleMemStates) {
//					constraints = constraints
//							.append(mappings.add(exictingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//							.append(" ");
//				}
//				if(possibleMemStates.size() > 0) {
//					constraints.append(" 0\n");
//				}

				/*
				 * Memory state cannot depend on states that are yet to be created or that were
				 * just, i.e. not yet in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber - 1)) {
					if (!nonExictingMemState.equals(currMemState)) {
						cnfEncoding.add(
								new SLTLxNegation(
										new SLTLxAtom(
											AtomType.R_RELATON, 
											nonExictingMemState,
											currMemState)));
					}
				}
			}
		}
		return cnfEncoding;
	}

	/**
	 * Generate constraints that ensure that tool inputs that reference data in
	 * memory depend on the same data as the referenced data instance.
	 * 
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> enforceDataDependencyOverDataReferencing(TypeAutomaton typeAutomaton) {

		// setting up dependency constraints
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		/** For each input state... */
		for (Block currInputBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currInputBlock.getBlockNumber();
			for (State currInputState : currInputBlock.getStates()) {
				/*
				 * and for each available memory state..
				 */
				for (int i = 0; i <= blockNumber; i++) {
					Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i);
					for (State currMemState : currMemBlock.getStates()) {
						/* ..if the input state references the memory state.. */
						for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
							cnfEncoding.add(
										new SLTLxImplication(
												new SLTLxConjunction(
														new SLTLxAtom(
																AtomType.MEM_TYPE_REFERENCE, 
																currMemState, 
																currInputState),
														new SLTLxAtom(
																AtomType.R_RELATON, 
																existingMemState,
																currMemState)
														),
												new SLTLxAtom(
														AtomType.R_RELATON, 
														existingMemState, 
														currInputState)
												));

							// and vice versa (if it does not depend on it, neither will the tool input
							// state)

							cnfEncoding.add(
									new SLTLxImplication(
											new SLTLxConjunction(
													new SLTLxAtom(
															AtomType.MEM_TYPE_REFERENCE, 
															currMemState, 
															currInputState),
													new SLTLxAtom(
															AtomType.R_RELATON, 
															existingMemState, 
															currInputState)
													),
											new SLTLxAtom(
													AtomType.R_RELATON, 
													existingMemState,
													currMemState)
											));
						}
					}
				}

				// Empty inputs have no data dependencies
				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber)) {
					cnfEncoding.add(new SLTLxNegatedConjunction(
							new SLTLxAtom(
									AtomType.MEM_TYPE_REFERENCE, 
									typeAutomaton.getNullState(), 
									currInputState),
							new SLTLxAtom(
									AtomType.R_RELATON, 
									existingMemState, 
									currInputState)
							));
				}
			}
		}
		return cnfEncoding;
	}

	private static Set<SLTLxFormula> enforceDataDependencyOverModules(TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		/** For tool inputs and outputs */
		for (int i = 0; i < typeAutomaton.getUsedTypesBlocks().size() - 1; i++) {
			Block currInputBlock = typeAutomaton.getUsedTypesBlock(i);
			Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i + 1);

			// For each output state
			for (State currMemState : currMemBlock.getStates()) {
				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
					// if the type depends on a data from the memory
					Set<SLTLxFormula> cnfDependency = new HashSet<SLTLxFormula>();
					
					cnfDependency.add(
							new SLTLxNegation(
									new SLTLxAtom(
											AtomType.R_RELATON, 
											existingMemState, 
											currMemState)));
					// one of the tool inputs does as well
					for (State currInputState : currInputBlock.getStates()) {
						cnfDependency.add(
								new SLTLxAtom(
										AtomType.R_RELATON, 
										existingMemState, 
										currInputState));
					}
					cnfEncoding.add(new SLTLxDisjunction(cnfDependency));

					// ..and vice versa, dependence of the input types is inherited to the outputs

					for (State currInputState : currInputBlock.getStates()) {
						cnfEncoding.add(
								new SLTLxImplication(
										new SLTLxAtom(
												AtomType.R_RELATON, 
												existingMemState, 
												currInputState),
										new SLTLxAtom(
												AtomType.R_RELATON, 
												existingMemState, 
												currMemState)));
					}
				}
			}
		}
		return cnfEncoding;
	}

	/**
	 * Function returns the encoding that ensures that tool outputs are used
	 * according to the configuration, e.g. if the config specifies that all
	 * workflow inputs have to be used, then each of them has to be referenced at
	 * least once.
	 *
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> enforcingUsageOfGeneratedTypesCons(SATSynthesisEngine synthesisInstance) {

		Type emptyType = synthesisInstance.getEmptyType();
		TypeAutomaton typeAutomaton = synthesisInstance.getTypeAutomaton();
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
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
				if (synthesisInstance.getConfig().getUseWorkflowInput() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();

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
						cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
					}
					/* In case that at least one workflow input need to be used */
				} else if (synthesisInstance.getConfig().getUseWorkflowInput() == ConfigEnum.ONE) {
					Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();
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
					cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
				}
				/* In case that none of the workflow input has to be used, do nothing. */
			} else {
				/* In case that all generated data need to be used. */
				if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();
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
						cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
					}
					/*
					 * In case that at least one of the generated data instances per tool need to be
					 * used.
					 */
				} else if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ONE) {
					Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();
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
					cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
				}
				/* In case that none generated data has to be used do nothing. */

			}
		}

		return cnfEncoding;
	}

	/**
	 * Return the CNF representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.<br>
	 * Generate constraints that preserve tool outputs.
	 *
	 * @return String representation of constraints.
	 */
	private static Set<SLTLxFormula> outputCons(SATSynthesisEngine synthesisInstance) {

		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();

		// for each module
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			// that is a Tool
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				// iterate through all the states
				for (State moduleState : synthesisInstance.getModuleAutomaton().getAllStates()) {
					int moduleNo = moduleState.getLocalStateNumber();
					// and for each state and output state of that module state
					List<State> currOutputStates = synthesisInstance.getTypeAutomaton().getMemoryTypesBlock(moduleNo)
							.getStates();
					List<Type> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							TaxonomyPredicate outputType = moduleOutputs.get(i);
							// single output
							// if module was used in the module state
							// require type and/or format to be used in one of the directly
							// proceeding output states if it exists, otherwise use empty type
							
							cnfEncoding.add(
									new SLTLxImplication(
												new SLTLxAtom(
														AtomType.MODULE, 
														module, 
														moduleState),
												new SLTLxAtom(
														AtomType.MEMORY_TYPE, 
														outputType, 
														currOutputStates.get(i))));

						} else {
							cnfEncoding.add(
									new SLTLxImplication(
												new SLTLxAtom(
														AtomType.MODULE, 
														module, 
														moduleState),
												new SLTLxAtom(
														AtomType.MEMORY_TYPE, 
														synthesisInstance.getEmptyType(), 
														currOutputStates.get(i))));
						}
					}
				}
			}
		}

		return cnfEncoding;
	}

	/**
	 * Generating the mutual exclusion constraints for each pair of tools from
	 * modules (excluding abstract modules from the taxonomy) in each state of
	 * moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return The String representation of constraints.
	 */
	
	public static Set<SLTLxFormula> moduleMutualExclusion(AllModules allModules, ModuleAutomaton moduleAutomaton) {

		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();

		for (Pair<PredicateLabel> pair : allModules.getSimplePairs()) {
			for (State moduleState : moduleAutomaton.getAllStates()) {
				cnfEncoding.add(
						new SLTLxNegatedConjunction(
									new SLTLxAtom(
											AtomType.MODULE, 
											pair.getFirst(), 
											moduleState),
									new SLTLxAtom(
											AtomType.MODULE, 
											pair.getSecond(), 
											moduleState)));
			}
		}

		return cnfEncoding;
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of constraints.
	 */
	public static Set<SLTLxFormula> moduleMandatoryUsage(AllModules allModules, ModuleAutomaton moduleAutomaton) {
		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		
		if (allModules.getModules().isEmpty()) {
			System.err.println("No tools were I/O annotated.");
			return cnfEncoding;
		}

		for (State moduleState : moduleAutomaton.getAllStates()) {
			Set<SLTLxFormula> allPossibilities = new HashSet<SLTLxFormula>();
			
			for (TaxonomyPredicate tool : allModules.getModules()) {
				if (tool instanceof Module) {
					allPossibilities.add(
							new SLTLxAtom(
								AtomType.MODULE, 
								tool, 
								moduleState));
				}
			}
			cnfEncoding.add(new SLTLxDisjunction(allPossibilities));
		}

		return cnfEncoding;
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param currModule      Module that should be used.
	 * @param moduleAutomaton Module automaton.
	 * @param mappings        Mapping function.
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications.
	 */
	public static Set<SLTLxFormula> moduleEnforceTaxonomyStructure(AllModules allModules, TaxonomyPredicate currModule,
			ModuleAutomaton moduleAutomaton) {

		Set<SLTLxFormula> cnfEncoding = new HashSet<SLTLxFormula>();
		for (State moduleState : moduleAutomaton.getAllStates()) {
			cnfEncoding.addAll(moduleEnforceTaxonomyStructureForState(allModules, currModule, moduleState));
		}
		return cnfEncoding;
	}

	/**
	 * Providing the recursive method used in
	 * {@link #moduleEnforceTaxonomyStructure}.
	 *
	 * @param allModules  All the modules.
	 * @param currModule  Module that should be used.
	 * @param moduleState State in which the module should be used.
	 * @param mappings    Mapping function.
	 */
	private static Set<SLTLxFormula> moduleEnforceTaxonomyStructureForState(AllModules allModules, TaxonomyPredicate currModule, State moduleState) {
		SLTLxAtom superModuleState = new SLTLxAtom(AtomType.MODULE, currModule, moduleState);

		Set<SLTLxFormula> fullCNFEncoding = new HashSet<SLTLxFormula>();
		Set<SLTLxFormula> currCNFEncoding = new HashSet<SLTLxFormula>();
		currCNFEncoding.add(
				new SLTLxNegation(superModuleState));

		List<SLTLxAtom> subModulesStates = new ArrayList<SLTLxAtom>();
		if (!(currModule.getSubPredicates() == null || currModule.getSubPredicates().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (TaxonomyPredicate subModule : APEUtils.safe(currModule.getSubPredicates())) {
				if (subModule == null) {
					System.out.println("Null error: " + currModule.getPredicateID() + " ->"
							+ currModule.getSubPredicates().toString());
				}
				SLTLxAtom subModuleState = new SLTLxAtom(AtomType.MODULE,subModule, moduleState);
				currCNFEncoding.add(subModuleState);
				subModulesStates.add(subModuleState);
				
				fullCNFEncoding.addAll(moduleEnforceTaxonomyStructureForState(allModules, subModule, moduleState));
			}
			fullCNFEncoding.add(new SLTLxDisjunction(currCNFEncoding));
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (SLTLxAtom subModuleState : subModulesStates) {
				fullCNFEncoding.add(
						new SLTLxImplication(
								subModuleState,
								superModuleState));
			}
		}
		
		return fullCNFEncoding;
	}

	/**
	 * Gets predicate pairs.
	 *
	 * @param predicateList List of predicates.
	 * @return A a list of pairs of tools from modules. Note that the abstract
	 *         modules are not returned, only the unique pairs of modules that are
	 *         representing actual tools.
	 */
	public static List<Pair<PredicateLabel>> getPredicatePairs(List<? extends PredicateLabel> predicateList) {
		List<Pair<PredicateLabel>> pairs = new ArrayList<Pair<PredicateLabel>>();

		for (int i = 0; i < predicateList.size() - 1; i++) {
			for (int j = i + 1; j < predicateList.size(); j++) {

				pairs.add(new Pair<PredicateLabel>(predicateList.get(i), predicateList.get(j)));
			}
		}

		return pairs;
	}
}