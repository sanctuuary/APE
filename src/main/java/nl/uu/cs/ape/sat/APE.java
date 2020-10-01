/**
 *
 */
package nl.uu.cs.ape.sat;

import guru.nidi.graphviz.attribute.Rank.RankDir;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResult;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.configuration.APEConfigException;
import nl.uu.cs.ape.sat.configuration.APECoreConfig;
import nl.uu.cs.ape.sat.utils.APEDimensionsException;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.configuration.APERunConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;
import nl.uu.cs.ape.sat.utils.OWLReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import javax.imageio.ImageIO;

/**
 * The {@code APE} class is the main class of the library and is supposed
 * to be the main interface for working with the library.
 *
 * @author Vedran Kasalica
 */
public class APE {

    /** Core configuration object defined from the configuration file. */
    private final APECoreConfig config;
    
    /** Object containing general APE encoding. */
    private APEDomainSetup apeDomainSetup;

    /**
     * Create instance of the APE solver.
     *
     * @param configPath Path to the APE JSON configuration file. If the string is null the default './ape.config' value is assumed.
     * @throws IOException        Exception reading the configuration file.
     * @throws OWLOntologyCreationException	Exception reading the OWL file.
     */
    public APE(String configPath) throws IOException, OWLOntologyCreationException {
        config = new APECoreConfig(configPath);
        if (config == null) {
            throw new APEConfigException("Configuration failed. Error in configuration file.");
        }
        if (!setupDomain()) {
            throw new APEConfigException("Error setting up the domain.");
        }
    }

    /**
     * Create instance of the APE solver.
     *
     * @param configObject The APE configuration {@link JSONObject}.
     * @throws IOException        Exception while reading the configuration file or the tool annotations file.
     * @throws OWLOntologyCreationException  Error in reading the OWL file.
     */
    public APE(JSONObject configObject) throws IOException, OWLOntologyCreationException {
        config = new APECoreConfig(configObject);
        if (!setupDomain()) {
            throw new APEConfigException("Error in setting up the domain.");
        }
    }
    
    /**
     * Create instance of the APE solver.
     *
     * @param config The APE configuration {@link APECoreConfig}.
     * @throws IOException        Exception while reading the configuration file or the tool annotations file.
     * @throws OWLOntologyCreationException  Error in reading the OWL file.
     */
    public APE(APECoreConfig config) throws IOException, OWLOntologyCreationException {
        this.config = config;
        if (!setupDomain()) {
            throw new APEConfigException("Error in setting up the domain.");
        }
    }

    /**
     * Method used to setup the domain using the configuration file and the
     * corresponding annotation and constraints files.
     *
     * @return true if the setup was successfully performed, false otherwise.
     * @throws APEDimensionsException Exception while reading the provided ontology.
     * @throws IOException Error in handling a JSON file containing tool annotations.
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
            System.out.println("Error occurred while reading the provided ontology.");
            return false;
        }

        // Update allModules and allTypes sets based on the module.json file
        succRun &= APEUtils.readModuleJson(config.getToolAnnotationsFile(), apeDomainSetup);

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
     * @return The object that contains all crucial information about the domain (e.g. list of tools, data types, constraint factory, etc.)
     */
    public APEDomainSetup getDomainSetup() {
        return apeDomainSetup;
    }

    /**
     * Function used to return all the elements of one data type dimension (e.g. all
     * data types or all data formats).
     *
     * @param dimensionRootID ID of the data taxonomy subtree that corresponds to the list of elements that should be returned.
     * @return List where each element correspond to a map that can be transformed into JSON objects.
     * @throws NullPointerException the null pointer exception
     */
    public List<Map<String, String>> getTaxonomyElements(String dimensionRootID) throws NullPointerException {
        SortedSet<? extends TaxonomyPredicate> elements = null;
        TaxonomyPredicate root = apeDomainSetup.getAllTypes().get(dimensionRootID);
        if (root != null) {
            elements = apeDomainSetup.getAllTypes().getElementsFromSubTaxonomy(root);
        } else {
            root = apeDomainSetup.getAllModules().get(dimensionRootID);
            if (root != null) {
                elements = apeDomainSetup.getAllModules().getElementsFromSubTaxonomy(root);
            } else {
                throw new NullPointerException();
            }
        }

        List<Map<String, String>> transformedTypes = new ArrayList<Map<String, String>>();
        for (TaxonomyPredicate currType : elements) {
            transformedTypes.add(currType.toMap());
        }

        return transformedTypes;
    }

    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param configObject   Object that contains run configurations.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     */
    public SATsolutionsList runSynthesis(JSONObject configObject) throws IOException {
        return runSynthesis(configObject, this.getDomainSetup());
    }

    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param runConfigPath     Path to the JSON that contains run configurations.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     */
    public SATsolutionsList runSynthesis(String runConfigPath) throws IOException {
    	String configContent = APEUtils.getFileContent(runConfigPath);
    	JSONObject configObject = new JSONObject(configContent);
        return runSynthesis(configObject, this.getDomainSetup());
    }
    
    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param runConfigJson   Object that contains run configurations.
     * @param apeDomainSetup Domain information, including all the existing tools and types.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     * @throws JSONException Error in configuration object.
     */
    private SATsolutionsList runSynthesis(JSONObject runConfigJson, APEDomainSetup apeDomainSetup) throws IOException, JSONException, APEConfigException {
        apeDomainSetup.clearConstraints();
        APERunConfig runConfig = new APERunConfig(runConfigJson, apeDomainSetup);
        SATsolutionsList solutions = executeSynthesis(runConfig);

        return solutions;
    }


    /**
     * Run the synthesis for the given workflow specification.
     * @param runConfig 
     *
     * @return The list of all the solutions.
     * @throws IOException Error in case of not providing a proper configuration file.
     */
    private SATsolutionsList executeSynthesis(APERunConfig runConfig) throws IOException {
        /* List of all the solutions */
        SATsolutionsList allSolutions = new SATsolutionsList(runConfig);
        
        APEUtils.readConstraints(new File(runConfig.getConstraintsPath().toString()), apeDomainSetup);

        /* Print the setup information when necessary. */
        APEUtils.debugPrintout(runConfig.getDebugMode(), apeDomainSetup);

        /*
         * Loop over different lengths of the workflow until either, max workflow length
         * or max number of solutions has been found.
         */
        String globalTimerID = "globalTimer";
        APEUtils.timerStart(globalTimerID, true);
        int solutionLength = runConfig.getSolutionLength().getMin();
        while (allSolutions.getNumberOfSolutions() < allSolutions.getMaxNumberOfSolutions()
                && solutionLength <= runConfig.getSolutionLength().getMax()) {

            SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(apeDomainSetup, allSolutions, runConfig,
                    solutionLength);

            APEUtils.printHeader(implSATsynthesis.getSolutionSize(), "Workflow discovery - length");

            /* Encoding of the synthesis problem */
            if (!implSATsynthesis.synthesisEncoding()) {
                System.err.println("Internal error in problem encoding.");
                return null;
            }
            /* Execution of the synthesis */
            implSATsynthesis.synthesisExecution();

            if ((allSolutions.getNumberOfSolutions() >= allSolutions.getMaxNumberOfSolutions() - 1)
                    || solutionLength == runConfig.getSolutionLength().getMax()) {
                APEUtils.timerPrintSolutions(globalTimerID, allSolutions.getNumberOfSolutions());
            }

            /* Increase the size of the workflow for the next depth iteration */
            solutionLength++;
        }

        return allSolutions;
    }

    /**
     * Validates all the tags in a configuration object.
     * If {@link ValidationResults#success()} ()} returns true,
     * the configuration object can be safely used to setup the
     * the APE framework and create an APERunConfiguration.
     *
     * @param config configuration file
     * @return the validation results
     */
    public static ValidationResults validate(JSONObject config){
        ValidationResults results = APECoreConfig.validate(config);
        if(results.hasFails()){
            return results;
        }
        try {
            APE ape = new APE(config);
            results.add(APERunConfig.validate(config, ape.getDomainSetup()));
        } catch (IOException | OWLOntologyCreationException ignored) { }

        return results;
    }

    /**
     * Write textual "human readable" version on workflow solutions to a file.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @return true if the writing was successfully performed, false otherwise.
     * @throws IOException Exception if file not found.
     */
    public static boolean writeSolutionToFile(SATsolutionsList allSolutions) throws IOException {
        StringBuilder solutions2write = new StringBuilder();

        for (int i = 0; i < allSolutions.size(); i++) {
            solutions2write = solutions2write.append(allSolutions.get(i).getNativeSATsolution().getRelevantSolution())
                    .append("\n");
        }
        APEUtils.write2file(solutions2write.toString(), allSolutions.getRunConfiguration().getSolutionDirPath2("solutions.txt").toFile(), false);

        return true;
    }

    /**
     * Generating scripts that represent executable versions of the workflow solutions and executing them.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @return true if the execution was successfully performed, false otherwise.
     */
    public static boolean writeExecutableWorkflows(SATsolutionsList allSolutions) {
        Path executionsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Executables();
        Integer noExecutions = allSolutions.getRunConfiguration().getNoExecutions();
        if (executionsFolder == null || noExecutions == null || noExecutions == 0 || allSolutions.isEmpty()) {
            return false;
        }
        APEUtils.printHeader(null, "Executing first " + noExecutions + " solution");
        APEUtils.timerStart("executingWorkflows", true);

        final File executeDir = executionsFolder.toFile();
        if(executeDir.isDirectory()) {
            Arrays.stream(
                    executionsFolder.toFile().listFiles((dir, name) -> name.toLowerCase().startsWith("workflowSolution_")))
                    .forEach(File::delete);
        } else {
            executeDir.mkdir();
        }
        System.out.print("Loading");

        /* Creating the requested scripts in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noExecutions).forEach(solution -> {
            try {
                String title = "workflowSolution_" + solution.getIndex() + ".sh";
                File script = executionsFolder.resolve(title).toFile();
                APEUtils.write2file(solution.getScriptExecution(), script, false);
                System.out.print(".");
            } catch (IOException e) {
                System.err.println("Error occurred while writing a graph to the file system.");
                e.printStackTrace();
            }
        });

        APEUtils.timerPrintText("executingWorkflows", "\nWorkflows have been executed.");
        return true;
    }

    /**
     * Generate the graphical representations of the workflow solutions and write
     * them to the file system. Each graph is shown in data-flow representation,
     * i.e. transformation of data is in focus.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @param orientation  Orientation in which the graph will be presented.
     * @return true if the generating was successfully performed, false otherwise.
     * @throws IOException Exception if graph cannot be written to the file system.
     */
    public static boolean writeDataFlowGraphs(SATsolutionsList allSolutions,  RankDir orientation) throws IOException {
        Path graphsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Figures();
        Integer noGraphs = allSolutions.getRunConfiguration().getNoGraphs();
        if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
            return false;
        }
        APEUtils.printHeader(null, "Geneating graphical representation", "of the first " + noGraphs + " workflows");
        APEUtils.timerStart("drawingGraphs", true);
        System.out.println();
        /* Removing the existing files from the file system. */
        File graphDir = graphsFolder.toFile();
        if(graphDir.isDirectory()) {
            Arrays.stream(graphsFolder.toFile().listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
                    .forEach(File::delete);
        } else {
            graphDir.mkdir();
        }
        System.out.print("Loading");
        /* Creating the requested graphs in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
            try {
                String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
                Path path = graphsFolder.resolve(title);
                solution.getDataflowGraph(title, orientation).getWrite2File(path.toFile(), allSolutions.getRunConfiguration().getDebugMode());
                System.out.print(".");
            } catch (IOException e) {
                System.err.println("Error occurred while writing a graph to the file system.");
                e.printStackTrace();
            }
        });

        APEUtils.timerPrintText("drawingGraphs", "\nGraphical files have been generated.");

        return true;
    }

    /**
     * Generate the graphical representations of the workflow solutions and write
     * them to the file system. Each graph is shown in control-flow representation,
     * i.e. order of the operations is in focus.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @param orientation  Orientation in which the graph will be presented.
     * @return true if the generating was successfully performed, false otherwise.
     * @throws IOException Exception if graphs cannot be written to the file system.
     */
    public static boolean writeControlFlowGraphs(SATsolutionsList allSolutions,  RankDir orientation) throws IOException {
        Path graphsFolder = allSolutions.getRunConfiguration().getSolutionDirPath2Figures();
        Integer noGraphs = allSolutions.getRunConfiguration().getNoGraphs();
        if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
            return false;
        }
        APEUtils.printHeader(null, "Generating graphical representation", "of the first " + noGraphs + " workflows");
        APEUtils.timerStart("drawingGraphs", true);
        System.out.println();
        /* Removing the existing files from the file system. */
        File graphDir = graphsFolder.toFile();
        if(graphDir.isDirectory()) {
            Arrays.stream(graphsFolder.toFile().listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
                    .forEach(File::delete);
        } else {
            graphDir.mkdir();
        }
        System.out.print("Loading");
        /* Creating the requested graphs in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
            try {
                String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
                Path path = graphsFolder.resolve(title);
                solution.getControlflowGraph(title, orientation).getWrite2File(path.toFile(), allSolutions.getRunConfiguration().getDebugMode());
                System.out.print(".");
            } catch (IOException e) {
                System.err.println("Error occurred while writing a graph to the file system.");
                e.printStackTrace();
            }
        });
        APEUtils.timerPrintText("drawingGraphs", "\nGraphical files have been generated.");

        return true;
    }

}
