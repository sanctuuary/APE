package nl.uu.cs.ape.core.implSMT;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import nl.uu.cs.ape.automaton.ModuleAutomaton;
import nl.uu.cs.ape.automaton.State;
import nl.uu.cs.ape.automaton.TypeAutomaton;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.core.SynthesisEngine;
import nl.uu.cs.ape.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.core.solutionStructure.SolutionsList;
import nl.uu.cs.ape.models.SMTPredicateMappings;
import nl.uu.cs.ape.models.Type;
import nl.uu.cs.ape.models.enums.WorkflowElement;
import nl.uu.cs.ape.models.logic.constructs.Atom;
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.utils.APEDomainSetup;
import nl.uu.cs.ape.utils.APEUtils;

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
public class SMTSynthesisEngine implements SynthesisEngine {

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
    private final SMTPredicateMappings mappings;

    /**
     * Set of all the solutions found by the library.
     */
    private final SolutionsList allSolutions;

    /**
     * CNF encoding of the problem.
     */
    private File smtEncoding;

    /**
     * File used as an input for the SAT solver.
     */
    private File smtInputFile;

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
    public SMTSynthesisEngine(APEDomainSetup domainSetup, SolutionsList allSolutions,
                               APERunConfig runConfig, int workflowLength) throws IOException {
        this.domainSetup = domainSetup;
        this.allSolutions = allSolutions;
        this.runConfig = runConfig;
        this.mappings = (SMTPredicateMappings) allSolutions.getMappings();
        this.smtInputFile = null;
        this.smtEncoding = File.createTempFile("smt2lib_" + workflowLength, null);
//        APEUtils.appendToFile(smtEncoding, "(set-option :produce-unsat-cores true)\n"); 

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

        /* Define the SMT data types that correspond to taxonomy terms. */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeTaxonomyTerms(this), mappings);
        
        /* Define the structure of the workflow. Define each operation and data state. */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeWorkflowStructure(this), mappings);
        
        /* Create constraints from the tool_annotations.json file regarding the Inputs/Outputs, preserving the structure of input and output fields. */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeModuleAnnotations(this), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

        /*
         * The constraints preserve the memory structure, i.e. preserve the data available in memory and the
         * logic of referencing data from memory in case of tool inputs.
         */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeMemoryStructure(this), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Memory structure encoding");

        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the tools
         * 2. Mandatory usage of the tools - from taxonomy.
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleMutualExclusion(domainSetup.getAllModules()), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleMandatoryUsage(domainSetup.getAllModules()), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleEnforceTaxonomyStructure(rootModule, true), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool taxonomy  encoding");
        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the types/formats (according to the search model)
         * 2. Mandatory usage of the types in the transition nodes (note: "empty type" is considered a type)
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeMutualExclusion(domainSetup.getAllTypes()), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeMandatoryUsage(domainSetup), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeEnforceTaxonomyStructure(domainSetup.getAllTypes()), mappings);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type taxonomy encoding");
        /*
         * Encode the constraints from the file based on the templates (manual templates)
         */
//        if (domainSetup.getUnformattedConstr() != null && !domainSetup.getUnformattedConstr().isEmpty()) {
//        	APEUtils.appendToFile(smtEncoding, APEUtils.encodeAPEConstraints(domainSetup, mappings, moduleAutomaton, typeAutomaton));
//            APEUtils.timerRestartAndPrint(currLengthTimer, "SLTL constraints");
//        }
//        
        /*
         * Encode data instance dependency constraints.
         */
//        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeDataInstanceDependencyCons(typeAutomaton), mappings);

        /*
         * Encode the workflow input. Workflow I/O are encoded the last in order to
         * reuse the mappings for states, instead of introducing new ones, using the I/O
         * types of NodeType.UNKNOWN.
         */
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.encodeInputData(domainSetup.getAllTypes(), runConfig.getProgramInputs(), typeAutomaton), mappings);
        /*
         * Encode the workflow output
         */
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.encodeOutputData(domainSetup.getAllTypes(), runConfig.getProgramOutputs(), typeAutomaton), mappings);

        
        /*
         * Setup the constraints ensuring that the auxiliary predicates are properly used and linked to the underlying taxonomy predicates.
         */
//        SMTUtils.appendToFile(smtEncoding, domainSetup.getConstraintsForAuxiliaryPredicates(mappings, moduleAutomaton, typeAutomaton), mappings);

        
        
        /*
         * Create the file that contains the encoding
         */
        System.out.println(smtEncoding);
        APEUtils.appendToFile(smtEncoding, "\n(check-sat)"); 
        APEUtils.appendToFile(smtEncoding, "\n(get-model)");
//        APEUtils.appendToFile(smtEncoding, "\n(get-unsat-core)"); 
        
        smtInputFile = new File("/home/vedran/Desktop/tmp.smt");
        FileUtils.copyFile(smtEncoding, smtInputFile);
//        smtEncoding.delete();
//		APEUtils.write2file(mknfEncoding.toString(), new File("/home/vedran/Desktop/tmp"+ problemSetupStartTime), false);

        
        /* testing sat input */
//        File actualFile = new File ("/home/vedran/Desktop/tmpt.txt");
//		InputStream tmpSat = IOUtils.toInputStream(smtInputFile.toString(), "ASCII");
//		tmpSat.close();
//		String encoding = APEUtils.convertCNF2humanReadable(new FileInputStream(smtInputFile), mappings);
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

    	 /* Add current solutions to list of all solutions. */
        List<SolutionWorkflow> currSolutions = runZ3Once(smtInputFile.getAbsolutePath(),
                allSolutions.getNumberOfSolutions(), allSolutions.getMaxNumberOfSolutions());
       
        return currSolutions;
    }

    /**
     * Gets SMTLIB2 encoding.
     *
     * @return the SMTLIB2 encoding
     */
    public String getSMT2Encoding() {
        return smtEncoding.toString();
    }
    
    
    private List<SolutionWorkflow> runZ3Once(String absolutePath, int solutionsFound, int solutionsFoundMax) {
        List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
//        ISolver solver = SolverFactory.newDefault();
        long globalTimeoutMs = runConfig.getTimeoutMs();
        // set timeout (in ms)
//        solver.setTimeoutMs(currTimeout);
        long realStartTime = 0;
        long realTimeElapsedMillis;
//        Reader reader = new DimacsReader(solver);
        try {
            // loading CNF encoding of the problem
            realStartTime = System.currentTimeMillis();
            boolean satisfiable = true;
            while (solutionsFound < solutionsFoundMax && satisfiable) {
	            ProcessBuilder builder = new ProcessBuilder("/home/vedran/git/z3/build/z3", absolutePath);
	            builder.redirectErrorStream(true);
	            final Process process = builder.start();
            
	            // Watch the process
	            List<Atom> facts = readTerminalOutput(process);
	            if(facts == null) {
	            	satisfiable = false;
	            	continue;
	            }
	            SolutionWorkflow sat_solution = new SolutionWorkflow(facts, this);
	            solutionsFound++;
	            solutions.add(sat_solution);
	            realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
	            System.out.println("Found in total " + solutionsFound + " solutions. Solving time: "
	                    + (realTimeElapsedMillis / 1000F) + " sec.");
            /*
             * Adding the negation of the positive part of the solution as a constraint
             * (default negation does not work)
             */
//                IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray(runConfig.getToolSeqRepeat()));
//                solver.addClause(negSol);
	            process.destroy();
            }
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
     * TODO implement better
     * @param process
     * @return
     */
    private List<Atom> readTerminalOutput(final Process process) {
    	List<Atom> atoms = new ArrayList<Atom>();
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null; 
                try {
                	WorkflowElement currFun = null;
                    while ((line = input.readLine()) != null) {
                    	if(line.equals("unsat")) {
                    		return null;
                    	} else if(line.startsWith("(error ")) {
                    		System.err.println("SMT_error: " +line);
                    		return null;
                    	}
                    	System.out.println("SMT: " +line);
                    	if(line.contains("nullMem")) {
                    		continue;
                    	} else if(line.trim().startsWith("(define-fun ")) {
                    		String[] words =  line.trim().replace(")", "").split(" ");
                    		currFun = getElement(words[1]);
                    	} else if ((currFun != null) & line.contains("x!0")) {
                    		String trimmedLine = cutUntil(line, "x!0");
                    		String[] words =  trimmedLine.trim().replace(")", "").split(" ");
                    		
                    		String stateS = words[1];
                    		String predicateS = words[4];
                    		
                    		PredicateLabel arg1 = mappings.findOriginal(stateS);
                    		PredicateLabel arg2 = mappings.findOriginal(predicateS);
                    		Atom currAtom  = new Atom(arg2, (State) arg1, currFun);
                    		atoms.add(currAtom);
                    	}
                    	
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        return atoms;
    }
    
    private WorkflowElement getElement(String string) {
		switch (string) {
		case "module":
			return WorkflowElement.MODULE;
		case "usedType":
			return WorkflowElement.USED_TYPE;
		case "memType":
			return WorkflowElement.MEMORY_TYPE;
		case "memRef":
			return WorkflowElement.MEM_TYPE_REFERENCE;
		default:
			return null;
		}
	}
    
    private static String cutUntil(String text, String subStr) {
    	if(text.contains(subStr)) {
    		return text.substring(text.indexOf(subStr));
    	} else {
    		return text;
    	}
    	
    }
    
    /**
     * Returns a set of {@link SATSolution SAT_solutions} by parsing the SAT
     * output. In case of the UNSAT the list is empty.
     *
     * @param sat_input CNF formula in dimacs form.
     * @return List of {@link SATSolution SAT_solutions}. Possibly empty list.
     */
//    private List<SolutionWorkflow> runZ3(String absolutePath, int solutionsFound, int solutionsFoundMax) {
//        List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
////        ISolver solver = SolverFactory.newDefault();
//        long globalTimeoutMs = runConfig.getTimeoutMs();
//		long currTimeout = APEUtils.timerTimeLeft("globalTimer", globalTimeoutMs);
//		if (currTimeout <= 0) {
//			System.err.println("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
//			return solutions;
//		}
//        // set timeout (in ms)
////        solver.setTimeoutMs(currTimeout);
//        long realStartTime = 0;
//        long realTimeElapsedMillis;
////        Reader reader = new DimacsReader(solver);
//        try {
//            // loading CNF encoding of the problem
//            IProblem problem = reader.parseInstance(sat_input);
//            realStartTime = System.currentTimeMillis();
//            while (solutionsFound < solutionsFoundMax && problem.isSatisfiable()) {
//                SolutionWorkflow sat_solution = new SolutionWorkflow(problem.model(), this);
//                solutions.add(sat_solution);
//                solutionsFound++;
//                if (solutionsFound % 500 == 0) {
//                    realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
//                    System.out.println("Found in total " + solutionsFound + " solutions. Solving time: "
//                            + (realTimeElapsedMillis / 1000F) + " sec.");
//                }
//                /*
//                 * Adding the negation of the positive part of the solution as a constraint
//                 * (default negation does not work)
//                 */
//                IVecInt negSol = new VecInt(sat_solution.getNegatedMappedSolutionArray(runConfig.getToolSeqRepeat()));
//                solver.addClause(negSol);
//            }
//            sat_input.close();
//        } catch (ParseFormatException e) {
//            System.out.println("Error while parsing the cnf encoding of the problem by the MiniSAT solver.");
//            System.err.println(e.getMessage());
//        } catch (ContradictionException e) {
//            if (solutionsFound == 0) {
//                System.err.println("Unsatisfiable");
//            }
//        } catch (TimeoutException e) {
//            System.err.println("Timeout. Total solving took longer than the timeout: " + globalTimeoutMs + " ms.");
//        } catch (IOException e) {
//            System.err.println("Internal error while parsing the encoding.");
//        }
//
//        if (solutionsFound == 0 || solutionsFound % 500 != 0) {
//            realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
//            System.out.println("Found " + solutionsFound + " solutions. Solving time: "
//                    + (realTimeElapsedMillis / 1000F) + " sec.");
//        }
//
//        return solutions;
//    }
    
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
    public SMTPredicateMappings getMappings() {
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
		smtEncoding.delete();
//		smtInputFile.delete();
		
	}

}