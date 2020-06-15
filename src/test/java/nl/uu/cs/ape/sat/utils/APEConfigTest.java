package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.TestUtil;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import util.TagInfo;
import util.TagTypeEvaluation;

import java.io.IOException;

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
     * The tags can be divided in these categories displayed in the array.
     * TagInfo takes the following data:
     * - Type
     * - Correct value example
     * - Incorrect value examples
     * - Tag names of this type.
     */
    private static final TagInfo[] tagTypes = new TagInfo[]{
            new TagInfo(
                    "Boolean",
                    true,
                    new Object[]{"wrong", 1},
                    new String[]{"shared_memory", "debug_mode"}),
            new TagInfo(
                    "Enum",
                    ConfigEnum.ONE.toString(),
                    new Object[]{false, "wrong", 1},
                    new String[]{"use_workflow_input", "use_all_generated_data"}),
            new TagInfo(
                    "Integer",
                    1,
                    new Object[]{false, "wrong"},
                    new String[]{"solution_min_length", "solution_max_length", "max_solutions", "number_of_execution_scripts", "number_of_generated_graphs"}),
            new TagInfo(
                    "OntologyPrefix",
                    "http://www.co-ode.org/ontologies/ont.owl#",
                    new Object[]{false, "wrong"},
                    new String[]{"ontologyPrexifIRI"}),
            new TagInfo(
                    "OntologyPath",
                    TestUtil.getAbsoluteResourcePath("correctTemplate/ontology.owl"),
                    new Object[]{false, "wrong"},
                    new String[]{"ontology_path"}),
            new TagInfo(
                    "JSONPath",
                    TestUtil.getAbsoluteResourcePath("correctTemplate/tool_annotations.json"),
                    new Object[]{false, "wrong"},
                    new String[]{"tool_annotations_path", "constraints_path"}),
            new TagInfo(
                    "DirectoryPath",
                    TestUtil.getAbsoluteResourcePath("correctTemplate/"),
                    new Object[]{false, "wrong"},
                    new String[]{"solutions_path", "execution_scripts_folder", "solution_graphs_folder"}),
            new TagInfo(
                    "TaxonomyEntity",
                    "ToolsTaxonomy",
                    new Object[]{false, "wrong"},
                    new String[]{"toolsTaxonomyRoot", "dataSubTaxonomyRoot"}),
    };

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

        /* Missing one of the obligatory core tags should result in an exception while creating the framework. */
        for (String tag : APEConfig.getObligatoryCoreTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertThrows(APEConfigException.class, () -> new APEConfig(obj));
        }

        /* Missing one of the obligatory run tags should  result in an exception while executing the run phase. */
        for (String tag : APEConfig.getObligatoryRunTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertDoesNotThrow(() -> new APEConfig(obj));
            assertThrows(APEConfigException.class, () -> setupRun(obj));
        }

        /* Missing one of the optional tags should not throw an exception, but should display a warning. */
        for (String tag : ArrayUtils.addAll(APEConfig.getOptionalCoreTags(), APEConfig.getOptionalRunTags())) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertDoesNotThrow(() -> new APEConfig(obj));
            assertDoesNotThrow(() -> setupRun(obj));
        }
    }

    /**
     * Test if the {@link APEConfigException#pathNotFound} exception
     * is thrown on an incorrect value for tags that expect a path.
     */
    @Test
    public void testIncorrectPaths() {

        final String[] pathTags = new String[]{"ontology_path", "tool_annotations_path", "execution_scripts_folder", "solution_graphs_folder", "solutions_path"};
        final String[] wrongPaths = new String[]{null, "", "./does/not/exist.json", "does/not/exist.json", "./does/not/exist/", "does/not/exist/", TestUtil.getAbsoluteResourcePath("") + "\\doesnotexist.json"};

        for (String tag : pathTags) {
            for (String path : wrongPaths) {
                try {
                    setupRun(getCorrectTemplate().put(tag, path));
                    fail(String.format("Expected exception for APEConfig with a wrong path '%s' for tag '%s' was not thrown.", path, tag));
                } catch (APEConfigException | JSONException | IOException e) {
                    assertTrue(e.getMessage().contains(tag));
                    TestUtil.success(String.format("Expected exception was thrown for APEConfig with a wrong path for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
                }
            }
        }
    }

    @Test
    public void tagTypeTest() {

        TagTypeEvaluation evaluation;

        for (TagInfo tagInfo : tagTypes) {

            // true positive
            evaluation = new TagTypeEvaluation(tagInfo.getTagType(), false);
            for (String tag : tagInfo.getTags()) {
                try {
                    setupRun(getCorrectTemplate().put(tag, tagInfo.getCorrectExample())); // set invalid value for non-boolean tag and run the configuration
                    evaluation.forTag(tag).result(true);
                } catch (Exception e) {
                    evaluation.forTag(tag).result(false, e);
                }
            }

            for (String tag : tagInfo.getTags()) {
                for (Object wrongExample : tagInfo.getWrongExample()) {
                    try {
                        setupRun(getCorrectTemplate().put(tag, wrongExample)); // set invalid value for boolean tag and run the configuration
                        evaluation.forTag(tag).result(false);
                    } catch (Exception e) {
                        evaluation.forTag(tag).result(true, e);
                    }
                }
            }

            evaluation = new TagTypeEvaluation(tagInfo.getTagType(), true);
            for (String tag : otherTagsThan(tagInfo.getTags())) {
                try {
                    setupRun(getCorrectTemplate().put(tag, tagInfo.getCorrectExample())); // set invalid value for non-boolean tag and run the configuration
                    evaluation.forTag(tag).result(false);
                } catch (Exception e) {
                    evaluation.forTag(tag).result(true, e);
                }
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

    private String[] otherTagsThan(String[] tags) {
        return ArrayUtils.removeElements(APEConfig.getAllTags(), tags);
    }
}
