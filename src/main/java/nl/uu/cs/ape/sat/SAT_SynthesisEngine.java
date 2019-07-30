package nl.uu.cs.ape.sat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.constraints.ConstraintFactory;
import nl.uu.cs.ape.sat.models.APEConfig;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.All_SAT_solutions;
import nl.uu.cs.ape.sat.models.All_solutions;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.SAT_solution;
import nl.uu.cs.ape.sat.models.Type;

/**
 * The {@code SAT_SynthesisEngine} class represents a synthesis instance, i.e. it is represented with the set of inputs (tools, types, constraints and workflow lenght that is being explored).<br>
 * It is used to execute synthesis algorithm over the given input, implemented using MiniSAT solver.
 * <br><br>
 * The class implements general synthesis interface {@link SynthesisEngine}.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_SynthesisEngine implements SynthesisEngine {

	private final AllModules allModules;
	private final AllTypes allTypes;
	private final APEConfig config;
	private final AllModules annotated_modules;
	private final AtomMapping mappings;
	private final ConstraintFactory allConsTemplates;
	/** Set of all the solutions found by the program */
	private final All_SAT_solutions allSolutions;
	private StringBuilder cnfEncoding;
	private File temp_sat_input;
	
	/**
	 * Setup of the SAT synthesis engine
	 * @param allModules
	 * @param allTypes
	 * @param allSolutions
	 * @param config
	 * @param annotated_modules
	 * @param allConsTemplates
	 */
	public SAT_SynthesisEngine(AllModules allModules, AllTypes allTypes, All_SAT_solutions allSolutions,
			APEConfig config, AllModules annotated_modules, ConstraintFactory allConsTemplates) {
		this.allModules = allModules;
		this.allTypes = allTypes;
		this.allSolutions = allSolutions;
		this.config = config;
		this.annotated_modules = annotated_modules;
		allSolutions.newEncoding();
		this.mappings = allSolutions.getMappings();
		this.allConsTemplates = allConsTemplates;
		
		this.temp_sat_input = null;
		this.cnfEncoding = new StringBuilder();
	}

	/**
	 * Generate the SAT encoding of the workflow synthesis and write it in a tmp file
	 * @throws IOException 
	 */
	public String synthesisEncoding() throws IOException {
		
		long problemSetupStartTime = System.currentTimeMillis();
		AbstractModule rootModule = allModules.getRootModule();
		Type rootType = allTypes.getRootType();
		
		/*
		 * Generate the automaton
		 */
		StaticFunctions.startTimer(config.getDebug_mode());
		ModuleAutomaton moduleAutomaton = new ModuleAutomaton(allSolutions.getCurrSolutionLenght(), config.getMax_no_tool_outputs());
		TypeAutomaton typeAutomaton = new TypeAutomaton(allSolutions.getCurrSolutionLenght(),
				config.getMax_no_tool_inputs(), config.getMax_no_tool_outputs());
		StaticFunctions.restartTimerNPrint("Automaton");
		/*
		 * Encode the workflow input
		 */
		String inputDataEncoding = allTypes.encodeInputData(config.getProgram_inputs(), typeAutomaton, mappings);
		if (inputDataEncoding == null) {
			return null;
		}
		cnfEncoding = cnfEncoding.append(inputDataEncoding);
		/*
		 * Encode the workflow output
		 */
		String outputDataEncoding = allTypes.encodeOutputData(config.getProgram_outputs(), typeAutomaton, mappings);
		if (outputDataEncoding == null) {
			return null;
		}
		cnfEncoding = cnfEncoding.append(outputDataEncoding);
		/*
		 * Create constraints from the module.xml file regarding the Inputs/Outputs
		 */
		cnfEncoding = cnfEncoding.append(annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, allTypes, config.getShared_memory(),
				allTypes.getEmptyType(), mappings));
		StaticFunctions.restartTimerNPrint("Tool I/O constraints");
		
		/*
		 * Create the constraints that provide distinction of data instances.
		 */
//		cnfEncoding = cnfEncoding.append(allTypes.endoceInstances(typeAutomaton));
		
		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the tools 2.
		 * Mandatory usage of the tools - from taxonomy. 3. Adding the constraints
		 * enforcing the taxonomy structure.
		 */
		cnfEncoding = cnfEncoding.append(allModules.moduleMutualExclusion(moduleAutomaton, mappings));
		StaticFunctions.restartTimerNPrint("Tool exclusions enfocements");
		cnfEncoding = cnfEncoding.append(allModules.moduleMandatoryUsage(annotated_modules, moduleAutomaton, mappings));
		cnfEncoding = cnfEncoding.append(allModules.moduleEnforceTaxonomyStructure(rootModule.getModuleID(), moduleAutomaton, mappings));
		StaticFunctions.restartTimerNPrint("Tool usage enfocements");
		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the types/formats 2.
		 * Mandatory usage of the types in the transition nodes (note: "empty type" is
		 * considered a type) 3. Adding the constraints enforcing the taxonomy
		 * structure.
		 */
		cnfEncoding = cnfEncoding.append(allTypes.typeMutualExclusion(typeAutomaton, mappings));
		StaticFunctions.restartTimerNPrint("Type exclusions enfocements");
		cnfEncoding = cnfEncoding.append(allTypes.typeMandatoryUsage(rootType, typeAutomaton, mappings));
		cnfEncoding = cnfEncoding.append(allTypes.typeEnforceTaxonomyStructure(rootType.getTypeID(), typeAutomaton, mappings));
		StaticFunctions.restartTimerNPrint("Type usage enfocements");
		/*
		 * Encode the constraints from the file based on the templates (manual templates)
		 */
		cnfEncoding = cnfEncoding.append(StaticFunctions.generateSLTLConstraints(config.getConstraints_path(), allConsTemplates, allModules,
				allTypes, mappings, moduleAutomaton, typeAutomaton));
		StaticFunctions.restartTimerNPrint("SLTL constraints");
		/*
		 * Counting the number of variables and clauses that will be given to the SAT
		 * solver TODO Improve thi-s approach, no need to read the whole String again.
		 */
		int variables = mappings.getSize();
		int clauses = StaticFunctions.countLinesNewFromString(cnfEncoding.toString());
		String sat_input_header = "p cnf " + variables + " " + clauses + "\n";
		StaticFunctions.restartTimerNPrint("Reading rows");
		System.out.println();
		/*
		 * Create a temp file that will be used as input for the SAT solver.
		 */
			temp_sat_input = File.createTempFile("sat_input_" + allSolutions.getCurrSolutionLenght() + "_len_", ".cnf");
//			temp_sat_input.deleteOnExit();

		/*
		 * Fixing the input and output files for easier testing.
		 */

		StaticFunctions.write2file(sat_input_header + cnfEncoding, temp_sat_input, false);

		long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
		System.out.println("Total problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec.");
		
		return cnfEncoding.toString();
	}
	
	/**
	 * Using the SAT input generated from SAT encoding and running MiniSAT solver to find the solutions
	 * 
	 * @param allSolutions - current set of {@link SAT_solution} that will be extended with newly found solutions, it 
	 * @return {@code true} if the synthesis execution runs properly, {@code false} if it fails.
	 */
	public boolean synthesisExecution() {
		
		List<SAT_solution> currSolutions = runMiniSAT(temp_sat_input.getAbsolutePath(), mappings,
				allModules, allTypes, allSolutions.getSolutionsFound(), allSolutions.getSolutionsFoundMax(), allSolutions.getCurrSolutionLenght());
		/** Add current solutions to list of all solutions. */
		return allSolutions.addAll(currSolutions);
	}
	
	
	public String getCnfEncoding() {
		return cnfEncoding.toString();
	}
	
	/**
	 * Returns a set of {@link SAT_solution SAT_solutions} by parsing the SAT
	 * output. In case of the UNSAT the list is empty.
	 * 
	 * @param dimacsFilePath    - path to the CNF formula in dimacs form
	 * @param mappings          - atom mappings
	 * @param allModules        - list of all the modules
	 * @param allTypes          - list of all the types
	 * @param solutionsFoundMax
	 * @return List of {@link SAT_solution SAT_solutions}. Possibly empty list.
	 */
	public static List<SAT_solution> runMiniSAT(String dimacsFilePath, AtomMapping mappings, AllModules allModules,
			AllTypes allTypes, int solutionsFound, int solutionsFoundMax, int solutionLength) {
		List<SAT_solution> solutions = new ArrayList<SAT_solution>();
		ISolver solver = SolverFactory.newDefault();
		int timeout = 3600;
		// ISolver solver = new ModelIterator(SolverFactory.newDefault(),
		// no_of_solutions); // iteration through at most
		// no_of_solutions solutions
		solver.setTimeout(timeout); // 1 hour timeout
		long realStartTime = 0;
		long realTimeElapsedMillis;
		Reader reader = new DimacsReader(solver);
		try {
			IProblem problem = reader.parseInstance(dimacsFilePath); // loading CNF encoding of the problem
			realStartTime = System.currentTimeMillis();
			while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
				SAT_solution sat_solution = new SAT_solution(problem.model(), mappings, allModules, allTypes,
						solutionLength);
				solutions.add(sat_solution);
				solutionsFound++;
				if (solutionsFound % 500 == 0) {
					realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
					System.out.println("Found " + solutionsFound + " solutions. Solving time: "
							+ (realTimeElapsedMillis / 1000F) + " sec.");
				}
				/*
				 * Adding the negation of the positive part of the solution as a constraint
				 * (default negation does not work)
				 */
				IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray());
				solver.addClause(negSol);
			}
		} catch (ParseFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ContradictionException e) {
			System.err.println("Unsatisfiable");
		} catch (TimeoutException e) {
			System.err.println("Timeout. Solving took longer than default timeout: " + timeout + " seconds.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (solutionsFound == 0 || solutionsFound % 500 != 0) {
			realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			System.out.println("Found " + solutionsFound + " solutions. Solving time: "
					+ (realTimeElapsedMillis / 1000F) + " sec.");
		}

		return solutions;
	}

}
