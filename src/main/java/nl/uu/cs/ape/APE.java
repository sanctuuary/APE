package nl.uu.cs.ape;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.configuration.APECoreConfig;
import nl.uu.cs.ape.configuration.APERunConfig;
import nl.uu.cs.ape.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.constraints.ConstraintTemplate;
import nl.uu.cs.ape.domain.APEDimensionsException;
import nl.uu.cs.ape.domain.APEDomainSetup;
import nl.uu.cs.ape.domain.OWLReader;
import nl.uu.cs.ape.models.MappingsException;
import nl.uu.cs.ape.models.enums.SynthesisFlag;
import nl.uu.cs.ape.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.solver.SynthesisEngine;
import nl.uu.cs.ape.solver.minisat.SATSynthesisEngine;
import nl.uu.cs.ape.solver.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.solver.solutionStructure.SolutionsList;
import nl.uu.cs.ape.solver.solutionStructure.cwl.DefaultCWLCreator;
import nl.uu.cs.ape.utils.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;

/**
 * The {@code APE} class is the main class of the library and is supposed to be
 * the main interface for working with the library.
 *
 * @author Vedran Kasalica
 */
@Slf4j
public class APE implements APEInterface {

	/** Core configuration object defined from the configuration file. */
	private final APECoreConfig config;

	/** Object containing general APE encoding. */
	private APEDomainSetup apeDomainSetup;

	/**
	 * Create instance of the APE solver.
	 *
	 * @param configPath Path to the APE JSON configuration file. If the string is
	 *                   null the default './config.json' value is assumed.
	 * @throws IOException                  Exception reading the configuration
	 *                                      file.
	 * @throws OWLOntologyCreationException Exception reading the OWL file.
	 */
	public APE(String configPath) throws IOException, OWLOntologyCreationException {
		config = new APECoreConfig(configPath);

		boolean setupSucc = setupDomain();
		if (!setupSucc) {
			throw new APEConfigException("Error setting up the domain.");
		}
	}

	/**
	 * Create instance of the APE solver.
	 *
	 * @param configObject The APE configuration {@link JSONObject}.
	 * @throws IOException                  Exception while reading the
	 *                                      configuration file or the tool
	 *                                      annotations file.
	 * @throws OWLOntologyCreationException Error in reading the OWL file.
	 */
	public APE(JSONObject configObject) throws IOException, OWLOntologyCreationException {
		config = new APECoreConfig(configObject);
		boolean setupSucc = setupDomain();
		if (!setupSucc) {
			throw new APEConfigException("Error in setting up the domain.");
		}
	}

	/**
	 * Create instance of the APE solver.
	 *
	 * @param config The APE configuration {@link APECoreConfig}.
	 * @throws IOException                  Exception while reading the
	 *                                      configuration file or the tool
	 *                                      annotations file.
	 * @throws OWLOntologyCreationException Error in reading the OWL file.
	 */
	public APE(APECoreConfig config) throws IOException, OWLOntologyCreationException {
		this.config = config;
		boolean setupSucc = setupDomain();
		if (!setupSucc) {
			throw new APEConfigException("Error in setting up the domain.");
		}
	}

	/**
	 * Method used to setup the domain using the configuration file and the
	 * corresponding annotation and constraints files.
	 *
	 * @return true if the setup was successfully performed, false otherwise.
	 * @throws MappingsException            Exception while reading the provided
	 *                                      ontology.
	 * @throws IOException                  Error in handling a JSON file containing
	 *                                      tool annotations.
	 * @throws OWLOntologyCreationException Error in reading the OWL file.
	 */
	private boolean setupDomain() throws APEDimensionsException, IOException, OWLOntologyCreationException {

		// Variable that describes a successful execution of the method.
		boolean succRun = true;
		/*
		 * Encode the taxonomies as objects - generate the list of all types / modules
		 * occurring in the taxonomies defining their submodules/subtypes
		 */
		apeDomainSetup = new APEDomainSetup(config);

		OWLReader owlReader = new OWLReader(apeDomainSetup, config.getOntologyFile());
		boolean ontologyRead = owlReader.readOntology();

		if (!ontologyRead) {
			log.warn("Error occurred while reading the provided ontology.");
			return false;
		}

		// Update allModules and allTypes sets based on the tool annotations
		succRun &= apeDomainSetup
				.annotateToolFromJson(APEFiles.readFileToJSONObject(config.getToolAnnotationsFile()));

		succRun &= apeDomainSetup.trimTaxonomy();

		// Define set of all constraint formats
		apeDomainSetup.initializeConstraints();

		return succRun;
	}

	/**
	 * Method that return all the supported constraint templates.
	 *
	 * @return List of {@link ConstraintTemplate} objects.
	 */
	public Collection<ConstraintTemplate> getConstraintTemplates() {
		return apeDomainSetup.getConstraintFactory().getConstraintTemplates();
	}

	/**
	 * The method returns the configuration file of the APE instance.
	 *
	 * @return Field {@link #config}.
	 */
	public APECoreConfig getConfig() {
		return config;
	}

	/**
	 * Gets domain setup.
	 *
	 * @return The object that contains all crucial information about the domain
	 *         (e.g. list of tools, data types, constraint factory, etc.)
	 */
	public APEDomainSetup getDomainSetup() {
		return apeDomainSetup;
	}

	/**
	 * Returns all the taxonomy elements that are subclasses of the given element.
	 * Can be used to retrieve all data types, formats or all taxonomy operations.
	 *
	 * @param taxonomyElementID ID of the taxonomy element that is parent of all the
	 *                          returned elements.
	 * @return Sorted set of elements that belong to the given taxonomy subtree.
	 */
	public SortedSet<TaxonomyPredicate> getTaxonomySubclasses(String taxonomyElementID) {
		SortedSet<TaxonomyPredicate> elements = null;
		TaxonomyPredicate root = apeDomainSetup.getAllTypes().get(taxonomyElementID);
		if (root != null) {
			elements = apeDomainSetup.getAllTypes().getElementsFromSubTaxonomy(root);
		} else {
			root = apeDomainSetup.getAllModules().get(taxonomyElementID);
			if (root != null) {
				elements = apeDomainSetup.getAllModules().getElementsFromSubTaxonomy(root);
			}
		}
		if (root == null) {
			return getTaxonomySubclasses(
					APEUtils.createClassIRI(taxonomyElementID, apeDomainSetup.getOntologyPrefixIRI()));
		} else {
			return elements;
		}
	}

	/**
	 * Returns the {@link TaxonomyPredicate} that corresponds to the given ID.
	 *
	 * @param taxonomyElementID ID of the taxonomy element
	 * @return The corresponding {@link TaxonomyPredicate}
	 */
	public TaxonomyPredicate getTaxonomyElement(String taxonomyElementID) {
		TaxonomyPredicate element = apeDomainSetup.getAllTypes().get(taxonomyElementID);
		if (element == null) {
			element = apeDomainSetup.getAllModules().get(taxonomyElementID);
		}
		if (element == null) {
			return getTaxonomyElement(
					APEUtils.createClassIRI(taxonomyElementID, apeDomainSetup.getOntologyPrefixIRI()));
		} else {
			return element;
		}
	}

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param configObject Object that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(JSONObject configObject) throws IOException, APEConfigException {
		return runSynthesis(configObject, this.getDomainSetup());
	}

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param runConfigPath Path to the JSON that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(String runConfigPath) throws IOException, JSONException, APEConfigException {
		JSONObject configObject = APEFiles.readFileToJSONObject(new File(runConfigPath));
		return runSynthesis(configObject, this.getDomainSetup());
	}

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param runConfig Configuration object that contains run configurations.
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	public SolutionsList runSynthesis(APERunConfig runConfig) throws IOException, JSONException {
		runConfig.getApeDomainSetup().clearConstraints();
		return executeSynthesis(runConfig);
	}

	/**
	 * Setup a new run instance of the APE solver and run the synthesis algorithm.
	 *
	 * @param runConfigJson  Object that contains run configurations.
	 * @param apeDomainSetup Domain information, including all the existing tools
	 *                       and types.
	 * @return The list of all the solutions.
	 * @throws IOException   Error in case of not providing a proper configuration
	 *                       file.
	 * @throws JSONException Error in configuration object.
	 */
	private SolutionsList runSynthesis(JSONObject runConfigJson, APEDomainSetup apeDomainSetup)
			throws IOException, JSONException, APEConfigException {
		apeDomainSetup.clearConstraints();
		APERunConfig runConfig = new APERunConfig(runConfigJson, apeDomainSetup);
		return executeSynthesis(runConfig);
	}

	/**
	 * Run the synthesis for the given workflow specification.
	 * 
	 * @param runConfig
	 *
	 * @return The list of all the solutions.
	 * @throws IOException Error in case of not providing a proper configuration
	 *                     file.
	 */
	private SolutionsList executeSynthesis(APERunConfig runConfig) throws IOException, JSONException {

		/* List of all the solutions */
		SolutionsList allSolutions = new SolutionsList(runConfig);

		apeDomainSetup.updateConstraints(runConfig.getConstraintsJSON());

		/* Print the setup information when necessary. */
		APEUtils.debugPrintout(runConfig, apeDomainSetup);

		/*
		 * Loop over different lengths of the workflow until either, max workflow length
		 * or max number of solutions has been found.
		 */
		String globalTimerID = "globalTimer";
		APEUtils.timerStart(globalTimerID, true);
		int solutionLength = runConfig.getSolutionLength().getMin();
		while (allSolutions.getNumberOfSolutions() < allSolutions.getMaxNumberOfSolutions()
				&& solutionLength <= runConfig.getSolutionLength().getMax()
				&& APEUtils.timerTimeLeft(globalTimerID, runConfig.getTimeoutMs()) > 0) {

			SynthesisEngine implSynthesis = new SATSynthesisEngine(apeDomainSetup, allSolutions, runConfig,
					solutionLength);

			APEUtils.printHeader(implSynthesis.getSolutionSize(), "Workflow discovery - length");

			/* Encoding of the synthesis problem */
			if (!implSynthesis.synthesisEncoding()) {
				log.error("Internal error in problem encoding.");
				return null;
			}
			/* Execution of the synthesis - updates the object allSolutions */
			allSolutions.addSolutions(implSynthesis.synthesisExecution());
			implSynthesis.deleteTempFiles();
			allSolutions.addNoSolutionsForLength(solutionLength, allSolutions.getNumberOfSolutions());

			/* Increase the size of the workflow for the next depth iteration */
			solutionLength++;
		}

		if ((allSolutions.getNumberOfSolutions() >= allSolutions.getMaxNumberOfSolutions() - 1)) {
			allSolutions.setFlag(SynthesisFlag.NONE);
		} else if (APEUtils.timerTimeLeft(globalTimerID, runConfig.getTimeoutMs()) <= 0) {
			allSolutions.setFlag(SynthesisFlag.TIMEOUT);
		} else if (allSolutions.getNumberOfSolutions() == 0) {
			allSolutions.setFlag(SynthesisFlag.UNSAT);
		} else if (solutionLength >= runConfig.getSolutionLength().getMax()) {
			allSolutions.setFlag(SynthesisFlag.MAX_LENGTH);
		} else {
			allSolutions.setFlag(SynthesisFlag.UNKNOWN);
		}

		log.info(allSolutions.getFlag().getMessage());
		long runTimeMS = APEUtils.timerPrintSolutions(globalTimerID, allSolutions.getNumberOfSolutions());

		allSolutions.setSolvingTime(runTimeMS);
		return allSolutions;
	}

	/**
	 * Validates all the tags in a configuration object. If
	 * {@link ValidationResults#success()} ()} returns true, the configuration
	 * object can be safely used to setup the the APE framework and create an
	 * APERunConfiguration.
	 *
	 * @param config configuration file
	 * @return the validation results
	 */
	public static ValidationResults validate(JSONObject config) {
		ValidationResults results = APECoreConfig.validate(config);
		if (results.hasFails()) {
			return results;
		}
		try {
			APE ape = new APE(config);
			results.add(APERunConfig.validate(config, ape.getDomainSetup()));
		} catch (IOException | OWLOntologyCreationException ignored) {
			results.add("ape_setup", "Configuration should be used to setup APE.", false);
		}

		return results;
	}

	/**
	 * Write textual "human readable" version on workflow solutions to a file.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @return true if the writing was successfully performed, false otherwise.
	 * @throws IOException Exception if file not found.
	 */
	public static boolean writeSolutionToFile(SolutionsList allSolutions) throws IOException {
		StringBuilder solutions2write = new StringBuilder();

		for (int i = 0; i < allSolutions.size(); i++) {
			solutions2write
					.append(allSolutions.get(i).getNativeSolution().getCompleteSolution()).append("\n");
		}
		APEFiles.write2file(solutions2write.toString(),
				allSolutions.getRunConfiguration().getSolutionDirPath2("solutions.txt").toFile(), false);

		return true;
	}

	/**
	 * Generating scripts that represent executable versions of the workflow
	 * solutions and executing them.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @return true if the execution was successfully performed, false otherwise.
	 */
	public static boolean writeExecutableWorkflows(SolutionsList allSolutions) {
		Path executionsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Executables();
		Integer noExecutions = allSolutions.getRunConfiguration().getNoExecutions();
		if (executionsFolder == null || noExecutions == null || noExecutions == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Executing first " + noExecutions + " solution");
		APEUtils.timerStart("executingWorkflows", true);

		final File executeDir = executionsFolder.toFile();
		if (executeDir.isDirectory()) {
			// If the directory already exists, empty it first
			deleteExistingFiles(executeDir, SolutionWorkflow.getFileNamePrefix(), "sh");
		} else {
			executeDir.mkdir();
		}
		log.debug("Generating executable scripts.");

		/* Creating the requested scripts in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noExecutions).forEach(solution -> {
			try {
				File script = executionsFolder.resolve(solution.getFileName() + ".sh").toFile();
				APEFiles.write2file(solution.getScriptExecution(), script, false);

			} catch (IOException e) {
				log.error("Error occurred while writing an executable (workflow) script to the file system.");
				e.printStackTrace();
			}
		});

		APEUtils.timerPrintText("executingWorkflows", "Workflow executables have been generated.");
		return true;
	}

	/**
	 * Generate the graphical representations of the workflow solutions, in top to
	 * bottom orientation, and write them to the file system. Each graph is shown in
	 * data-flow representation (in the default, top-to-bottom orientation)., i.e.
	 * transformation of data is in focus.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeDataFlowGraphs(SolutionsList allSolutions) {
		return writeDataFlowGraphs(allSolutions, RankDir.TOP_TO_BOTTOM);
	}

	/**
	 * Generate the graphical representations of the workflow solutions and write
	 * them to the file system. Each graph is shown in data-flow representation,
	 * i.e. transformation of data is in focus.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @param orientation  Orientation in which the graph will be presented.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeDataFlowGraphs(SolutionsList allSolutions, RankDir orientation) {
		Path graphsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Figures();
		Integer noGraphs = allSolutions.getRunConfiguration().getNoGraphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Generating graphical representation",
				"of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);

		/* Removing the existing files from the file system. */
		File graphDir = graphsFolder.toFile();
		if (graphDir.isDirectory()) {
			// If the directory already exists, empty it first
			deleteExistingFiles(graphDir, SolutionWorkflow.getFileNamePrefix(), "png");
		} else {
			graphDir.mkdir();
		}
		log.debug("Generating data flow graphs (png).");
		/* Creating the requested graphs in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
			try {
				String title = solution.getFileName();
				Path path = graphsFolder.resolve(title);
				solution.getDataflowGraph(title, orientation).write2File(path.toFile(), Format.PNG,
						allSolutions.getRunConfiguration().getDebugMode());

			} catch (IOException e) {
				log.error("Error occurred while data flow graphs (png) to the file system.");
				e.printStackTrace();
			}
		});

		APEUtils.timerPrintText("drawingGraphs", "Graphical files have been generated.");

		return true;
	}

	/**
	 * Generate the graphical representations of the workflow solutions, and write
	 * them to the file system. Each graph is styled based on the Apache Taverna
	 * workflow management system (in the default, top-to-bottom orientation).
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeTavernaDesignGraphs(SolutionsList allSolutions) {
		return writeTavernaDesignGraphs(allSolutions, Format.PNG);
	}

	/**
	 * Generate the graphical representations of the workflow solutions, and write
	 * them to the file system. Each graph is styled based on the Apache Taverna
	 * workflow management system.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @param format       Format in which the graph will be rendered.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeTavernaDesignGraphs(SolutionsList allSolutions, Format format) {
		Path graphsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Figures();
		Integer noGraphs = allSolutions.getRunConfiguration().getNoGraphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Generating graphical representation",
				"of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);

		/* Removing the existing files from the file system. */
		File graphDir = graphsFolder.toFile();
		if (graphDir.isDirectory()) {
			// If the directory already exists, empty it first
			deleteExistingFiles(graphDir, SolutionWorkflow.getFileNamePrefix(), format.fileExtension);
		} else {
			graphDir.mkdir();
		}
		log.debug("Generating data flow graphs (png).");
		/* Creating the requested graphs in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
			try {
				String title = solution.getFileName();
				Path path = graphsFolder.resolve(title);
				solution.getTavernaStyleGraph(title).write2File(path.toFile(), format,
						allSolutions.getRunConfiguration().getDebugMode());

			} catch (IOException e) {
				log.error("Error occurred while data flow graphs (png) to the file system.");
				e.printStackTrace();
			}
		});

		APEUtils.timerPrintText("drawingGraphs", "Graphical files have been generated.");

		return true;
	}

	/**
	 * Generate the graphical representations of the workflow solutions, in left to
	 * right orientation and write them to the file system. Each graph is shown in
	 * control-flow representation (in left to right orientation), i.e. order of the
	 * operations is in focus.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeControlFlowGraphs(SolutionsList allSolutions) {
		return writeControlFlowGraphs(allSolutions, RankDir.LEFT_TO_RIGHT);
	}

	/**
	 * Generate the graphical representations of the workflow solutions and write
	 * them to the file system. Each graph is shown in control-flow representation,
	 * i.e. order of the operations is in focus.
	 *
	 * @param allSolutions Set of {@link SolutionWorkflow}.
	 * @param orientation  Orientation in which the graph will be presented.
	 * @return true if the generating was successfully performed, false otherwise.
	 */
	public static boolean writeControlFlowGraphs(SolutionsList allSolutions, RankDir orientation) {
		Path graphsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Figures();
		Integer noGraphs = allSolutions.getRunConfiguration().getNoGraphs();
		if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
			return false;
		}
		APEUtils.printHeader(null, "Generating graphical representation",
				"of the first " + noGraphs + " workflows");
		APEUtils.timerStart("drawingGraphs", true);

		/* Removing the existing files from the file system. */
		File graphDir = graphsFolder.toFile();
		if (graphDir.isDirectory()) {
			// If the directory already exists, empty it first
			deleteExistingFiles(graphDir, SolutionWorkflow.getFileNamePrefix(), "png");
		} else {
			graphDir.mkdir();
		}
		log.debug("Generating control flow graphs (png).");
		/* Creating the requested graphs in parallel. */
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
			try {
				String title = solution.getFileName();
				Path path = graphsFolder.resolve(title);
				solution.getControlflowGraph(title, orientation).write2File(path.toFile(), Format.PNG,
						allSolutions.getRunConfiguration().getDebugMode());

			} catch (IOException e) {
				log.error("Error occurred while writing control flow graphs (png) to the file system.");
				e.printStackTrace();
			}
		});
		APEUtils.timerPrintText("drawingGraphs", "Graphical files have been generated.");

		return true;
	}

	/**
	 * Generate CWL scripts that represent executable versions of the workflows
	 * solutions.
	 * 
	 * @param allSolutions Set of {@link SolutionWorkflow} which should be
	 *                     represented in CWL.
	 * @return true if the execution was successfully performed, false otherwise.
	 */
	public static boolean writeCWLWorkflows(SolutionsList allSolutions) {

		if (allSolutions.isEmpty()) {
			return false;
		}
		// Check the configuration before continuing.
		Path cwlFolder = allSolutions.getRunConfiguration().getSolutionDirPath2CWL();
		int noCWLFiles = allSolutions.getRunConfiguration().getNoCWL();
		if (cwlFolder == null || noCWLFiles == 0 || allSolutions.isEmpty()) {
			return false;
		}
		final String timerID = "writingCWL";
		APEUtils.printHeader(null, String.format("Writing the first %o solution(s) to CWL files", noCWLFiles));
		APEUtils.timerStart(timerID, true);

		final File cwlDir = cwlFolder.toFile();
		if (cwlDir.isDirectory()) {
			// If the directory already exists, empty it first
			deleteExistingFiles(cwlDir, SolutionWorkflow.getFileNamePrefix(), "cwl");
		} else {
			// Create the CWL directory if it does not already exist
			cwlDir.mkdir();
		}
		log.debug("Generating CWL files.");

		// Write the CWL files
		allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noCWLFiles).forEach(solution -> {
			try {
				// Write the cwl file to the file system
				String titleCWL = solution.getFileName() + ".cwl";
				File script = cwlFolder.resolve(titleCWL).toFile();
				DefaultCWLCreator cwlCreator = new DefaultCWLCreator(solution);
				APEFiles.write2file(cwlCreator.generate(), script, false);

			} catch (IOException e) {
				log.error("Error occurred while writing a CWL file to the file system.");
				e.printStackTrace();
			}
		});

		// Write the cwl input file (in YML) to the file system
		String titleInputs = "input.yml";
		File inputScipt = cwlFolder.resolve(titleInputs).toFile();
		DefaultCWLCreator cwlCreator = new DefaultCWLCreator(allSolutions.get(0));
		try {
			APEFiles.write2file(cwlCreator.generateCWLWorkflowInputs(), inputScipt, false);
		} catch (IOException e) {
			log.error("Error occurred while writing a CWL input file (yml) to the file system.");
			e.printStackTrace();
		}

		APEUtils.timerPrintText(timerID, "CWL files have been generated.");
		return true;
	}

	/**
	 * Delete all files in the given directory that start with the given prefix and
	 * have the given extension.
	 * 
	 * @param dirName       The directory to delete files from.
	 * @param filePrefix    The prefix of the files to delete.
	 * @param fileExtension The extension of the files to delete.
	 */
	private static void deleteExistingFiles(File dirName, String filePrefix, String fileExtension) {
		File[] oldFiles = dirName
				.listFiles((dir, fileName) -> fileName.toLowerCase().startsWith(filePrefix.toLowerCase())
						&& fileName.toLowerCase().endsWith(fileExtension.toLowerCase()));
		if (oldFiles != null) {
			Arrays.stream(oldFiles).forEach(f -> {
				try {
					Files.delete(f.toPath());
				} catch (IOException e) {
					log.warn("Failed to delete file {}.", f.getName());
				}
			});
		}
	}
}
