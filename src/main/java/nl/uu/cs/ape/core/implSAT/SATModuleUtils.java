package nl.uu.cs.ape.core.implSAT;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.models.*;
import nl.uu.cs.ape.models.enums.ConfigEnum;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
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
	public static String encodeModuleAnnotations(SATSynthesisEngine synthesisInstance) {
		StringBuilder constraints = new StringBuilder();
		constraints.append(inputCons(synthesisInstance));

		constraints.append(outputCons(synthesisInstance));
		return constraints.toString();
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
	public static String encodeMemoryStructure(SATSynthesisEngine synthesisInstance) {
		StringBuilder constraints = new StringBuilder();

		constraints.append(allowDataReferencingCons(synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
		constraints.append(enforcingUsageOfGeneratedTypesCons(synthesisInstance));
		constraints.append(enforceDataReferenceRules(synthesisInstance.getDomainSetup(),
				synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
		return constraints.toString();
	}

	
	/**
	 * Return a CNF formula that preserves the dependency between data instances. Instance A depends on Instance B if it was 
	 * computed by using Instance B, directly or indirectly.
	 * 
	 * @param typeAutomaton
	 * @param mappings
	 * @return
	 */
	public static String encodeDataInstanceDependencyCons(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		
		constraints.append(allowDataDependencyCons(typeAutomaton, mappings));
		constraints.append(enforceDataDependencyOverModules(typeAutomaton, mappings));
		constraints.append(enforceDataDependencyOverDataReferencing(typeAutomaton, mappings));
		
		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Returns the CNF representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 *
	 * @return String representation of constraints.
	 */
	private static String inputCons(SATSynthesisEngine synthesisInstance) {

		StringBuilder constraints = new StringBuilder();
		SATAtomMappings mappings = synthesisInstance.getMappings();
		/* For each module.. */
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			/* ..which is a Tool.. */
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				/* ..iterate through all the states.. */
				for (State moduleState : synthesisInstance.getModuleAutomaton().getAllStates()) {
					int moduleNo = moduleState.getStateNumber();
					/* ..and for each state and input state of that module state.. */
					List<State> currInputStates = synthesisInstance.getTypeAutomaton().getUsedTypesBlock(moduleNo - 1)
							.getStates();
					List<Type> moduleInputs = module.getModuleInput();
					for (State currInputState : currInputStates) {
						int currInputStateNo = currInputState.getStateNumber();
						/*
						 * ..require data type and/or format to be used in one of the directly preceding
						 * input states, if the data type/format it exists, otherwise use empty type.
						 */
						if (currInputStateNo < moduleInputs.size()) {
							/* Get input type and/or format that are/is required by the tool */
							TaxonomyPredicate currInputType = moduleInputs.get(currInputStateNo);
							/* Encode: if module was used in the module state */
							constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							/*
							 * .. the corresponding data and format types need to be provided in input
							 * states
							 */
							constraints = constraints
									.append(mappings.add(currInputType, currInputState, WorkflowElement.USED_TYPE))
									.append(" 0\n");
						} else {
							constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints.append(mappings.add(synthesisInstance.getEmptyType(),
									currInputState, WorkflowElement.USED_TYPE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link WorkflowElement#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 *
	 * @return String representing the constraints required to ensure that the
	 *         {@link WorkflowElement#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private static String enforceDataReferenceRules(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton,
			SATAtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();

		/* For each type instance */
		for (TaxonomyPredicate currType : domainSetup.getAllTypes().getTypes()) {
			if (currType.isSimplePredicate() || currType.isEmptyPredicate()) {
				/* ..for each state in which type can be used .. */
				for (Block currUsedBlock : typeAutomaton.getUsedTypesBlocks()) {
					for (State currUsedTypeState : currUsedBlock.getStates()) {
						if (!currType.isEmptyPredicate()) {
							/* ..the referenced memory state cannot be null.. */
							constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints.append("-").append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");

							/* ..and for each state in which type can be created in memory .. */
							for (Block memoryBlock : typeAutomaton.getMemoryTypesBlocks()) {
								for (State refMemoryTypeState : memoryBlock.getStates()) {
									/*
									 * If the type (currType) is used as an input for a tool (in state
									 * currUsedTypeState)..
									 */
									constraints.append("-").append(
											mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
											.append(" ");
									/*
									 * ..and the state is referencing a memory state where the type was created
									 * (refMemoryTypeState)
									 */
									constraints.append("-").append(mappings.add(refMemoryTypeState,
											currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" ");
									/* ..the type has to be generated in the the referenced memory type state. */
									constraints.append(
											mappings.add(currType, refMemoryTypeState, WorkflowElement.MEMORY_TYPE))
											.append(" 0\n");

								}
							}
							/* If the type is empty the referenced state has to be null. */
						} else {
							constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints.append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
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
	private static String allowDataReferencingCons(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

		// setting up input constraints (Shared Memory Approach)
		StringBuilder constraints = new StringBuilder();
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
				for (State exictingMemState : possibleMemStates) {
					constraints = constraints
							.append(mappings.add(exictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
				}
				constraints.append(" 0\n");

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<PredicateLabel> pair : getPredicatePairs(possibleMemStates)) {
					constraints.append("-")
							.append(mappings.add(pair.getFirst(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
					constraints.append("-")
							.append(mappings.add(pair.getSecond(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}

				/*
				 * Used state cannot reference states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					constraints.append("-").append(
							mappings.add(nonExictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the data instances can depend on
	 * instances that are available in memory, and that each data instance depends
	 * on itself.
	 * 
	 * @return String representation of constraints.
	 */
	private static String allowDataDependencyCons(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

		// setting up dependency constraints
		StringBuilder constraints = new StringBuilder();
		/** For each input state... */
		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/* Input state does not depend on the null state */
				constraints.append("-").append(
						mappings.add(currInputState, typeAutomaton.getNullState(), WorkflowElement.TYPE_DEPENDENCY))
						.append(" 0\n");
				/*
				 * Tool input state can depend on states that are currently in the shared
				 * memory, i.e. already created.
				 */
//				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber);
//				for (State exictingMemState : possibleMemStates) {
//					constraints = constraints
//							.append(mappings.add(exictingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
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
					constraints.append("-")
							.append(mappings.add(nonExictingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
							.append(" 0\n");
				}
			}
		}
		/** For each memory state... */
		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currMemState : currBlock.getStates()) {
				/* Memory state depends on itself */
				constraints = constraints
						.append(mappings.add(currMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
						.append(" 0\n");
				/* ..and does not depend on the null state */
				constraints.append("-").append(
						mappings.add(currMemState, typeAutomaton.getNullState(), WorkflowElement.TYPE_DEPENDENCY))
						.append(" 0\n");
				/*
				 * and the memory state can depend on states that are currently in the shared
				 * memory, i.e. already created.
				 */
//				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber - 1);
//				for (State exictingMemState : possibleMemStates) {
//					constraints = constraints
//							.append(mappings.add(exictingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
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
						constraints.append("-").append(
								mappings.add(nonExictingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
								.append(" 0\n");
					}
				}
			}
		}
		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that tool inputs that reference data in
	 * memory depend on the same data as the referenced data instance.
	 * 
	 * @return String representation of constraints.
	 */
	private static String enforceDataDependencyOverDataReferencing(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {

		// setting up dependency constraints
		StringBuilder constraints = new StringBuilder();
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
						for (State exictingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
							constraints.append("-").append(
									mappings.add(currMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
							/** ..each data dependency over the memory state.. */
							constraints.append("-").append(
									mappings.add(exictingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
									.append(" ");
							/** ..is the dependency of the tool input state. */
							constraints.append(
									mappings.add(exictingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
									.append(" 0\n");

							// and vice versa (if it does not depend on it, neither will the tool input
							// state)

							constraints.append("-").append(
									mappings.add(currMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
							/** ..each data dependency over the memory state.. */
							constraints.append(
									mappings.add(exictingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
									.append(" ");
							/** ..is the dependency of the tool input state. */
							constraints.append("-").append(
									mappings.add(exictingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
									.append(" 0\n");
						}
					}
				}

				// Empty inputs have no data dependencies
				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber)) {
					constraints.append("-").append(mappings.add(typeAutomaton.getNullState(),
							currInputState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" ");

					constraints.append("-")
							.append(mappings.add(existingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
							.append(" 0\n");
				}
			}
		}
		return constraints.toString();
	}

	private static String enforceDataDependencyOverModules(TypeAutomaton typeAutomaton, SATAtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();
		/** For tool inputs and outputs */
		for (int i = 0; i < typeAutomaton.getUsedTypesBlocks().size() - 1; i++) {
			Block currInputBlock = typeAutomaton.getUsedTypesBlock(i);
			Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i + 1);

			// For each output state
			for (State currMemState : currMemBlock.getStates()) {
				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
					// if the type depends on a data from the memory
					constraints.append("-")
							.append(mappings.add(existingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
							.append(" ");
					// one of the tool inputs does as well
					for (State currInputState : currInputBlock.getStates()) {
						constraints = constraints
								.append(mappings.add(existingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
								.append(" ");
					}
					constraints.append(" 0\n");

					// ..and vice versa, dependence of the input types is inherited to the outputs

					for (State currInputState : currInputBlock.getStates()) {
						constraints.append("-")
								.append(mappings.add(existingMemState, currInputState, WorkflowElement.TYPE_DEPENDENCY))
								.append(" ");

						constraints = constraints
								.append(mappings.add(existingMemState, currMemState, WorkflowElement.TYPE_DEPENDENCY))
								.append(" 0\n");
					}
				}
			}
		}
		return constraints.toString();
	}

	/**
	 * Function returns the encoding that ensures that tool outputs are used
	 * according to the configuration, e.g. if the config specifies that all
	 * workflow inputs have to be used, then each of them has to be referenced at
	 * least once.
	 *
	 * @return String representation of constraints.
	 */
	private static String enforcingUsageOfGeneratedTypesCons(SATSynthesisEngine synthesisInstance) {

		SATAtomMappings mappings = synthesisInstance.getMappings();
		Type emptyType = synthesisInstance.getEmptyType();
		TypeAutomaton typeAutomaton = synthesisInstance.getTypeAutomaton();
		StringBuilder constraints = new StringBuilder();
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
						constraints = constraints
								.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
								.append(" ");
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints.append(" 0\n");
					}
					/* In case that at least one workflow input need to be used */
				} else if (synthesisInstance.getConfig().getUseWorkflowInput() == ConfigEnum.ONE) {
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getStateNumber() == 0) {
							constraints = constraints
									.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
									.append(" ");
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {

							constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints.append(" 0\n");
				}
				/* In case that none of the workflow input has to be used, do nothing. */
			} else {
				/* In case that all generated data need to be used. */
				if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						constraints = constraints
								.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
								.append(" ");
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints.append(" 0\n");
					}
					/*
					 * In case that at least one of the generated data instances per tool need to be
					 * used.
					 */
				} else if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ONE) {
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getStateNumber() == 0) {
							constraints = constraints
									.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
									.append(" ");
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints.append(" 0\n");
				}
				/* In case that none generated data has to be used do nothing. */

			}
		}

		return constraints.toString();
	}

	/**
	 * Return the CNF representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.<br>
	 * Generate constraints that preserve tool outputs.
	 *
	 * @return String representation of constraints.
	 */
	private static String outputCons(SATSynthesisEngine synthesisInstance) {

		SATAtomMappings mappings = synthesisInstance.getMappings();
		StringBuilder constraints = new StringBuilder();

		// for each module
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			// that is a Tool
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				// iterate through all the states
				for (State moduleState : synthesisInstance.getModuleAutomaton().getAllStates()) {
					int moduleNo = moduleState.getStateNumber();
					// and for each state and output state of that module state
					List<State> currOutputStates = synthesisInstance.getTypeAutomaton().getMemoryTypesBlock(moduleNo)
							.getStates();
					List<Type> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							TaxonomyPredicate outputType = moduleOutputs.get(i);
							// single output
							// if module was used in the module state
							constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							// require type and/or format to be used in one of the directly
							// proceeding output states if it exists, otherwise use empty type

							constraints.append(
									mappings.add(outputType, currOutputStates.get(i), WorkflowElement.MEMORY_TYPE))
									.append(" 0\n");
						} else {
							constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints.append(mappings.add(synthesisInstance.getEmptyType(),
									currOutputStates.get(i), WorkflowElement.MEMORY_TYPE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
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
	public static String moduleMutualExclusion(AllModules allModules, ModuleAutomaton moduleAutomaton,
			SATAtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();

		for (Pair<PredicateLabel> pair : allModules.getSimplePairs()) {
			for (State moduleState : moduleAutomaton.getAllStates()) {
				constraints.append("-")
						.append(mappings.add(pair.getFirst(), moduleState, WorkflowElement.MODULE)).append(" ");
				constraints.append("-")
						.append(mappings.add(pair.getSecond(), moduleState, WorkflowElement.MODULE)).append(" 0\n");
			}
		}

		return constraints.toString();
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
	public static String moduleMandatoryUsage(AllModules allModules, ModuleAutomaton moduleAutomaton,
			SATAtomMappings mappings) {
		if (allModules.getModules().isEmpty()) {
			System.err.println("No tools were I/O annotated.");
			return "";
		}
		StringBuilder constraints = new StringBuilder();

		for (State moduleState : moduleAutomaton.getAllStates()) {
			for (TaxonomyPredicate tool : allModules.getModules()) {
				if (tool instanceof Module) {
					constraints.append(mappings.add(tool, moduleState, WorkflowElement.MODULE))
							.append(" ");
				}
			}
			constraints.append(" 0\n");
		}

		return constraints.toString();
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
	public static String moduleEnforceTaxonomyStructure(AllModules allModules, TaxonomyPredicate currModule,
			ModuleAutomaton moduleAutomaton, SATAtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();
		for (State moduleState : moduleAutomaton.getAllStates()) {
			constraints = constraints
					.append(moduleEnforceTaxonomyStructureForState(allModules, currModule, mappings, moduleState));
		}
		return constraints.toString();
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
	private static String moduleEnforceTaxonomyStructureForState(AllModules allModules, TaxonomyPredicate currModule,
			SATAtomMappings mappings, State moduleState) {
		String superModuleState = mappings.add(currModule, moduleState, WorkflowElement.MODULE).toString();

		StringBuilder constraints = new StringBuilder();
		StringBuilder currConstraint = new StringBuilder("-").append(superModuleState).append(" ");

		List<String> subModulesStates = new ArrayList<String>();
		if (!(currModule.getSubPredicates() == null || currModule.getSubPredicates().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (TaxonomyPredicate subModule : APEUtils.safe(currModule.getSubPredicates())) {
				if (subModule == null) {
					System.out.println("Null error: " + currModule.getPredicateID() + " ->"
							+ currModule.getSubPredicates().toString());
				}
				String subModule_State = mappings.add(subModule, moduleState, WorkflowElement.MODULE).toString();
				currConstraint.append(subModule_State).append(" ");
				subModulesStates.add(subModule_State);
				constraints = constraints
						.append(moduleEnforceTaxonomyStructureForState(allModules, subModule, mappings, moduleState));
			}
			currConstraint.append("0\n");
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (String subModule_State : subModulesStates) {
				currConstraint.append("-").append(subModule_State).append(" ").append(superModuleState)
						.append(" 0\n");
			}
			return currConstraint.append(constraints).toString();
		} else {
			return "";
		}
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