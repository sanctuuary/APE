package nl.uu.cs.ape.test.sat.ape;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.w3id.cwl.cwl1_2.CommandInputParameter;
import org.w3id.cwl.cwl1_2.CommandInputParameter;
import org.w3id.cwl.cwl1_2.CommandOutputParameter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import nl.uu.cs.ape.utils.WorkflomicsConstants;
import nl.uu.cs.ape.utils.cwl_parser.CWLParser;

public class TestCWLParser {

    @Test
    public void testLoadCWLFromURL() throws StreamReadException, DatabindException, IOException {
        // Load CWL from Workflomics URL
        String cometCwlUrl = WorkflomicsConstants.getCwlToolUrl("comet");
        CWLParser parser = new CWLParser(cometCwlUrl);
        List<String> operations = parser.getOperations();
        assert operations.size() == 1 : "Expected exactly one operation";
        String label = parser.getLabel();
        assert label.equals("comet");
        // Assert that operations are not empty
        assert !operations.isEmpty() : "Operations list should not be empty";
        List<CommandInputParameter> inputs = parser.getInputs();
        assert !inputs.isEmpty() : "Inputs list should not be empty";
        List<CommandOutputParameter> outputs = parser.getOutputs();
        assert !outputs.isEmpty() : "Outputs list should not be empty";
        CommandInputParameter input_1 = inputs.get(1);
        assert ((String) input_1.getFormat()).endsWith("format_3244");

    }

    @Test
    public void testLoadCWLFromFile() throws StreamReadException, DatabindException, IOException {
        // Load CWL from a local file
        File cwlFile = new File("./src/test/resources/comet.cwl");
        CWLParser parser = new CWLParser(cwlFile.toPath());
        List<String> operations = parser.getOperations();
        // Assert that operations are not empty
        assert !operations.isEmpty() : "Operations list should not be empty";
        List<CommandInputParameter> inputs = parser.getInputs();
        assert !inputs.isEmpty() : "Inputs list should not be empty";

    }

}
