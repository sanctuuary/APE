package nl.uu.cs.ape.sat.core.implSAT;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.SynthesisEngine;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionsList;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.configuration.APERunConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.apache.commons.io.IOUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code SATSynthesisEngine} class represents a <b>synthesis instance</b>,
 * i.e. it is represented with the set of inputs (tools, types, constraints and
 * workflow length that is being explored).
 * <p>
 * It is used to execute synthesis algorithm over the given input, implemented
 * using MiniSAT solver. The class implements general synthesis interface {@link SynthesisEngine}.
 *
 * @author Vedran Kasalica
 */
public class SATSynthesisEngine implements SynthesisEngine {

    /**
     * Object that contains all the domain information.
     */
    private final APEDomainSetup domainSetup;

    /**
     * APE library configuration object.
     */
    private final APERunConfig runConfig;

    /**
     * Mapping of all the predicates to integers.
     */
    private final AtomMappings mappings;

    /**
     * Set of all the solutions found by the library.
     */
    private final SolutionsList allSolutions;

    /**
     * CNF encoding of the problem.
     */
    private File cnfEncoding;

    /**
     * File used as an input for the SAT solver.
     */
    private File satInputFile;

    /**
     * Representation of the tool part of the automaton used to encode the structure of the solution.
     */
    private ModuleAutomaton moduleAutomaton;

    /**
     * Representation of the type part of the automaton used to encode the structure of the solution.
     */
    private TypeAutomaton typeAutomaton;
    
    /**
     * Setup of an instance of the SAT synthesis engine.
     *
     * @param domainSetup  Domain information, including all the existing tools and types.
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @param runConfig    Setup configuration for the synthesis.
     * @param workflowLength         Workflow length
     * @throws IOException - Error if the temp file cannot be created
     */
    public SATSynthesisEngine(APEDomainSetup domainSetup, SolutionsList allSolutions,
                               APERunConfig runConfig, int workflowLength) throws IOException {
        this.domainSetup = domainSetup;
        this.allSolutions = allSolutions;
        this.runConfig = runConfig;
        allSolutions.newEncoding();
        this.mappings = allSolutions.getMappings();
        this.satInputFile = null;
        this.cnfEncoding = File.createTempFile("satCNF" + workflowLength, null);

        int maxNoToolInputs = Math.max(domainSetup.getMaxNoToolInputs(), runConfig.getProgramOutputs().size());
        int maxNoToolOutputs = Math.max(domainSetup.getMaxNoToolOutputs(), runConfig.getProgramInputs().size());
        moduleAutomaton = new ModuleAutomaton(workflowLength, maxNoToolInputs, maxNoToolOutputs);
        typeAutomaton = new TypeAutomaton(workflowLength, maxNoToolInputs, maxNoToolOutputs);
    }

    /**
     * Generate the SAT encoding of the workflow synthesis and return it as a string.
     *
     * @return true if the encoding was performed successfully, false otherwise.
     * @throws IOException Error if taxonomies have not been setup properly.
     */
    public boolean synthesisEncoding() throws IOException {

        long problemSetupStartTime = System.currentTimeMillis();
        TaxonomyPredicate rootModule = domainSetup.getAllModules().getRootModule();

        if (rootModule == null) {
            System.err.println("Taxonomies have not been setup properly.");
            return false;
        }
        /* Generate the automaton */
        String currLengthTimer = "length" + this.getSolutionSize();
        APEUtils.timerStart(currLengthTimer, runConfig.getDebugMode());

        APEUtils.timerRestartAndPrint(currLengthTimer, "Automaton encoding");

        /* Create constraints from the tool_annotations.json file regarding the Inputs/Outputs, preserving the structure of input and output fields. */
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.encodeModuleAnnotations(this));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

        /*
         * The constraints preserve the memory structure, i.e. preserve the data available in memory and the
         * logic of referencing data from memory in case of tool inputs.
         */
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.encodeMemoryStructure(this));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Memory structure encoding");

        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the tools
         * 2. Mandatory usage of the tools - from taxonomy.
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.moduleMutualExclusion(domainSetup.getAllModules(), moduleAutomaton, mappings));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions encoding");
        
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.moduleMandatoryUsage(domainSetup.getAllModules(), moduleAutomaton, mappings));
        
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.moduleEnforceTaxonomyStructure(domainSetup.getAllModules(), rootModule, moduleAutomaton, mappings));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage encoding");
        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the types/formats (according to the search model)
         * 2. Mandatory usage of the types in the transition nodes (note: "empty type" is considered a type)
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        APEUtils.appendToFile(cnfEncoding, SATTypeUtils.typeMutualExclusion(domainSetup.getAllTypes(), typeAutomaton, mappings));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions encoding");
        
        APEUtils.appendToFile(cnfEncoding, SATTypeUtils.typeMandatoryUsage(domainSetup, typeAutomaton, mappings));
        
        APEUtils.appendToFile(cnfEncoding, SATTypeUtils.typeEnforceTaxonomyStructure(domainSetup.getAllTypes(), typeAutomaton, mappings));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage encoding");
        /*
         * Encode the constraints from the file based on the templates (manual templates)
         */
        if (domainSetup.getUnformattedConstr() != null && !domainSetup.getUnformattedConstr().isEmpty()) {
        	APEUtils.appendToFile(cnfEncoding, APEUtils.encodeAPEConstraints(domainSetup, mappings, moduleAutomaton, typeAutomaton));
            APEUtils.timerRestartAndPrint(currLengthTimer, "SLTL constraints");
        }
        
        /*
         * Encode data instance dependency constraints.
         */
        APEUtils.appendToFile(cnfEncoding, SATModuleUtils.encodeDataInstanceDependencyCons(typeAutomaton, mappings));

        /*
         * Encode the workflow input. Workflow I/O are encoded the last in order to
         * reuse the mappings for states, instead of introducing new ones, using the I/O
         * types of NodeType.UNKNOWN.
         */
        String inputDataEncoding = SATTypeUtils.encodeInputData(domainSetup.getAllTypes(), runConfig.getProgramInputs(), typeAutomaton, mappings);
        if (inputDataEncoding == null) {
            return false;
        }
        APEUtils.appendToFile(cnfEncoding, inputDataEncoding);
        /*
         * Encode the workflow output
         */
        String outputDataEncoding = SATTypeUtils.encodeOutputData(domainSetup.getAllTypes(), runConfig.getProgramOutputs(), typeAutomaton, mappings);
        if (outputDataEncoding == null) {
            return false;
        }
        APEUtils.appendToFile(cnfEncoding, outputDataEncoding);

        /*
         * Setup the constraints ensuring that the auxiliary predicates are properly used and linked to the underlying taxonomy predicates.
         */
        APEUtils.appendToFile(cnfEncoding, domainSetup.getConstraintsForAuxiliaryPredicates(mappings, moduleAutomaton, typeAutomaton));

        /*
         * Counting the number of variables and clauses that will be given to the SAT solver
         * TODO Improve this approach, no need to read the whole String again to count lines.
         */
        int variables = mappings.getSize();
        int clauses = APEUtils.countLines(cnfEncoding);
        
        String sat_input_header = "p cnf " + variables + " " + clauses + "\n";
        APEUtils.timerRestartAndPrint(currLengthTimer, "Reading rows");
        System.out.println();
        
        satInputFile = APEUtils.concatIntoFile(sat_input_header, cnfEncoding);
        cnfEncoding.delete();
//		APEUtils.write2file(mknfEncoding.toString(), new File("/home/vedran/Desktop/tmp"+ problemSetupStartTime), false);

        
        /* testing sat input */
//        File actualFile = new File ("/home/vedran/Desktop/tmpt.txt");
//		InputStream tmpSat = IOUtils.toInputStream(satInputFile.toString(), "ASCII");
//		tmpSat.close();
//		String encoding = APEUtils.convertCNF2humanReadable(new FileInputStream(satInputFile), mappings);
//		APEUtils.write2file(encoding, new File("/home/vedran/Desktop/tmp.txt"), false);

        long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
        System.out.println("Total problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec.");

        return true;
    }


    /**
     * Using the SAT input generated from SAT encoding and running MiniSAT solver to find the solutions.
     *
     * @return The list of new solutions.
     * @throws IOException  Error if the sat encoding file does not exist.
     */
    public List<SolutionWorkflow> synthesisExecution() throws IOException {

    	InputStream tmpSatInput = new FileInputStream(satInputFile);
        List<SolutionWorkflow> currSolutions = runMiniSAT(tmpSatInput,
                allSolutions.getNumberOfSolutions(), allSolutions.getMaxNumberOfSolutions());
        tmpSatInput.close();
        /* Add current solutions to list of all solutions. */
        return currSolutions;
    }

    /**
     * Gets cnf encoding.
     *
     * @return the cnf encoding
     */
    public String getCnfEncoding() {
        return cnfEncoding.toString();
    }

    /**
     * Returns a set of {@link SATSolution SAT_solutions} by parsing the SAT
     * output. In case of the UNSAT the list is empty.
     *
     * @param sat_input CNF formula in dimacs form.
     * @return List of {@link SATSolution SAT_solutions}. Possibly empty list.
     */
    private List<SolutionWorkflow> runMiniSAT(InputStream sat_input, int solutionsFound, int solutionsFoundMax) {
        List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
        ISolver solver = SolverFactory.newDefault();
        long globalTimeoutMs = runConfig.getTimeoutMs();
		long currTimeout = APEUtils.timerTimeLeft("globalTimer", globalTimeoutMs);
		if (currTimeout <= 0) {
			System.err.println("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
			return solutions;
		}
        // set timeout (in ms)
        solver.setTimeoutMs(currTimeout);
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
                IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray(runConfig.getToolSeqRepeat()));
                solver.addClause(negSol);
            }
            sat_input.close();
        } catch (ParseFormatException e) {
            System.out.println("Error while parsing the cnf encoding of the problem by the MiniSAT solver.");
            System.err.println(e.getMessage());
        } catch (ContradictionException e) {
            if (solutionsFound == 0) {
                System.err.println("Unsatisfiable");
            }
        } catch (TimeoutException e) {
            System.err.println("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
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


    /**
     * Gets run configuration.
     *
     * @return the runConfig
     */
    public APERunConfig getConfig() {
        return runConfig;
    }

    /**
     * Gets domain setup.
     *
     * @return the domain setup
     */
    public APEDomainSetup getDomainSetup() {
        return domainSetup;
    }

    /**
     * Gets mappings.
     *
     * @return the mappings
     */
    public AtomMappings getMappings() {
        return mappings;
    }

    /**
     * Gets all solutions.
     *
     * @return the all solutions
     */
    public SolutionsList getAllSolutions() {
        return allSolutions;
    }

    /**
     * Gets module automaton.
     *
     * @return the module automaton
     */
    public ModuleAutomaton getModuleAutomaton() {
        return moduleAutomaton;
    }

    /**
     * Gets type automaton.
     *
     * @return the type automaton
     */
    public TypeAutomaton getTypeAutomaton() {
        return typeAutomaton;
    }

    /**
     * Gets empty type.
     *
     * @return The {@link Type} object that represents the empty type, i.e. absence of types.
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

    /**
     * Delete all temporary files created.
     */
	public void deleteTempFiles() {
		cnfEncoding.delete();
		satInputFile.delete();
		
	}

}
