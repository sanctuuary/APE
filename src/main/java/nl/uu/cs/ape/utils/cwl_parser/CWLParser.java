package nl.uu.cs.ape.utils.cwl_parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import nl.uu.cs.ape.utils.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;

public class CWLParser {

    private Map<String, Object> cwlContent;

    private final String inputsKey = "inputs";
    private final String outputsKey = "outputs";
    private final String operationsKey = "intent";
    private final String formatTypeKey = "format";
    private final String dataTypeKey = "http://edamontology.org/data_0006";

    // Constructor to load CWL from a URL
    public CWLParser(String urlString) throws IOException {
        InputStream inputStream = new FileInputStream(APEFiles.readPathToFile(urlString));
        parseCWL(inputStream);
    }

    // Constructor to load CWL from a local file
    public CWLParser(java.nio.file.Path filePath) throws IOException {
        InputStream inputStream = Files.newInputStream(filePath);
        parseCWL(inputStream);
    }

    /**
     * Recursively traverses and expands all YAML nodes by substituting
     * namespace-prefixed keys and string values
     * using the provided namespace mapping. The method replaces every occurrence of
     * a prefix (e.g., "edam:xxx")
     * with the full IRI (e.g., "http://edamontology.org/xxx").
     *
     * @param node       The input YAML node (could be a Map, List, String, or
     *                   primitive)
     * @param namespaces A map of namespace prefixes to full IRIs
     * @return A new object with all applicable keys and string values expanded
     */
    private Object expandKeysAndValues(Object node, Map<String, String> namespaces) {
        if (node instanceof Map<?, ?> map) {
            Map<String, Object> expanded = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String rawKey = entry.getKey().toString();
                String newKey = expandNamespace(rawKey, namespaces);
                expanded.put(newKey, expandKeysAndValues(entry.getValue(), namespaces));
            }
            return expanded;
        } else if (node instanceof List<?> list) {
            return list.stream()
                    .map(item -> expandKeysAndValues(item, namespaces))
                    .toList();
        } else if (node instanceof String str) {
            return expandNamespace(str, namespaces);
        } else {
            return node;
        }
    }

    /**
     * Expands a single string containing a namespace prefix (e.g.,
     * "edam:format_3244")
     * to its full IRI form using the given namespace mapping.
     *
     * @param value      The input string, possibly containing a prefix
     * @param namespaces A map of namespace prefixes to full IRIs
     * @return The expanded string if a known prefix is found, otherwise the
     *         original string
     */
    private String expandNamespace(String value, Map<String, String> namespaces) {
        int colonIdx = value.indexOf(':');
        if (colonIdx > 0) {
            String prefix = value.substring(0, colonIdx);
            String suffix = value.substring(colonIdx + 1);
            if (namespaces.containsKey(prefix)) {
                return namespaces.get(prefix) + suffix;
            }
        }
        return value;
    }

    /**
     * Updates `cwlContent` field, by parsing the CWL content from the provided InputStream and expands keys and
     * values using the namespace mapping.
     *
     * @param inputStream The InputStream containing the CWL content.
     */
    private void parseCWL(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> raw = yaml.load(inputStream);

        Map<String, String> namespaces = Map.of();
        Object nsObj = raw.get("$namespaces");
        if (nsObj instanceof Map<?, ?> nsMap) {
            namespaces = nsMap.entrySet().stream()
                    .filter(e -> e.getKey() instanceof String && e.getValue() instanceof String)
                    .collect(Collectors.toMap(
                            e -> (String) e.getKey(),
                            e -> (String) e.getValue()));
        }

        cwlContent = (Map<String, Object>) expandKeysAndValues(raw, namespaces);
    }

    // Method to get a field by key (top-level)
    public Object getField(String key) {
        return cwlContent.get(key);
    }

    public List<String> getOperations() {
        Object intentObj = cwlContent.get(operationsKey);
        if (intentObj instanceof List) {
            return (List<String>) intentObj;
        }
        return List.of();
    }


    /**
     * Retrieves the input and/or output types from the CWL content based on the
     * specified key.
     *
     * @param cwlIOKey The key to retrieve either inputs or outputs.
     * @return A list of CWLData objects representing the input/output types.
     */
    public List<CWLData> getIOTypes(String cwlIOKey) {
        Object inputsObj = cwlContent.get(cwlIOKey);
        if (!(inputsObj instanceof Map)) {
            return List.of();
        }

        Map<String, Object> inputs = (Map<String, Object>) inputsObj;
        List<CWLData> inputList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            String inputId = entry.getKey();
            Object inputVal = entry.getValue();

            if (inputVal instanceof Map) {
                Map<String, Object> inputMap = (Map<String, Object>) inputVal;
                String currFormat = APEUtils.mapGetString(inputMap, formatTypeKey);
                String currData = APEUtils.mapGetString(inputMap, dataTypeKey);

                if (currFormat != null && currData != null) {
                    inputList.add(new CWLData(currFormat, currData, inputId));
                }
            }
        }

        return inputList;
    }

    public List<CWLData> getInputs() {
        return getIOTypes(inputsKey);
    }

    public List<CWLData> getOutputs() {
        return getIOTypes(outputsKey);
    }

}
