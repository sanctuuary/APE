package nl.uu.cs.ape.test.sat.ape;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import nl.uu.cs.ape.utils.cwl_parser.CWLData;
import nl.uu.cs.ape.utils.cwl_parser.CWLParser;

public class TestCWLParser {

    // Example test method (to be implemented)
    public void testLoadCWLFromURL() {
        // Implement test logic here
    }

    @Test
    public void testLoadCWLFromFile() throws StreamReadException, DatabindException, IOException {
        // Load CWL from a local file
        File cwlFile = new File("./src/test/resources/comet.cwl");
        CWLParser parser = new CWLParser(cwlFile.toPath());
        List<String> operations = parser.getOperations();
        // Assert that operations are not empty
        Set<String> taxonomyOperations = new HashSet<>(operations);
        assert !operations.isEmpty() : "Operations list should not be empty";
        List<CWLData> inputs = parser.getFilteredInputsWithFormatAndEDAM();

        parser.printInputTypesAndFormats();
        parser.printOutputTypesAndFormats();
    }

}
