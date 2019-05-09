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
import nl.uu.cs.ape.sat.constraints.AllConstraintTamplates;
import nl.uu.cs.ape.sat.models.APEConfig;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.All_solutions;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.SAT_solution;
import nl.uu.cs.ape.sat.models.Type;

public class SAT_SynthesisEngine implements SynthesisEngine {

	private final AllModules allModules;
	private final AllTypes allTypes;
	private final int solutionLength;
	private final APEConfig config;
	private final AllModules annotated_modules;
	private final AtomMapping mappings;
	private final AllConstraintTamplates allConsTemplates;
	private File temp_sat_input;
	private int solutionsFound;
	private int solutionsFoundMax;
	private List<SAT_solution> allSolutions;
	
	private String cnfEncoding;
	
	/**
	 * Setup of the SAT synthesis engine
	 * @param allModules
	 * @param allTypes
	 * @param solutionLength
	 * @param config
	 * @param annotated_modules
	 * @param mappings
	 * @param allConsTemplates
	 */
	public SAT_SynthesisEngine(AllModules allModules, AllTypes allTypes, int solutionLength, APEConfig config, AllModules annotated_modules, AtomMapping mappings, AllConstraintTamplates allConsTemplates, int solutionsFoundMax) {
		this.allModules = allModules;
		this.allTypes = allTypes;
		this.solutionLength = solutionLength;
		this.config = config;
		this.annotated_modules = annotated_modules;
		this.mappings = mappings;
		this.allConsTemplates = allConsTemplates;
		allSolutions = new ArrayList<SAT_solution>();
		
		this.temp_sat_input = null;
		this.cnfEncoding = "";
		this.solutionsFound = 0;
		this.solutionsFoundMax = solutionsFoundMax;
	}
	
	public SAT_SynthesisEngine(AllModules allModules, AllTypes allTypes, All_solutions allSolutions,
			APEConfig config, AllModules annotated_modules, AllConstraintTamplates allConsTemplates) {
		this.allModules = allModules;
		this.allTypes = allTypes;
		this.solutionLength = allSolutions.getSolutionLengthMax();
		this.config = config;
		this.annotated_modules = annotated_modules;
		this.mappings = allSolutions.getMappings();
		this.allConsTemplates = allConsTemplates;
		allSolutions = new ArrayList<SAT_solution>();
		
		this.temp_sat_input = null;
		this.cnfEncoding = "";
		this.solutionsFound = 0;
		this.solutionsFoundMax = allSolutions.getsolutionsFoundMax;
	}

	/**
	 * Generate the SAT encoding of the workflow synthesis and write it in a tmp file
	 * @throws IOException 
	 */
	public String synthesisEncoding() throws IOException {
		
		long problemSetupStartTime = System.currentTimeMillis();
		AbstractModule rootModule = allModules.getRootModule();
		Type rootType = allTypes.getRootType();
		
		ModuleAutomaton moduleAutomaton = new ModuleAutomaton();
		TypeAutomaton typeAutomaton = new TypeAutomaton();

		/*
		 * Generate the automaton in CNF
		 */
		StaticFunctions.generateAutomaton(moduleAutomaton, typeAutomaton, solutionLength,
				config.getMax_no_tool_outputs());

		/*
		 * Encode the workflow input
		 */
		String inputDataEncoding = allTypes.encodeInputData(config.getProgram_inputs(), typeAutomaton, mappings);
		if (inputDataEncoding == null) {
			return null;
		}
		cnfEncoding += inputDataEncoding;
		/*
		 * Encode the workflow output
		 */
		String outputDataEncoding = allTypes.encodeOutputData(config.getProgram_outputs(), typeAutomaton, mappings);
		if (outputDataEncoding == null) {
			return null;
		}
		cnfEncoding += outputDataEncoding;
		/*
		 * Create constraints from the module.csv file
		 */
		cnfEncoding += annotated_modules.modulesConstraints(moduleAutomaton, typeAutomaton, config.getShared_memory(),
				allTypes.getEmptyType(), mappings);
		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the tools 2.
		 * Mandatory usage of the tools - from taxonomy. 3. Adding the constraints
		 * enforcing the taxonomy structure.
		 */
		cnfEncoding += allModules.moduleMutualExclusion(moduleAutomaton, mappings);
		cnfEncoding += allModules.moduleMandatoryUsage(annotated_modules, moduleAutomaton, mappings);
		cnfEncoding += allModules.moduleEnforceTaxonomyStructure(rootModule.getModuleID(), moduleAutomaton, mappings);

		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the types/formats 2.
		 * Mandatory usage of the types in the transition nodes (note: "empty type" is
		 * considered a type) 3. Adding the constraints enforcing the taxonomy
		 * structure.
		 */
		cnfEncoding += allTypes.typeMutualExclusion(typeAutomaton, mappings);
		cnfEncoding += allTypes.typeMandatoryUsage(rootType, typeAutomaton, mappings);
		cnfEncoding += allTypes.typeEnforceTaxonomyStructure(rootType.getTypeID(), typeAutomaton, mappings);

		/*
		 * Encode the constraints from the file based on the templates (manual templates)
		 */
		cnfEncoding += StaticFunctions.generateSLTLConstraints(config.getConstraints_path(), allConsTemplates, allModules,
				allTypes, mappings, moduleAutomaton, typeAutomaton);
		
		/*
		 * Counting the number of variables and clauses that will be given to the SAT
		 * solver TODO Improve thi-s approach, no need to read the whole String again.
		 */
		int variables = mappings.getSize();
		int clauses = StaticFunctions.countLinesNewFromString(cnfEncoding);
		String sat_input_header = "p cnf " + variables + " " + clauses + "\n";

		/*
		 * Create a temp file that will be used as input for the SAT solver.
		 */
			temp_sat_input = File.createTempFile("sat_input_" + solutionLength + "_len_", ".cnf");
			temp_sat_input.deleteOnExit();

		/*
		 * Fixing the input and output files for easier testing.
		 */

		StaticFunctions.write2file(sat_input_header + cnfEncoding, temp_sat_input, false);

		long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
		System.out.println("Problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec.");
		
		return cnfEncoding;
	}

	/**
	 * Using the SAT input generated from SAT encoding and running MiniSAT solver to find the solutions
	 * 
	 * @param allSolutions - current set of solutions that will be extended with newly found solutions
	 * @return {@code true} if the synthesis execution runs properly, {@code false} if it fails.
	 */
	public boolean synthesisExecution(List<SAT_solution> allSolutions) {
		
		
		List<SAT_solution> currSolutions = runMiniSAT(temp_sat_input.getAbsolutePath(), mappings,
				allModules, allTypes, solutionsFound, solutionsFoundMax, solutionLength);
		solutionsFound += currSolutions.size();
		/**
		 * Add current solutions to list of all solutions.
		 */
		allSolutions.addAll(currSolutions);
		long realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
		if ((solutionsFound >= solutionsFoundMax - 1) || solutionLength == solutionLengthMax) {
			System.out.println("\nAPE found " + solutionsFound + " solutions. Total solving time: "
					+ (realTimeElapsedMillis / 1000F) + " sec.");
		} else {
//			System.out.println("Found " + solutionsFound + " solutions. Solving time: "
//					+ (realTimeElapsedMillis / 1000F) + " sec.");
		}
		return false;
	}
	
	
	public String getCnfEncoding() {
		return cnfEncoding;
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
