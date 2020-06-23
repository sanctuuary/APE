/**
 *
 */
package nl.uu.cs.ape.sat;

import guru.nidi.graphviz.attribute.RankDir;
import nl.uu.cs.ape.sat.constraints.ConstraintTemplate;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.core.implSAT.SATsolutionsList;
import nl.uu.cs.ape.sat.core.solutionStructure.SolutionWorkflow;
import nl.uu.cs.ape.sat.models.logic.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The {@code APE} class is the main class of the library and is supposed
 * to be the main interface for working with the library.
 *
 * @author Vedran Kasalica
 */
public class APE {

    /** Configuration object defined from the configuration file. */
    private final APEConfig config;

    /** Object containing general APE encoding. */
    private APEDomainSetup apeDomainSetup;

    /**
     * Create instance of the APE solver.
     *
     * @param configPath Path to the APE configuration file. If the string is null the default './ape.config' value is assumed.
     * @throws IOException        Exception while reading the configuration file.
     * @throws JSONException      Exception while reading the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APE(String configPath) throws IOException, JSONException, APEConfigException {
        config = new APEConfig(configPath);
        if (config.getCoreConfigJsonObj() == null) {
            throw new APEConfigException("Configuration failed. Error in configuration file.");
        }
        if (!setupDomain()) {
            throw new APEConfigException("Error in setting up the domain.");
        }
    }

    /**
     * Create instance of the APE solver.
     *
     * @param configObject The APE configuration JSONObject{@link JSONObject}.
     * @throws IOException        Exception while reading the configuration file.
     * @throws JSONException      Exception while reading the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APE(JSONObject configObject) throws IOException, JSONException, APEConfigException {
        config = new APEConfig(configObject);
        if (!setupDomain()) {
            throw new APEConfigException("Error in setting up the domain.");
        }
    }

    /**
     * Method used to setup the domain using the configuration file and the
     * corresponding annotation and constraints files.
     *
     * @return true if the setup was successfully performed, false otherwise.
     * @throws ExceptionInInitializerError Exception while reading the provided ontology.
     */
    private boolean setupDomain() throws ExceptionInInitializerError {
        // Variable that describes a successful run of the program.
        boolean succRun = true;
        /*
         * Encode the taxonomies as objects - generate the list of all types / modules
         * occurring in the taxonomies defining their submodules/subtypes
         */
        apeDomainSetup = new APEDomainSetup(config);

        OWLReader owlReader = new OWLReader(apeDomainSetup, config.getOntologyPath());
        boolean ontologyRead = owlReader.readOntology();

        if (!ontologyRead) {
            System.out.println("Error occurred while reading the provided ontology.");
            return false;
        }

        // Update allModules and allTypes sets based on the module.json file
        succRun &= APEUtils.readModuleJson(config.getToolAnnotationsPath(), apeDomainSetup);

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
    public APEConfig getConfig() {
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
     * @throws JSONException Error in configuration object.
     */
    public SATsolutionsList runSynthesis(JSONObject configObject) throws IOException, JSONException, APEConfigException {
        return runSynthesis(configObject, this.getDomainSetup());
    }

    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param configObject   Object that contains run configurations.
     * @param apeDomainSetup Domain information, including all the existing tools and types.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     * @throws JSONException Error in configuration object.
     */
    public SATsolutionsList runSynthesis(JSONObject configObject, APEDomainSetup apeDomainSetup) throws IOException, JSONException, APEConfigException {
        apeDomainSetup.clearConstraints();
        config.setupRunConfiguration(configObject, apeDomainSetup);
        if (config.getRunConfigJsonObj() == null) {
            throw new JSONException("Run configuration failed. Error in configuration object.");
        }
        SATsolutionsList solutions = executeSynthesis();

        return solutions;
    }

    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param configPath     Path to the JSON that contains run configurations.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     * @throws JSONException Error in configuration object.
     */
    public SATsolutionsList runSynthesis(String configPath) throws IOException, JSONException, APEConfigException {
        return runSynthesis(configPath, this.getDomainSetup());
    }

    /**
     * Setup a new run instance of the APE solver and run the synthesis algorithm.
     *
     * @param configPath     Path to the JSON that contains run configurations.
     * @param apeDomainSetup Domain information, including all the existing tools and types.
     * @return The list of all the solutions.
     * @throws IOException   Error in case of not providing a proper configuration file.
     * @throws JSONException Error in configuration object.
     */
    public SATsolutionsList runSynthesis(String configPath, APEDomainSetup apeDomainSetup) throws IOException, JSONException, APEConfigException {
        config.setupRunConfiguration(configPath, apeDomainSetup);
        if (config.getRunConfigJsonObj() == null) {
            throw new JSONException("Run configuration failed. Error in configuration file.");
        }

        SATsolutionsList solutions = executeSynthesis();

        return solutions;
    }

    /**
     * Run the synthesis for the given workflow specification.
     *
     * @return The list of all the solutions.
     * @throws IOException Error in case of not providing a proper configuration file.
     */
    private SATsolutionsList executeSynthesis() throws IOException {
        /* List of all the solutions */
        SATsolutionsList allSolutions = new SATsolutionsList(config);

        APEUtils.readConstraints(config.getConstraintsPath(), apeDomainSetup);

        /* Print the setup information when necessary. */
        APEUtils.debugPrintout(config.getDebugMode(), apeDomainSetup);

        /*
         * Loop over different lengths of the workflow until either, max workflow length
         * or max number of solutions has been found.
         */
        String globalTimerID = "globalTimer";
        APEUtils.timerStart(globalTimerID, true);
        int solutionLength = config.getSolutionMinLength();
        while (allSolutions.getNumberOfSolutions() < allSolutions.getMaxNumberOfSolutions()
                && solutionLength <= config.getSolutionMaxLength()) {

            SAT_SynthesisEngine implSATsynthesis = new SAT_SynthesisEngine(apeDomainSetup, allSolutions, config,
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
                    || solutionLength == config.getSolutionMaxLength()) {
                APEUtils.timerPrintSolutions(globalTimerID, allSolutions.getNumberOfSolutions());
            }

            /* Increase the size of the workflow for the next depth iteration */
            solutionLength++;
        }

        return allSolutions;
    }

    /**
     * Write textual "human readable" version on workflow solutions to a file.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @return true if the writing was successfully performed, false otherwise.
     * @throws IOException Exception if file not found.
     */
    public boolean writeSolutionToFile(SATsolutionsList allSolutions) throws IOException {
        StringBuilder solutions2write = new StringBuilder();

        for (int i = 0; i < allSolutions.size(); i++) {
            solutions2write = solutions2write.append(allSolutions.get(i).getNativeSATsolution().getRelevantSolution())
                    .append("\n");
        }
        APEUtils.write2file(solutions2write.toString(), new File(config.getSolutionPath()), false);

        return true;
    }

    /**
     * Generating scripts that represent executable versions of the workflow solutions and executing them.
     *
     * @param allSolutions Set of {@link SolutionWorkflow}.
     * @return true if the execution was successfully performed, false otherwise.
     */
    public boolean writeExecutableWorkflows(SATsolutionsList allSolutions) {
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

        /* Creating the requested scripts in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noExecutions).forEach(solution -> {
            try {
                String title = "workflowSolution_" + solution.getIndex() + ".sh";
                File script = new File(executionsFolder + "/" + title);
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
        Arrays.stream(new File(graphsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
                .forEach(File::delete);
        System.out.print("Loading");
        /* Creating the requested graphs in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
            try {
                String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
                String path = graphsFolder + "/" + title;
                solution.getDataflowGraph(title, orientation).getWrite2File(new File(path));
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
    public boolean writeControlFlowGraphs(SATsolutionsList allSolutions, RankDir orientation) throws IOException {
        String graphsFolder = config.getSolutionGraphsFolder();
        Integer noGraphs = config.getNoGraphs();
        if (graphsFolder == null || noGraphs == null || noGraphs == 0 || allSolutions.isEmpty()) {
            return false;
        }
        APEUtils.printHeader(null, "Generating graphical representation", "of the first " + noGraphs + " workflows");
        APEUtils.timerStart("drawingGraphs", true);
        System.out.println();
        /* Removing the existing files from the file system. */
        Arrays.stream(new File(graphsFolder).listFiles((dir, name) -> name.toLowerCase().startsWith("SolutionNo")))
                .forEach(File::delete);
        System.out.print("Loading");
        /* Creating the requested graphs in parallel. */
        allSolutions.getParallelStream().filter(solution -> solution.getIndex() < noGraphs).forEach(solution -> {
            try {
                String title = "SolutionNo_" + solution.getIndex() + "_length_" + solution.getSolutionlength();
                String path = graphsFolder + "/" + title;
                solution.getControlflowGraph(title, orientation).getWrite2File(new File(path));
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
