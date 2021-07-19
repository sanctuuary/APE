package nl.uu.cs.ape.core.implSMT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.core.implSAT.SATModuleUtils;
import nl.uu.cs.ape.models.AllModules;
import nl.uu.cs.ape.models.AllTypes;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.ConfigEnum;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.Atom;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.smtStruc.Assertion;
import nl.uu.cs.ape.models.smtStruc.BinaryBoolFuncDeclaration;
import nl.uu.cs.ape.models.smtStruc.DataTypeDeclaration;
import nl.uu.cs.ape.models.smtStruc.SMTLib2Row;
import nl.uu.cs.ape.models.smtStruc.SMTComment;
import nl.uu.cs.ape.models.smtStruc.boolStatements.BinarySMTPredicate;
import nl.uu.cs.ape.models.smtStruc.boolStatements.DeclareSimplifiedFunction;
import nl.uu.cs.ape.models.smtStruc.boolStatements.EqualStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ExistsStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.Fact;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ForallStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.ImplicationStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NandStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.NotStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.OrStatement;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBitVec;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBoundedVar;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTFunctionArgument;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTPredicateFunArg;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTBitVectorOp;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code ModuleUtils} class is used to encode SAT constraints based on the
 * module annotations.
 *
 * @author Vedran Kasalica
 */
public final class SMTModuleUtils {

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private SMTModuleUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a SMTLib2 representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of SMTLib2 constraints regarding the required INPUT
	 *         and OUTPUT types of the modules.
	 */
	public static List<SMTLib2Row> encodeModuleAnnotations(SMTSynthesisEngine synthesisInstance) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		
		allClauses.add(new SMTComment("Encoding tool input dependencies."));
		allClauses.addAll(inputCons(synthesisInstance));
		
		allClauses.add(new SMTComment("Encoding tool output dependencies."));
		allClauses.addAll(outputCons(synthesisInstance));
		return allClauses;
	}

	/**
	 * Define taxonomy terms as data types in SMT encoding.
	 * @param synthesisInstance
	 * @return List of SMTLib2 clauses.
	 */
	public static List<SMTLib2Row> encodeTaxonomyTerms(SMTSynthesisEngine synthesisInstance) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		allClauses.add(new SMTComment("Encoding taxonomy terms and workflow states."));
		//define tools as data types
		AllModules allModules = synthesisInstance.getDomainSetup().getAllModules();
		allClauses.add(new DataTypeDeclaration(SMTDataType.MODULE, allModules.getModules()));

		// define each data dimension as a data type
		AllTypes allTypes = synthesisInstance.getDomainSetup().getAllTypes();
		
		Set<PredicateLabel> types = new HashSet<PredicateLabel>();
		types.add(allTypes.getEmptyType());
		allTypes.getDataTaxonomyDimensionsAndLabel().stream().forEach(
			 dimension -> types.addAll(allTypes.getElementsFromSubTaxonomy(dimension))
		);
		
		allClauses.add(new DataTypeDeclaration(SMTDataType.TYPE, types));
		
		
		// define labels as data type TODO test if this is working with empty being a data type
//		Type labelRoot = synthesisInstance.getDomainSetup().getAllTypes().getLabelRoot();
//		allClauses.add(new DeclareSimplifiedFunction(new SMTDataType(labelRoot), allTypes.getElementsFromSubTaxonomy(labelRoot)));
		
		// define workflow states as data types
//		allClauses.add(new DeclareSimplifiedFunction(SMTDataType.MODULE_STATE, synthesisInstance.getModuleAutomaton().getAllStates()));
//		List<State> memoryStates = synthesisInstance.getTypeAutomaton().getAllMemoryTypesStates();
//		memoryStates.add(synthesisInstance.getTypeAutomaton().getNullState());
//		allClauses.add(new DeclareSimplifiedFunction(SMTDataType.MEMORY_TYPE_STATE, memoryStates));
//		allClauses.add(new DeclareSimplifiedFunction(SMTDataType.USED_TYPE_STATE, synthesisInstance.getTypeAutomaton().getAllUsedTypesStates()));
		
		return allClauses;
	}
	
	/**
	 * Generate the binary functions that map pairs of states (represented as BitVec that corresponding 
	 * to the order number of the state) and Modules/Types
	 * to boolean values, depending of the truth value of the pair.
	 * 
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return List of SMTLib2 clauses.
	 */
	public static List<SMTLib2Row> encodeWorkflowStructure(SMTSynthesisEngine synthesisInstance) {

		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		allClauses.add(new SMTComment("Encoding workflow structure functions."));
		// encode that each tool state is defined over an operation (where tool states are represented as BitVec that corresponding to the order number of the state)
		allClauses.add(new BinaryBoolFuncDeclaration(
									WorkflowElement.MODULE, 
									SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MODULE_STATE)), 
									SMTDataType.MODULE));

//		SMTBoundedVar moduleState = new SMTBoundedVar("moduleState");
//		SMTBoundedVar module = new SMTBoundedVar("module");
//		allClauses.add(new Assertion(
//							new ForallStatement(
//								module,
//								SMTDataType.MODULE,
//								new ForallStatement(true,
//										moduleState,
//										SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MODULE_STATE)),
//										new ImplicationStatement(
//												new BinarySMTPredicate(
//														SMTBitVectorOp.GREATER_OR_EQUAL, 
//														moduleState, 
//														new SMTBitVec(
//																SMTDataType.MODULE_STATE, 
//																synthesisInstance.getAutomatonSize(SMTDataType.MODULE_STATE))),
//												new NotStatement(
//														new BinarySMTPredicate(
//																WorkflowElement.MODULE,
//																moduleState, 
//																module)
//														)
//												)
//										)
//								
//						)
//				));

		allClauses.add(new BinaryBoolFuncDeclaration(
									WorkflowElement.USED_TYPE, 
									SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE)), 
									SMTDataType.TYPE));
		
//		SMTBoundedVar usedState = new SMTBoundedVar("usedState");
//		SMTBoundedVar type = new SMTBoundedVar("type");
//		
//		allClauses.add(new Assertion(
//							new ForallStatement(
//								type,
//								SMTDataType.TYPE,
//								new ForallStatement(true,
//										usedState,
//										SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE)),
//										new ImplicationStatement(
//												new BinarySMTPredicate(
//														SMTBitVectorOp.GREATER_OR_EQUAL, 
//														usedState, 
//														new SMTBitVec(
//																SMTDataType.USED_TYPE_STATE, 
//																synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE))),
//												new NotStatement(
//														new BinarySMTPredicate(
//																WorkflowElement.USED_TYPE,
//																usedState, 
//																type)
//														)
//												)
//										)
//								
//						)
//				));
		
		allClauses.add(new BinaryBoolFuncDeclaration(
									WorkflowElement.MEMORY_TYPE, 
									SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE)), 
									SMTDataType.TYPE));
		
//		SMTBoundedVar memoryState = new SMTBoundedVar("memoryState");
//		allClauses.add(new Assertion(
//							new ForallStatement(
//								type,
//								SMTDataType.TYPE,
//								new ForallStatement(true,
//										memoryState,
//										SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE)),
//										new ImplicationStatement(
//												new BinarySMTPredicate(
//														SMTBitVectorOp.GREATER_OR_EQUAL, 
//														memoryState, 
//														new SMTBitVec(
//																SMTDataType.MEMORY_TYPE_STATE, 
//																synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE))),
//												new NotStatement(
//														new BinarySMTPredicate(
//																WorkflowElement.MEMORY_TYPE,
//																memoryState, 
//																type)
//														)
//												)
//										)
//								
//						)
//				));
			
		
		// encode relation between used types and types available in memory
		allClauses.add(new BinaryBoolFuncDeclaration(
									WorkflowElement.MEM_TYPE_REFERENCE, 
									SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE)), 
									SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE))));
		

//		allClauses.add(new Assertion(
//				new ForallStatement(true,
//					memoryState,
//					SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE)),
//					new ForallStatement(true,
//							usedState,
//							SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE)),
//							new ImplicationStatement(
//									new OrStatement(
//											new BinarySMTPredicate(
//													SMTBitVectorOp.GREATER_OR_EQUAL, 
//													memoryState, 
//													new SMTBitVec(
//															SMTDataType.MEMORY_TYPE_STATE, 
//															synthesisInstance.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE))),
//											new BinarySMTPredicate(
//													SMTBitVectorOp.GREATER_OR_EQUAL, 
//													usedState,
//													new SMTBitVec(
//															SMTDataType.USED_TYPE_STATE, 
//															synthesisInstance.getAutomatonSize(SMTDataType.USED_TYPE_STATE)))),
//									new NotStatement(
//											new BinarySMTPredicate(
//													WorkflowElement.MEM_TYPE_REFERENCE,
//													usedState, 
//													memoryState)
//											)
//									)
//							)
//					
//			)
//	));

		return allClauses;
	}
	
	/**
	 * Return a SMTLib2 formula that preserves the memory structure that is being used
	 * (e.g. 'shared memory'), i.e. ensures that the referenced items are available
	 * according to the mem. structure and that the input type and the referenced
	 * type from the memory represent the same data.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return String representation of SMTLib2 constraints regarding the required
	 *         memory structure implementation.
	 */
	public static List<SMTLib2Row> encodeMemoryStructure(SMTSynthesisEngine synthesisInstance) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		
		allClauses.add(new SMTComment("Encoding rules of shared memory referencing (reference only the memory states already available)."));
		allClauses.addAll(allowDataReferencingCons(synthesisInstance.getTypeAutomaton()));
		
		allClauses.add(new SMTComment("Encoding rules that enforce usage of data generated in memory."));
		allClauses.addAll(enforcingUsageOfGeneratedTypesCons(synthesisInstance));

		allClauses.add(new SMTComment("Encoding rules that ensure that the data used by the tool as input and the data referenced in memory are the same."));
		allClauses.addAll(enforceDataReferenceRules(synthesisInstance.getDomainSetup().getAllTypes(),
				synthesisInstance.getTypeAutomaton()));
		
		return allClauses;
	}
	
	public static List<SMTLib2Row> encodeDataInstanceDependencyCons(TypeAutomaton typeAutomaton, SMTPredicateMappings mappings) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		
		
//		allClauses.addAll(allowDataDependencyCons(typeAutomaton));
//		allClauses.addAll(enforceDataDependencyOverModules(typeAutomaton, mappings));
//		allClauses.addAll(enforceDataDependencyOverDataReferencing(typeAutomaton, mappings));
		
		return allClauses;
	}

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Returns the SMTLib2 representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 *                          
	 * @return String representation of constraints.
	 */
	private static List<SMTLib2Row> inputCons(SMTSynthesisEngine synthesisInstance) {

		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
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
							/* Encode: if module was used in the module state  the corresponding data and format types need to be provided in input
							 * states*/
							allClauses.add(new Assertion(
									new ImplicationStatement(
											new BinarySMTPredicate(
													WorkflowElement.MODULE, 
													new SMTBitVec(SMTDataType.MODULE_STATE, moduleState),
													new SMTPredicateFunArg(module)), 
											new BinarySMTPredicate(
													WorkflowElement.USED_TYPE, 
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
													new SMTPredicateFunArg(currInputType)
													)
											)
									));
						} else {
							allClauses.add(new Assertion(
									new ImplicationStatement(
											new BinarySMTPredicate(
													WorkflowElement.MODULE, 
													new SMTBitVec(SMTDataType.MODULE_STATE, moduleState),
													new SMTPredicateFunArg(module)), 
											new BinarySMTPredicate(
													WorkflowElement.USED_TYPE, 
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
													new SMTPredicateFunArg(synthesisInstance.getEmptyType())
													)
											)
									));
						}
					}
				}
			}
		}

		return allClauses;
	}

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link SMTDataType#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 *                          

	 * @param allTypes
	 * @param typeAutomaton
	 * @return String representing the constraints required to ensure that the
	 *         {@link SMTDataType#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private static List<SMTLib2Row> enforceDataReferenceRules(AllTypes allTypes, TypeAutomaton typeAutomaton) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
//		moduleState,
//		SMTDataType.BITVECTOR(synthesisInstance.getAutomatonSize(SMTDataType.MODULE_STATE)),
		SMTBoundedVar useState = new SMTBoundedVar("useState");
		SMTBoundedVar memState = new SMTBoundedVar("memState");
		SMTBoundedVar type = new SMTBoundedVar("type");
			/* 
			 * For each type dimension, the Used type states have to be equal to the referenced state values.
			 */
			allClauses.add(
					new Assertion(
							new ForallStatement(
								useState,
								SMTDataType.USED_TYPE_STATE,
								new ForallStatement(
									memState,
									SMTDataType.MEMORY_TYPE_STATE,
									new ForallStatement(
										type,
										SMTDataType.TYPE,
										new ImplicationStatement(
													new BinarySMTPredicate(
														WorkflowElement.MEM_TYPE_REFERENCE, 
														useState, 
														memState),
													new EqualStatement(
														new BinarySMTPredicate(
																WorkflowElement.USED_TYPE, 
																useState, 
																type
																),
														new BinarySMTPredicate(
																WorkflowElement.MEMORY_TYPE, 
																memState, 
																type
																)
														)
										))))));
		
		/* 
		 * Empty states reference the null state.
		 */
		allClauses.add(new Assertion(
						new ForallStatement(
								useState,
								SMTDataType.USED_TYPE_STATE,
								new EqualStatement(
										new BinarySMTPredicate(
												WorkflowElement.USED_TYPE, 
												useState, 
												new SMTPredicateFunArg(allTypes.getEmptyType())),
										new BinarySMTPredicate(
												WorkflowElement.MEM_TYPE_REFERENCE, 
												useState, 
												new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, typeAutomaton.getNullState())
												)
										)
								)
						));
		
		/* 
		 * Where the null state has no type.
		 * TODO: MaybE we could remove this statement
		 */
//		allClauses.add(new Assertion(
//						new BinarySMTPredicate(
//								WorkflowElement.MEMORY_TYPE, 
//								new SMTFunctionArgument(typeAutomaton.getNullState()), 
//								new SMTFunctionArgument(allTypes.getEmptyType())
//								)
//						));
//		allClauses.add(new Assertion(
//						new BinarySMTPredicate(
//								WorkflowElement.MEMORY_TYPE, 
//								new SMTFunctionArgument(typeAutomaton.getNullState()), 
//								new SMTFunctionArgument(allTypes.getEmptyAPELabel())
//								)
//						));

		return allClauses;
	}
	
	/**
	 * Generate constraints that ensure that the all tool inputs can reference data
	 * that is available in memory at the time (where order number of the state is lower).
	 *
	 * <br>
	 * Return the SMTLib2 representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Shared Memory Approach.
	 *
	 * @param typeAutomaton
	 * @return String representation of constraints.
	 */
	private static List<SMTLib2Row> allowDataReferencingCons(TypeAutomaton typeAutomaton) {

		// setting up input constraints (Shared Memory Approach)
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
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
				List<Fact> allPossibilities = new ArrayList<Fact>();
				for (State exictingMemState : possibleMemStates) {
					allPossibilities.add(new BinarySMTPredicate(
												WorkflowElement.MEM_TYPE_REFERENCE, 
												new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
												new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, exictingMemState)
												));
				}
				allClauses.add(new Assertion(
							new OrStatement(allPossibilities)));
				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<PredicateLabel> pair : SATModuleUtils.getPredicatePairs(possibleMemStates)) {
					allClauses.add(new Assertion(
							new NandStatement(
									new BinarySMTPredicate(
											WorkflowElement.MEM_TYPE_REFERENCE, 
											new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
											new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, ((State) pair.getFirst()))
											), 
									new BinarySMTPredicate(
											WorkflowElement.MEM_TYPE_REFERENCE, 
											new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
											new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, ((State) pair.getSecond()))
											)
									)
							));
				}

				/*
				 * Used state cannot reference states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					allClauses.add(new Assertion(
							new NotStatement(
									new BinarySMTPredicate(
											WorkflowElement.MEM_TYPE_REFERENCE, 
											new SMTBitVec(SMTDataType.USED_TYPE_STATE, currInputState),
											new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, nonExictingMemState)
											)
									)
							));
				}
			}
		}

		return allClauses;
	}

	/**
	 * Generate constraints that ensure that the data instances can depend on
	 * instances that are available in memory, and that each data instance depends
	 * on itself.
	 * 
	 * @return String representation of constraints.
	 */
//	private static List<SMTLib2Row> allowDataDependencyCons(TypeAutomaton typeAutomaton) {
//
//		// setting up dependency constraints
//		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
//		/** For each input state... */
//		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
//			int blockNumber = currBlock.getBlockNumber();
//			for (State currInputState : currBlock.getStates()) {
//				/* Input state does not depend on the null state */
//				constraints.append("-").append(
//						mappings.add(currInputState, typeAutomaton.getNullState(), SMTDataType.TYPE_DEPENDENCY))
//						.append(" 0\n");
//
//				/*
//				 * Used state cannot depend on states that are yet to be created, i.e. not yet
//				 * in the shared memory.
//				 */
//				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
//					constraints.append("-")
//							.append(mappings.add(nonExictingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//							.append(" 0\n");
//				}
//			}
//		}
//		/** For each memory state... */
//		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
//			int blockNumber = currBlock.getBlockNumber();
//			for (State currMemState : currBlock.getStates()) {
//				/* Memory state depends on itself */
//				constraints = constraints
//						.append(mappings.add(currMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//						.append(" 0\n");
//				/* ..and does not depend on the null state */
//				constraints.append("-").append(
//						mappings.add(currMemState, typeAutomaton.getNullState(), SMTDataType.TYPE_DEPENDENCY))
//						.append(" 0\n");
//
//				/*
//				 * Memory state cannot depend on states that are yet to be created or that were
//				 * just, i.e. not yet in the shared memory.
//				 */
//				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber - 1)) {
//					if (!nonExictingMemState.equals(currMemState)) {
//						constraints.append("-").append(
//								mappings.add(nonExictingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//								.append(" 0\n");
//					}
//				}
//			}
//		}
//		return allClauses;
//	}
//
//	/**
//	 * Generate constraints that ensure that tool inputs that reference data in
//	 * memory depend on the same data as the referenced data instance.
//	 * 
//	 * @return String representation of constraints.
//	 */
//	private static List<SMTLib2Row> enforceDataDependencyOverDataReferencing(TypeAutomaton typeAutomaton, SMTPredicateMappings mappings) {
//
//		// setting up dependency constraints
//		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
//		/** For each input state... */
//		for (Block currInputBlock : typeAutomaton.getUsedTypesBlocks()) {
//			int blockNumber = currInputBlock.getBlockNumber();
//			for (State currInputState : currInputBlock.getStates()) {
//				/*
//				 * and for each available memory state..
//				 */
//				for (int i = 0; i <= blockNumber; i++) {
//					Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i);
//					for (State currMemState : currMemBlock.getStates()) {
//						/* ..if the input state references the memory state.. */
//						for (State exictingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
//							constraints.append("-").append(
//									mappings.add(currMemState, currInputState, SMTDataType.MEM_TYPE_REFERENCE))
//									.append(" ");
//							/** ..each data dependency over the memory state.. */
//							constraints.append("-").append(
//									mappings.add(exictingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//									.append(" ");
//							/** ..is the dependency of the tool input state. */
//							constraints.append(
//									mappings.add(exictingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//									.append(" 0\n");
//
//							// and vice versa (if it does not depend on it, neither will the tool input
//							// state)
//
//							constraints.append("-").append(
//									mappings.add(currMemState, currInputState, SMTDataType.MEM_TYPE_REFERENCE))
//									.append(" ");
//							/** ..each data dependency over the memory state.. */
//							constraints.append(
//									mappings.add(exictingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//									.append(" ");
//							/** ..is the dependency of the tool input state. */
//							constraints.append("-").append(
//									mappings.add(exictingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//									.append(" 0\n");
//						}
//					}
//				}
//
//				// Empty inputs have no data dependencies
//				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber)) {
//					constraints.append("-").append(mappings.add(typeAutomaton.getNullState(),
//							currInputState, SMTDataType.MEM_TYPE_REFERENCE)).append(" ");
//
//					constraints.append("-")
//							.append(mappings.add(existingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//							.append(" 0\n");
//				}
//			}
//		}
//		return allClauses;
//	}
//
//	private static List<SMTLib2Row> enforceDataDependencyOverModules(TypeAutomaton typeAutomaton, SMTPredicateMappings mappings) {
//		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
//		/** For tool inputs and outputs */
//		for (int i = 0; i < typeAutomaton.getUsedTypesBlocks().size() - 1; i++) {
//			Block currInputBlock = typeAutomaton.getUsedTypesBlock(i);
//			Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i + 1);
//
//			// For each output state
//			for (State currMemState : currMemBlock.getStates()) {
//				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(i)) {
//					// if the type depends on a data from the memory
//					constraints.append("-")
//							.append(mappings.add(existingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//							.append(" ");
//					// one of the tool inputs does as well
//					for (State currInputState : currInputBlock.getStates()) {
//						constraints = constraints
//								.append(mappings.add(existingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//								.append(" ");
//					}
//					constraints.append(" 0\n");
//
//					// ..and vice versa, dependence of the input types is inherited to the outputs
//
//					for (State currInputState : currInputBlock.getStates()) {
//						constraints.append("-")
//								.append(mappings.add(existingMemState, currInputState, SMTDataType.TYPE_DEPENDENCY))
//								.append(" ");
//
//						constraints = constraints
//								.append(mappings.add(existingMemState, currMemState, SMTDataType.TYPE_DEPENDENCY))
//								.append(" 0\n");
//					}
//				}
//			}
//		}
//		return allClauses;
//	}

	/**
	 * Function returns the encoding that ensures that tool outputs are used
	 * according to the configuration, e.g. if the config specifies that all
	 * workflow inputs have to be used, then each of them has to be referenced at
	 * least once.
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 *                          
	 * @return String representation of constraints.
	 */
	private static List<SMTLib2Row> enforcingUsageOfGeneratedTypesCons(SMTSynthesisEngine synthesisInstance) {

		Type emptyType = synthesisInstance.getEmptyType();
		TypeAutomaton typeAutomaton = synthesisInstance.getTypeAutomaton();
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
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

						List<Fact> allPossibilities = new ArrayList<Fact>();
						allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE, 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState),
													new SMTPredicateFunArg(emptyType)));
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEM_TYPE_REFERENCE, 
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, inputState), 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState)));
						}
						allClauses.add(new Assertion(
								new OrStatement(allPossibilities)));
					}
					/* In case that at least one workflow input need to be used */
				} else if (synthesisInstance.getConfig().getUseWorkflowInput() == ConfigEnum.ONE) {
					List<Fact> allPossibilities = new ArrayList<Fact>();
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getLocalStateNumber() == 0) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE, 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState), 
													new SMTPredicateFunArg(emptyType)));
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEM_TYPE_REFERENCE,
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, inputState),
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState)));
						}
					}
					allClauses.add(new Assertion(
							new OrStatement(allPossibilities)));
				}
				/* In case that none of the workflow input has to be used, do nothing. */
			} else {
				/* In case that all generated data need to be used. */
				if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						List<Fact> allPossibilities = new ArrayList<Fact>();
						allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE,
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState), 
													new SMTPredicateFunArg(emptyType)));
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEM_TYPE_REFERENCE, 
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, inputState), 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState)));
						}
						allClauses.add(new Assertion(
								new OrStatement(allPossibilities)));
					}
					/*
					 * In case that at least one of the generated data instances per tool need to be
					 * used.
					 */
				} else if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.ONE) {
					List<Fact> allPossibilities = new ArrayList<Fact>();
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getLocalStateNumber() == 0) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE, 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState), 
													new SMTPredicateFunArg(emptyType)));
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							allPossibilities.add(new BinarySMTPredicate(
													WorkflowElement.MEM_TYPE_REFERENCE, 
													new SMTBitVec(SMTDataType.USED_TYPE_STATE, inputState), 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currMemoryState)));
						}
					}
					allClauses.add(new Assertion(
							new OrStatement(allPossibilities)));
				} else if (synthesisInstance.getConfig().getUseAllGeneratedData() == ConfigEnum.NONE) {
					/* In case that none generated data has to be used do nothing. */
				}
			}
		}

		return allClauses;
	}

	/**
	 * Return the SMTLib2 representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.<br>
	 * Generate constraints that preserve tool outputs.
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 *                          
	 * @return String representation of constraints.
	 */
	private static List<SMTLib2Row> outputCons(SMTSynthesisEngine synthesisInstance) {
		
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
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
							
							// for each output
							// if module was used in the module state require type (and format) to be used in one of the directly
							// proceeding output states if it exists, otherwise use the empty type.

							allClauses.add(new Assertion(
									new ImplicationStatement(
											new BinarySMTPredicate(
													WorkflowElement.MODULE,
													new SMTBitVec(SMTDataType.MODULE_STATE, moduleState),
													new SMTPredicateFunArg(module)),
											new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE, 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currOutputStates.get(i)),
													new SMTPredicateFunArg(outputType))
											)
									));
							
						} else {
							allClauses.add(new Assertion(
									new ImplicationStatement(
											new BinarySMTPredicate(
													WorkflowElement.MODULE, 
													new SMTBitVec(SMTDataType.MODULE_STATE, moduleState), 
													new SMTPredicateFunArg(module)), 
											new BinarySMTPredicate(
													WorkflowElement.MEMORY_TYPE, 
													new SMTBitVec(SMTDataType.MEMORY_TYPE_STATE, currOutputStates.get(i)),
													new SMTPredicateFunArg(synthesisInstance.getEmptyType()))
											)
									));
						}
					}
				}
			}
		}

		return allClauses;
	}

	/**
	 * Generating the mutual exclusion constraints for each pair of tools from
	 * modules (excluding abstract modules from the taxonomy) in each state of
	 * moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @return The String representation of constraints.
	 */
	public static List<SMTLib2Row> moduleMutualExclusion(AllModules allModules) {

		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		allClauses.add(new SMTComment("Encoding rules of mutual exclusion of tools."));
		
		SMTBoundedVar moduleState = new SMTBoundedVar("state");
		for (Pair<PredicateLabel> pair : allModules.getSimplePairs()) {
				allClauses.add(new Assertion(
						new ForallStatement(
								moduleState,
								SMTDataType.MODULE_STATE,
								new NandStatement(
										new BinarySMTPredicate(
												WorkflowElement.MODULE, 
												moduleState,
												new SMTPredicateFunArg(pair.getFirst())), 
										new BinarySMTPredicate(
												WorkflowElement.MODULE, 
												moduleState,
												new SMTPredicateFunArg(pair.getSecond()))
										)
								)
						));
		}

		return allClauses;
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @return String representation of constraints.
	 */
	public static List<SMTLib2Row> moduleMandatoryUsage(AllModules allModules) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		allClauses.add(new SMTComment("Encoding rules of mandatory usage of tools."));
		
		if (allModules.getModules().isEmpty()) {
			System.err.println("No tools were I/O annotated.");
			return allClauses;
		}
		SMTBoundedVar moduleState = new SMTBoundedVar("state");
		SMTBoundedVar tool = new SMTBoundedVar("tool");
		allClauses.add(new Assertion(
				new ForallStatement(
						moduleState,
						SMTDataType.MODULE_STATE,
						new ExistsStatement(
								tool,
								SMTDataType.MODULE,
								new BinarySMTPredicate(
										WorkflowElement.MODULE, 
										moduleState, 
										tool)
						)
				)
		));

		return allClauses;
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, and vice-versa, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.<br><br>
	 * 
	 * Taxonomy is enforced top-down and bottom-up, i.e., the subsumption relation is complete.
	 *
	 * @param parentModule      parent module for which the rules are curently created
	 * @param firstCall       true if it is the first time that the method is call
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications.
	 */
	public static List<SMTLib2Row> moduleEnforceTaxonomyStructure(TaxonomyPredicate parentModule, boolean firstCall) {

		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		if(firstCall) {
			allClauses.add(new SMTComment("Encoding rules to preserve the structure of the tool taxonomy."));
		}
		
		SMTBoundedVar moduleState = new SMTBoundedVar("state");
		if (!(parentModule.getSubPredicates() == null || parentModule.getSubPredicates().isEmpty())) {
			allClauses.add(new Assertion(
							new ForallStatement(
									moduleState,
									SMTDataType.MODULE_STATE,
									new EqualStatement(
											new BinarySMTPredicate(
													WorkflowElement.MODULE, 
													moduleState, 
													new SMTPredicateFunArg(parentModule)),
											new OrStatement(parentModule.getSubPredicates().stream()
													.map(subModule -> new BinarySMTPredicate(
																			WorkflowElement.MODULE, 
																			moduleState, 
																			new SMTPredicateFunArg(subModule)))
													.collect(Collectors.toList())
													)
											)
									)
					));
			
			for (TaxonomyPredicate subModule : APEUtils.safe(parentModule.getSubPredicates())) {
				allClauses.addAll(moduleEnforceTaxonomyStructure(subModule, false));
			}
		}
		
		return allClauses;
	}
	
	/**
     * Define SMT functions that can be easily parsed from the model into the {@link Atom} objects..
     * 
     *  @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 *                          
	 * @return String representation of the function definitions..
     */
	public static List<SMTLib2Row> encodeDefineParsablePredicates(SMTSynthesisEngine smtSynthesisEngine) {
		List<SMTLib2Row> allClauses = new ArrayList<SMTLib2Row>();
		allClauses.add(new SMTComment("Define new functions that are easy to parse from the model."));
		
		for(int i = 0; i < smtSynthesisEngine.getSolutionSize(); i++) {
			allClauses.add(new DeclareSimplifiedFunction(WorkflowElement.MODULE, i, SMTDataType.MODULE, SMTDataType.MODULE_STATE));
		}
		
		for(int i = 0; i < smtSynthesisEngine.getAutomatonSize(SMTDataType.USED_TYPE_STATE); i++) {
			allClauses.add(new DeclareSimplifiedFunction(WorkflowElement.USED_TYPE, i, SMTDataType.TYPE, SMTDataType.USED_TYPE_STATE));
		}
		
		for(int i = 0; i < smtSynthesisEngine.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE); i++) {
			allClauses.add(new DeclareSimplifiedFunction(WorkflowElement.MEMORY_TYPE, i, SMTDataType.TYPE, SMTDataType.MEMORY_TYPE_STATE));
		}
		
		for(int i = 0; i < smtSynthesisEngine.getAutomatonSize(SMTDataType.USED_TYPE_STATE); i++) {
			allClauses.add(new DeclareSimplifiedFunction(WorkflowElement.MEM_TYPE_REFERENCE, i, SMTDataType.BITVECTOR(smtSynthesisEngine.getAutomatonSize(SMTDataType.MEMORY_TYPE_STATE)), SMTDataType.USED_TYPE_STATE));
		}
		
		
		return allClauses;
	}
}