package nl.uu.cs.ape.test.sat.ape;

import nl.uu.cs.ape.domain.BioToolsAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class BioToolsAPITest {

    private static File testFile;

    @BeforeAll
    static void setup() throws IOException {
        // Create a temporary file with some sample tool IDs for testing.
        testFile = File.createTempFile("testToolList", ".json");
        testFile.deleteOnExit();
        JSONArray sampleToolIDs = new JSONArray();
        sampleToolIDs.put("comet");
        sampleToolIDs.put("sage");
        sampleToolIDs.put("proteinprophet");

        Files.writeString(Path.of(testFile.getAbsolutePath()), sampleToolIDs.toString(4));
    }

    @Test
    void testGetAndConvertToolListFromFile() throws IOException {
        // Call the method and get the result
        JSONObject result = BioToolsAPI.getAndConvertToolList(testFile);

        // Assert the result is not null and check the number of tools
        assertNotNull(result, "The result should not be null");
        assertEquals(3, result.getJSONArray("functions").length(),
                "The number of functions should match the input size");

        System.out.println("Response: " + result.toString(4));
    }

    @Test
    void testGetAndConvertToolListFromList() throws IOException {
        // Create a list of tool IDs
        List<String> toolIDs = List.of("comet");

        // Call the method and get the result
        JSONObject result = BioToolsAPI.getAndConvertToolList(toolIDs);

        // Assert the result is not null and check the number of tools
        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.getJSONArray("functions").length(),
                "The number of functions should match the input size");

        // Check the content of the retrieved tool annotation
        JSONObject comet = result.getJSONArray("functions").getJSONObject(0);
        assertEquals("comet", comet.getString("biotoolsID"), "The biotoolsID should match the input");
        assumeTrue(comet.getJSONArray("inputs").length() > 0, "The tool should have at least one input");
        assumeTrue(comet.getJSONArray("outputs").length() > 0, "The tool should have at least one output");
        assumeTrue(comet.getJSONArray("taxonomyOperations").length() > 0, "The tool should have at least one taxonomyOperation");
       

        System.out.println("Response: " + result.toString(4));
    }

    @Test
    void testGetToolsFromDomain() throws IOException {
        String domainName = "proteomics";
        JSONObject result = BioToolsAPI.getToolsFromDomain(domainName, false);

        // Check that the result is not null and contains some tools
        assertNotNull(result, "The result should not be null");
        assertTrue(result.getJSONArray("functions").length() > 700, "There should be some functions in the result");
    }

}