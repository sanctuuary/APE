/**
 * 
 */
package nl.uu.cs.ape.sat.models.SATEncodingUtils;

import java.util.ArrayList;
import java.util.List;

import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Pair;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code ModuleUtils} class is used to encode SAT constraints  based on the module annotations.
 *
 * @author Vedran Kasalica
 *
 */
public final class SATModuleUtils {

	/** Private constructor is used to to prevent instantiation. */
	private SATModuleUtils() {
		throw new UnsupportedOperationException();
	}

	
	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 * 
	 *  @param synthesisInstance - a specific synthesis run that contains all the information specific for it
	 * @return {@link String} representation of CNF constraints regarding the required
	 *         INPUT and OUTPUT types of the modules
	 */
	public static String encodeModuleAnnotations(SAT_SynthesisEngine synthesisInstance) {
		StringBuilder constraints = new StringBuilder();
		constraints = constraints.append(inputCons(synthesisInstance));
		
		constraints = constraints.append(outputCons(synthesisInstance));
		return constraints.toString();
	}
	
	/**
	 * Return a CNF formula that preserves the memory structure that is being used (e.g. 'shared memory'), i.e. ensures that the 
	 * referenced items are available according to the mem. structure and that the input type and the referenced type from the memory
	 * represent the same data.
	 * 
	 * @param synthesisInstance - a specific synthesis run that contains all the information specific for it
	 * @return {@link String} representation of CNF constraints regarding the required memory structure implementation
	 */
	public static String encodeMemoryStructure(SAT_SynthesisEngine synthesisInstance) {
		StringBuilder constraints = new StringBuilder();
		
		if (synthesisInstance.getConfig().getSharedMemory()) {
			/* Case when the using shared memory system. */
			constraints = constraints.append(inputSharedMemCons(synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
			constraints = constraints.append(enforcingUsageOfGeneratedTypesSharedMemCons(synthesisInstance));
		} else {
			/* Case when the using message passing memory system. */
			constraints = constraints.append(inputMsgPassingCons(synthesisInstance.getDomainSetup().getAllModules(), synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
			constraints = constraints.append(enforcingUsageOfGeneratedTypesMsgPassingCons(synthesisInstance));
		}

		constraints = constraints.append(generalReferenceCons(synthesisInstance.getDomainSetup(), synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
		return constraints.toString();
	}
	
	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Return the CNF representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton. <br>
	 * <br>
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param emptyType       - represents absence of types
	 * @param mappings
	 * @return String representation of constraints
	 */
	private static String inputCons(SAT_SynthesisEngine synthesisInstance) {

		StringBuilder constraints = new StringBuilder();
		AtomMappings mappings = synthesisInstance.getMappings();
		/* For each module.. */
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			/* ..which is a Tool.. */
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				/* ..iterate through all the states.. */
				for (State moduleState : synthesisInstance.getModuleAutomaton().getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					/* ..and for each state and input state of that module state.. */
					List<State> currInputStates = synthesisInstance.getTypeAutomaton().getUsedTypesBlock(moduleNo - 1).getStates();
					List<DataInstance> moduleInputs = module.getModuleInput();
					for (State currInputState : currInputStates) {
						int currInputStateNo = currInputState.getStateNumber();
						/*
						 * ..require data type and/or format to be used in one of the directly preceding
						 * input states, if the data type/format it exists, otherwise use empty type.
						 */
						if (currInputStateNo < moduleInputs.size()) {
							/* Get input type and/or format that are/is required by the tool */
							for (TaxonomyPredicate currInputType : moduleInputs.get(currInputStateNo).getTypes()) {
								/* Encode: if module was used in the module state */
								constraints = constraints.append("-")
										.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
								/*
								 * .. the corresponding data and format types need to be provided in input
								 * states
								 */
								constraints = constraints
										.append(mappings.add(currInputType, currInputState, WorkflowElement.USED_TYPE))
										.append(" 0\n");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints = constraints
									.append(mappings.add(synthesisInstance.getEmptyType(), currInputState, WorkflowElement.USED_TYPE))
									.append(" 0\n");
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
	 * @param allTypes
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representing the constraints required to ensure that the
	 *         {@link WorkflowElement#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private static String generalReferenceCons(APEDomainSetup domainSetup, TypeAutomaton typeAutomaton, AtomMappings mappings) {
		StringBuilder constraints = new StringBuilder();

		/* For each type instance */
		for (TaxonomyPredicate currType : domainSetup.getAllTypes().getTypes()) {
			if (currType.isSimplePredicate() || currType.isEmptyPredicate()) {
				/* ..for each state in which type can be used .. */
				for (Block currUsedBlock : typeAutomaton.getUsedTypesBlocks()) {
					for (State currUsedTypeState : currUsedBlock.getStates()) {
						if (!currType.isEmptyPredicate()) {
							/* ..the referenced memory state cannot be null.. */
							constraints = constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints = constraints.append("-").append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");

							/* ..and for each state in which type can be created in memory .. */
							for (Block memoryBlock : typeAutomaton.getMemoryTypesBlocks()) {
								for (State refMemoryTypeState : memoryBlock.getStates()) {
									/*
									 * If the type (currType) is used as an input for a tool (in state
									 * currUsedTypeState)..
									 */
									constraints = constraints.append("-").append(
											mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
											.append(" ");
									/*
									 * ..and the state is referencing a memory state where the type was created
									 * (refMemoryTypeState)
									 */
									constraints = constraints.append("-").append(mappings.add(refMemoryTypeState,
											currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" ");
									/* ..the type has to be generated in the the referenced memory type state. */
									constraints = constraints.append(
											mappings.add(currType, refMemoryTypeState, WorkflowElement.MEMORY_TYPE))
											.append(" 0\n");

								}
							}
							/* If the type is empty the referenced state has to be null. */
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints = constraints.append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the inputs are available in the memory.
	 * Memory in Message Passing Approach is limited to one the output of the
	 * previous tool. <br>
	 * Return the CNF representation of the input type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Message Passing Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	private static String inputMsgPassingCons(AllModules allModules, TypeAutomaton typeAutomaton, AtomMappings mappings) {

		// setting up input constraints (Message Passing Approach)
		StringBuilder constraints = new StringBuilder();

		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/* Used state can reference states that are directly preceding the state */
				List<State> possibleMemStates = typeAutomaton.getMemoryTypesBlock(blockNumber).getStates();
				possibleMemStates.add(typeAutomaton.getNullState());
				for (State exictingMemState : possibleMemStates) {
					constraints = constraints
							.append(mappings.add(exictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
				}
				constraints = constraints.append(" 0\n");

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<PredicateLabel> pair : getPredicatePairs(possibleMemStates)) {
					constraints = constraints.append("-")
							.append(mappings.add(pair.getFirst(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
					constraints = constraints.append("-")
							.append(mappings.add(pair.getSecond(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}
				/*
				 * Used state cannot reference states that are not directly preceding the state
				 */
				for (int notCurrBlockNumber = 0; notCurrBlockNumber < typeAutomaton.getMemoryTypesBlocks()
						.size(); notCurrBlockNumber++) {
					if (notCurrBlockNumber != blockNumber) {
						for (State nonExictingMemState : typeAutomaton.getMemoryTypesBlock(notCurrBlockNumber)
								.getStates()) {
							constraints = constraints.append("-").append(mappings.add(nonExictingMemState,
									currInputState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * TODO: TEST THE METHOD!!
	 * Function returns the encoding that ensures that each time a memory type is
	 * referenced by a tool's input type, it has to be of right type. <br>
	 * Function is implementing the Message Passing Approach.
	 * 
	 * @param emptyType
	 * @param typeAutomaton
	 * @param mappings
	 * @param enforceUsageOfAllWorkflowInputTypes - true if all the inputs given to
	 *                                            the workflow should be used
	 * @param enforceUsageOfAllGeneratedTypes     - true if all the generated types
	 *                                            have to be used
	 * @return String representation of constraints.
	 */
	private static String enforcingUsageOfGeneratedTypesMsgPassingCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMappings mappings = synthesisInstance.getMappings();
		Type emptyType = synthesisInstance.getEmptyType();
		
		
		StringBuilder constraints = new StringBuilder();
		String usageOfAllTypes = " ", usageOfAllWorkflowInputType = " ";
		String usageOfOneType = " 0\n", usageOfWorkflowInputType = " 0\n";
		if (synthesisInstance.getConfig().getUseWorkflowInput() == ConfigEnum.ALL) {
			usageOfAllWorkflowInputType = " 0\n";
			usageOfWorkflowInputType = " ";
		}
		if (synthesisInstance.getConfig().getUseAllGeneratedData()  == ConfigEnum.ALL) {
			
			usageOfAllTypes = " 0\n";
			usageOfOneType = " ";
		}
		/*
		 * Setting up the constraints that ensure usage of the generated types in the
		 * memory. (e.g.  all workflow inputs and at least one of each of the tool outputs
		 * needs to be used in the program, unless they are empty).
		 */
		for (Block currBlock : synthesisInstance.getTypeAutomaton().getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currMemoryState : currBlock.getStates()) {
				/* If the memory is provided as input */
				if (blockNumber == 0) {
					if(synthesisInstance.getConfig().getUseWorkflowInput()  == ConfigEnum.NONE) {
						continue;
					}
					constraints = constraints
							.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE)).append(" ");
					for (State inputState : synthesisInstance.getTypeAutomaton().getUsedTypesBlock(blockNumber).getStates()) {
						constraints = constraints
								.append(mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
								.append(usageOfAllWorkflowInputType);
					}
					constraints = constraints.append(usageOfWorkflowInputType);
				} else {
					if(synthesisInstance.getConfig().getUseAllGeneratedData()  == ConfigEnum.NONE) {
						break;
					}
					constraints = constraints
							.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE)).append(" ");
					for (State inputState : synthesisInstance.getTypeAutomaton().getUsedTypesBlock(blockNumber).getStates()) {
						constraints = constraints
								.append(mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
								.append(usageOfAllTypes);
					}
					constraints = constraints.append(usageOfOneType);
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the inputs are available in the memory.
	 * Memory in Shared Memory Approach contains outputs of all the previous tools.
	 * <br>
	 * Return the CNF representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Shared Memory Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints.
	 */
	private static String inputSharedMemCons(TypeAutomaton typeAutomaton, AtomMappings mappings) {

		// setting up input constraints (Shared Memory Approach)
		StringBuilder constraints = new StringBuilder();

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
				constraints = constraints.append(" 0\n");

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<PredicateLabel> pair : getPredicatePairs(possibleMemStates)) {
					constraints = constraints.append("-")
							.append(mappings.add(pair.getFirst(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
					constraints = constraints.append("-")
							.append(mappings.add(pair.getSecond(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}

				/*
				 * Used state cannot reference states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					constraints = constraints.append("-").append(
							mappings.add(nonExictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Function returns the encoding that ensures that each time a memory type is
	 * referenced by a tool's input type, it has to be of right type. <br>
	 * Function is implementing the Shared Memory Approach.
	 * 
	 * @param emptyType
	 * @param typeAutomaton
	 * @param mappings
	 * @param enforceUsageOfAllWorkflowInputTypes - true if all the inputs given to
	 *                                            the workflow should be used
	 * @param enforceUsageOfAllGeneratedTypes     - true if all the generated types
	 *                                            have to be used
	 * @return String representation of constraints.
	 */
	private static String enforcingUsageOfGeneratedTypesSharedMemCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMappings mappings = synthesisInstance.getMappings();
		Type emptyType = synthesisInstance.getEmptyType();
		TypeAutomaton typeAutomaton = synthesisInstance.getTypeAutomaton();
		StringBuilder constraints = new StringBuilder();
		/*
		 * Setting up the constraints that ensure usage of the generated types in the
		 * memory, (e.g. all workflow inputs and at least one of each of the tool outputs
		 * needs to be used in the program, unless they are empty.)
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
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints = constraints.append(" 0\n");
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

							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints = constraints.append(" 0\n");
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
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints = constraints.append(" 0\n");
					}
					/* In case that at least one of the generated data instances per tool need to be used. */
				} else if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ONE) {
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getStateNumber() == 0) {
							constraints = constraints
									.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
									.append(" ");
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints = constraints.append(" 0\n");
				}
				/* In case that none generated data has to be used do nothing. */

			}
		}

		return constraints.toString();
	}

	/**
	 * Return the CNF representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton. <br>
	 * <br>
	 * Generate constraints that preserve tool outputs.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param emptyType       - represents absence of types
	 * @param mappings
	 * @return String representation of constraints
	 */
	private static String outputCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMappings mappings = synthesisInstance.getMappings();
		StringBuilder constraints = new StringBuilder();

		// for each module
		for (TaxonomyPredicate potentialModule : synthesisInstance.getDomainSetup().getAllModules().getModules()) {
			// that is a Tool
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				// iterate through all the states
				for (State moduleState : synthesisInstance.getModuleAutomaton().getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// and for each state and output state of that module state
					List<State> currOutputStates = synthesisInstance.getTypeAutomaton().getMemoryTypesBlock(moduleNo).getStates();
					List<DataInstance> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							for (TaxonomyPredicate outputType : moduleOutputs.get(i).getTypes()) { // set type and format for the
																						// single output
								// if module was used in the module state
								constraints = constraints.append("-")
										.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
								// require type and/or format to be used in one of the directly
								// proceeding output states if it exists, otherwise use empty type

								constraints = constraints.append(
										mappings.add(outputType, currOutputStates.get(i), WorkflowElement.MEMORY_TYPE))
										.append(" 0\n");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints = constraints.append(
									mappings.add(synthesisInstance.getEmptyType(), currOutputStates.get(i), WorkflowElement.MEMORY_TYPE))
									.append(" 0\n");
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
	 * @param moduleAutomaton
	 * @param mappings
	 * @return {@link String} representation of constraints
	 */
	public static String moduleMutualExclusion(AllModules allModules, ModuleAutomaton moduleAutomaton, AtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();

		for (Pair<PredicateLabel> pair : allModules.getSimplePairs()) {
			for (State moduleState : moduleAutomaton.getModuleStates()) {
				constraints = constraints.append("-")
						.append(mappings.add(pair.getFirst(), moduleState, WorkflowElement.MODULE)).append(" ");
				constraints = constraints.append("-")
						.append(mappings.add(pair.getSecond(), moduleState, WorkflowElement.MODULE)).append(" 0\n");
			}
		}

		return constraints.toString();
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootModuleID    - represent the ID of the root module in the module
	 *                        taxonomy
	 * @param moduleAutomaton - module automaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	public static String moduleMandatoryUsage(AllModules allModules, ModuleAutomaton moduleAutomaton,
			AtomMappings mappings) {
		if(allModules.getModules().isEmpty()) {
			System.err.println("No tools were I/O annotated.");
			return "";
		}
		StringBuilder constraints = new StringBuilder();
		
		for (State moduleState : moduleAutomaton.getModuleStates()) {
			for (TaxonomyPredicate tool : allModules.getModules()) {
				if(tool instanceof Module) {
				constraints = constraints.append(mappings.add(tool, moduleState, WorkflowElement.MODULE))
						.append(" ");
				}
			}
			constraints = constraints.append(" 0\n");
		}

		return constraints.toString();
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 * 
	 * @param rootModuleID    - represent the ID of the root module in the module
	 *                        taxonomy
	 * @param moduleAutomaton - module automaton
	 * @param mappings
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public static String moduleEnforceTaxonomyStructure(AllModules allModules, TaxonomyPredicate currModule, ModuleAutomaton moduleAutomaton,
			AtomMappings mappings) {

		StringBuilder constraints = new StringBuilder();
		for (State moduleState : moduleAutomaton.getModuleStates()) {
			constraints = constraints.append(
					moduleEnforceTaxonomyStructureForState(allModules, currModule, mappings, moduleState));
		}
		return constraints.toString();
	}

	/**
	 * Providing the recursive method used in
	 * {@link #moduleEnforceTaxonomyStructure(String, ModuleAutomaton, AtomMappings)
	 * moduleEnforceTaxonomyStructure}.
	 */
	private static String moduleEnforceTaxonomyStructureForState(AllModules allModules, TaxonomyPredicate currModule,
			AtomMappings mappings, State moduleState) {
		String superModule_state = mappings.add(currModule, moduleState, WorkflowElement.MODULE).toString();

		StringBuilder constraints = new StringBuilder();
		StringBuilder currConstraint = new StringBuilder("-").append(superModule_state).append(" ");

		List<String> subModules_States = new ArrayList<String>();
		if (!(currModule.getSubPredicates() == null || currModule.getSubPredicates().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (TaxonomyPredicate subModule : APEUtils.safe(currModule.getSubPredicates())) {
				if(subModule == null) {
					System.out.println("Null error: " + currModule.getPredicateID() + " ->" + currModule.getSubPredicates().toString());
				}
				String subModule_State = mappings.add(subModule, moduleState, WorkflowElement.MODULE).toString();
				currConstraint = currConstraint.append(subModule_State).append(" ");
				subModules_States.add(subModule_State);
				constraints = constraints.append(moduleEnforceTaxonomyStructureForState(allModules, subModule, mappings, moduleState));
			}
			currConstraint = currConstraint.append("0\n");
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (String subModule_State : subModules_States) {
				currConstraint = currConstraint.append("-").append(subModule_State).append(" ")
						.append(superModule_state).append(" 0\n");
			}
			return currConstraint.append(constraints).toString();
		} else {
			return "";
		}
	}
	
	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
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
