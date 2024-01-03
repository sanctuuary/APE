package nl.uu.cs.ape.solver.domainconfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.Block;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.DomainModules;
import nl.uu.cs.ape.models.Module;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.AtomType;
import nl.uu.cs.ape.models.enums.ConfigEnum;
import nl.uu.cs.ape.models.logic.constructs.Predicate;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxAtom;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxConjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxDisjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxEquivalence;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxImplication;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegatedConjunction;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxNegation;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxXOR;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;

/**
 * The {@code ModuleUtils} class is used to encode SLTLx constraints based on
 * the module annotations that would encode the workflow structure.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public final class EnforceModuleRelatedRules {

	/**
	 * Private constructor is used to to prevent instantiation.
	 */
	private EnforceModuleRelatedRules() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return Set of SLTLx formulas that represent the CNF constraints regarding
	 *         the required INPUT
	 *         and OUTPUT types of the modules.
	 */
	public static Set<SLTLxFormula> moduleAnnotations(Domain domain, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();
		fullEncoding.addAll(toolInputTypes(domain, moduleAutomaton, typeAutomaton));

		fullEncoding.addAll(toolOutputTypes(domain, moduleAutomaton, typeAutomaton));
		return fullEncoding;
	}

	/**
	 * Return a CNF formula that preserves the memory structure that is being used
	 * (shared memory), i.e. ensures that the referenced items are available
	 * according to the mem. structure and that the input type and the referenced
	 * type from the memory represent the same data.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return Set of SLTLx formulas that represent the CNF constraints regarding
	 *         the required
	 *         memory structure implementation.
	 */
	public static Set<SLTLxFormula> memoryStructure(Domain domain, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		fullEncoding.addAll(allowDataReferencing(typeAutomaton));
		fullEncoding.addAll(usageOfGeneratedTypes(synthesisInstance));
		fullEncoding.addAll(dataReference(domain,
				typeAutomaton));
		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that ancestor relation (R)
	 * among data objects is preserved.
	 * 
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	public static Set<SLTLxFormula> ancestorRelationsDependency(Domain domain, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/**
		 * Encode reflexivity and transitivity of the relation.
		 */
		fullEncoding.addAll(relationalReflexivity(AtomType.R_RELATION, typeAutomaton));
		fullEncoding.addAll(relationalTransitivity(AtomType.R_RELATION, typeAutomaton));

		/**
		 * Ancestor relation:
		 * - encode restrictions
		 * - preserve ancestor relation among tool I/O
		 * - preserve ancestor relation when data referencing
		 * - restrict that outputs can ONLY depend on inputs of the tool
		 * ONLY
		 * - restrict that empty types don't depend on anything
		 */
		fullEncoding.addAll(restrictAncestorRelationDomain(typeAutomaton));
		fullEncoding.addAll(ancestorRelRestrictOverModules(domain.getAllTypes().getEmptyType(), typeAutomaton));
		fullEncoding.addAll(ancestorRelDependencyOverModules(domain.getAllTypes().getEmptyType(), typeAutomaton));
		fullEncoding.addAll(ancestorRelOverDataReferencing(typeAutomaton));

		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that identity relation (IS)
	 * among data objects is preserved.
	 * 
	 * @param typeAutomaton - collection of states representing the data objects in
	 *                      the workflow
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	public static Set<SLTLxFormula> identityRelationsDependency(TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/**
		 * Encode that the relation in an identity.
		 */

		fullEncoding.addAll(relationalIdentity(AtomType.IDENTITY_RELATION, typeAutomaton));

		return fullEncoding;
	}

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Returns the CNF representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 *
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> toolInputTypes(Domain domain, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();
		/* For each module.. */
		for (TaxonomyPredicate potentialModule : domain.getAllModules().getModules()) {
			/* ..which is a Tool.. */
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				/* ..iterate through all the states.. */
				for (State moduleState : moduleAutomaton.getAllStates()) {
					int moduleNo = moduleState.getLocalStateNumber();
					/* ..and for each state and input state of that module state.. */
					List<State> currInputStates = typeAutomaton.getUsedTypesBlock(moduleNo - 1)
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
							/*
							 * Encode: if module was used in the module state
							 * the corresponding data and format types need to be provided in input
							 * states
							 */
							fullEncoding.add(
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
							fullEncoding.add(
									new SLTLxImplication(
											new SLTLxAtom(
													AtomType.MODULE,
													module,
													moduleState),
											new SLTLxAtom(
													AtomType.USED_TYPE,
													domain.getAllTypes().getEmptyType(),
													currInputState)));
						}
					}
				}
			}
		}

		return fullEncoding;
	}

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link AtomType#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 *
	 * @return String representing the constraints required to ensure that the
	 *         {@link AtomType#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private static Set<SLTLxFormula> dataReference(Domain domain, TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/* For each type instance */
		for (TaxonomyPredicate currType : domain.getAllTypes().getTypes()) {
			if (currType.isSimplePredicate() || currType.isEmptyPredicate()) {
				/* ..for each state in which type can be used .. */
				for (State currUsedTypeState : typeAutomaton.getAllUsedTypesStates()) {
					if (!currType.isEmptyPredicate()) {
						/*
						 * If the predicate is not empty
						 * the referenced memory state cannot be null..
						 */
						fullEncoding.add(
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
						for (State refMemoryTypeState : typeAutomaton.getAllMemoryTypesStates()) {
							/*
							 * Pairs of referenced states have to be of the same types.
							 */

							fullEncoding.add(
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
															refMemoryTypeState))));
						}
						/* If the type is empty the referenced state has to be null. */
					} else {

						fullEncoding.add(
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

		return fullEncoding;
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
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> allowDataReferencing(TypeAutomaton typeAutomaton) {

		// setting up input constraints (Shared Memory Approach)
		Set<SLTLxFormula> fullEncoding = new HashSet<>();
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
				Set<SLTLxFormula> allPossibilities = new HashSet<>();
				for (State existingMemState : possibleMemStates) {
					allPossibilities.add(new SLTLxAtom(AtomType.MEM_TYPE_REFERENCE, existingMemState, currInputState));
				}
				fullEncoding.add(new SLTLxDisjunction(allPossibilities));

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair<Predicate> pair : getPredicatePairs(possibleMemStates)) {
					fullEncoding.add(
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
				for (State nonExistingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {

					fullEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
											AtomType.MEM_TYPE_REFERENCE,
											nonExistingMemState,
											currInputState)));
				}
			}
		}

		return fullEncoding;
	}

	/**
	 * Generate constraints that ensure the data objects cannot depend (have
	 * ancestors) on
	 * data objects that are not available in memory.
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> restrictAncestorRelationDomain(TypeAutomaton typeAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/** For each used state... */
		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {

				/*
				 * Used state cannot depend on states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExistingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					fullEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
											AtomType.R_RELATION,
											nonExistingMemState,
											currInputState)));
				}

				// Empty inputs have no data dependencies
				for (State existingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber)) {
					/* !(input -> empty) || !R(mem,input) */
					fullEncoding.add(
							new SLTLxNegatedConjunction(
									new SLTLxAtom(
											AtomType.MEM_TYPE_REFERENCE,
											typeAutomaton.getNullState(),
											currInputState),
									new SLTLxAtom(
											AtomType.R_RELATION,
											existingMemState,
											currInputState)));
				}
			}
		}

		/** For each memory state... */
		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currMemState : currBlock.getStates()) {

				/*
				 * Memory state cannot depend on states that are yet to be created or that were
				 * just, i.e. not yet in the shared memory.
				 */
				for (State nonExistingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber - 1)) {
					if (!nonExistingMemState.equals(currMemState)) {
						fullEncoding.add(
								new SLTLxNegation(
										new SLTLxAtom(
												AtomType.R_RELATION,
												nonExistingMemState,
												currMemState)));
					}
				}
			}
		}
		return fullEncoding;
	}

	/**
	 * Generate constraints that ensure that tool inputs that reference data in
	 * memory are in ancestor relation (R) with the referenced data.
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> ancestorRelOverDataReferencing(TypeAutomaton typeAutomaton) {

		// setting up dependency constraints
		Set<SLTLxFormula> fullEncoding = new HashSet<>();
		/** For each input state... */
		for (Block currInputBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currInputBlock.getBlockNumber();
			for (State currInputState : currInputBlock.getStates()) {
				/*
				 * and for each available memory state..
				 */
				for (State availableMemState : typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber)) {
					/*
					 * If input references a memory, they are in ancestor relation
					 * (used -> mem) => R(mem, used)
					 */
					fullEncoding.add(
							new SLTLxImplication(
									new SLTLxAtom(
											AtomType.MEM_TYPE_REFERENCE,
											availableMemState,
											currInputState),
									new SLTLxAtom(
											AtomType.R_RELATION,
											availableMemState,
											currInputState)));
				}
			}
		}
		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that tool inputs and outputs are
	 * preserving the ancestor relation (R). Outputs have to depend on inputs.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> ancestorRelDependencyOverModules(Type emptyType,
			TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();
		/** For tool inputs and outputs */
		for (int i = 0; i < typeAutomaton.getUsedTypesBlocks().size() - 1; i++) {
			Block currInputBlock = typeAutomaton.getUsedTypesBlock(i);
			Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i + 1);

			// For each output state..
			for (State currMemState : currMemBlock.getStates()) {

				// .. the memory (output) state has all tool inputs as ancestors, as long as
				// none of them is empty
				for (State currInputState : currInputBlock.getStates()) {
					fullEncoding.add(
							new SLTLxXOR(
									new SLTLxAtom(
											AtomType.R_RELATION,
											currInputState,
											currMemState),
									new SLTLxDisjunction(
											new SLTLxAtom(
													AtomType.MEMORY_TYPE,
													emptyType,
													currMemState),
											new SLTLxAtom(
													AtomType.USED_TYPE,
													emptyType,
													currInputState))));
				}
			}
		}
		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that tool outputs are only in the
	 * ancestor relation (R)
	 * with the tool inputs (and their ancestors), and not any other data objects.
	 * Outputs can depend only on inputs.
	 *
	 * @param synthesisInstance A specific synthesis run that contains all the
	 *                          information specific for it.
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> ancestorRelRestrictOverModules(Type emptyType,
			TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		for (int i = 0; i < typeAutomaton.getUsedTypesBlocks().size() - 1; i++) {
			Block currInputBlock = typeAutomaton.getUsedTypesBlock(i);
			Block currMemBlock = typeAutomaton.getMemoryTypesBlock(i + 1);
			/** For each tool output.. */
			for (State currMemState : currMemBlock.getStates()) {
				/* ..(unless it is empty).. */
				SLTLxAtom outputEmpty = new SLTLxAtom(
						AtomType.MEMORY_TYPE,
						emptyType,
						currMemState);
				/* ..and an arbitrary element in the memory.. */
				for (State existingType : typeAutomaton.getAllMemoryStatesUntilBlockNo(i)) {

					/* ..if the element from the memory is not ancestor of any of the inputs.. */
					Set<SLTLxFormula> notInputAncestors = new HashSet<>();
					for (State currInputState : currInputBlock.getStates()) {
						notInputAncestors.add(
								new SLTLxNegation(
										new SLTLxAtom(
												AtomType.R_RELATION,
												existingType,
												currInputState)));
					}
					/* .., it cannot be an ancestor of the output either (unless it is empty). */
					fullEncoding.add(
							new SLTLxDisjunction(
									outputEmpty,
									new SLTLxImplication(
											new SLTLxConjunction(notInputAncestors),
											new SLTLxNegation(
													new SLTLxAtom(
															AtomType.R_RELATION,
															existingType,
															currMemState)))));
				}
			}
		}
		return fullEncoding;
	}

	/**
	 * Generate constraints that ensure that the relations (e.g., identity relation
	 * (IS)) are reflexive.
	 * 
	 * @param binRel - binary relation that is reflexive
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> relationalReflexivity(AtomType binRel, TypeAutomaton typeAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/* Relation is reflexive for any state in the system. */
		typeAutomaton.getAllStates()
				.forEach(state -> {
					/* Rel(state,state) */
					fullEncoding.add(
							new SLTLxAtom(
									binRel,
									state,
									state));
					/* ..no state is related to null state */
					fullEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
											binRel,
											state,
											typeAutomaton.getNullState())));
				});

		return fullEncoding;
	}

	/**
	 * Generate constraints that ensure that the relations (e.g., identity relation
	 * (IS)) are an identity.
	 * Forall X,Y IS(X,Y) IFF
	 * 
	 * @param binRel - binary relation that is reflexive
	 * 
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> relationalIdentity(AtomType binRel, TypeAutomaton typeAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/* Relation is reflexive for any state in the system. */
		typeAutomaton.getAllStates()
				.forEach(state1 -> {
					/* ..no state is identical to null state */
					fullEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
											binRel,
											state1,
											typeAutomaton.getNullState())));
					fullEncoding.add(
							new SLTLxNegation(
									new SLTLxAtom(
											binRel,
											typeAutomaton.getNullState(),
											state1)));
					typeAutomaton.getAllStates()
							.forEach(state2 -> {
								if (state1.equals(state2)) {
									/* state=state -> IS(state,state) */
									fullEncoding.add(
											new SLTLxAtom(
													binRel,
													state1,
													state2));
								} else {
									/* state1<>state2 then !IS(state1,state2) */
									fullEncoding.add(
											new SLTLxNegation(
													new SLTLxAtom(
															binRel,
															state1,
															state2)));
								}
							});
				});

		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that the relation (e.g., ancestor
	 * relation (R)) is transitive.
	 * 
	 * @param binRel        - relation that is transitive
	 * @param typeAutomaton - system that represents states in the workflow
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> relationalTransitivity(AtomType binRel, TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/* Relation is transitive for any 3 states in the system. */
		typeAutomaton.getAllStates()
				.forEach(state1 -> typeAutomaton.getAllStates()
						.forEach(state2 -> typeAutomaton.getAllStates()
								.forEach(state3 ->
								/*
								 * Encode the transitivity.
								 * E.g., R(s1,s2) & R(s2,s3) => R(s1,s3)
								 */
								fullEncoding.add(
										new SLTLxImplication(
												new SLTLxConjunction(
														new SLTLxAtom(
																binRel,
																state1,
																state2),
														new SLTLxAtom(
																binRel,
																state2,
																state3)),
												new SLTLxAtom(
														binRel,
														state1,
														state3))))));
		return fullEncoding;
	}

	/**
	 * Function returns the encoding that ensures that
	 * the relation (e.g., identity relations (IS)) is symmetrical.
	 * 
	 * @param binRel        - binary relation that is symmetrical
	 * @param typeAutomaton - system that represents states in the workflow
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> relationalSymmetry(AtomType binRel, TypeAutomaton typeAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		/* Relation is symmetric for any 2 states in the system. */
		typeAutomaton.getAllMemoryTypesStates()
				.forEach(state1 -> typeAutomaton.getAllMemoryTypesStates()
						.forEach(state2 ->
						/*
						 * Encode the symmetry.
						 * E.g., IS(s1,s2) => IS(s2,s1)
						 */
						fullEncoding.add(
								new SLTLxImplication(
										new SLTLxAtom(
												binRel,
												state1,
												state2),
										new SLTLxAtom(
												binRel,
												state2,
												state1)))));
		return fullEncoding;
	}

	/**
	 * Return the CNF representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.<br>
	 * Generate constraints that preserve tool outputs.
	 * 
	 * @param synthesisInstance - instance of the synthesis engine
	 *
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	private static Set<SLTLxFormula> toolOutputTypes(Domain domain, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		// for each module
		for (TaxonomyPredicate potentialModule : domain.getAllModules().getModules()) {
			// that is a Tool
			if ((potentialModule instanceof Module)) {
				Module module = (Module) potentialModule;
				// iterate through all the states
				for (State moduleState : moduleAutomaton.getAllStates()) {
					int moduleNo = moduleState.getLocalStateNumber();
					// and for each state and output state of that module state
					List<State> currOutputStates = typeAutomaton.getMemoryTypesBlock(moduleNo)
							.getStates();
					List<Type> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							TaxonomyPredicate outputType = moduleOutputs.get(i);
							// single output
							// if module was used in the module state
							// require type and/or format to be used in one of the directly
							// proceeding output states if it exists, otherwise use empty type

							fullEncoding.add(
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
							fullEncoding.add(
									new SLTLxImplication(
											new SLTLxAtom(
													AtomType.MODULE,
													module,
													moduleState),
											new SLTLxAtom(
													AtomType.MEMORY_TYPE,
													domain.getAllTypes().getEmptyType(),
													currOutputStates.get(i))));
						}
					}
				}
			}
		}

		return fullEncoding;
	}

	/**
	 * Generating the mutual exclusion constraints for the pair of tools from
	 * modules (excluding abstract modules from the taxonomy) in each state of
	 * moduleAutomaton.
	 *
	 * @param pair            pair of modules.
	 * @param moduleAutomaton Module automaton.
	 * @return The Set of SLTLx formulas that represent the constraints.
	 */

	public static Set<SLTLxFormula> moduleMutualExclusion(Pair<Predicate> pair, ModuleAutomaton moduleAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		for (State moduleState : moduleAutomaton.getAllStates()) {
			fullEncoding.add(
					new SLTLxDisjunction(
							new SLTLxNegation(
									new SLTLxAtom(
											AtomType.MODULE,
											pair.getFirst(),
											moduleState)),
							new SLTLxNegation(
									new SLTLxAtom(
											AtomType.MODULE,
											pair.getSecond(),
											moduleState))));
		}

		return fullEncoding;
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param moduleAutomaton Module automaton.
	 * @return Set of SLTLx formulas that represent the constraints.
	 */
	public static Set<SLTLxFormula> moduleMandatoryUsage(DomainModules allModules, ModuleAutomaton moduleAutomaton) {
		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		if (allModules.getModules().isEmpty()) {
			log.warn("No tools were I/O annotated.");
			return fullEncoding;
		}

		for (State moduleState : moduleAutomaton.getAllStates()) {
			Set<SLTLxFormula> allPossibilities = new HashSet<>();

			for (TaxonomyPredicate tool : allModules.getModules()) {
				if (tool instanceof Module) {
					allPossibilities.add(
							new SLTLxAtom(
									AtomType.MODULE,
									tool,
									moduleState));
				}
			}
			fullEncoding.add(new SLTLxDisjunction(allPossibilities));
		}

		return fullEncoding;
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 *
	 * @param allModules      All the modules.
	 * @param currModule      Module that should be used.
	 * @param moduleAutomaton Module automaton.
	 * @return Set of SLTLx formulas that represent the constraints enforcing
	 *         taxonomy
	 *         classifications.
	 */
	public static Set<SLTLxFormula> moduleTaxonomyStructure(DomainModules allModules, TaxonomyPredicate currModule,
			ModuleAutomaton moduleAutomaton) {

		Set<SLTLxFormula> fullEncoding = new HashSet<>();
		for (State moduleState : moduleAutomaton.getAllStates()) {
			fullEncoding.addAll(moduleTaxonomyStructureForState(allModules, currModule, moduleState));
		}
		return fullEncoding;
	}

	/**
	 * The recursive method used in
	 * {@link #moduleEnforceTaxonomyStructure}, to enforce the taxonomy structure in
	 * the solution.
	 *
	 * @param allModules  All the modules.
	 * @param currModule  Module that should be used.
	 * @param moduleState State in which the module should be used.
	 */
	private static Set<SLTLxFormula> moduleTaxonomyStructureForState(DomainModules allModules,
			TaxonomyPredicate currModule, State moduleState) {
		SLTLxAtom superModuleState = new SLTLxAtom(AtomType.MODULE, currModule, moduleState);

		Set<SLTLxFormula> fullEncoding = new HashSet<>();

		List<SLTLxAtom> subModulesStates = new ArrayList<>();
		if (!(currModule.getSubPredicates() == null || currModule.getSubPredicates().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (TaxonomyPredicate subModule : APEUtils.safe(currModule.getSubPredicates())) {
				if (subModule == null) {
					log.error("Submodule is 'null': " + currModule.getPredicateID() + " ->"
							+ currModule.getSubPredicates().toString());
				}
				SLTLxAtom subModuleState = new SLTLxAtom(AtomType.MODULE, subModule, moduleState);
				subModulesStates.add(subModuleState);

				fullEncoding.addAll(moduleTaxonomyStructureForState(allModules, subModule, moduleState));
			}
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			fullEncoding.add(
					new SLTLxImplication(
							superModuleState,
							new SLTLxDisjunction(subModulesStates)));
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (SLTLxAtom subModuleState : subModulesStates) {
				fullEncoding.add(
						new SLTLxImplication(
								subModuleState,
								superModuleState));
			}
		}

		return fullEncoding;
	}

	/**
	 * Gets predicate pairs.
	 *
	 * @param predicateList List of predicates.
	 * @return A list of pairs of tools from modules. Note that the abstract
	 *         modules are not returned, only the unique pairs of modules that are
	 *         representing actual tools.
	 */
	public static List<Pair<Predicate>> getPredicatePairs(List<? extends Predicate> predicateList) {
		List<Pair<Predicate>> pairs = new ArrayList<>();

		for (int i = 0; i < predicateList.size() - 1; i++) {
			for (int j = i + 1; j < predicateList.size(); j++) {

				pairs.add(new Pair<>(predicateList.get(i), predicateList.get(j)));
			}
		}

		return pairs;
	}
}