package nl.uu.cs.ape.sat.core.implSAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
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
import nl.uu.cs.ape.sat.core.SynthesisEngine;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.ConstraintData;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.ModuleUtils;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code SAT_SynthesisEngine} class represents a <b>synthesis instance</b>,
 * i.e. it is represented with the set of inputs (tools, types, constraints and
 * workflow lenght that is being explored).<br>
 * It is used to execute synthesis algorithm over the given input, implemented
 * using MiniSAT solver. <br>
 * <br>
 * The class implements general synthesis interface {@link SynthesisEngine}.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_SynthesisEngine implements SynthesisEngine {

	/** Hierarchy of all the modules in the domain. */
	private final AllModules allModules;
	/** Hierarchy of all the data types annotated in the domain. */
	private final AllTypes allTypes;
	/** APE library configuration object. */
	private final APEConfig config;
	/** Mapping of all the predicates to integers. */
	private final AtomMapping mappings;
	/** Factory object used to generate constraints. */
	private final ConstraintFactory allConsTemplates;
	/** Representation of the constraint file. */
	private final List<ConstraintData> unformattedConstr;
	/** Set of all the solutions found by the library. */
	private final All_SAT_solutions allSolutions;
	/** CNF encoding of the problem. */
	private StringBuilder cnfEncoding;
	/** String used as an input for the SAT solver. */
	private InputStream temp_sat_input;
	/** Configuration of the program. */
	/*
	 * Representation of the tool part of the automaton used to encode the structure
	 * of the solution.
	 */
	private ModuleAutomaton moduleAutomaton;
	/*
	 * Representation of the type part of the automaton used to encode the structure
	 * of the solution.
	 */
	private TypeAutomaton typeAutomaton;

	/**
	 * Setup of an instance of the SAT synthesis engine.
	 * 
	 * @param allModules
	 * @param allTypes
	 * @param allSolutions
	 * @param config
	 * @param annotated_modules
	 * @param allConsTemplates
	 * @param unformattedConstr
	 */
	public SAT_SynthesisEngine(AllModules allModules, AllTypes allTypes, All_SAT_solutions allSolutions,
			APEConfig config, ConstraintFactory allConsTemplates,
			List<ConstraintData> unformattedConstr, int size) {
		this.allModules = allModules;
		this.allTypes = allTypes;
		this.allSolutions = allSolutions;
		this.config = config;
		allSolutions.newEncoding();
		this.mappings = allSolutions.getMappings();
		this.allConsTemplates = allConsTemplates;
		this.unformattedConstr = unformattedConstr;
		this.temp_sat_input = null;
		this.cnfEncoding = new StringBuilder();

		moduleAutomaton = new ModuleAutomaton(size, config.getMax_no_tool_outputs());
		typeAutomaton = new TypeAutomaton(size, config.getMax_no_tool_inputs(), config.getMax_no_tool_outputs());

	}

	/**
	 * Generate the SAT encoding of the workflow synthesis and return it as a
	 * string.
	 * 
	 * @return {@code true} if the encoding was performed successfully, {@code false} otherwise.
	 * @throws IOException
	 */
	public boolean synthesisEncoding() throws IOException {

		long problemSetupStartTime = System.currentTimeMillis();
		AbstractModule rootModule = allModules.getRootModule();
		Type rootType = allTypes.getRootType();

		/*
		 * Generate the automaton
		 */
		String currLengthTimer = "length" + this.getSolutionSize();
		APEUtils.timerStart(currLengthTimer, config.getDebug_mode());

		APEUtils.timerRestartAndPrint(currLengthTimer, "Automaton");

		/*
		 * Create constraints from the module.xml file regarding the Inputs/Outputs
		 */
		cnfEncoding = cnfEncoding.append(ModuleUtils.modulesConstraints(this));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

		/*
		 * Create the constraints that provide distinction of data instances.
		 */
//		cnfEncoding = cnfEncoding.append(allTypes.endoceInstances(typeAutomaton));

		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the tools 2.
		 * Mandatory usage of the tools - from taxonomy. 3. Adding the constraints
		 * enforcing the taxonomy structure.
		 */
		cnfEncoding = cnfEncoding.append(ModuleUtils.moduleMutualExclusion(allModules,moduleAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions enfocements");
		cnfEncoding = cnfEncoding.append(ModuleUtils.moduleMandatoryUsage(allModules, moduleAutomaton, mappings));
		cnfEncoding = cnfEncoding.append(
				ModuleUtils.moduleEnforceTaxonomyStructure(allModules, rootModule.getPredicateID(), moduleAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage enfocements");
		/*
		 * Create the constraints enforcing: 1. Mutual exclusion of the types/formats 2.
		 * Mandatory usage of the types in the transition nodes (note: "empty type" is
		 * considered a type) 3. Adding the constraints enforcing the taxonomy
		 * structure.
		 */
		cnfEncoding = cnfEncoding.append(allTypes.typeMutualExclusion(typeAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions enfocements");
		cnfEncoding = cnfEncoding.append(allTypes.typeMandatoryUsage(rootType, typeAutomaton, mappings));
		cnfEncoding = cnfEncoding
				.append(allTypes.typeEnforceTaxonomyStructure(rootType.getPredicateID(), typeAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage enfocements");
		/*
		 * Encode the constraints from the file based on the templates (manual
		 * templates)
		 */
		if (unformattedConstr != null && !unformattedConstr.isEmpty()) {
			cnfEncoding = cnfEncoding
					.append(APEUtils.encodeAPEConstraints(unformattedConstr, allConsTemplates,
							allModules, allTypes, mappings, moduleAutomaton, typeAutomaton));
			APEUtils.timerRestartAndPrint(currLengthTimer, "SLTL constraints");
		}
		/*
		 * Encode the workflow input. Workflow I/O are encoded the last in order to
		 * reuse the mappings for states, instead of introducing new ones, using the I/O
		 * types of NodeType.UNKNOWN.
		 */
		String inputDataEncoding = allTypes.encodeInputData(config.getProgram_inputs(), typeAutomaton, mappings);
		if (inputDataEncoding == null) {
			return false;
		}
		cnfEncoding = cnfEncoding.append(inputDataEncoding);
		/*
		 * Encode the workflow output
		 */
		String outputDataEncoding = allTypes.encodeOutputData(config.getProgram_outputs(), typeAutomaton, mappings);
		if (outputDataEncoding == null) {
			return false;
		}
		cnfEncoding = cnfEncoding.append(outputDataEncoding);

		/*
		 * Counting the number of variables and clauses that will be given to the SAT
		 * solver TODO Improve thi-s approach, no need to read the whole String again.
		 */
		int variables = mappings.getSize();
		int clauses = APEUtils.countNewLines(cnfEncoding.toString());
		StringBuilder sat_input_header = new StringBuilder("p cnf " + variables + " " + clauses + "\n");
		APEUtils.timerRestartAndPrint(currLengthTimer, "Reading rows");
		System.out.println();
		/*
		 * Create a temp file that will be used as input for the SAT solver.
		
		temp_sat_input = File.createTempFile("sat_input_" + this.getSolutionSize() + "_len_", ".cnf");
		temp_sat_input.deleteOnExit();
		 */
		/*
		 * Fixing the input and output files for easier testing.
		 
		APEUtils.write2file(sat_input_header + cnfEncoding, temp_sat_input, false);
		*/
		StringBuilder mknfEncoding = sat_input_header.append(cnfEncoding);

		

		temp_sat_input = IOUtils.toInputStream(mknfEncoding.toString(), "UTF-8");
		temp_sat_input.close();
//		testing sat input
//		InputStream tmpSat = IOUtils.toInputStream(mknfEncoding.toString(), "UTF-8");
//		tmpSat.close();
//		String encoding = APEUtils.convert2humanReadable(tmpSat, mappings);
//		APEUtils.write2file(encoding, new File("/home/vedran/Desktop/tmp"), false);

		long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
		System.out.println("Total problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec.");

		return true;
	}
	

	/**
	 * Using the SAT input generated from SAT encoding and running MiniSAT solver to
	 * find the solutions
	 * 
	 * @param allSolutions - current set of {@link SAT_solution} that will be
	 *                     extended with newly found solutions, it
	 * @return {@code true} if the synthesis execution results in new candidate solutions, otherwise {@code false}.
	 */
	public boolean synthesisExecution() {

		List<SolutionWorkflow> currSolutions = runMiniSAT(temp_sat_input,
				allSolutions.getNumberOfSolutions(), allSolutions.getMaxNumberOfSolutions());
		/** Add current solutions to list of all solutions. */
		return allSolutions.addSolutions(currSolutions);
	}

	public String getCnfEncoding() {
		return cnfEncoding.toString();
	}

	/**
	 * Returns a set of {@link SAT_solution SAT_solutions} by parsing the SAT
	 * output. In case of the UNSAT the list is empty.
	 * 
	 * @param sat_input   		- CNF formula in dimacs form
	 * @param mappings          - atom mappings
	 * @param allModules        - list of all the modules
	 * @param allTypes          - list of all the types
	 * @param solutionsFoundMax
	 * @return List of {@link SAT_solution SAT_solutions}. Possibly empty list.
	 */
	public List<SolutionWorkflow> runMiniSAT(InputStream sat_input, int solutionsFound, int solutionsFoundMax) {
		List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
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
			IProblem problem = reader.parseInstance(sat_input); // loading CNF encoding of the problem
			realStartTime = System.currentTimeMillis();
			while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
				SolutionWorkflow sat_solution = new SolutionWorkflow(problem.model(), this);
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
			System.out.println("Error while parsing the cnf encoding of the problem by the MiniSAT solver.");
		} catch (ContradictionException e) {
			System.err.println("Unsatisfiable");
		} catch (TimeoutException e) {
			System.err.println("Timeout. Solving took longer than default timeout: " + timeout + " seconds.");
		} catch (Exception e) {
			System.err.println("Internal error while creating an object of SolutionWorkflow class.");
		}

		if (solutionsFound == 0 || solutionsFound % 500 != 0) {
			realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			System.out.println("Found " + solutionsFound + " solutions. Solving time: "
					+ (realTimeElapsedMillis / 1000F) + " sec.");
		}

		return solutions;
	}

	public AllModules getAllModules() {
		return allModules;
	}

	public AllTypes getAllTypes() {
		return allTypes;
	}

	public APEConfig getConfig() {
		return config;
	}

	public AtomMapping getMappings() {
		return mappings;
	}

	public ConstraintFactory getAllConsTemplates() {
		return allConsTemplates;
	}

	/**
	 * @return the field {@link unformattedConstr}
	 */
	public List<ConstraintData> getUnformattedConstr() {
		return unformattedConstr;
	}

	public All_SAT_solutions getAllSolutions() {
		return allSolutions;
	}

	public ModuleAutomaton getModuleAutomaton() {
		return moduleAutomaton;
	}

	public TypeAutomaton getTypeAutomaton() {
		return typeAutomaton;
	}

	/**
	 * Get size of the solution that is being synthesized.
	 * 
	 * @return Length of the solution.
	 */
	public int getSolutionSize() {
		return moduleAutomaton.size();
	}

}
