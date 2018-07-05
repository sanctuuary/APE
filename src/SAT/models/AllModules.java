package SAT.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeState;

public class AllModules {

	private Set<AbstractModule> modules;

	public AllModules() {
		this.modules = new HashSet<>();
	}

	public AllModules(Collection<? extends AbstractModule> readCSV) {
		this.modules = new HashSet<>();
		this.modules.addAll(readCSV);
	}

	public Set<AbstractModule> getModules() {
		return modules;
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional
	 * operation). More formally, adds the specified element e to this set if the
	 * set contains no element e2 such that (e==null ? e2==null : e.equals(e2)). If
	 * this set already contains the element, the call leaves the set unchanged and
	 * returns false. In combination with the restriction on constructors, this
	 * ensures that sets never contain duplicate elements.
	 * 
	 * @param module
	 * @return true if this set contains the specified element
	 */
	public boolean addModule(AbstractModule module) {
		return modules.add(module);
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
		return modules.contains(module);
	}

	public int size() {
		return modules.size();
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules are not returned, only the unique pairs of modules that are representing actual tools.
	 * @return list of pairs of modules
	 */
	public List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<>();

		List<AbstractModule> iterator = new ArrayList<>();
		for (AbstractModule module : modules) {
			if (module.isTool())
				iterator.add(module);
		}

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}
	
	

	/**
	 * Return the CNF representation of the input type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Pipeline Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	private String inputPipelineCons(ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String constraints = "";
		// setting up input constraints (Pipeline)

		// for each module
		for (AbstractModule module : modules) {
			// iterate through all the states
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				int moduleNo = moduleState.getStateNumber();
				// that are not the first state (no input state)
				if (!moduleState.isFirst()) {
					// and for each input type of that module
					for (Type type : module.getModuleInput()) {
						// if module was used in the state
						constraints += "-" + module.getAtom() + "(" + moduleState.getStateName() + ") ";
						// require the type to be used in at least one of the
						// directly preceding input states
						for (TypeState typeState : typeAutomaton.getBlock(moduleNo - 1).getTypeStates()) {
							constraints += type.getAtom() + "(" + typeState.getStateName() + ") ";
						}
						constraints += "0\n";
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * Return the CNF representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the General Memory Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	private String inputGenMemoryCons(ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String constraints = "";
		// setting up input constraints (General Memory)

		// for each module
		for (AbstractModule module : modules) {
			// iterate through all the states
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				int moduleNo = moduleState.getStateNumber();
				// that are not the first state (no input state)
				if (!moduleState.isFirst()) {
					// and for each input type of that module
					for (Type type : module.getModuleInput()) {
						// if module was used in the state
						constraints += "-" + module.getAtom() + "(" + moduleState.getStateName() + ") ";
						// require the type to be used in at least one of the
						// preceding input states
						for (int i = 0; i < moduleNo; i++) {
							for (TypeState typeState : typeAutomaton.getBlock(i).getTypeStates()) {
								constraints += type.getAtom() + "(" + typeState.getStateName() + ") ";
							}
						}
						constraints += "0\n";
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * Return the CNF representation of the output type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	private String outputCons(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String constraints = "";

		// for each module
		for (AbstractModule module : modules) {
			// iterate through all the states
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				int moduleNo = moduleState.getStateNumber();
				// that are not the last state (no output state)
				if (!moduleState.isLast()) {
					// and for each output type of that module
					int i = 0;
					for (Type type : module.getModuleOutput()) {
						// if module was used in the state
						constraints += "-" + module.getAtom() + "(" + moduleState.getStateName() + ") ";
						// require type to be used in one of the directly
						// proceeding output states
						TypeState typeState = typeAutomaton.getBlock(moduleNo).getTypeStates().get(++i);
						constraints += type.getAtom() + "(" + typeState.getStateName() + ") 0\n";
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * 
	 * @param modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param pipeline
	 * @return  String representation of constraints regarding the required input and output types of the modules
	 */
	public String modulesConstraints(AllModules modules, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, boolean pipeline) {

		String constraints = "";
		if (pipeline) {
			constraints += inputPipelineCons(moduleAutomaton, typeAutomaton);
		} else {
			constraints += inputGenMemoryCons(moduleAutomaton, typeAutomaton);
		}
		constraints += outputCons(moduleAutomaton, typeAutomaton);

		return constraints;
	}

	

}
