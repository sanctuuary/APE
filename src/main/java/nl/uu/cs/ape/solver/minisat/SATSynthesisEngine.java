package nl.uu.cs.ape.solver.minisat;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.domain.Domain;
import nl.uu.cs.ape.utils.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;
import nl.uu.cs.ape.models.Pair;
import nl.uu.cs.ape.models.SATAtomMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.logic.constructs.Predicate;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxFormula;
import nl.uu.cs.ape.models.sltlxStruc.SLTLxVariableOccurrenceCollection;
import nl.uu.cs.ape.solver.SynthesisEngine;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The {@code SATSynthesisEngine} class represents a <b>synthesis instance</b>,
 * i.e. it is represented with the set of inputs (tools, types, constraints and
 * workflow length that is being explored).
 * <p>
 * It is used to execute synthesis algorithm over the given input, implemented
 * using MiniSAT solver. The class implements general synthesis interface
 * {@link SynthesisEngine}.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class SATSynthesisEngine implements SynthesisEngine {
    /**
     * Object that contains all the domain information.
     */
    @Getter
    private final Domain domainSetup;

    /**
     * APE library configuration object.
     */
    @Getter
    private final APERunConfig runConfig;

    /**
     * Mapping of all the predicates to integers.
     */
    @Getter
    private final SATAtomMappings mappings;

    /**
     * Set of all the solutions found by the library.
     */
    @Getter
    private final SolutionsList allSolutions;

    /**
     * CNF encoding of the problem.
     */
    @Getter
    private File cnfEncoding;

    /**
     * File used as an input for the SAT solver.
     */
    @Getter
    private File satInputFile;

    /**
     * Representation of the tool part of the automaton used to encode the structure
     * of the solution.
     */
    @Getter
    private ModuleAutomaton moduleAutomaton;

    /**
     * Representation of the type part of the automaton used to encode the structure
     * of the solution.
     */
    @Getter
    private TypeAutomaton typeAutomaton;

    /**
     * Mapping of all the variables that are utilised in the encoding to the
     * predicates use them.
     */
    private SLTLxVariableOccurrenceCollection varUsage;

    /**
     * Variable used to count the encoding time of the APE instance run,
     * strictly used to display the APE CLI run stats.
     */
    private static AtomicLong totalEncodingTime = new AtomicLong(0);

    /**
     * Static variable used to count the SAT solving time of the APE instance run,
     * strictly used to display the APE CLI run stats.
     */
    private static AtomicLong totalSolvingTime = new AtomicLong(0);

    /**
     * Setup of an instance of the SAT synthesis engine.
     *
     * @param domainSetup    Domain information, including all the existing tools
     *                       and types.
     * @param allSolutions   Set of {@link SolutionWorkflow}.
     * @param runConfig      Setup configuration for the synthesis.
     * @param workflowLength Workflow length
     * @throws IOException - Error if the temp file cannot be created
     */
    public SATSynthesisEngine(Domain domainSetup, SolutionsList allSolutions,
            APERunConfig runConfig, int workflowLength) throws IOException {
        this.domainSetup = domainSetup;
        this.allSolutions = allSolutions;
        this.runConfig = runConfig;
        this.mappings = allSolutions.getMappings();
        this.mappings.resetAuxVariables();
        this.varUsage = new SLTLxVariableOccurrenceCollection();

        this.satInputFile = null;
        this.cnfEncoding = File.createTempFile("satCNF" + workflowLength, null);

        int maxNoToolInputs = Math.max(domainSetup.getMaxNoToolInputs(), runConfig.getProgramOutputs().size());
        int maxNoToolOutputs = Math.max(domainSetup.getMaxNoToolOutputs(), runConfig.getProgramInputs().size());
        moduleAutomaton = new ModuleAutomaton(workflowLength, maxNoToolInputs, maxNoToolOutputs);
        typeAutomaton = new TypeAutomaton(workflowLength, maxNoToolInputs, maxNoToolOutputs);
    }

    /**
     * Generate the SAT encoding of the workflow synthesis and return it as a
     * string.
     *
     * @return true if the encoding was performed successfully, false otherwise.
     * @throws IOException Error if taxonomies have not been setup properly.
     */
    public boolean synthesisEncoding() throws IOException {
        long problemSetupStartTime = System.currentTimeMillis();
        TaxonomyPredicate rootModule = domainSetup.getAllModules().getRootModule();

        if (rootModule == null) {
            log.error("Taxonomies have not been setup properly.");
            return false;
        }
        /* Generate the automaton */
        String currLengthTimer = "length" + this.getSolutionSize();
        APEUtils.timerStart(currLengthTimer, runConfig.getDebugMode());

        APEUtils.timerRestartAndPrint(currLengthTimer, "Automaton encoding");

        /*
         * Create constraints from the tool_annotations.json file regarding the
         * Inputs/Outputs, preserving the structure of input and output fields.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceModuleRelatedRules.moduleAnnotations(this));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

        /*
         * The constraints preserve the memory structure, i.e. preserve the data
         * available in memory and the
         * logic of referencing data from memory in case of tool inputs.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceModuleRelatedRules.memoryStructure(this));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Memory structure encoding");

        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the tools
         * 2. Mandatory usage of the tools - from taxonomy.
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        for (Pair<Predicate> pair : domainSetup.getAllModules().getSimplePairs()) {
            SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                    EnforceModuleRelatedRules.moduleMutualExclusion(pair, moduleAutomaton));
        }
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions encoding");

        SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                EnforceModuleRelatedRules.moduleMandatoryUsage(domainSetup.getAllModules(), moduleAutomaton));

        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceModuleRelatedRules
                .moduleTaxonomyStructure(domainSetup.getAllModules(), rootModule, moduleAutomaton));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage encoding");
        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the types/formats (according to the search model)
         * 2. Mandatory usage of the types in the transition nodes (note: "empty type"
         * is considered a type)
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        for (Pair<Predicate> pair : domainSetup.getAllTypes().getTypePairsForEachSubTaxonomy()) {
            SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                    EnforceTypeRelatedRules.memoryTypesMutualExclusion(pair, typeAutomaton));
        }
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions encoding");

        SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                EnforceTypeRelatedRules.typeMandatoryUsage(domainSetup, typeAutomaton));

        SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                EnforceTypeRelatedRules.typeEnforceTaxonomyStructure(domainSetup.getAllTypes(), typeAutomaton));
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage encoding");

        /*
         * Encode data ancestor relation (R) constraints.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceModuleRelatedRules.ancestorRelationsDependency(this));

        /*
         * Encode data equivalence/identity relation (IS) constraints.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this,
                EnforceModuleRelatedRules.identityRelationsDependency(typeAutomaton));

        /*
         * Setup encoding of 'true' and 'false' atoms to ensure proper SLTLx
         * interpretation.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceSLTLxRelatedRules.setTrueFalse());

        /*
         * Encode rule that the given inputs should not be used as workflow outputs
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceTypeRelatedRules
                .inputsAreNotOutputs(typeAutomaton));

        /*
         * Workflow I/O are encoded the last in order to
         * reuse the mappings for states, instead of introducing new ones, using the I/O
         * types of NodeType.UNKNOWN.
         * 
         * Encode the workflow input.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceTypeRelatedRules
                .workflowInputs(domainSetup.getAllTypes(), runConfig.getProgramInputs(), typeAutomaton));
        /*
         * Encode the workflow output
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceTypeRelatedRules
                .workdlowOutputs(domainSetup.getAllTypes(), runConfig.getProgramOutputs(), typeAutomaton));

        /*
         * Encode the constraints from the file based on the templates (manual
         * templates)
         */
        if (!domainSetup.getUnformattedConstr().isEmpty() || !domainSetup.getSLTLxConstraints().isEmpty()) {
            APEFiles.appendToFile(cnfEncoding,
                    SatEncodingUtils.encodeAPEConstraints(this, domainSetup, mappings, moduleAutomaton, typeAutomaton));
            APEUtils.timerRestartAndPrint(currLengthTimer, "SLTLx constraints");
        }
        /*
         * Setup the constraints ensuring that the auxiliary predicates are properly
         * used and linked to the underlying taxonomy predicates.
         */
        SLTLxFormula.appendCNFToFile(cnfEncoding, this, EnforceSLTLxRelatedRules
                .preserveAuxiliaryPredicateRules(moduleAutomaton, typeAutomaton, domainSetup.getHelperPredicates()));

        /*
         * Counting the number of variables and clauses that will be given to the SAT
         * solver. As an improvement, the number of lines could be stored, not to be
         * counted again.
         */
        int variables = mappings.getSize();
        int clauses = APEUtils.countLines(cnfEncoding);
        String satInputHeader = "p cnf " + variables + " " + clauses + "\n";
        APEUtils.timerRestartAndPrint(currLengthTimer, "Reading rows");
        satInputFile = APEFiles.prependToFile(satInputHeader, cnfEncoding);
        cnfEncoding.delete();

        /* add the cnf encoding file to Desktop */
        // Files.copy(satInputFile, new File("~/Desktop/tmp"+ problemSetupStartTime));

        /*
         * Add human readable version of the cnf encoding file to Desktop. Used when
         * needed.
         */
        // FileInputStream cnfStream = new FileInputStream(satInputFile);
        // String encoding = APEUtils.convertCNF2humanReadable(cnfStream, mappings);
        // cnfStream.close();
        // APEFiles.write2file(encoding, new File("~/Desktop/tmp.txt"), false);

        long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
        log.info("Total problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec (" + clauses
                + " clauses).");
        addEncodingTime(problemSetupTimeElapsedMillis);
        return true;
    }

    /**
     * Using the SAT input generated from SAT encoding and running MiniSAT solver to
     * find the solutions.
     *
     * @return The list of new solutions.
     * @throws IOException Error if the sat encoding file does not exist.
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
     * Returns a set of {@link SATOutput SAT_solutions} by parsing the SAT
     * output. In case of the UNSAT the list is empty.
     *
     * @param satInput CNF formula in dimacs form.
     * @return List of {@link SATOutput SAT_solutions}. Possibly empty list.
     */
    private List<SolutionWorkflow> runMiniSAT(InputStream satInput, int solutionsFound, int solutionsFoundMax) {
        List<SolutionWorkflow> solutions = new ArrayList<>();
        ISolver solver = SolverFactory.newDefault();
        long globalTimeoutMs = runConfig.getTimeoutMs();
        long currTimeout = APEUtils.timerTimeLeft("globalTimer", globalTimeoutMs);
        if (currTimeout <= 0) {
            log.warn("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
            return solutions;
        }
        // set timeout (in ms)
        solver.setTimeoutMs(currTimeout);
        long realStartTime = 0;
        long realTimeElapsedMillis;
        Reader reader = new DimacsReader(solver);
        try {
            // loading CNF encoding of the problem
            IProblem problem = reader.parseInstance(satInput);
            realStartTime = System.currentTimeMillis();
            while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
                SolutionWorkflow satSolution = new SolutionWorkflow(problem.model(), this);
                solutions.add(satSolution);
                solutionsFound++;
                if (solutionsFound % 500 == 0) {
                    realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
                    log.info("Found in total " + solutionsFound + " solutions. Solving time: "
                            + (realTimeElapsedMillis / 1000F) + " sec.");
                }
                /*
                 * Adding the negation of the positive part of the solution as a constraint
                 * (default negation does not work)
                 */
                IVecInt negSol = new VecInt(((SATOutput) satSolution.getNativeSolution())
                        .getNegatedMappedSolutionArray(runConfig.getAllowToolSeqRepeat()));
                solver.addClause(negSol);
            }
            satInput.close();
        } catch (ParseFormatException e) {
            log.error("Error while parsing the cnf encoding of the problem by the MiniSAT solver.");
            log.error(e.getMessage());
            return solutions;
        } catch (ContradictionException e) {
            if (solutionsFound == 0) {
                log.warn("Unsatisfiable");
                return solutions;
            }
        } catch (TimeoutException e) {
            log.warn("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
        } catch (IOException e) {
            log.warn("Internal error while parsing the encoding.");
            return solutions;
        }

        if (solutionsFound == 0 || solutionsFound % 500 != 0) {
            realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
            log.info("Found " + solutionsFound + " solutions. Solving time: "
                    + (realTimeElapsedMillis / 1000F) + " sec.");
            addSolvingTime(realTimeElapsedMillis);
        }

        return solutions;
    }

    private void addEncodingTime(long time) {
        totalEncodingTime.addAndGet(time);
    }

    public static long getTotalEncodingTime() {
        return totalEncodingTime.get();
    }

    private void addSolvingTime(long time) {
        totalSolvingTime.addAndGet(time);
    }

    public static long getTotalSolvingTime() {
        return totalSolvingTime.get();
    }

    /**
     * Gets empty type.
     *
     * @return The {@link Type} object that represents the empty type, i.e. absence
     *         of types.
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
     * Get mapping of all the variables that are utilised in the encoding to the
     * predicates use them.
     * 
     * @return Variable usage class {@link SLTLxVariableOccurrenceCollection}.
     */
    public SLTLxVariableOccurrenceCollection getVariableUsage() {
        return varUsage;
    }

    /**
     * Delete all temporary files created.
     * 
     * @throws IOException - Failed to delete temp files.
     */
    public void deleteTempFiles() throws IOException {
        try {
            Files.delete(cnfEncoding.toPath());
            Files.delete(satInputFile.toPath());
        } catch (IOException ignored) {
        }
    }

}
