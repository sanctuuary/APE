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
import nl.uu.cs.ape.models.logic.constructs.PredicateLabel;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.models.satStruc.Atom;
import nl.uu.cs.ape.models.smtStruc.LogicFragmentDeclaration;
import nl.uu.cs.ape.models.smtStruc.boolStatements.SMTDataType;
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
        this.smtEncoding = File.createTempFile("smtlib2_" + workflowLength, null);
        this.smtEncoding.deleteOnExit();
        
        if(runConfig.getZ3LogicFragment() != null) {
        	APEUtils.appendToFile(smtEncoding, new LogicFragmentDeclaration(runConfig.getZ3LogicFragment()).getSMT2Encoding(this));
        }
        
        
        APEUtils.appendToFile(smtEncoding, runConfig.getSMTOptions());
//        APEUtils.appendToFile(smtEncoding, "(set-option :pp.bv-literals false)\n");
//        APEUtils.appendToFile(smtEncoding, "(set-option :produce-models true)\n");
        APEUtils.appendToFile(smtEncoding, "(set-option :timeout " + runConfig.getTimeoutMs() + ")\n");
        
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
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeTaxonomyTerms(this), this);
        
        /* Define the structure of the workflow. Define each operation and data state. */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeWorkflowStructure(this), this);
        
        /* Create constraints from the tool_annotations.json file regarding the Inputs/Outputs, preserving the structure of input and output fields. */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeModuleAnnotations(this), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool I/O constraints");

        /*
         * The constraints preserve the memory structure, i.e. preserve the data available in memory and the
         * logic of referencing data from memory in case of tool inputs.
         */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeMemoryStructure(this), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Memory structure encoding");

        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the tools
         * 2. Mandatory usage of the tools - from taxonomy.
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleMutualExclusion(domainSetup.getAllModules()), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool exclusions encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleMandatoryUsage(domainSetup.getAllModules()), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool usage encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.moduleEnforceTaxonomyStructure(rootModule, true), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Tool taxonomy  encoding");
        /*
         * Create the constraints enforcing:
         * 1. Mutual exclusion of the types/formats (according to the search model)
         * 2. Mandatory usage of the types in the transition nodes (note: "empty type" is considered a type)
         * 3. Adding the constraints enforcing the taxonomy structure.
         */
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeMutualExclusion(domainSetup.getAllTypes()), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type exclusions encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeMandatoryUsage(domainSetup), this);
        APEUtils.timerRestartAndPrint(currLengthTimer, "Type usage encoding");
        
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.typeEnforceTaxonomyStructure(domainSetup.getAllTypes()), this);
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
//        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeDataInstanceDependencyCons(typeAutomaton), this);

        /*
         * Encode the workflow input. Workflow I/O are encoded the last in order to
         * reuse the mappings for states, instead of introducing new ones, using the I/O
         * types of NodeType.UNKNOWN.
         */
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.encodeInputData(domainSetup.getAllTypes(), runConfig.getProgramInputs(), typeAutomaton), this);
        /*
         * Encode the workflow output
         */
        SMTUtils.appendToFile(smtEncoding, SMTTypeUtils.encodeOutputData(domainSetup.getAllTypes(), runConfig.getProgramOutputs(), typeAutomaton), this);

        
        /*
         * Setup the constraints ensuring that the auxiliary predicates are properly used and linked to the underlying taxonomy predicates.
         */
//        SMTUtils.appendToFile(smtEncoding, domainSetup.getConstraintsForAuxiliaryPredicates(mappings, moduleAutomaton, typeAutomaton), this);

        
        
        /*
         * Create the file that contains the encoding
         */
//        System.out.println(smtEncoding);
        
        
//        smtInputFile = File.createTempFile("smtlib2_", null);
//        smtInputFile.deleteOnExit();
        
        /*
         * Encode predicates that can be easily parsable as the part of the SMT model.
         */
        SMTUtils.appendToFile(smtEncoding, SMTModuleUtils.encodeDefineParsablePredicates(this), this);

        
        smtInputFile = new File("/home/vedran/Desktop/tmp.smt");
        FileUtils.copyFile(smtEncoding, smtInputFile);
        addSMTFooter(smtInputFile);
        
        
//        smtEncoding.delete();
//		APEUtils.write2file(mknfEncoding.toString(), new File("/home/vedran/Desktop/tmp"+ problemSetupStartTime), false);

        
        /* testing sat input */
//        File actualFile = new File ("/home/vedran/Desktop/tmpt.txt");
//		InputStream tmpSat = IOUtils.toInputStream(smtInputFile.toString(), "ASCII");
//		tmpSat.close();
//		String encoding = APEUtils.convertCNF2humanReadable(new FileInputStream(smtInputFile), this);
//		APEUtils.write2file(encoding, new File("/home/vedran/Desktop/tmp.txt"), false);

        long problemSetupTimeElapsedMillis = System.currentTimeMillis() - problemSetupStartTime;
        System.out.println("Total problem setup time: " + (problemSetupTimeElapsedMillis / 1000F) + " sec.");

        return true;
    }


    private void addSMTFooter(File currSmtEncoding) throws IOException {
    	APEUtils.appendToFile(currSmtEncoding, "\n(check-sat)"); 
        APEUtils.appendToFile(currSmtEncoding, "\n(get-model)");
//        APEUtils.appendToFile(smtEncoding, "\n(get-unsat-core)"); 
		
	}

	/**
     * Using the SAT input generated from SAT encoding and running MiniSAT solver to find the solutions.
     *
     * @return The list of new solutions.
     * @throws IOException  Error if the sat encoding file does not exist.
     */
    public List<SolutionWorkflow> synthesisExecution() throws IOException {

    	 /* Add current solutions to list of all solutions. */
        List<SolutionWorkflow> currSolutions = runZ3Once(allSolutions.getNumberOfSolutions(), allSolutions.getMaxNumberOfSolutions());
       
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
    
    
    private List<SolutionWorkflow> runZ3Once(int solutionsFound, int solutionsFoundMax)  {
        List<SolutionWorkflow> solutions = new ArrayList<SolutionWorkflow>();
        long globalTimeoutMs = runConfig.getTimeoutMs();

        long realStartTime = 0;
        long realTimeElapsedMillis;
        try {
            // loading CNF encoding of the problem
            realStartTime = System.currentTimeMillis();
            boolean satisfiable = true;
            while (solutionsFound < solutionsFoundMax && satisfiable) {
	            ProcessBuilder builder = new ProcessBuilder("/home/vedran/git/z3/build/z3", smtInputFile.getAbsolutePath());
	            List<Atom> facts = null;
	            
	            builder.redirectErrorStream(true);
	            final Process process = builder.start();
           
//	            if(!process.waitFor(globalTimeoutMs, TimeUnit.MILLISECONDS)) {
//	                //timeout - kill the process. 
//	                process.destroy(); // consider using destroyForcibly instead
//	            } else {
	            	facts = readTerminalOutput(process);
//	            }
	            
	            // Watch the process
	            if(facts == null) {
	            	satisfiable = false;
	            	process.destroy();
	            	continue;
	            }
	            SolutionWorkflow smtSolution = new SolutionWorkflow(facts, this);
	            solutionsFound++;
	            solutions.add(smtSolution);
	            if (solutionsFound % 500 == 0) {
		            realTimeElapsedMillis = System.currentTimeMillis() - realStartTime;
		            System.out.println("Found in total " + solutionsFound + " solutions. Solving time: "
		                    + (realTimeElapsedMillis / 1000F) + " sec.");
	            }
            /*
             * Adding the negation of the positive part of the solution as a constraint
             * (default negation does not work)
             */
	            process.destroy();
	            SMTUtils.appendToFile(smtEncoding, ((SMTSolution)smtSolution.getNativeSolution()).getSMTnegatedSolution(runConfig.getAllowToolSeqRepeat()), this);
	            FileUtils.copyFile(smtEncoding, smtInputFile);
	            addSMTFooter(smtInputFile);
            }
        } catch (IOException e) {
            System.err.println("Internal error while parsing the encoding.");
//        } catch (InterruptedException e) {
//        	System.err.println("Z3 processs was interrupted.");
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
                	State tmpState = null;
                    while ((line = input.readLine()) != null) {
                    	if(line.equals("unsat")) {
                    		return null;
                    	} else if(line.contains("(error ")) {
                    		System.err.println("SMT_error: " +line);
                    		return null;
                    	}
//                    	System.out.println("SMT: " +line);
                    	if(line.trim().startsWith(")")) {
                    		continue;
                    	} else if(line.trim().startsWith("(define-fun ")) {
                    		String[] words =  line.trim().replace(")", "").split(" ");
                    		currFun = getElement(words[1]);
                    		if(currFun != null) {
                    			int stateNo = Integer.parseInt(words[1].replace(currFun.toString(), ""));
	                    		if(currFun == WorkflowElement.MODULE) {
	                    			tmpState = moduleAutomaton.getState(stateNo);
	                    		} else if(currFun == WorkflowElement.MEMORY_TYPE) {
	                    			tmpState = typeAutomaton.getState(SMTDataType.MEMORY_TYPE_STATE, stateNo);
	                    		} else {
	                    			// in case of USED_TYPE or MEM_TYPE_REFERENCE
	                    			tmpState = typeAutomaton.getState(SMTDataType.USED_TYPE_STATE, stateNo);
	                    		}
                    		}
                    	} else if(currFun != null) {
                    		if(currFun == WorkflowElement.MEM_TYPE_REFERENCE) {
                    			if(!line.trim().startsWith("(not")) {
                    				String[] words =  splitFromChar(line, "x!0");
                    				int a2 = Integer.parseInt(words[2].replace("bv", ""));
                    				State arg2 = typeAutomaton.getState(SMTDataType.MEMORY_TYPE_STATE, a2);
                    				
                    				Atom currAtom  = new Atom(arg2, tmpState, currFun);
    	                    		atoms.add(currAtom);
                    			}
                    			
                    		} else {
								if(line.contains("x!0")) {
									// parse in one line both the state and the type of the module
									String recur = cutUntil(line, "x!0");
									while(recur.contains("x!0")) {
										String[] words =  splitFromChar(recur, "x!0");
										String a2 = words[1];
										PredicateLabel arg2 = mappings.findOriginal(a2);

										Atom currAtom  = new Atom(arg2, tmpState, currFun);
			                    		atoms.add(currAtom);
			                    		
			                    		recur = cutUntil(recur.substring(3), "x!0");;
									}
								}
							}
                    	}
                    	
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        return atoms;
    }
    
    /**
     * Get element that the string correposnds to.
     * @param string parsed string from the SMT2 model.
     * @return
     */
	private WorkflowElement getElement(String string) {
		
		for(WorkflowElement element : WorkflowElement.values()) {
			if(string.equals(element.toString())){
				return null;
			} else if(string.startsWith(element.toString())) {
				return element;
			}
		}
		return null;
	}
    
    private static String cutUntil(String text, String subStr) {
    	if(text.contains(subStr)) {
    		return text.substring(text.indexOf(subStr));
    	} else {
    		return text;
    	}
    }
    
    private static String[] splitFromChar(String line, String cutChar) {
    	String trimmedLine = cutUntil(line, cutChar);
		return trimmedLine.trim().replace("(", "").replace(")", "").split(" ");
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
     * Gets the general domain information and given constraints that have to be encoded.
     *
     * @return The general domain information.
     */
    public APEDomainSetup getDomainSetup() {
        return domainSetup;
    }

    /**
     * Gets SMT mappings object used to store the data used for representing the predicates with unique strings.
     *
     * @return The SMT mappings object.
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
     * Gets number of states used in the workflow for the specific type (module, data instances in the memory or data instances used by tools).
     * @return Integer representing the number of states (or -1 in case of an error).
     */
    public int getAutomatonSize(SMTDataType dataType) {
    	switch (dataType) {
		case MODULE_STATE:
			return moduleAutomaton.size(); 
		case MEMORY_TYPE_STATE:
			return typeAutomaton.getAllMemoryTypesStates().size() + 1;
		case USED_TYPE_STATE:
			return typeAutomaton.getAllUsedTypesStates().size();
		default:
			return -1;
		}
    	
    }
    
//    /**
//     * Gets number of tools used in the workflow.
//     * @return Integer representing the number of module states.
//     */
//    public int getModuleAutomatonSize() {
//    	return moduleAutomaton.size();
//    }
//    
//    /**
//     * Gets number of memory data instances available in the workflow.
//     * @return Integer representing the number of states that represent data instances available in memory.
//     */
//    public int getMemoryTypeAutomatonSize() {
//    	return typeAutomaton.getAllMemoryTypesStates().size();
//    }
//    
//    /**
//     * Gets number of used data instances expected in the workflow.
//     * @return Integer representing the number of states that define data instances used by tools.
//     */
//    public int getUsedTypeAutomatonSize() {
//    	return typeAutomaton.getAllUsedTypesStates().size();
//    }

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
