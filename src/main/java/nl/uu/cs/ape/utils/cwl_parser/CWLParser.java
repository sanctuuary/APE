package nl.uu.cs.ape.utils.cwl_parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import nl.uu.cs.ape.utils.APEUtils;

public class CWLParser {

    private Map<String, Object> cwlContent;

    // Constructor to load CWL from a URL
    public CWLParser(String urlString) throws IOException {
        InputStream inputStream = loadCWLFromURL(urlString);
        parseCWL(inputStream);
    }

    // Constructor to load CWL from a local file
    public CWLParser(java.nio.file.Path filePath) throws IOException {
        InputStream inputStream = Files.newInputStream(filePath);
        parseCWL(inputStream);
    }

    // Method to load CWL from a URL
    private InputStream loadCWLFromURL(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == 200) {
            return connection.getInputStream();
        } else {
            throw new IOException("Failed to load CWL file. HTTP response code: " + connection.getResponseCode());
        }
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    // Method to get nested fields by path (e.g., "inputs.name")
    public Object getNestedField(String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = cwlContent;
        Object value = null;

        for (String key : keys) {
            value = currentMap.get(key);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                break;
            }
        }

        return value;
    }

    public List<String> getOperations() {
        Object intentObj = cwlContent.get("intent");
        if (intentObj instanceof List) {
            return (List<String>) intentObj;
        }
        return List.of();
    }

    private final String inputsKey = "inputs";
    private final String outputsKey = "outputs";
    private final String formatTypeKey = "format";
    private final String dataTypeKey = "http://edamontology.org/data_0006";


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
