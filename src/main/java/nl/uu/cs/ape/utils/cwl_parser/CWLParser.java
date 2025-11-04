package nl.uu.cs.ape.utils.cwl_parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.commonwl.cwlsdk.cwl1_2.CommandInputParameter;
import org.commonwl.cwlsdk.cwl1_2.CommandInputParameter;
import org.commonwl.cwlsdk.cwl1_2.CommandLineTool;
import org.commonwl.cwlsdk.cwl1_2.CommandOutputParameter;
import org.commonwl.cwlsdk.cwl1_2.CommandOutputParameter;
import org.commonwl.cwlsdk.cwl1_2.utils.RootLoader;

import nl.uu.cs.ape.utils.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;

public class CWLParser {

    // private Map<String, Object> cwlContent;
    private CommandLineTool cwlToolContent;

    public static final String DATA_ROOT = "data_0006";
    public static final String FORMAT_ROOT = "format_1915";
    public static final String DATA_ROOT_IRI = "http://edamontology.org/" + DATA_ROOT;

    // Constructor to load CWL from a URL
    public CWLParser(String urlString) throws IOException {
        InputStream inputStream = new FileInputStream(APEFiles.readPathToFile(urlString));
        String fileContent = new String(inputStream.readAllBytes());
        inputStream.close();
        cwlToolContent = (CommandLineTool) RootLoader.loadDocument(fileContent, urlString);
    }

    // Constructor to load CWL from a local file
    public CWLParser(java.nio.file.Path filePath) throws IOException {
        InputStream inputStream = Files.newInputStream(filePath);
        String fileContent = new String(inputStream.readAllBytes());
        inputStream.close();
        cwlToolContent = (CommandLineTool) RootLoader.loadDocument(fileContent, filePath.toString());
    }

    public List<String> getOperations() {
        Optional<List<String>> intentObj = cwlToolContent.getIntent();

        return intentObj.orElse(List.of());
    }



    public List<CommandInputParameter> getInputs() {
        return cwlToolContent.getInputs().stream()
            .map(obj -> (CommandInputParameter) obj)
            .collect(Collectors.toList());

    }

    public List<CommandOutputParameter> getOutputs() {
        return cwlToolContent.getOutputs().stream()
                .map(obj -> (CommandOutputParameter) obj)
                .collect(Collectors.toList());
    }
    
    public String getLabel() {
        return cwlToolContent.getLabel().orElse("");
    }

}
