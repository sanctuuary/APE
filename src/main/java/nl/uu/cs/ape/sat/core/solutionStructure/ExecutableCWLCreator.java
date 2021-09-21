package nl.uu.cs.ape.sat.core.solutionStructure;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutableCWLCreator extends CWLCreatorBase {
    Map<String, Object> cwlAnnotations;
    HashMap<String, String> inputMap = new HashMap<>();
    HashMap<String, String> stepNames = new HashMap<>();
    int stepIndex = 1;

    public ExecutableCWLCreator(SolutionsList solutionsList, SolutionWorkflow solution) {
        super(solution);

        // TODO: load CWL annotations elsewhere
        Path configPath = solutionsList.getRunConfiguration().getSolutionDirPath().getParent();
        Yaml yaml = new Yaml();
        File cwlYamlFile = configPath.resolve("cwl.yaml").toFile();
        try {
            this.cwlAnnotations = yaml.load(new FileInputStream(cwlYamlFile));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find CWL yaml configuration file!");
            e.printStackTrace();
        }
    }

    @Override
    public String getCWLVersion() {
        return "v1.0";
    }

    @Override
    protected void generateCWLRepresentation() {
        generateRequirements();
        cwlRepresentation.append("\n");

        generateInputs();
        int offset = cwlRepresentation.length();
        generateSteps();
        generateOutputs(offset);
    }

    /**
     * Generate the requirements section of the CWL file.
     */
    private void generateRequirements() {
        cwlRepresentation
            .append("requirements:").append("\n")
            .append(ind(1)).append("ShellCommandRequirement: {}").append("\n")
            .append(ind(1)).append("InlineJavascriptRequirement: {}").append("\n");
    }

    /**
     * Generate the top-level inputs of the CWL workflow.
     */
    private void generateInputs() {
        cwlRepresentation.append("inputs:").append("\n");
        // Keep track of the inputs that need to be appended.
        ArrayList<Object> toAppend = new ArrayList<>();
        // Keep track of the name replacements that need to be done.
        HashMap<String, String> toReplace = new HashMap<>();
        // Keep track of how many times a certain input name appears. Used to prevent duplicate names.
        HashMap<String, Integer> nameCounter = new HashMap<>();

        for (ModuleNode module : solution.getModuleNodes()) {
            Map<String, Object> tool = (Map<String, Object>) this.cwlAnnotations.get(module.getNodeLabel());
            ArrayList<LinkedHashMap<String, String>> cwlInputs = (ArrayList<LinkedHashMap<String, String>>) tool.get("inputs");

            // If a module does not have any input types, it is assumed it is a workflow input
            if (module.getInputTypes().isEmpty()) {
                for (int i = 0; i < module.getOutputTypes().size(); i++) {
                    TypeNode node = module.getOutputTypes().get(i);
                    if (!node.getUsedByModules().isEmpty()) {
                        generateInput(node, cwlInputs, toAppend, toReplace, nameCounter, i);
                    }
                }
            } else {
                // If the input type of a module is not created by another module, it is assumed to be a workflow input
                List<TypeNode> inputTypes = module.getInputTypes();
                for (int i = 0; i < inputTypes.size(); i++) {
                    TypeNode node = inputTypes.get(i);
                    if (node.getCreatedByModule() == null) {
                        generateInput(node, cwlInputs, toAppend, toReplace, nameCounter, i);
                    }
                }
            }
        }

        // Convert the ArrayList to a HashMap to make sure it will be in the right format when it is dumped by SnakeYAML.
        HashMap<String, Object> appending = new HashMap<>();
        for (Object obj : toAppend.toArray()) {
            LinkedHashMap<String, Object> entry = (LinkedHashMap<String, Object>) obj;
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) entry.entrySet().toArray()[0];
            appending.put(e.getKey(), e.getValue());
        }

        // Perform the necessary name replacement from their names in the CWL annotations to the names in the generated CWL file.
        String data = new Yaml().dump(appending);
        for (Map.Entry<String, String> entry : toReplace.entrySet()) {
            data = replaceInputName(data, entry.getKey(), entry.getValue());
        }
        // Append all input data
        appendYamlData(data, 1);
    }

    /**
     * Generate all data needed to add the input to the CWL file.
     * @param node The {@link TypeNode} the input represents.
     * @param cwlInputs The "inputs" from the CWL annotations of this tool / {@link ModuleNode}.
     * @param toAppend The inputs that will be appended.
     * @param toReplace The name replacements that need to be done after the inputs have been generated.
     * @param nameCounter A counter keeping track of duplicate input names.
     * @param i The index of the TypeNodes of this {@link ModuleNode}.
     */
    private void generateInput(
        TypeNode node,
        ArrayList<LinkedHashMap<String, String>> cwlInputs,
        ArrayList<Object> toAppend,
        HashMap<String, String> toReplace,
        HashMap<String, Integer> nameCounter,
        int i
    ) {
        String code = new Yaml().dump(cwlInputs.get(i));
        String inputName = getInputName(code);
        String newName = inputName;
        if (nameCounter.containsKey(inputName)) {
            int count = nameCounter.get(inputName);
            newName += count + 1;
            nameCounter.replace(inputName, count + 1);
        } else {
            newName += 1;
            nameCounter.put(inputName, 1);
        }
        inputMap.put(node.getNodeID(), newName);
        toAppend.add(cwlInputs.get(i));
        toReplace.put(inputName, newName);
    }

    /**
     * Generate the top-level outputs of the CWL workflow.
     */
    private void generateOutputs(int offset) {
        StringBuilder toInsert = new StringBuilder();
        for (TypeNode outputType : solution.getWorkflowOutputTypeStates()) {
            String name = inputMap.get(outputType.getNodeID());
            if (name == null) {
                continue;
            }
            toInsert
                .append("outputs:").append("\n")
                .append(ind(1)).append("out:").append("\n")
                .append(ind(2)).append("type: File").append("\n")
                .append(ind(2)).append("outputSource: ").append(name).append("\n");
        }
        cwlRepresentation.insert(offset, toInsert);
    }

    /**
     * Generate each step of the workflow.
     * A single step represents a {@link ModuleNode} in the solution.
     */
    private void generateSteps() {
        cwlRepresentation.append("steps:").append("\n");

        for (ModuleNode moduleNode : solution.getModuleNodes()) {
            Map<String, Object> tool = (Map<String, Object>) this.cwlAnnotations.get(moduleNode.getNodeLabel());
            Map<String, Object> implementation = (Map<String, Object>) tool.get("implementation");
            if (implementation == null) {
                stepIndex++;
                continue;
            }

            String cwl = setStepName(moduleNode, implementation);
            String toAppend = connectStepInputs(moduleNode, cwl);
            findOutputs(moduleNode, cwl);
            appendYamlData(toAppend, 1);
            stepIndex++;
        }
    }

    /**
     * Get the input name from the CWL code defined in the CWL annotations.
     * @param cwl The CWL code.
     * @return The name of the input in the CWL code.
     */
    private String getInputName(String cwl) {
        Pattern pattern = Pattern.compile("\\\\@(\\w+)\\\\@");
        Matcher matcher = pattern.matcher(cwl);
        matcher.find();
        return matcher.group(1);
    }

    /**
     * Replace the CWL annotations name of an input in the CWL code.
     * @param cwl The CWL code wherein the name should be replaced.
     * @param old The old name of the input in the CWL code.
     * @param newName The new name for the input.
     * @return The CWL code with CWL annotations name for the input replaced by the new name.
     */
    private String replaceInputName(String cwl, String old, String newName) {
        return cwl.replaceFirst(String.format("\\\\@%s\\\\@", old), newName);
    }

    /**
     * Give a step its name in the generated CWL file.
     * This guarantees each workflow step has a unique name.
     * @param moduleNode The {@link ModuleNode} that is represented in this step.
     * @param implementation The CWL implementation of this step.
     * @return The CWL implementation as a string after giving all steps their definitive name.
     */
    private String setStepName(ModuleNode moduleNode, Map<String, Object> implementation) {
        String cwl = new Yaml().dump(implementation);
        Object[] keySet = implementation.keySet().toArray();
        for (int i = 0; i < keySet.length; i++) {
            String key = (String) keySet[i];
            String newKey = key + stepIndex;
            cwl = cwl.replaceAll(key, newKey);
            if (i == keySet.length - 1) {
                stepNames.put(moduleNode.getNodeID(), newKey);
            }
        }
        return cwl;
    }

    /**
     * Replace all placeholder input names from the annotations with
     * the definitive input names that are used in the generated CWL file.
     * @param moduleNode The {@link ModuleNode} that is represented in this step.
     * @param cwl The CWL implementation of this step.
     * @return The CWL implementation as a string, where all inputs have the correct names.
     */
    private String connectStepInputs(ModuleNode moduleNode, String cwl) {
        Pattern pattern = Pattern.compile("\\'\\\\\\@input\\[(\\d+)\\]\\'");
        Matcher matcher = pattern.matcher(cwl);
        List<TypeNode> inputs = moduleNode.getInputTypes();

        String result = cwl;
        while (matcher.find()) {
            String number = matcher.group(1);
            int index = Integer.parseInt(number);
            String newName = inputMap.get(inputs.get(index).getNodeID());
            result = result.replaceFirst(String.format("\\'\\\\\\@input\\[%s\\]\\'", number), newName);
        }
        return result;
    }

    /**
     * Detect the output(s) of a workflow step and store them in the input map, so they can be used as input in other steps.
     * @param moduleNode The {@link ModuleNode} that is represented by this step.
     * @param cwl The CWL implementation of this step.
     */
    private void findOutputs(ModuleNode moduleNode, String cwl) {
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> code = yaml.load(cwl);
        Object[] entryList = code.entrySet().toArray();
        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryList[entryList.length - 1];
        LinkedHashMap<String, Object> implementation = (LinkedHashMap<String, Object>) entry.getValue();
        ArrayList<String> outputs = (ArrayList<String>) implementation.get("out");
        int i = 0;
        for (TypeNode outputType : moduleNode.getOutputTypes()) {
            if (i >= outputs.size()) {
                break;
            }
            String outName = String.format("%s/%s", stepNames.get(moduleNode.getNodeID()), outputs.get(i));
            inputMap.put(outputType.getNodeID(), outName);
            i++;
        }
    }

    /**
     * Append the CWL implementation code of a certain tool to the generated CWL file.
     * @param toolName The name of the tool whose code should be appended.
     */
    private void appendCWLImplementation(String toolName) {
        Map<String, Object> tool = (Map<String, Object>) this.cwlAnnotations.get(toolName);
        Map<String, Object> implementation = (Map<String, Object>) tool.get("implementation");
        if (implementation == null) {
            return;
        }

        Yaml yaml = new Yaml();
        String data = yaml.dump(implementation);
        appendYamlData(data, 1);
    }

    /**
     * Append YAML data line-by-line to get the desired indentation level.
     * @param data The data to be appended.
     * @param indentLevel The desired indentation level.
     */
    private void appendYamlData(String data, int indentLevel) {
        for (String line : data.split("\\r?\\n")) {
            this.cwlRepresentation.append(ind(1)).append(line).append("\n");
        }
    }
}
