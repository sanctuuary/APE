package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import nl.uu.cs.ape.sat.configuration.*;
import nl.uu.cs.ape.sat.models.Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.DataInstance;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;

/**
 * The {@link APERunConfig} class is used to define the run configuration
 * variables, required for the proper execution of the synthesis process.
 *
 * @author Vedran Kasalica
 */
public class APERunConfig extends APEConfig {

	/**
	 * Should be in correct order of dependencies.
	 *
	 * @return all the Tags specified in this class.
	 */
	@Override
	public APEConfigTag<?>[] getAllTags() {
		return new APEConfigTag<?>[]{
				CONSTRAINTS,
				SHARED_MEMORY,
				SOLUTION_PATH,
				SOLUTION_LENGTH_RANGE,
				MAX_NO_SOLUTIONS,
				EXECUTION_SCRIPTS_FOLDER,
				SOLUTION_GRAPHS_FOLDER,
				NO_EXECUTIONS,
				NO_GRAPHS,
				USE_WORKFLOW_INPUT,
				USE_ALL_GENERATED_DATA,
				DEBUG_MODE,
				TOOL_SEQ_REPEAT,
				PROGRAM_INPUTS,
				PROGRAM_OUTPUTS
		};
	}

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
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	public final APEConfigTag<Path> SOLUTION_PATH = new APEConfigTagFactory.TAGS.SOLUTION_PATH();

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
	 * Path to the folder that will contain all the scripts generated based on the
	 * candidate workflows.
	 */
	public final APEConfigTag<Path> EXECUTION_SCRIPTS_FOLDER = new APEConfigTagFactory.TAGS.EXECUTION_SCRIPTS_FOLDER();

	/**
	 * Path to the folder that will contain all the figures/graphs generated based
	 * on the candidate workflows.
	 */
	public final APEConfigTag<Path> SOLUTION_GRAPHS_FOLDER = new APEConfigTagFactory.TAGS.SOLUTION_GRAPHS_FOLDER();

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
	 * Input types of the workflow.
	 */
	public final APEConfigTag<List<DataInstance>> PROGRAM_INPUTS = new APEConfigTagFactory.TAGS.PROGRAM_INPUTS(this::getApeDomainSetup);

	/**
	 * Output types of the workflow.
	 */
	public final APEConfigTag<List<DataInstance>> PROGRAM_OUTPUTS = new APEConfigTagFactory.TAGS.PROGRAM_OUTPUTS(this::getApeDomainSetup);

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
	private final APEConfigTag<Boolean> TOOL_SEQ_REPEAT = new APEConfigTagFactory.TAGS.TOOL_SEQ_REPEAT();

	/**
	 * Object containing domain information needed for the execution.
	 */
	private final APEDomainSetup apeDomainSetup;

	/**
	 * Constructor used to implement the Builder Pattern.
	 * 
	 * @param builder Builder object
	 */
	private APERunConfig(Builder builder) {

		if (getApeDomainSetup() == null) {
			throw new APEConfigException("Domain setup provided cannot have null value.");
		}

		this.apeDomainSetup = builder.apeDomainSetup;

		setConstraintsPath(builder.constraintsPath);
		setSolutionLength(builder.solutionMinLength, builder.solutionMaxLength);
		setMaxNoSolutions(builder.maxNoSolutions);
		setSharedMemory(builder.sharedMemory);
		setToolSeqRepeat(builder.toolSeqRepeat);
		setSolutionPath(builder.solutionPath);
		setExecutionScriptsFolder(builder.executionScriptsFolder);
		setSolutionGraphsFolder(builder.solutionGraphsFolder);
		setNoExecutions(builder.noExecutions);
		setNoGraphs(builder.noGraphs);
		setProgramInputs(builder.programInputs);
		setProgramOutputs(builder.programOutputs);
		setUseWorkflowInput(builder.useWorkflowInput);
		setUseAllGeneratedData(builder.useAllGeneratedData);
		setDebugMode(builder.debugMode);
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
		if (getApeDomainSetup() == null) {
			throw new APEConfigException("Domain setup provided cannot have null value.");
		}

		this.apeDomainSetup = apeDomainSetup;

		// set the apeDomain BEFORE setting the tags
		for(APEConfigTag<?> tag : allTags()){
			tag.setValue(runConfiguration);
		}
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
	 * Returns true if the shared memory structure should be used, i.e. if the
	 * generated data is available in memory to all the tools used subsequently, or
	 * false in case of a restrictive message passing structure, i.e. if the
	 * generated data is available only to the tool next in sequence.
	 *
	 * @return true if the shared memory structure should be used, false in case of
	 *         a restrictive message passing structure.
	 */
	public boolean getSharedMemory() {
		return SHARED_MEMORY.getValue();
	}

	/**
	 * Returns false if the provided solutions should be distinguished based on the
	 * tool sequences alone, i.e. tool sequences cannot repeat, ignoring the types
	 * in the solutions.
	 *
	 * @return true if tool sequences cannot repeat, ignoring the types in the
	 *         solutions, or false in case that the tool sequences can repeat as
	 *         long as the corresponding types differ.
	 */
	public boolean getToolSeqRepeat() {
		return TOOL_SEQ_REPEAT.getValue();
	}

	/**
	 * Gets solution path.
	 *
	 * @return the value of {@link #SOLUTION_PATH}
	 */
	public Path getSolutionPath() {
		return SOLUTION_PATH.getValue();
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
	 * Gets execution scripts folder.
	 *
	 * @return the value of {@link #EXECUTION_SCRIPTS_FOLDER}
	 */
	public Path getExecutionScriptsFolder() {
		return EXECUTION_SCRIPTS_FOLDER.getValue();
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
	 * Gets solution graphs folder.
	 *
	 * @return the value of {@link #SOLUTION_GRAPHS_FOLDER}
	 */
	public Path getSolutionGraphsFolder() {
		return SOLUTION_GRAPHS_FOLDER.getValue();
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
	 * Gets program inputs.
	 *
	 * @return the value of {@link #PROGRAM_INPUTS}
	 */
	public List<DataInstance> getProgramInputs() {
		return PROGRAM_INPUTS.getValue();
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
	 * Gets use workflow input.
	 *
	 * @return the value of {@link #USE_WORKFLOW_INPUT}
	 */
	public ConfigEnum getUseWorkflowInput() {
		return USE_WORKFLOW_INPUT.getValue();
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
	 * Gets debug mode.
	 *
	 * @return the value of {@link #DEBUG_MODE}
	 */
	public boolean getDebugMode() {
		return DEBUG_MODE.getValue();
	}
	
	/**
	 * @param constraintsPath the constraintsPath to set
	 */
	public void setConstraintsPath(String constraintsPath) {
		CONSTRAINTS.setValue(Paths.get(constraintsPath));
	}

	/**
	 * @param sharedMemory the sharedMemory to set
	 */
	public void setSharedMemory(boolean sharedMemory) {
		SHARED_MEMORY.setValue(sharedMemory);
	}

	/**
	 * @param toolSeqRepeat the toolSeqRepeat to set
	 */
	public void setToolSeqRepeat(boolean toolSeqRepeat) {
		TOOL_SEQ_REPEAT.setValue(toolSeqRepeat);
	}

	/**
	 * @param solutionPath the solutionPath to set
	 */
	public void setSolutionPath(String solutionPath) {
		SOLUTION_PATH.setValue(Paths.get(solutionPath));
	}

	/**
	 * @param solutionMinLength the solutionMinLength to set
	 */
	public void setSolutionLength(int solutionMinLength, int solutionMaxLength) {
		this.SOLUTION_LENGTH_RANGE.setValue(Range.of(solutionMinLength, solutionMaxLength));
	}

	/**
	 * @param maxNoSolutions the maxNoSolutions to set
	 */
	public void setMaxNoSolutions(int maxNoSolutions) {
		MAX_NO_SOLUTIONS.setValue(maxNoSolutions);
	}

	/**
	 * @param executionScriptsFolder the executionScriptsFolder to set
	 */
	public void setExecutionScriptsFolder(String executionScriptsFolder) {
		EXECUTION_SCRIPTS_FOLDER.setValue(Paths.get(executionScriptsFolder));
	}

	/**
	 * @param noExecutions the noExecutions to set
	 */
	public void setNoExecutions(int noExecutions) {
		NO_EXECUTIONS.setValue(noExecutions);
	}

	/**
	 * @param solutionGraphsFolder the solutionGraphsFolder to set
	 */
	public void setSolutionGraphsFolder(String solutionGraphsFolder) {
		SOLUTION_GRAPHS_FOLDER.setValue(Paths.get(solutionGraphsFolder));
	}

	/**
	 * @param noGraphs the noGraphs to set
	 */
	public void setNoGraphs(int noGraphs) {
		NO_GRAPHS.setValue(noGraphs);
	}

	/**
	 * @param programInputs the programInputs to set
	 */
	public void setProgramInputs(List<DataInstance> programInputs) {
		PROGRAM_INPUTS.setValue(programInputs);
	}

	/**
	 * @param programOutputs the programOutputs to set
	 */
	public void setProgramOutputs(List<DataInstance> programOutputs) {
		PROGRAM_OUTPUTS.setValue(programOutputs);
	}

	/**
	 * @param useWorkflowInput the useWorkflowInput to set
	 */
	public void setUseWorkflowInput(ConfigEnum useWorkflowInput) {
		USE_WORKFLOW_INPUT.setValue(useWorkflowInput);
	}

	/**
	 * @param useAllGeneratedData the useAllGeneratedData to set
	 */
	public void setUseAllGeneratedData(ConfigEnum useAllGeneratedData) {
		USE_ALL_GENERATED_DATA.setValue(useAllGeneratedData);
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		DEBUG_MODE.setValue(debugMode);
	}

	/**
	 * Gets cwl format root. TODO: Set real values.
	 * 
	 * @return the cwl format root
	 */
	public String getCWLFormatRoot() {
		return "format_1915";
	}

	private APEDomainSetup getApeDomainSetup() {
		return apeDomainSetup;
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
	 * Initialize the class without setting any parameters.
	 * This private constructor is used to create an empty class to retrieve the tags in a static way.
	 */
	private APERunConfig(){
		apeDomainSetup = null;
	}

	/**
	 * Creates builder to build {@link APERunConfig}.
	 * 
	 * @return created builder
	 */
	public static ISolutionMinLengthStage builder() {
		return new Builder();
	}

	public static JSONArray JSONTagInfo() {
		return new APERunConfig().getAllTagInfoJSON();
	}
	public static APEConfigTag<?>[] allTags() {
		return new APERunConfig().getAllTags();
	}
	public static APEConfigTag<?>[] obligatoryTags() {
		return new APERunConfig().getObligatoryTags();
	}
	public static APEConfigTag<?>[] optionalTags() {
		return new APERunConfig().getOptionalTags();
	}



	public interface ISolutionMinLengthStage {
		public ISolutionMaxLengthStage withSolutionMinLength(int solutionMinLength);
	}

	public interface ISolutionMaxLengthStage {
		public IMaxNoSolutionsStage withSolutionMaxLength(int solutionMaxLength);
	}

	public interface IMaxNoSolutionsStage {
		public IApeDomainSetupStage withMaxNoSolutions(int maxNoSolutions);
	}

	public interface IApeDomainSetupStage {
		public IBuildStage withApeDomainSetup(APEDomainSetup apeDomainSetup);
	}

	public interface IBuildStage {
		public IBuildStage withConstraintsPath(String constraintsPath);

		public IBuildStage withSharedMemory(boolean sharedMemory);

		public IBuildStage withToolSeqRepeat(boolean toolSeqRepeat);

		public IBuildStage withSolutionPath(String solutionPath);

		public IBuildStage withExecutionScriptsFolder(String executionScriptsFolder);

		public IBuildStage withNoExecutions(int noExecutions);

		public IBuildStage withSolutionGraphsFolder(String solutionGraphsFolder);

		public IBuildStage withNoGraphs(int noGraphs);

		public IBuildStage withProgramInputs(List<DataInstance> programInputs);

		public IBuildStage withProgramOutputs(List<DataInstance> programOutputs);

		public IBuildStage withUseWorkflowInput(ConfigEnum useWorkflowInput);

		public IBuildStage withUseAllGeneratedData(ConfigEnum useAllGeneratedData);

		public IBuildStage withDebugMode(boolean debugMode);

		public APERunConfig build();
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
		private String solutionPath;
		private String executionScriptsFolder;
		private int noExecutions;
		private String solutionGraphsFolder;
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
		public IBuildStage withSolutionPath(String solutionPath) {
			this.solutionPath = solutionPath;
			return this;
		}

		@Override
		public IBuildStage withExecutionScriptsFolder(String executionScriptsFolder) {
			this.executionScriptsFolder = executionScriptsFolder;
			return this;
		}

		@Override
		public IBuildStage withNoExecutions(int noExecutions) {
			this.noExecutions = noExecutions;
			return this;
		}

		@Override
		public IBuildStage withSolutionGraphsFolder(String solutionGraphsFolder) {
			this.solutionGraphsFolder = solutionGraphsFolder;
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
