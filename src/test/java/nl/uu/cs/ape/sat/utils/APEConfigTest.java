package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.TestUtil;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests whether human mistakes in the configuration are detected and
 * exceptions are thrown with relevant information.
 */
class APEConfigTest {

    /**
     * A correct template construct that is converted to a String at initialisation.
     */
    private static final String jsonTemplate = new JSONObject()
            .put("ontology_path", TestUtil.getAbsoluteResourcePath("correctTemplate/ontology.owl"))
            .put("ontologyPrexifIRI", "http://www.co-ode.org/ontologies/ont.owl#")
            .put("toolsTaxonomyRoot", "ToolsTaxonomy")
            .put("dataSubTaxonomyRoot", new String[]{"TypesTaxonomy"})
            .put("tool_annotations_path", TestUtil.getAbsoluteResourcePath("correctTemplate/tool_annotations.json"))
            .put("constraints_path", TestUtil.getAbsoluteResourcePath("correctTemplate/constraints.json"))
            .put("solutions_path", TestUtil.getAbsoluteResourcePath("correctTemplate"))
            .put("shared_memory", true)
            .put("solution_min_length", 1)
            .put("solution_max_length", 5)
            .put("max_solutions", 5)
            .put("execution_scripts_folder", TestUtil.getAbsoluteResourcePath("correctTemplate/Implementations"))
            .put("number_of_execution_scripts", 1)
            .put("solution_graphs_folder", TestUtil.getAbsoluteResourcePath("correctTemplate/Figures"))
            .put("number_of_generated_graphs", 1)
            .put("inputs", new JSONObject[]{new JSONObject().put("TypesTaxonomy", new String[]{"XYZ_table_file"})})
            .put("outputs", new JSONObject[]{new JSONObject().put("TypesTaxonomy", new String[]{"PostScript"})})
            .put("use_workflow_input", ConfigEnum.ALL.toString())
            .put("debug_mode", false)
            .put("use_all_generated_data", ConfigEnum.ONE.toString())
            .toString();

    /**
     * Get a copy of the configuration template.
     *
     * @return A copy of the configuration template.
     */
    public static JSONObject getCorrectTemplate() {
        return new JSONObject(jsonTemplate);
    }

    /**
     * Verify that the template is still correct.
     * Setting up thh framework should not induce any exceptions.
     */
    @Test
    public void verifyCorrectTemplate() {
        assertDoesNotThrow(() -> {
            JSONObject template = getCorrectTemplate();
            APEConfig config = new APEConfig(template);
            APE ape = new APE(template);
            boolean success = config.setupRunConfiguration(template, ape.getDomainSetup());
            assertTrue(success);
        });
    }

    /**
     * Test if the {@link APEConfigException#missingTag} exception
     * is thrown on obligatory tags that are missing.
     */
    @Test
    public void testMissingTags() {

        for (String tag : APEConfig.getObligatoryCoreTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertThrows(APEConfigException.class, () -> new APEConfig(obj));
        }

        for (String tag : APEConfig.getObligatoryRunTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertDoesNotThrow(() -> new APEConfig(obj));
            assertThrows(APEConfigException.class, () -> setupRun(obj));
        }
    }

    /**
     * Test if the {@link APEConfigException#pathNotFound} exception
     * is thrown on an incorrect value for tags that expect a path.
     */
    @Test
    public void testIncorrectPaths() {

        final String[] pathTags = new String[]{"ontology_path", "tool_annotations_path", "execution_scripts_folder", "solution_graphs_folder", "solutions_path"};
        final String[] wrongPaths = new String[]{null, "", "./does/not/exist.txt", "does/not/exist.txt", "./does/not/exist/", "does/not/exist/", TestUtil.getAbsoluteResourcePath("") + "\\doesnotexist.txt"};

        for (String tag : pathTags) {
            for (String path : wrongPaths) {
                try {
                    JSONObject obj = getCorrectTemplate().put(tag, path);
                    APEConfig config = new APEConfig(obj);
                    APE ape = new APE(obj);
                    config.setupRunConfiguration(obj, ape.getDomainSetup());
                    fail(String.format("Expected exception for APEConfig with a wrong path '%s' for tag '%s' was not thrown.", path, tag));
                } catch (APEConfigException | JSONException | IOException e) {
                    assertTrue(e.getMessage().contains(tag));
                    TestUtil.success(String.format("Expected exception was thrown for APEConfig with a wrong path for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
                }
            }
        }
    }

    /**
     * Test if the {@link APEConfigException#invalidValue} exception is thrown
     * on an incorrect value for tags that expect an Integer number.
     */
    @Test
    public void testNumericTags() {
        ArrayList<String> numericTags = new ArrayList<>(Arrays.asList("solution_min_length", "solution_max_length", "max_solutions", "number_of_execution_scripts", "number_of_generated_graphs"));
        ArrayList<String> nonNumericTags = new ArrayList<>(Arrays.asList(APEConfig.getAllTags()));
        nonNumericTags.removeAll(numericTags);

        for (String tag : numericTags) {
            String nonNumericValue = "test";
            try {
                JSONObject obj = getCorrectTemplate().put(tag, nonNumericValue);
                APEConfig config = new APEConfig(obj);
                APE ape = new APE(obj);
                config.setupRunConfiguration(obj, ape.getDomainSetup());
                fail(String.format("Expected exception for APEConfig with a non-numeric value '%s' for numeric tag '%s' was not thrown.", nonNumericValue, tag));
            } catch (APEConfigException | JSONException | IOException e) {
                assertTrue(e.getMessage().contains(tag));
                TestUtil.success(String.format("Expected exception was thrown for APEConfig with a non-numeric value for numeric tag '%s'\nAPE message was: %s", tag, e.getMessage()));
            }
        }

        for (String tag : nonNumericTags) {
            int numericValue = 1;
            try {
                JSONObject obj = getCorrectTemplate().put(tag, numericValue);
                APEConfig config = new APEConfig(obj);
                APE ape = new APE(obj);
                config.setupRunConfiguration(obj, ape.getDomainSetup());
                fail(String.format("Expected exception for APEConfig with a numeric value '%s' for non-numeric tag '%s' was not thrown.", numericValue, tag));
            } catch (APEConfigException | JSONException | IOException e) {
                assertTrue(e.getMessage().contains(tag));
                TestUtil.success(String.format("Expected exception was thrown for APEConfig with a non-numeric value for non-numeric tag '%s'\nAPE message was: %s", tag, e.getMessage()));
            }
        }
    }

    /**
     * Creates a configuration from JSON and executes {@link APEConfig#setupRunConfiguration}
     *
     * @param obj The configuration to set up.
     * @throws IOException        Error if a path provided in the configuration file is incorrect.
     * @throws APEConfigException Error if a tag provided in the configuration file is incorrect.
     */
    private void setupRun(JSONObject obj) throws IOException, APEConfigException {
        APE ape = new APE(obj);
        APEConfig config = new APEConfig(obj);
        config.setupRunConfiguration(obj, ape.getDomainSetup());
    }
}
