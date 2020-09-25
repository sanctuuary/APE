package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.configuration.tags.APEConfigDependentTag;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTag;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTagFactory;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTagFactory.TAGS.*;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTags;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.Range;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * The {@link APERunConfig} class is used to define the run configuration
 * variables, required for the proper execution of the synthesis process.
 *
 * @author Vedran Kasalica
 */
public class APERunConfig {

    /**
     * Static versions of the Tags specified in this class. Should be in correct order for the Web API.
     */
    private static final APEConfigTags tag_info = new APEConfigTags(
            new CONSTRAINTS(),
            new SHARED_MEMORY(),
            new SOLUTION_DIR_PATH(),
            new SOLUTION_LENGTH_RANGE(),
            new MAX_NO_SOLUTIONS(),
            new NO_EXECUTIONS(),
            new NO_GRAPHS(),
            new USE_WORKFLOW_INPUT(),
            new USE_ALL_GENERATED_DATA(),
            new DEBUG_MODE(),
            new TOOL_SEQ_REPEAT(),
            new PROGRAM_OUTPUTS(null),
            new PROGRAM_INPUTS(null)
    );

    /**
     * Path to the file with all workflow constraints.
     */
    public final APEConfigTag<Path> CONSTRAINTS = new APEConfigTagFactory.TAGS.CONSTRAINTS();
    /**
     * true if the shared memory structure should be used, false in case of a
     * restrictive message passing structure.
     */
    public final APEConfigTag<Boolean> SHARED_MEMORY = new APEConfigTagFactory.TAGS.SHARED_MEMORY();
    /**
     * Path to the directory that will contain all the solutions to the problem.
     */
    public final APEConfigTag<Path> SOLUTION_DIR_PATH = new APEConfigTagFactory.TAGS.SOLUTION_DIR_PATH();
    /**
     * Min and Max possible length of the solutions (length of the automaton). For
     * no upper limit, max length should be set to 0.
     */
    public final APEConfigTag<Range> SOLUTION_LENGTH_RANGE = new APEConfigTagFactory.TAGS.SOLUTION_LENGTH_RANGE();
    /**
     * Max number of solution that the solver will return.
     */
    public final APEConfigTag<Integer> MAX_NO_SOLUTIONS = new APEConfigTagFactory.TAGS.MAX_NO_SOLUTIONS();
    /**
     * Number of the workflow scripts that should be generated from candidate
     * workflows. Default is 0.
     */
    public final APEConfigTag<Integer> NO_EXECUTIONS = new APEConfigTagFactory.TAGS.NO_EXECUTIONS();
    /**
     * Number of the solution graphs that should be generated from candidate
     * workflows. Default is 0.
     */
    public final APEConfigTag<Integer> NO_GRAPHS = new APEConfigTagFactory.TAGS.NO_GRAPHS();
    /**
     * Determines the required usage for the data instances that are given as
     * workflow input:<br>
     * {@link ConfigEnum#ALL} if all the workflow inputs have to be used,<br>
     * {@link ConfigEnum#ONE} if one of the workflow inputs should be used or <br>
     * {@link ConfigEnum#NONE} if none of the workflow inputs has to be used
     */
    public final APEConfigTag<ConfigEnum> USE_WORKFLOW_INPUT = new APEConfigTagFactory.TAGS.USE_WORKFLOW_INPUT();
    /**
     * Determines the required usage for the generated data instances:<br>
     * {@link ConfigEnum#ALL} if all the generated data has to be used,<br>
     * {@link ConfigEnum#ONE} if one of the data instances that are generated as
     * output, per tool, has to be used or <br>
     * {@link ConfigEnum#NONE} if none of the data instances is obligatory to use.
     */
    public final APEConfigTag<ConfigEnum> USE_ALL_GENERATED_DATA = new APEConfigTagFactory.TAGS.USE_ALL_GENERATED_DATA();
    /**
     * Mode is true if debug mode is turned on.
     */
    public final APEConfigTag<Boolean> DEBUG_MODE = new APEConfigTagFactory.TAGS.DEBUG_MODE();
    /**
     * false iff the provided solutions should be distinguished based on the tool
     * sequences alone, i.e. tool sequences cannot repeat, ignoring the types in the
     * solutions.
     */
    public final APEConfigTag<Boolean> TOOL_SEQ_REPEAT = new APEConfigTagFactory.TAGS.TOOL_SEQ_REPEAT();
    /**
     * Input types of the workflow.
     */
    public final APEConfigDependentTag.One<List<DataInstance>, APEDomainSetup> PROGRAM_INPUTS = new APEConfigTagFactory.TAGS.PROGRAM_INPUTS(this::getApeDomainSetup);
    /**
     * Output types of the workflow.
     */
    public final APEConfigDependentTag.One<List<DataInstance>, APEDomainSetup> PROGRAM_OUTPUTS = new APEConfigTagFactory.TAGS.PROGRAM_OUTPUTS(this::getApeDomainSetup);
    /**
     * All the Tags specified in this class. Should be in correct order of dependencies.
     */
    private final APEConfigTag<?>[] all_tags = new APEConfigTag[]{
            this.CONSTRAINTS,
            this.SHARED_MEMORY,
            this.SOLUTION_DIR_PATH,
            this.SOLUTION_LENGTH_RANGE,
            this.MAX_NO_SOLUTIONS,
            this.NO_EXECUTIONS,
            this.NO_GRAPHS,
            this.USE_WORKFLOW_INPUT,
            this.USE_ALL_GENERATED_DATA,
            this.DEBUG_MODE,
            this.TOOL_SEQ_REPEAT,
            this.PROGRAM_OUTPUTS,
            this.PROGRAM_INPUTS
    };
    /**
     * Object containing domain information needed for the execution.
     */
    public APEDomainSetup apeDomainSetup;

    /**
     * Constructor used to implement the Builder Pattern.
     *
     * @param builder Builder object
     */
    private APERunConfig(Builder builder) {

        if (builder.apeDomainSetup == null) {
            throw new APEConfigException("Domain setup provided cannot have null value.");
        }

        this.apeDomainSetup = builder.apeDomainSetup;

        setConstraintsPath(builder.constraintsPath);
        setSolutionLength(builder.solutionMinLength, builder.solutionMaxLength);
        setMaxNoSolutions(builder.maxNoSolutions);
        setSharedMemory(builder.sharedMemory);
        setToolSeqRepeat(builder.toolSeqRepeat);
        setSolutionPath(builder.solutionDirPath);
        setNoExecutions(builder.noExecutions);
        setNoGraphs(builder.noGraphs);
        setUseWorkflowInput(builder.useWorkflowInput);
        setUseAllGeneratedData(builder.useAllGeneratedData);
        setDebugMode(builder.debugMode);
        setProgramInputs(builder.programInputs);
        setProgramOutputs(builder.programOutputs);
    }

    private APERunConfig(APEDomainSetup setup){
        this.apeDomainSetup = setup;
    }

    public static ValidationResults validate(JSONObject json, APEDomainSetup setup){
        APERunConfig dummy = new APERunConfig(setup);
        ValidationResults results = new ValidationResults();
        for(APEConfigTag<?> tag : dummy.all_tags){
            results.add(tag.validate(json));
            if(results.hasFails()){
                return results;
            }
            else{
                tag.setValue(json); // for dependencies
            }
        }
        return results;
    }

    /**
     * Setup the configuration for the current run of the synthesis.
     *
     * @param runConfiguration The APE configuration {@link JSONObject}.
     * @param apeDomainSetup   The ape domain setup
     * @throws IOException        Error in reading the configuration file.
     * @throws JSONException      Error in parsing the configuration file.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public APERunConfig(JSONObject runConfiguration, APEDomainSetup apeDomainSetup)
            throws IOException, JSONException, APEConfigException {

        /* JSONObject must have been parsed correctly. */
        if (runConfiguration == null) {
            throw new APEConfigException(
                    "Cannot set up the run configuration, because the JSONObject is initialized to NULL. The configuration file might not have been parsed correctly.");
        }
        if (apeDomainSetup == null) {
            throw new APEConfigException("Domain setup provided cannot have null value.");
        }

        this.apeDomainSetup = apeDomainSetup;

        // set the apeDomain BEFORE setting the tags
        for (APEConfigTag<?> tag : all_tags) {
            tag.setValue(runConfiguration);
        }
    }

    /**
     * Function that returns the tags that are used in the JSON files. Function can
     * be used to rename the tags.
     *
     * @param tag that is used
     * @return json tags
     */
    public static String getJsonTags(String tag) {
        switch (tag) {
            case "id":
                return "id";
            case "label":
                return "label";
            case "inputs":
                return "inputs";
            case "taxonomyOperations":
                return "taxonomyOperations";
            case "outputs":
                return "outputs";
            case "implementation":
                return "implementation";
            case "code":
                return "code";
            default:
                return null;
        }
    }

    /**
     * Creates builder to build {@link APERunConfig}.
     *
     * @return created builder
     */
    public static ISolutionMinLengthStage builder() {
        return new Builder();
    }

    public static APEConfigTags getTags() {
        return tag_info;
    }

    public APEDomainSetup getApeDomainSetup() {
        return this.apeDomainSetup;
    }

    /**
     * Gets constraints path.
     *
     * @return the value of {@link #CONSTRAINTS}
     */
    public Path getConstraintsPath() {
        return CONSTRAINTS.getValue();
    }

    /**
     * @param constraintsPath the constraintsPath to set
     */
    public void setConstraintsPath(String constraintsPath) {
        CONSTRAINTS.setValue(Paths.get(constraintsPath));
    }

    /**
     * Returns true if the shared memory structure should be used, i.e. if the
     * generated data is available in memory to all the tools used subsequently, or
     * false in case of a restrictive message passing structure, i.e. if the
     * generated data is available only to the tool next in sequence.
     *
     * @return true if the shared memory structure should be used, false in case of
     * a restrictive message passing structure.
     */
    public boolean getSharedMemory() {
        return SHARED_MEMORY.getValue();
    }

    /**
     * @param sharedMemory the sharedMemory to set
     */
    public void setSharedMemory(boolean sharedMemory) {
        SHARED_MEMORY.setValue(sharedMemory);
    }

    /**
     * Returns false if the provided solutions should be distinguished based on the
     * tool sequences alone, i.e. tool sequences cannot repeat, ignoring the types
     * in the solutions.
     *
     * @return true if tool sequences cannot repeat, ignoring the types in the
     * solutions, or false in case that the tool sequences can repeat as
     * long as the corresponding types differ.
     */
    public boolean getToolSeqRepeat() {
        return TOOL_SEQ_REPEAT.getValue();
    }

    /**
     * @param toolSeqRepeat the toolSeqRepeat to set
     */
    public void setToolSeqRepeat(boolean toolSeqRepeat) {
        TOOL_SEQ_REPEAT.setValue(toolSeqRepeat);
    }

    /**
     * Gets solution path.
     *
     * @return the value of {@link #SOLUTION_DIR_PATH}
     */
    public Path getSolutionDirPath() {
        return SOLUTION_DIR_PATH.getValue();
    }

    /**
     * Get the path to the directory where the graphs representation of the solutions should be stored.
     *
     * @return
     */
    public Path getSolutionDirPath2(String relativePath) {
        // relative paths should not start with '/' or '\'
        if (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
            return getSolutionDirPath().resolve(relativePath.substring(1));
        }
        return getSolutionDirPath().resolve(relativePath);
    }

    /**
     * Get the path to the directory where the executable scripts corresponding to the given solutions should be stored.
     *
     * @return
     */
    public Path getSolutionDirPath2Executables() {
        return getSolutionDirPath2("Executables");
    }

    /**
     * Get the path to the directory where the graphs representation of the solutions should be stored.
     *
     * @return
     */
    public Path getSolutionDirPath2Figures() {
        return getSolutionDirPath2("Figures");
    }

    /**
     * @param solutionPath the solutionPath to set
     */
    public void setSolutionPath(String solutionPath) {
        SOLUTION_DIR_PATH.setValue(Paths.get(solutionPath));
    }

    /**
     * Gets solution min and max length.
     *
     * @return the value of {@link #SOLUTION_LENGTH_RANGE}
     */
    public Range getSolutionLength() {
        return SOLUTION_LENGTH_RANGE.getValue();
    }

    /**
     * Gets max no solutions.
     *
     * @return the value of {@link #MAX_NO_SOLUTIONS}
     */
    public int getMaxNoSolutions() {
        return MAX_NO_SOLUTIONS.getValue();
    }

    /**
     * @param maxNoSolutions the maxNoSolutions to set
     */
    public void setMaxNoSolutions(int maxNoSolutions) {
        MAX_NO_SOLUTIONS.setValue(maxNoSolutions);
    }

    /**
     * Gets no executions.
     *
     * @return the value of {@link #NO_EXECUTIONS}
     */
    public int getNoExecutions() {
        return NO_EXECUTIONS.getValue();
    }

    /**
     * @param noExecutions the noExecutions to set
     */
    public void setNoExecutions(int noExecutions) {
        NO_EXECUTIONS.setValue(noExecutions);
    }

    /**
     * Gets no graphs.
     *
     * @return the value of {@link #NO_GRAPHS}
     */
    public int getNoGraphs() {
        return NO_GRAPHS.getValue();
    }

    /**
     * @param noGraphs the noGraphs to set
     */
    public void setNoGraphs(int noGraphs) {
        NO_GRAPHS.setValue(noGraphs);
    }

    /**
     * Gets program inputs.
     *
     * @return the value of {@link #PROGRAM_INPUTS}
     */
    public List<DataInstance> getProgramInputs() {
        return PROGRAM_INPUTS.getValue();
    }

    /**
     * @param programInputs the programInputs to set
     */
    public void setProgramInputs(List<DataInstance> programInputs) {
        PROGRAM_INPUTS.setValue(programInputs);
    }

    /**
     * Gets program outputs.
     *
     * @return the value of {@link #PROGRAM_OUTPUTS}
     */
    public List<DataInstance> getProgramOutputs() {
        return PROGRAM_OUTPUTS.getValue();
    }

    /**
     * @param programOutputs the programOutputs to set
     */
    public void setProgramOutputs(List<DataInstance> programOutputs) {
        PROGRAM_OUTPUTS.setValue(programOutputs);
    }

    /**
     * Gets use workflow input.
     *
     * @return the value of {@link #USE_WORKFLOW_INPUT}
     */
    public ConfigEnum getUseWorkflowInput() {
        return USE_WORKFLOW_INPUT.getValue();
    }

    /**
     * @param useWorkflowInput the useWorkflowInput to set
     */
    public void setUseWorkflowInput(ConfigEnum useWorkflowInput) {
        USE_WORKFLOW_INPUT.setValue(useWorkflowInput);
    }

    /**
     * Gets all generated data.
     *
     * @return the value of {@link #USE_ALL_GENERATED_DATA}
     */
    public ConfigEnum getUseAllGeneratedData() {
        return USE_ALL_GENERATED_DATA.getValue();
    }

    /**
     * @param useAllGeneratedData the useAllGeneratedData to set
     */
    public void setUseAllGeneratedData(ConfigEnum useAllGeneratedData) {
        USE_ALL_GENERATED_DATA.setValue(useAllGeneratedData);
    }

    /**
     * Gets debug mode.
     *
     * @return the value of {@link #DEBUG_MODE}
     */
    public boolean getDebugMode() {
        return DEBUG_MODE.getValue();
    }

    /**
     * @param debugMode the debugMode to set
     */
    public void setDebugMode(boolean debugMode) {
        DEBUG_MODE.setValue(debugMode);
    }

    /**
     * @param solutionMinLength the solutionMinLength to set
     */
    public void setSolutionLength(int solutionMinLength, int solutionMaxLength) {
        this.SOLUTION_LENGTH_RANGE.setValue(Range.of(solutionMinLength, solutionMaxLength));
    }

    /**
     * Gets cwl format root. TODO: Set real values.
     *
     * @return the cwl format root
     */
    public String getCWLFormatRoot() {
        return "format_1915";
    }


    /**
     * Creates builder to build {@link APERunConfig}.
     *
     * @return created builder
     */
    public interface ISolutionMinLengthStage {
        ISolutionMaxLengthStage withSolutionMinLength(int solutionMinLength);
    }

    public interface ISolutionMaxLengthStage {
        IMaxNoSolutionsStage withSolutionMaxLength(int solutionMaxLength);
    }

    public interface IMaxNoSolutionsStage {
        IApeDomainSetupStage withMaxNoSolutions(int maxNoSolutions);
    }

    public interface IApeDomainSetupStage {
        IBuildStage withApeDomainSetup(APEDomainSetup apeDomainSetup);
    }

    public interface IBuildStage {
        IBuildStage withConstraintsPath(String constraintsPath);

        IBuildStage withSharedMemory(boolean sharedMemory);

        IBuildStage withToolSeqRepeat(boolean toolSeqRepeat);

        IBuildStage withSolutionDirPath(String solutionPath);

        IBuildStage withNoExecutions(int noExecutions);

        IBuildStage withNoGraphs(int noGraphs);

        IBuildStage withProgramInputs(List<DataInstance> programInputs);

        IBuildStage withProgramOutputs(List<DataInstance> programOutputs);

        IBuildStage withUseWorkflowInput(ConfigEnum useWorkflowInput);

        IBuildStage withUseAllGeneratedData(ConfigEnum useAllGeneratedData);

        IBuildStage withDebugMode(boolean debugMode);

        APERunConfig build();
    }

    /**
     * Builder to build {@link APERunConfig}.
     */
    public static final class Builder implements ISolutionMinLengthStage, ISolutionMaxLengthStage, IMaxNoSolutionsStage,
            IApeDomainSetupStage, IBuildStage {
        private int solutionMinLength;
        private int solutionMaxLength;
        private int maxNoSolutions;
        private APEDomainSetup apeDomainSetup;
        private String constraintsPath;
        private boolean sharedMemory;
        private boolean toolSeqRepeat;
        private String solutionDirPath;
        private int noExecutions;
        private int noGraphs;
        private List<DataInstance> programInputs = Collections.emptyList();
        private List<DataInstance> programOutputs = Collections.emptyList();
        private ConfigEnum useWorkflowInput;
        private ConfigEnum useAllGeneratedData;
        private boolean debugMode;

        private Builder() {
        }

        @Override
        public ISolutionMaxLengthStage withSolutionMinLength(int solutionMinLength) {
            this.solutionMinLength = solutionMinLength;
            return this;
        }

        @Override
        public IMaxNoSolutionsStage withSolutionMaxLength(int solutionMaxLength) {
            this.solutionMaxLength = solutionMaxLength;
            return this;
        }

        @Override
        public IApeDomainSetupStage withMaxNoSolutions(int maxNoSolutions) {
            this.maxNoSolutions = maxNoSolutions;
            return this;
        }

        @Override
        public IBuildStage withApeDomainSetup(APEDomainSetup apeDomainSetup) {
            this.apeDomainSetup = apeDomainSetup;
            return this;
        }

        @Override
        public IBuildStage withConstraintsPath(String constraintsPath) {
            this.constraintsPath = constraintsPath;
            return this;
        }

        @Override
        public IBuildStage withSharedMemory(boolean sharedMemory) {
            this.sharedMemory = sharedMemory;
            return this;
        }

        @Override
        public IBuildStage withToolSeqRepeat(boolean toolSeqRepeat) {
            this.toolSeqRepeat = toolSeqRepeat;
            return this;
        }

        @Override
        public IBuildStage withSolutionDirPath(String solutionDirPath) {
            this.solutionDirPath = solutionDirPath;
            return this;
        }


        @Override
        public IBuildStage withNoExecutions(int noExecutions) {
            this.noExecutions = noExecutions;
            return this;
        }

        @Override
        public IBuildStage withNoGraphs(int noGraphs) {
            this.noGraphs = noGraphs;
            return this;
        }

        @Override
        public IBuildStage withProgramInputs(List<DataInstance> programInputs) {
            this.programInputs = programInputs;
            return this;
        }

        @Override
        public IBuildStage withProgramOutputs(List<DataInstance> programOutputs) {
            this.programOutputs = programOutputs;
            return this;
        }

        @Override
        public IBuildStage withUseWorkflowInput(ConfigEnum useWorkflowInput) {
            this.useWorkflowInput = useWorkflowInput;
            return this;
        }

        @Override
        public IBuildStage withUseAllGeneratedData(ConfigEnum useAllGeneratedData) {
            this.useAllGeneratedData = useAllGeneratedData;
            return this;
        }

        @Override
        public IBuildStage withDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }

        @Override
        public APERunConfig build() {
            return new APERunConfig(this);
        }
    }
}
