package nl.uu.cs.ape.test.sat.ape;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import nl.uu.cs.ape.utils.WorkflomicsConstants;
import nl.uu.cs.ape.utils.cwl_parser.CWLData;
import nl.uu.cs.ape.utils.cwl_parser.CWLParser;

public class TestCWLParser {

    @Test
    public void testLoadCWLFromURL() throws StreamReadException, DatabindException, IOException {
        // Load CWL from Workflomics URL
        String cometCwlUrl = WorkflomicsConstants.getCwlToolUrl("comet");
        CWLParser parser = new CWLParser(cometCwlUrl);
        List<String> operations = parser.getOperations();
        // Assert that operations are not empty
        assert !operations.isEmpty() : "Operations list should not be empty";
        List<CWLData> inputs = parser.getInputs();
        assert !inputs.isEmpty() : "Inputs list should not be empty";
    }

    @Test
    public void testLoadCWLFromFile() throws StreamReadException, DatabindException, IOException {
        // Load CWL from a local file
        File cwlFile = new File("./src/test/resources/comet.cwl");
        CWLParser parser = new CWLParser(cwlFile.toPath());
        List<String> operations = parser.getOperations();
        // Assert that operations are not empty
        assert !operations.isEmpty() : "Operations list should not be empty";
        List<CWLData> inputs = parser.getInputs();
        assert !inputs.isEmpty() :  "Inputs list should not be empty";

    }

}
