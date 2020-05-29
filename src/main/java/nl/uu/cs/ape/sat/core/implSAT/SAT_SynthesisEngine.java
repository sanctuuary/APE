package nl.uu.cs.ape.sat.core.implSAT;

import java.io.IOException;
import java.io.InputStream;
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
import nl.uu.cs.ape.sat.core.SynthesisEngine;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.SATModuleUtils;
import nl.uu.cs.ape.sat.models.SATEncodingUtils.SATTypeUtils;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;

/**
 * The {@code SAT_SynthesisEngine} class represents a <b>synthesis instance</b>,
 * i.e. it is represented with the set of inputs (tools, types, constraints and
 * workflow length that is being explored).<br>
 * It is used to execute synthesis algorithm over the given input, implemented
 * using MiniSAT solver. <br>
 * <br>
 * The class implements general synthesis interface {@link SynthesisEngine}.
 * 
 * @author Vedran Kasalica
 *
 */
public class SAT_SynthesisEngine implements SynthesisEngine {

	/** Object that contains all the domain information. */
	private final APEDomainSetup domainSetup;
	/** APE library configuration object. */
	private final APEConfig config;
	/** Mapping of all the predicates to integers. */
	private final AtomMappings mappings;
	/** Set of all the solutions found by the library. */
	private final SATsolutionsList allSolutions;
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
	 * @param domainSetup
	 * @param allSolutions
	 * @param config
	 */
	public SAT_SynthesisEngine(APEDomainSetup domainSetup, SATsolutionsList allSolutions,
			APEConfig config, int size) {
		this.domainSetup = domainSetup;
		this.allSolutions = allSolutions;
		this.config = config;
		allSolutions.newEncoding();
		this.mappings = allSolutions.getMappings();
		this.temp_sat_input = null;
		this.cnfEncoding = new StringBuilder();

		moduleAutomaton = new ModuleAutomaton(size, config.getMaxNoToolOutputs());
		typeAutomaton = new TypeAutomaton(size, config.getMaxNoToolInputs(), config.getMaxNoToolOutputs());

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
		TaxonomyPredicate rootModule = domainSetup.getAllModules().getRootPredicates().get(0);

		if(rootModule == null) {
			System.err.println("Taxonomies have not been setup properly.");
			return false;
		}
		/*
		 * Generate the automaton
		 */
		String currLengthTimer = "length" + this.getSolutionSize();
		APEUtils.timerStart(currLengthTimer, config.getDebugMode());

		APEUtils.timerRestartAndPrint(currLengthTimer, "Automaton encoding");

		/*
		 * Create constraints from the module.xml file regarding the Inputs/Outputs, preserving the structure of input and output fields.
		 */
		cnfEncoding = cnfEncoding.append(SATModuleUtils.encodeModuleAnnotations(this));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

		/*
		 * The constraints preserve the memory structure (e.g. shared memory structure), i.e. preserve the data available in memory and the
		 * logic of referencing data from memory in case of tool inputs.
		 */
		cnfEncoding = cnfEncoding.append(SATModuleUtils.encodeMemoryStructure(this));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Memory structure encoding");
		
		/*
		 * Create the constraints enforcing: 
		 * 1. Mutual exclusion of the tools 
		 * 2. Mandatory usage of the tools - from taxonomy. 
		 * 3. Adding the constraints enforcing the taxonomy structure.
		 */
		cnfEncoding = cnfEncoding.append(SATModuleUtils.moduleMutualExclusion(domainSetup.getAllModules(),moduleAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions encoding");
		cnfEncoding = cnfEncoding.append(SATModuleUtils.moduleMandatoryUsage(domainSetup.getAllModules(), moduleAutomaton, mappings));
		cnfEncoding = cnfEncoding.append(
				SATModuleUtils.moduleEnforceTaxonomyStructure(domainSetup.getAllModules(), rootModule, moduleAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage encoding");
		/*
		 * Create the constraints enforcing: 
		 * 1. Mutual exclusion of the types/formats 
		 * 2. Mandatory usage of the types in the transition nodes (note: "empty type" is considered a type) 
		 * 3. Adding the constraints enforcing the taxonomy structure.
		 */
		cnfEncoding = cnfEncoding.append(SATTypeUtils.typeMutualExclusion(domainSetup.getAllTypes(), typeAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions encoding");
		cnfEncoding = cnfEncoding.append(SATTypeUtils.typeMandatoryUsage(domainSetup, typeAutomaton, mappings));
		cnfEncoding = cnfEncoding.append(SATTypeUtils.typeEnforceTaxonomyStructure(domainSetup.getAllTypes(), typeAutomaton, mappings));
		APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage encoding");
		/*
		 * Encode the constraints from the file based on the templates (manual templates)
		 */
		if (domainSetup.getUnformattedConstr() != null && !domainSetup.getUnformattedConstr().isEmpty()) {
			cnfEncoding = cnfEncoding
					.append(APEUtils.encodeAPEConstraints(domainSetup, mappings, moduleAutomaton, typeAutomaton));
			APEUtils.timerRestartAndPrint(currLengthTimer, "SLTL constraints");
		}
		/*
		 * Encode the workflow input. Workflow I/O are encoded the last in order to
		 * reuse the mappings for states, instead of introducing new ones, using the I/O
		 * types of NodeType.UNKNOWN.
		 */
		String inputDataEncoding = SATTypeUtils.encodeInputData(domainSetup.getAllTypes(), config.getProgramInputs(), typeAutomaton, mappings);
		if (inputDataEncoding == null) {
			return false;
		}
		cnfEncoding = cnfEncoding.append(inputDataEncoding);
		/*
		 * Encode the workflow output
		 */
		String outputDataEncoding = SATTypeUtils.encodeOutputData(domainSetup.getAllTypes(), config.getProgram_outputs(), typeAutomaton, mappings);
		if (outputDataEncoding == null) {
			return false;
		}
		cnfEncoding = cnfEncoding.append(outputDataEncoding);

		/*
		 * Setup the constraints ensuring that the auxiliary predicates are properly used and linked to the underlying taxonomy predicates.
		 */
		cnfEncoding = cnfEncoding.append(domainSetup.getConstraintsForAuxiliaryPredicates(mappings, moduleAutomaton, typeAutomaton));
		
		/*
		 * Counting the number of variables and clauses that will be given to the SAT solver 
		 * TODO Improve this approach, no need to read the whole String again to cound lines.
		 */
		int variables = mappings.getSize();
		int clauses = APEUtils.countNewLines(cnfEncoding.toString());
		StringBuilder sat_input_header = new StringBuilder("p cnf " + variables + " " + clauses + "\n");
		APEUtils.timerRestartAndPrint(currLengthTimer, "Reading rows");
		System.out.println();
		
		StringBuilder mknfEncoding = sat_input_header.append(cnfEncoding);
//		APEUtils.write2file(mknfEncoding.toString(), new File("/home/vedran/Desktop/tmp"+ problemSetupStartTime), false);
		

		temp_sat_input = IOUtils.toInputStream(mknfEncoding.toString(), "UTF-8");
		temp_sat_input.close();
		
		/* testing sat input */
//		InputStream tmpSat = IOUtils.toInputStream(mknfEncoding.toString(), "UTF-8");
//		tmpSat.close();
//		String encoding = APEUtils.convertCNF2humanReadable(tmpSat, mappings);
//		APEUtils.write2file(encoding, new File("/home/vedran/Desktop/tmp.txt"), false);

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
		/* Add current solutions to list of all solutions. */
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
	 * @param solutionsFoundMax
	 * @return List of {@link SAT_solution SAT_solutions}. Possibly empty list.
	 */
	private List<SolutionWorkflow> runMiniSAT(InputStream sat_input, int solutionsFound, int solutionsFoundMax) {
		List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
		ISolver solver = SolverFactory.newDefault();
		int timeout = 3600;
		// 1 hour timeout
		solver.setTimeout(timeout); 
		long realStartTime = 0;
		long realTimeElapsedMillis;
		Reader reader = new DimacsReader(solver);
		try {
			// loading CNF encoding of the problem
			IProblem problem = reader.parseInstance(sat_input); 
			realStartTime = System.currentTimeMillis();
			while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
				SolutionWorkflow sat_solution = new SolutionWorkflow(problem.model(), this);
				solutions.add(sat_solution);
				solutionsFound++;
				if (solutionsFound % 500 == 0) {
					realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
					System.out.println("Found in total " + solutionsFound + " solutions. Solving time: "
							+ (realTimeElapsedMillis / 1000F) + " sec.");
				}
				/*
				 * Adding the negation of the positive part of the solution as a constraint
				 * (default negation does not work)
				 */
				
				IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray(config.getToolSeqRepeat()));
				solver.addClause(negSol);
			}
		} catch (ParseFormatException e) {
			System.out.println("Error while parsing the cnf encoding of the problem by the MiniSAT solver.");
		} catch (ContradictionException e) {
			if(solutionsFound == 0) {
				System.err.println("Unsatisfiable");
			}
		} catch (TimeoutException e) {
			System.err.println("Timeout. Solving took longer than the default timeout: " + timeout + " seconds.");
		} catch (IOException e) {
			System.err.println("Internal error while parsing the encoding.");
		}

		if (solutionsFound == 0 || solutionsFound % 500 != 0) {
			realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
			System.out.println("Found " + solutionsFound + " solutions. Solving time: "
					+ (realTimeElapsedMillis / 1000F) + " sec.");
		}

		return solutions;
	}


	public APEConfig getConfig() {
		return config;
	}
	
	public APEDomainSetup getDomainSetup() {
		return domainSetup;
	}

	public AtomMappings getMappings() {
		return mappings;
	}

	public SATsolutionsList getAllSolutions() {
		return allSolutions;
	}

	public ModuleAutomaton getModuleAutomaton() {
		return moduleAutomaton;
	}

	public TypeAutomaton getTypeAutomaton() {
		return typeAutomaton;
	}
	
	/** 
	 * Return the {@link Type} object that represents the empty type, i.e. absence of types.
	 * @return
	 */
	public Type getEmptyType() {
		return domainSetup.getAllTypes().getEmptyType();
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
