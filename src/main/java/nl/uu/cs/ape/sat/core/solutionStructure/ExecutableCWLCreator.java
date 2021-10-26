package nl.uu.cs.ape.sat.core.solutionStructure;

import nl.uu.cs.ape.sat.configuration.APEConfigException;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to generate executable CWL workflows from a given workflow solution.
 */
public class ExecutableCWLCreator extends CWLCreatorBase {
    // Keep track of the inputs that are available in the workflow (key: TypeNode NodeID, value: input name).
    private final HashMap<String, String> inputMap = new HashMap<>();
    // Keep track of the step names related to the ModuleNode NodeIDs (key: NodeID, value: step name).
    private final HashMap<String, String> stepNames = new HashMap<>();
    // Keep track the current step number being added to the result.
    private int stepIndex = 1;

    public ExecutableCWLCreator(SolutionWorkflow solution) {
        super(solution);
    }

    @Override
    public String getCWLVersion() {
        return "v1.0";
    }

    @Override
    protected void generateCWLRepresentation() {
        generateRequirements();
        cwlRepresentation.append("\n");

        /*
         * A tool may not have its CWL inputs, catch this and print en error to the console.
         * A tool is allowed to not have an implementation, therefore it is not in the try/catch block.
         */
        try {
            generateInputs();
        } catch (APEConfigException e) {
            System.err.println(e.getMessage());
            return;
        }
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
    private void generateInputs() throws APEConfigException {
        cwlRepresentation.append("inputs:").append("\n");

        // Keep track of the inputs that need to be appended.
        LinkedHashMap<TypeNode, Object> toAppend = new LinkedHashMap<>();

        // Find all input TypeNodes
        for (TypeNode inputState : solution.getWorkflowInputTypeStates()) {
            ModuleNode module = inputState.getUsedByModules().get(0);
            ArrayList<LinkedHashMap<String, String>> cwlInputs = module.getUsedModule().getCwlInputs();
            if (cwlInputs == null) {
                throw new APEConfigException(String.format("Tool \"%s\" is missing its CWL implementation!", module.getNodeLabel()));
            }

            int index = 0;
            for (TypeNode t : module.getInputTypes()) {
                if (t.getNodeID().equals(inputState.getNodeID())) {
                    break;
                }
                index++;
            }
            toAppend.put(inputState, cwlInputs.get(index));
        }

        for (ModuleNode module : solution.getModuleNodes()) {
            ArrayList<LinkedHashMap<String, String>> cwlInputs = module.getUsedModule().getCwlInputs();
            if (cwlInputs == null) {
                throw new APEConfigException(String.format("Tool \"%s\" is missing its CWL implementation!", module.getNodeLabel()));
            }

            // If a module does not have any input types, it is assumed it is a workflow input
            if (module.getInputTypes().isEmpty()) {
                for (int i = 0; i < module.getOutputTypes().size(); i++) {
                    TypeNode node = module.getOutputTypes().get(i);
                    if (!node.getUsedByModules().isEmpty() && !cwlInputs.isEmpty()) {
                        toAppend.put(node, cwlInputs.get(i));
                    }
                }
            }
        }

        // Keep track of the name replacements that need to be done.
        ArrayList<String> toReplace = new ArrayList<>();
        // Keep track of how many times a certain input name appears. Used to prevent duplicate names.
        HashMap<String, Integer> nameCounter = new HashMap<>();
        Yaml yaml = new Yaml();
        // Give each input a unique name
        for (Map.Entry<TypeNode, Object> entry : toAppend.entrySet()) {
            String inputName = getInputName(yaml.dump(entry.getValue()));
            String newName = inputName;
            if (nameCounter.containsKey(inputName)) {
                int count = nameCounter.get(inputName);
                newName += count + 1;
                nameCounter.replace(inputName, count + 1);
            } else {
                newName += 1;
                nameCounter.put(inputName, 1);
            }
            toReplace.add(newName);
            inputMap.put(entry.getKey().getNodeID(), newName);
        }

        // Create the CWL data to append
        HashMap<String, Object> appending = new HashMap<>();
        Object[] h = toAppend.values().toArray();
        int index = 0;
        for (Object obj : toAppend.values()) {
            LinkedHashMap<String, Object> entry = (LinkedHashMap<String, Object>) obj;
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) entry.entrySet().toArray()[0];
            appending.put(toReplace.get(index), e.getValue());
            index++;
        }
        appendYamlData(yaml.dump(appending), 1);
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
            Map<String, Object> implementation = moduleNode.getUsedModule().getCwlImplementation();
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
     * Append YAML data line-by-line to get the desired indentation level.
     * @param data The data to be appended.
     * @param indentLevel The desired indentation level.
     */
    private void appendYamlData(String data, int indentLevel) {
        for (String line : data.split("\\r?\\n")) {
            this.cwlRepresentation.append(ind(indentLevel)).append(line).append("\n");
        }
    }
}
