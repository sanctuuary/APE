package SAT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SAT.automaton.AtomMapping;
import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;
import SAT.models.*;

public class main {

	private static AtomMapping mappings;

	public static List<Module> readCSV(String file) {

		List<Module> modulesNew = new ArrayList<Module>();

		String line = "";
		String cvsSplitBy = ",";
		BufferedReader csvReader;

		try {
			csvReader = new BufferedReader(new FileReader(file));

			while ((line = csvReader.readLine()) != null) {
				String[] stringModule = line.split(cvsSplitBy, -1);
				modulesNew.add(Module.moduleFromString(stringModule));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return modulesNew;
	}

	/**
	 * Return the CNF representation of the input type constraints for all @modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Pipeline Approach.
	 * 
	 * @param modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	public static String inputPipelineCons(AllModules modules, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String constraints = "";
		// setting up input constraints (Pipeline)

		// for each module
		for (AbstractModule module : modules.getModules()) {
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
	 * Return the CNF representation of the input type constraints for all @modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the General Memory Approach.
	 * 
	 * @param modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	public static String inputGenMemoryCons(AllModules modules, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton) {

		String constraints = "";
		// setting up input constraints (General Memory)

		// for each module
		for (AbstractModule module : modules.getModules()) {
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
	 * Return the CNF representation of the output type constraints for all @modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 * 
	 * @param modules
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	public static String outputCons(AllModules modules, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {

		String constraints = "";

		// for each module
		for (AbstractModule module : modules.getModules()) {
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

	public static String modulesConstraints(AllModules modules, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, boolean pipeline) {

		String constraints = "";
		if (pipeline) {
			constraints += inputPipelineCons(modules, moduleAutomaton, typeAutomaton);
		} else {
			constraints += inputGenMemoryCons(modules, moduleAutomaton, typeAutomaton);
		}
		constraints += outputCons(modules, moduleAutomaton, typeAutomaton);

		return constraints;
	}

	/**
	 * Generating the mutual exclusion for each pair of tools from @modules (excluding abstract modules from the taxonomy) in each state of @moduleAutomaton.
	 * @param modules
	 * @param moduleAutomaton
	 * @return String representation of constraints
	 */
	public static String moduleMutualExclusion(AllModules allModules, ModuleAutomaton moduleAutomaton) {

		String constraints = "";
		
		
		for(Pair pair : allModules.getToolPairs()){
				for(ModuleState moduleState : moduleAutomaton.getModuleStates()){
					constraints += "-" + pair.getFirst().getAtom() + "(" + moduleState.getStateName() + ") ";
					constraints += "-" + pair.getSecond().getAtom() + "(" + moduleState.getStateName() + ") 0\n";
				}
		}
		
		return constraints;
	}

	/**
	 * Generating the mutual exclusion for each pair of types in each state of
	 * typeAutomaton.
	 * 
	 * @param types
	 * @param typeAutomaton
	 * @return String representation of constraints
	 */
	public static String typeMutualExclusion(AllTypes allTypes, TypeAutomaton typeAutomaton) {

		String constraints = "";

		return constraints;
	}

	public static void main(String[] args) {

		int automata_bound = 10;
		int branching = 2;
		boolean pipeline = false;
		String cnf = "";

		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
		TypeAutomaton typeAutomaton = new TypeAutomaton();

		// TODO create a mapping for each atom to a number
		mappings = new AtomMapping();

		// TODO generate the automaton in CNF
		for (int i = 0; i < automata_bound; i++) {
			ModuleState tmpModuleState = new ModuleState("S" + i, i);
			if (i == 0) {
				tmpModuleState.setFirst();
			} else if (i == automata_bound - 1) {
				tmpModuleState.setLast();
			}
			moduleAutomaton.addState(tmpModuleState);

			TypeBlock tmpTypeBlock = new TypeBlock();
			for (int j = 0; j < branching; j++) {
				TypeState tmpTypeState = new TypeState("S" + i + "." + j, j);
				tmpTypeBlock.addState(tmpTypeState);
			}
			typeAutomaton.addBlock(tmpTypeBlock);
		}

		// TODO create constraints for module.csv
		AllModules annotated_modules = new AllModules(
				readCSV("/home/vedran/Dropbox/PhD/GEO_project/UseCase_Paper/modules.csv"));

		cnf += modulesConstraints(annotated_modules, moduleAutomaton, typeAutomaton, pipeline);

		// TODO encode the taxonomies - generate the list of all types / modules

		AllModules allModules = new AllModules(annotated_modules.getModules());
		AllTypes allTypes = new AllTypes();

		// TODO create constraints on the mutual exclusion of the tools - from taxonomy
		System.out.println(moduleMutualExclusion(allModules, moduleAutomaton));

		// TODO create constraints on the mutual exclusion of the types
		// TODO generate list of all Types
		cnf += typeMutualExclusion(allTypes, typeAutomaton);

		// TODO encode the constraints from the paper manualy

		// use module/type in the synthesis
		AbstractModule draw_water = new AbstractModule("draw_water", "draw_water", true);
		SLTL_formula_F e0_1 = new SLTL_formula_F(draw_water);
		// System.out.println(e0_1.getCNF(moduleAutomaton, typeAutomaton));

		// don't use module/type in the synthesis
		AbstractModule _3d_surfaces = new AbstractModule("3d_surfaces", "3d_surfaces", true);
		SLTL_formula_G g5 = new SLTL_formula_G(_3d_surfaces, true);
		// System.out.println(g5.getCNF(moduleAutomaton, typeAutomaton));

		// TODO if using module X use module Y subsequently
		AbstractModule modules_with_xyz_input = new AbstractModule("Modules_with_xyz_input", "Modules_with_xyz_input", true);
		AbstractModule modules_with_xyz_output = new AbstractModule("Modules_with_xyz_output",
				"Modules_with_xyz_output", true);
		System.out.println(
				SLTL_formula.ite(modules_with_xyz_input, modules_with_xyz_output, moduleAutomaton, typeAutomaton));

		// TODO implement SAT

		List<SLTL_formula> constraints;

		// for(Module module: modules.getModules() ){
		// System.out.println(module.print());
		// }

	}

}
