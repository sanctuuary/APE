/**
 * 
 */
package nl.uu.cs.ape.sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.json.JSONException;
import org.json.JSONObject;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SAT_solution;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;
import nl.uu.cs.ape.sat.utils.OWLReader;

/**
 * The {@code APE} class is the main class of the library and is supposed to be the main interface for working with the library.
 *
 * @author Vedran Kasalica
 *
 */
public class APE {
	/** Configuration object defined from the configuration file. */
	private final APEConfig config;
	/** Object containing general APE encoding */
	private APEDomainSetup apeDomainSetup;
	
	
	
	/**
	 * Create instance of the APE solver.
	 * @param configPath - path to the APE configuration file. If the string is null the default './ape.config' value is assumed.
	 * @throws IOException error in reading the configuration file
	 * @throws JSONException error in reading the configuration file
	 */
	public APE(String configPath) throws IOException, JSONException {
		config = new APEConfig(configPath);
		if (config == null || config.getCoreConfigJsonObj() == null) {
			System.err.println("Configuration failed. Error in configuration file.");
			throw new ExceptionInInitializerError();
		}
		if(!setupDomain()) {
			throw new IOException("Error in settin up the domain.");
		}
	}
	
	/**
	 * Create instance of the APE solver.
	 * @param configPath - the APE configuration JSONObject{@link JSONObject}.
	 * @throws ExceptionInInitializerError 
	 * @throws IOException 
	 */
	public APE(JSONObject configObject) throws ExceptionInInitializerError, IOException {
		config = new APEConfig(configObject);
		if (config == null) {
			System.err.println("Configuration failed. Error in configuration object.");
			throw new ExceptionInInitializerError();
		}
		if(!setupDomain()) {
			throw new IOException("Error in settin up the domain.");
		}
	}
	
	/**
	 * Method that return all the supported constraint templates.
	 * @return list of {@link ConstraintTemplate} objects.
	 */
	public Collection<ConstraintTemplate> getConstraintTemplates() {
		return apeDomainSetup.getConstraintFactory().getConstraintTamplates();
	}
	
	
	/** 
	 * The method returns the configuration file of the APE instance.
	 * @return the field {@link config}. */
	public APEConfig getConfig() {
		return config;
	}
	
	public APEDomainSetup getDomainSetup() {
		return apeDomainSetup;
	}
	
	/**
	 * Method used to setup the domain using the configuration file and the corresponding annotation and constraints files.
	 * @return {@code true} if the setup was successfully performed, {@code false} otherwise.
	 * @throws ExceptionInInitializerError 
	 */
	public boolean setupDomain() throws ExceptionInInitializerError {
		/** Variable that describes a successful run of the program. */
		boolean succRun = true;
		/*
		 * Encode the taxonomies as objects - generate the list of all types / modules
		 * occurring in the taxonomies defining their submodules/subtypes
		 */
		apeDomainSetup = new APEDomainSetup(config);

		OWLReader owlReader = new OWLReader(apeDomainSetup, config.getOntologyPath());
		Boolean ontologyRead = owlReader.readOntology();

		if (ontologyRead == false) {
			System.out.println("Error occured while reading the provided ontology.");
			return false;
		}

		/*
		 * Set the the empty type (representing the absence of types) as a direct child
		 * of root type
		 */
		succRun &= apeDomainSetup.getAllTypes().getRootPredicate().addSubPredicate(apeDomainSetup.getAllTypes().getEmptyType());

		/*
		 * Update allModules and allTypes sets based on the module.json file
		 */
		APEUtils.readModuleJson(config.getToolAnnotationsPath(), apeDomainSetup);
		
		succRun &= apeDomainSetup.trimTaxonomy();
		
		/*
		 * Define set of all constraint formats
		 */
		apeDomainSetup.initializeConstraints();
		
		return succRun;
	}
	
	/**
	 * Function used to return all the elements of one data type dimension (e.g. all data types or all data formats).
	 * @param dimensionRootID - root of the data taxonomy subtree that corresponds to the list of elements that should be returned.
	 * @return List where each element correspond to a map that can be transformed into JSON objects.
	 */
	public List<Map<String, String>> getTaxonomyElements(String dimensionRootID) throws NullPointerException {
		SortedSet<? extends TaxonomyPredicate> elements = null;
		TaxonomyPredicate root = apeDomainSetup.getAllTypes().get(dimensionRootID);
		if(root != null) {
			elements = apeDomainSetup.getAllTypes().getElementsFromSubTaxonomy(root);
		} else {
			root = apeDomainSetup.getAllModules().get(dimensionRootID);
			if(root != null) {
				elements = apeDomainSetup.getAllModules().getElementsFromSubTaxonomy(root);
			} else {
				throw new NullPointerException();
			}
		}
		
		List<Map<String, String>> transformedTypes = new ArrayList<Map<String, String>>();
		for(TaxonomyPredicate currType : elements) {
			transformedTypes.add(currType.toMap());
		}
		
		return transformedTypes;
	}
	
	/** Setup a new run instance of the APE solver and run the synthesis algorithm.
	 * 
	 * @param configObject - JSON object that contains run configurations
	 * @return The list of all the solutions.
	 * @throws JSONException
	 */
	public SATsolutionsList runSynthesis(JSONObject configObject) throws IOException, JSONException {
		apeDomainSetup.clearConstraints();
		config.setupRunConfiguration(configObject);
		if (config == null || config.getRunConfigJsonObj() == null) {
			throw new JSONException("Run configuration failed. Error in configuration object.");
		}
		SATsolutionsList solutions = executeSynthesis();
		
		return solutions;
	}
	
	/** Setup a new run instance of the APE solver and run the synthesis algorithm.
	 * 
	 * @param configPath - path to the JSON that contains run configurations
	 * @return The list of all the solutions.
	 * @throws JSONException
	 */
	public SATsolutionsList runSynthesis(String configPath) throws IOException, JSONException {
		config.setupRunConfiguration(configPath);
		if (config == null || config.getRunConfigJsonObj() == null) {
			throw new JSONException("Run configuration failed. Error in configuration file.");
		}
		SATsolutionsList solutions = executeSynthesis();
		
		return solutions;
	}
	/**
	 * Run the synthesis for the given workflow specification.
	 * 
	 * @return The list of all the solutions.
	 * @throws IOException error in case of not providing a proper configuration file.
	 */
	private SATsolutionsList executeSynthesis() throws IOException {
		/**
		 * List of all the solutions
		 */
		SATsolutionsList allSolutions = new SATsolutionsList(config);

		
		APEUtils.readConstraints(config.getConstraintsPath(), apeDomainSetup);
		
		/** Print the setup information when necessary. */
		APEUtils.debugPrintout(config.getDebugMode(), apeDomainSetup);

		/**
		 * Loop over different lengths of the workflow until either, max workflow length
		 * or max number of solutions has been found.
		 */
		String globalTimerID = "globalTimer";
		APEUtils.timerStart(globalTimerID, true);
		int solutionLength = config.getSolutionMinLength();
		while (allSolutions.getNumberOfSolutions() < allSolutions.getMaxNumberOfSolutions()
				&& solutionLength <= config.getSolutionMaxLength()) {

			SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(apeDomainSetup, allSolutions, config, solutionLength);

			APEUtils.printHeader(implSATsynthesis.getSolutionSize(), "Workflow discovery - length");

			/** Encoding of the synthesis problem */
			if (!implSATsynthesis.synthesisEncoding()) {
				System.err.println("Internal error in problem encoding.");
				return null;
			}
			/** Execution of the synthesis */
			implSATsynthesis.synthesisExecution();

			if ((allSolutions.getNumberOfSolutions() >= allSolutions.getMaxNumberOfSolutions() - 1)
					|| solutionLength == config.getSolutionMaxLength()) {
				APEUtils.timerPrintSolutions(globalTimerID, allSolutions.getNumberOfSolutions());
			}

			/** Increase the size of the workflow for the next depth iteration */
			solutionLength++;
		}

		
		return allSolutions;
	}
	
	/**
	 * Write textual "human readable" version on workflow solutions to a file.
	 * @param allSolutions 
	 * @param allSolutions
	 * @return {@code true} if the writing was successfully performed, {@code false} otherwise.
	 */
	public boolean writeSolutionToFile(SATsolutionsList allSolutions) {
		StringBuilder solutions2write = new StringBuilder();

		for (int i = 0; i < allSolutions.size(); i++) {
			solutions2write = solutions2write.append(allSolutions.get(i).getnativeSATsolution().getRelevantSolution())
					.append("\n");
		}
		return APEUtils.write2file(solutions2write.toString(), new File(config.getSolutionPath()), false);
	}

	/**
	 * TODO: This feature is not fully functioning yet.
	 * Generating scripts that represent executable versions of the workflow solutions and executing them. 
	 * @param allSolutions
	 * @param allModules
	 * @return {@code true} if the execution was successfully performed, {@code false} otherwise.
	 * @throws IOException
	 */
	public boolean writeExecutableWorkflows(SATsolutionsList allSolutions) throws IOException {
		String executionsFolder = config.getExecutionScriptsFolder();
		Integer noExecutions = config.getNoExecutions();
		if (executionsFolder == null || noExecutions == null || noExecutions == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Executing first " + noExecutions + " solution");
		APEUtils.timerStart("executingWorkflows", true);

		Arrays.stream(
				new File(executionsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("workflowSolution_")))
				.forEach(File::delete);
		System.out.print("Loading");
		for (int i = 0; i < noExecutions && i < allSolutions.size(); i++) {

			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter(executionsFolder + "/workflowSolution_" + i + ".sh", false)));
			out.println("");
			out.close();
			SAT_solution currSol = allSolutions.get(i).getnativeSATsolution();
			currSol.getRelevantSolutionModules(apeDomainSetup.getAllModules());
			for (Module curr : currSol.getRelevantSolutionModules(apeDomainSetup.getAllModules())) {
				if (curr.getModuleExecution() != null) {
					curr.getModuleExecution()
							.run(config.getExecutionScriptsFolder() + "/workflowSolution_" + i + ".sh");
				}
			}
			System.out.print(".");
			if (i > 0 && i % 60 == 0) {
				System.out.println();
			}
		}
		APEUtils.timerPrintText("executingWorkflows", "\nWorkflows have been executed.");
		return true;
	}

	/**
	 * Generate the graphical representations of the workflow solutions and write them to the file system. Each graph is shown in data-flow representation, i.e. transformation of data is in focus.
	 * @param allSolutions
	 * @param orientation - orientation in which the graph will be presented
	 * @return {@code true} if the generating was successfully performed, {@code false} otherwise.
	 * @throws IOException
	 */
	public boolean writeDataFlowGraphs(SATsolutionsList allSolutions, RankDir orientation) throws IOException {
		String graphsFolder = config.getSolutionGraphsFolder();
		Integer noGraphs = config.getNoGraphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Geneating graphical representation", "of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);
		System.out.println();
		/* Removing the existing files from the file system. */
		Arrays.stream(
				new File(graphsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
				.forEach(File::delete);
		System.out.print("Loading");
		/* Creating the requested graphs in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs)
										.forEach(solution -> {
			try {
				String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
				String path = graphsFolder + "/" + title;
				solution.getDataflowGraph(title, orientation)
						.getWrite2File(new File(path));
				System.out.print(".");
			} catch (IOException e) {
				System.err.println("Error occured while writing a graph to the file system.");
				e.printStackTrace();
			}
		});
		
		APEUtils.timerPrintText("drawingGraphs", "\nGraphical files have been generated.");

		return true;
	}
	
	/**
	 * Generate the graphical representations of the workflow solutions and write them to the file system. Each graph is shown in control-flow representation, i.e. order of the operations is in focus.
	 * @param allSolutions
	 * @param orientation - orientation in which the graph will be presented
	 * @return {@code true} if the generating was successfully performed, {@code false} otherwise.
	 * @throws IOException
	 */
	public boolean writeControlFlowGraphs(SATsolutionsList allSolutions, RankDir orientation) throws IOException {
		String graphsFolder = config.getSolutionGraphsFolder();
		Integer noGraphs = config.getNoGraphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Geneating graphical representation", "of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);
		System.out.println();
		/* Removing the existing files from the file system. */
		Arrays.stream(
				new File(graphsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
				.forEach(File::delete);
		System.out.print("Loading");
		/* Creating the requested graphs in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
			try {
				String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
				String path = graphsFolder + "/" + title;
				solution.getControlflowGraph(title, orientation)
						.getWrite2File(new File(path));
				System.out.print(".");
			} catch (IOException e) {
				System.err.println("Error occured while writing a graph to the file system.");
				e.printStackTrace();
			}
		});
		APEUtils.timerPrintText("drawingGraphs", "\nGraphical files have been generated.");

		return true;
	}
	
	
}
