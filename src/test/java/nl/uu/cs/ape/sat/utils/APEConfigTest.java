package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.TestUtil;
import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import util.TagInfo;
import util.TagTypeEvaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
            .put("dataDimensionsTaxonomyRoots", new String[]{"TypesTaxonomy"})
            .put("tool_annotations_path", TestUtil.getAbsoluteResourcePath("correctTemplate/tool_annotations.json"))
            .put("constraints_path", TestUtil.getAbsoluteResourcePath("correctTemplate/constraints.json"))
            .put("solutions_path", TestUtil.getAbsoluteResourcePath("correctTemplate") + "\\solutions.txt")
            .put("shared_memory", true)
            .put("tool_seq_repeat", false)
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
                    new String[]{"shared_memory", "debug_mode", "tool_seq_repeat"}),
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
                    "TaxonomyEntity",
                    "ToolsTaxonomy",
                    new Object[]{false, "wrong"},
                    new String[]{"toolsTaxonomyRoot", "dataDimensionsTaxonomyRoots"}),
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
     * Setting up the framework should not induce any exceptions.
     */
    @Test
    public void verifyCorrectTemplate() {
        assertDoesNotThrow(() -> {
            JSONObject template = getCorrectTemplate();
            APECoreConfig config = new APECoreConfig(template);
            APE ape = new APE(template);
            APERunConfig runCOnfig = new APERunConfig(template, ape.getDomainSetup());
            boolean success = true;
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
        for (String tag : APECoreConfig.getObligatoryCoreTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertThrows(APEConfigException.class, () -> new APECoreConfig(obj));
        }

        /* Missing one of the obligatory run tags should  result in an exception while executing the run phase. */
        for (String tag : APERunConfig.getObligatoryRunTags()) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertDoesNotThrow(() -> new APECoreConfig(obj));
            assertThrows(APEConfigException.class, () -> setupRun(obj));
        }

        /* Missing one of the optional tags should not throw an exception, but should display a warning. */
        for (String tag : ArrayUtils.addAll(APECoreConfig.getOptionalCoreTags(), APERunConfig.getOptionalRunTags())) {
            JSONObject obj = getCorrectTemplate();
            obj.remove(tag);
            assertDoesNotThrow(() -> new APECoreConfig(obj));
            assertDoesNotThrow(() -> setupRun(obj));
        }
    }

    /**
     * Test if the {@link APEConfigException#pathNotFound} exception
     * is thrown on an incorrect value for tags that expect a path.
     * @throws OWLOntologyCreationException Error reading the OWL (ontology) file.
     */
    @Test
    public void testIncorrectFilePaths() {

        final String[] pathTags = new String[]{"ontology_path", "tool_annotations_path"};
        final String[] wrongPaths = new String[]{null, "", "./does/not/exist.json", "does/not/exist.json", "./does/not/exist/", "does/not/exist/", TestUtil.getAbsoluteResourcePath("") + "\\doesnotexist.json"};

        for (String tag : pathTags) {
            for (String path : wrongPaths) {
                try {
                    setupRun(getCorrectTemplate().put(tag, path));
                    fail(String.format("Expected exception for APECoreConfig with a wrong path '%s' for tag '%s' was not thrown.", path, tag));
                } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
                    assertTrue(e.getMessage().contains(tag));
                    TestUtil.success(String.format("Expected exception was thrown for APECoreConfig with a wrong path for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
                }
            }
        }
    }

    /**
     * Test if the {@link APEConfigException#pathNotFound} exception
     * is thrown on an incorrect value for tags that expect a path.
     * @throws OWLOntologyCreationException Error reading the OWL (ontology) file.
     */
    @Test
    public void testIncorrectDirectoryPaths()  {

        final String[] pathTags = new String[]{ "execution_scripts_folder", "solution_graphs_folder"};
        final String[] wrongPaths = new String[]{"file.json", TestUtil.getAbsoluteResourcePath("") + "\\file.json"};

        for (String tag : pathTags) {
            for (String path : wrongPaths) {
                try {
                    setupRun(getCorrectTemplate().put(tag, path));
                    fail(String.format("Expected exception for APEConfig with a wrong path '%s' for tag '%s' was not thrown.", path, tag));
                } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
                    assertTrue(e.getMessage().contains(tag));
                    TestUtil.success(String.format("Expected exception was thrown for APEConfig with a wrong path for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
                }
            }
        }
    }

    @Test
    public void testSolutionsPath(){
        final String tag = "solutions_path";

        // an existing file should be allowed
        try {
            final Path existingFile = Paths.get(Objects.requireNonNull(TestUtil.getAbsoluteResourcePath("testFiles/existingFile.txt")));
            assertTrue(Files.exists(existingFile));
            setupRun(getCorrectTemplate().put(tag, existingFile.toString()));
        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            assertTrue(e.getMessage().contains(tag));
            fail(String.format("Unexpected exception was thrown for APEConfig with a correct file path for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
        }

        // a non existing file should also be allowed, APE will create the directories and file if possible
        try {
            final Path nonExistingFile = Paths.get(TestUtil.getAbsoluteResourcePath("testFiles") + "\\newFile.json");
            assertTrue(Files.notExists(nonExistingFile)); // file should not exists
            setupRun(getCorrectTemplate().put(tag, nonExistingFile.toString())); // setup APE
            assertTrue(Files.exists(nonExistingFile)); // file should now exist

            //clean up
            Files.delete(nonExistingFile); // delete file
            assertTrue(Files.notExists(nonExistingFile)); // file should not exists

        } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
            assertTrue(e.getMessage().contains(tag));
            fail(String.format("Unexpected exception was thrown for APEConfig with a correct value for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
        }

        for(String incorrect : new String[]{"", "./a/directory", "a/directory", TestUtil.getAbsoluteResourcePath("") + "\\newDirectory" }){
            try {
                setupRun(getCorrectTemplate().put(tag, incorrect));
                fail(String.format("Expected exception for APEConfig with an incorrect value '%s' for tag '%s' was not thrown.", incorrect, tag));
            } catch (APEConfigException | JSONException | IOException | OWLOntologyCreationException e) {
                assertTrue(e.getMessage().contains(tag));
                TestUtil.success(String.format("Expected exception was thrown for APEConfig with an incorrect value for tag '%s'\nAPE message was: %s", tag, e.getMessage()));
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
        }
    }


    /**
     * Creates a configuration from JSON and executes {@link APECoreConfig#setupRunConfiguration}
     *
     * @param obj The configuration to set up.
     * @throws IOException        			Error if a path provided in the configuration file is incorrect.
     * @throws OWLOntologyCreationException Error reading the OWL (ontology) file.
     */
    private void setupRun(JSONObject obj) throws IOException, OWLOntologyCreationException {
        APE ape = new APE(obj);
        APECoreConfig config = new APECoreConfig(obj);
        APERunConfig runConfig = new APERunConfig(obj, ape.getDomainSetup());
    }

//    private String[] otherTagsThan(String[] tags) {
//        return ArrayUtils.removeElements(APECoreConfig.getAllTags(), tags);
//    }
}
