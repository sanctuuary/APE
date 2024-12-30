package nl.uu.cs.ape.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

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

    // Method to parse the CWL content using SnakeYAML
    private void parseCWL(InputStream inputStream) {
        Yaml yaml = new Yaml();
        cwlContent = yaml.load(inputStream);
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
}
