package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.uu.cs.ape.sat.StaticFunctions;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.ModuleState;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeBlock;
import nl.uu.cs.ape.sat.automaton.TypeState;

/**
 * The {@code AllModules} class represent the set of all modules/tools that can
 * be part of our program. Each of them is either {@link Module} or
 * {@link AbstractModule}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllModules {

	private Map<String, AbstractModule> modules;
	/** Set of all the module IDs of the annotated modules in the domain. */
	private Set<String> annotatedModules;

	public AllModules() {
		this.modules = new HashMap<String, AbstractModule>();
		this.annotatedModules = new HashSet<String>();
	}

	/**
	 * Create a map-set containing all modules from @modules, omitting the
	 * duplicates.
	 * 
	 * @param modules
	 */
	public AllModules(Collection<? extends AbstractModule> modules) {
		this.modules = new HashMap<String, AbstractModule>();
		for (AbstractModule module : modules) {
			this.addModule(module);
		}
	}

	public Map<String, AbstractModule> getModules() {
		return modules;
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional
	 * operation) and returns it. More formally, adds the specified element e to
	 * this set if the set contains no element e2 such that (e==null ? e2==null :
	 * e.equals(e2)). If this set already contains the element, the call leaves the
	 * set unchanged and returns the existing element. In combination with the
	 * restriction on constructors, this ensures that sets never contain duplicate
	 * elements. It also check whether the new element extends the existing one, it
	 * that case the existing one is replaced by the extended one.
	 * 
	 * @param module - The AbstractModule/Module that needs to be added.
	 * @return The element if it's a new one or the existing element if this set
	 *         contains the specified element.
	 */
	public AbstractModule addModule(AbstractModule module) {
		AbstractModule tmpModule = modules.get(module.getModuleID());
		if (module instanceof Module && (tmpModule != null)) {
			if (tmpModule instanceof Module) {
				return tmpModule;
			} else {
				Module newModule = new Module(((Module) module), tmpModule);
				/*
				 * swap the AbstractModule with the Module
				 */
				swapAbstractModule2Module(newModule, tmpModule);
				return module;
			}
		} else {
			if (tmpModule != null) {
				return tmpModule;
			} else {
				this.modules.put(module.getModuleID(), module);
				return module;
			}
		}
	}

	/**
	 * Removes the {@link AbstractModule} from the set of all modules and adds the
	 * {@link Module} element (or vice versa). Swaps the objects in the set of all
	 * Modules.
	 * 
	 * @param newModule - object that will be added
	 * @param oldModule - object that will be removed
	 */
	public void swapAbstractModule2Module(AbstractModule newModule, AbstractModule oldModule) {
		this.modules.remove(oldModule.getModuleID());
		this.modules.put(newModule.getModuleID(), newModule);
	}

	/**
	 * Returns the module to which the specified key is mapped to, or {@code null}
	 * if the moduleID has no mappings.
	 * 
	 * @param moduleID - the key whose associated value is to be returned
	 * @return {@link AbstractModule} or {@link Module} to which the specified key
	 *         is mapped to, or {@code null} if the moduleID has no mappings
	 */
	public AbstractModule get(String moduleID) {
		return this.modules.get(moduleID);
	}

	/**
	 * Returns the root module of the taxonomy.
	 * 
	 * @return The root module.
	 */
	public AbstractModule getRootModule() {
		return this.modules.get(APEConfig.getConfig().getTool_taxonomy_root());
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param module
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(AbstractModule module) {
		return modules.containsKey(module.getModuleID());
	}

	public int size() {
		return modules.size();
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	private List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<Pair>();

		List<AbstractModule> iterator = new ArrayList<AbstractModule>();
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			if (module.isTool())
				iterator.add(module);
		}

//		System.out.println(APEConfig.getConfig().getTool_taxonomy_root() + ": " + iterator.size());

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Return {@code true} if the module is annotated.
	 * 
	 * @param moduleID - ID of the module that is evaluated.
	 * @return {@code true} if the module is annotated, {@code false} otherwise.
	 */
	public boolean getIsAnnotatedModule(String moduleID) {
		return annotatedModules.contains(moduleID);
	}

	/**
	 * Adds the module to the set of annotated modules.
	 * 
	 * @param moduleID - ID of the module that is annotated.
	 */
	public void addAnnotatedModule(String moduleID) {
		annotatedModules.add(moduleID);
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
	private String inputCons(AllTypes allTypes, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, Type emptyType,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();

		/* For each module..  */
		StringBuilder allToolsInputConstraints = new StringBuilder();
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			/* ..which is a Tool.. */
			if ((module instanceof Module)) {
				/* ..iterate through all the states..  */
				StringBuilder toolInputConstraint = new StringBuilder();
				for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					if(moduleNo > 1) {
						toolInputConstraint = toolInputConstraint.append(") & ");
					}
					/* ..and for each state and input state of that module state.. */
					List<TypeState> currInputStates = typeAutomaton.getUsedTypesBlock(moduleNo - 1).getTypeStates();
					List<Types> moduleInputs = module.getModuleInput();
					for (TypeState currInputState : currInputStates) {
						int currInputStateNo = currInputState.getStateNumber();
						/* ..require data type and/or format to be used in one of the directly
						 preceding input states if it exists, otherwise use empty type.  */
						if (currInputStateNo < moduleInputs.size()) {
//							
							/* Encode: if module was used in the module state  */
							toolInputConstraint = toolInputConstraint.append("(-")
									.append(mappings.add(module, moduleState))
									.append(" ");
							/* Get all memory type states that were generated in order to find the desired data instance  */
							for (TypeState exictingMemState : typeAutomaton.getMemoryStatesUntilBlockNo(moduleNo)) {
								toolInputConstraint = toolInputConstraint.append("| (");
								int inputs = 0;
								/* Get input type and/or format that are/is required by the tool */
								for (Type currInputType : moduleInputs.get(currInputStateNo).getTypes()) {
									inputs++;
									String typeInstance = currInputType.getPredicate() + "[" + exictingMemState.getStateName() + "]";
									/* Generate a new instance of the data type/format */
									Type.generateType(typeInstance, typeInstance, currInputType.getRootNode(), NodeType.INSTANCE, allTypes, currInputType);
									toolInputConstraint = toolInputConstraint
											.append(mappings.add(currInputType,
													exictingMemState, currInputState));
									if(inputs < moduleInputs.get(currInputStateNo).getTypes().size()) {
										toolInputConstraint = toolInputConstraint.append(" & ");
									}
								}
								toolInputConstraint = toolInputConstraint.append(") ");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState))
									.append(" ");
							constraints = constraints
									.append(mappings.add(emptyType, currInputState))
									.append(" 0\n");
						}
					}
//					
					}
//				System.out.println("Transforming to CNF: " + toolInputConstraint);
				if(!toolInputConstraint.toString().replace("(", "").replace(")", "").replace("&", "").trim().matches("")) {
					toolInputConstraint = toolInputConstraint.append(")");
					if(allToolsInputConstraints.length()!=0) {
						allToolsInputConstraints = allToolsInputConstraints.append(" & ").append(toolInputConstraint.toString());
					} else {
						allToolsInputConstraints = allToolsInputConstraints.append(toolInputConstraint.toString());
					}
				}
			}
		}
		constraints = constraints
				.append(StaticFunctions.convert2CNF(allToolsInputConstraints.toString(), mappings));

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
	private String inputMsgPassingCons(AllTypes allTypes, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();
		// setting up input constraints (Pipeline)

		// for each state that is used as input for tools
		for (int i1 = 1; i1 <= typeAutomaton.getWorkflowLength(); i1++) {
			TypeBlock currUsedTypesBlock = typeAutomaton.getUsedTypesBlock(i1);
			for (int j1 = 0; j1 < currUsedTypesBlock.getBlockSize(); j1++) {
				TypeState currUsedTypeState = currUsedTypesBlock.getState(j1);
				// and for each data type that can occur there
				for (Entry<String, Type> mapType : allTypes.getTypes().entrySet()) {
					Type type = mapType.getValue();
					if (type.isSimpleType()) {

						constraints = constraints.append("-")
								.append(mappings.add(type, currUsedTypeState));
						// there needs to be a place (directly prior to it) when it was added to the
						// memory
						int i2 = i1;
						TypeBlock currMemoryTypesBlock = typeAutomaton.getMemoryTypesBlock(i2);
						for (int j2 = 0; j2 < currUsedTypesBlock.getBlockSize(); j2++) {
							TypeState currMemoryTypeState = currMemoryTypesBlock.getState(j2);
							constraints = constraints.append(" ")
									.append(mappings.add(type, currMemoryTypeState));

						}
						constraints = constraints.append(" 0\n");
					}
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
	 * @return String representation of constraints
	 */
	private String inputSharedMemCons(AllTypes allTypes, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();
		// setting up input constraints (Pipeline)

		// for each state that is used as input for tools
		for (int i1 = 0; i1 <= typeAutomaton.getWorkflowLength(); i1++) {
			TypeBlock currUsedTypesBlock = typeAutomaton.getUsedTypesBlock(i1);
			for (int j1 = 0; j1 < currUsedTypesBlock.getBlockSize(); j1++) {
				TypeState currUsedTypeState = currUsedTypesBlock.getState(j1);
				// and for each data type that can occur there
				for (Entry<String, Type> mapType : allTypes.getTypes().entrySet()) {
					Type type = mapType.getValue();
					if (type.isSimpleType()) {

						constraints = constraints.append("-")
								.append(mappings.add(type, currUsedTypeState));
						// there needs to be a place (prior to it) when it was added to the memory
						for (int i2 = 0; i2 <= i1; i2++) {
							TypeBlock currMemoryTypesBlock = typeAutomaton.getMemoryTypesBlock(i2);
							for (int j2 = 0; j2 < currUsedTypesBlock.getBlockSize(); j2++) {
								TypeState currMemoryTypeState = currMemoryTypesBlock.getState(j2);
								constraints = constraints.append(" ")
										.append(mappings.add(type, currMemoryTypeState));

							}
						}
						constraints = constraints.append(" 0\n");
					}
				}
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
	private String outputCons(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, Type emptyType,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();

		// for each module
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			// that is a Tool
			if ((module instanceof Module)) {
				// iterate through all the states
				for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// and for each state and output state of that module state
					List<TypeState> currOutputStates = typeAutomaton.getMemoryTypesBlock(moduleNo).getTypeStates();
					List<Types> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							for (Type outputType : moduleOutputs.get(i).getTypes()) { // set type and format for the
																						// single output
								// if module was used in the module state
								constraints = constraints.append("-")
										.append(mappings.add(module, moduleState))
										.append(" ");
								// require type and/or format to be used in one of the directly
								// proceeding output states if it exists, otherwise use empty type

								constraints = constraints.append(
										mappings.add(outputType, currOutputStates.get(i)))
										.append(" 0\n");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState))
									.append(" ");
							constraints = constraints.append(
									mappings.add(emptyType, currOutputStates.get(i)))
									.append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 * 
	 * @param moduleAutomaton - represents the module automaton
	 * @param typeAutomaton   - represent the type automaton
	 * @param shared_memory   - if false pipeline approach, otherwise the general
	 *                        memory approach is used
	 * @param emptyType       - represents absence of types
	 * @param mappings
	 * @return {@link String} representation of constraints regarding the required
	 *         INPUT and OUTPUT types of the modules
	 */
	public String modulesConstraints(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AllTypes allTypes,
			boolean shared_memory, Type emptyType, AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();
		constraints = constraints.append(inputCons(allTypes, moduleAutomaton, typeAutomaton, emptyType, mappings));
		if (!shared_memory) {
			constraints = constraints.append(inputMsgPassingCons(allTypes, moduleAutomaton, typeAutomaton, mappings));
		} else {
			constraints = constraints.append(inputSharedMemCons(allTypes, moduleAutomaton, typeAutomaton, mappings));
		}

		constraints = constraints.append(outputCons(moduleAutomaton, typeAutomaton, emptyType, mappings));
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
	public String moduleMutualExclusion(ModuleAutomaton moduleAutomaton, AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();

		for (Pair pair : getToolPairs()) {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				constraints = constraints.append("-")
						.append(mappings.add(pair.getFirst(), moduleState)).append(" ");
				constraints = constraints.append("-")
						.append(mappings.add(pair.getSecond(), moduleState))
						.append(" 0\n");
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
	public String moduleMandatoryUsage(AllModules annotatedTools, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings) {
		StringBuilder constraints = new StringBuilder();

		for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
			for (Entry<String, AbstractModule> tool : annotatedTools.getModules().entrySet()) {
				constraints = constraints
						.append(mappings.add(tool.getValue(), moduleState)).append(" ");
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
	public String moduleEnforceTaxonomyStructure(String rootModuleID, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();
		for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
			constraints = constraints.append(
					moduleEnforceTaxonomyStructureForState(rootModuleID, moduleAutomaton, mappings, moduleState));
		}
		return constraints.toString();
	}

	/**
	 * Providing the recursive method used in
	 * {@link #moduleEnforceTaxonomyStructure(String, ModuleAutomaton, AtomMapping)
	 * moduleEnforceTaxonomyStructure}.
	 */
	private String moduleEnforceTaxonomyStructureForState(String rootModuleID, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings, ModuleState moduleState) {
		AbstractModule currModule = modules.get(rootModuleID);
		String superModule_state = mappings.add(currModule, moduleState).toString();

		StringBuilder constraints = new StringBuilder();
		StringBuilder currConstraint = new StringBuilder("-").append(superModule_state).append(" ");

		List<String> subModules_States = new ArrayList<String>();
		if (!(currModule.getSubModules() == null || currModule.getSubModules().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (String subModuleID : currModule.getSubModules()) {
				AbstractModule subModule = modules.get(subModuleID);
				String subModule_State = mappings.add(subModule, moduleState).toString();
				currConstraint = currConstraint.append(subModule_State).append(" ");
				subModules_States.add(subModule_State);
				constraints = constraints.append(
						moduleEnforceTaxonomyStructureForState(subModuleID, moduleAutomaton, mappings, moduleState));
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

}
