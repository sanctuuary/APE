package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.APE;
import nl.uu.cs.ape.sat.configuration.tags.APEConfigTag;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import util.TestResources;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.Evaluation.success;

public class APEConfigTagTest {

    @Test
    public void infoOutputTest() {

        System.out.println("Test type tags..");

        for (APEConfigTag.Info<?> tag : APERunConfig.getTags().getAll()) {

            if (tag.type == APEConfigTag.TagType.INTEGER) {
                System.out.printf("Web API shows `%s` box for tag `%s`, with min:`%s` and max:`%s`\n", tag.type, tag.label, tag.constraints.getInt("min"), tag.constraints.getInt("max"));
            }
        }

        System.out.printf("\n### Display all tag info ####\nCORE:\n%s\n\nRUN:\n%s\n",
                APECoreConfig.getTags().toJSON().toString(3),
                APERunConfig.getTags().toJSON().toString(3));
    }

    @Test
    public void coreValidationTest() {
        final JSONObject correct_config = TestResources.getJSONResource("cli/gmt/base_config.json")
                // add paths to the other files to the configuration
                .put("ontology_path", TestResources.getAbsoluteResourcePath("cli/gmt/GMT_UseCase_taxonomy.owl"))
                .put("tool_annotations_path", TestResources.getAbsoluteResourcePath("cli/gmt/tool_annotations.json"))
                .put("constraints_path", TestResources.getAbsoluteResourcePath("cli/gmt/constraints_e0.json"))
                .put("solutions_dir_path", TestResources.getAbsoluteResourcePath("cli/gmt"));

        ValidationResults results = APECoreConfig.validate(correct_config);

        assertFalse(results.hasFails());

        /* Test missing obligatory tag  */
        List<String> tags = APECoreConfig.getTags().getObligatory().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config);
            altered_config.remove(tag);

            results = APECoreConfig.validate(altered_config);

            assertTrue(results.hasFails());
            assertTrue(results.stream().anyMatch(result -> result.isFail() && result.getTag().equals(tag)));

            success("CoreConfig is missing an obligatory tag -> %s", results.getFails().get(0).toJSON().toString());
        }

        /* Test missing optional tag  */
        tags = APECoreConfig.getTags().getOptional().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config);
            altered_config.remove(tag);

            results = APECoreConfig.validate(altered_config);

            assertFalse(results.hasFails());
        }

        /* Test incorrect tag  */
        tags = APECoreConfig.getTags().getAll().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config)
                    .put(tag, "");

            results = APECoreConfig.validate(altered_config);

            assertTrue(results.hasFails());
            assertTrue(results.stream().anyMatch(result -> result.isFail() && result.getTag().equals(tag)));

            success("CoreConfig contains an incorrect tag -> %s", results.getFails().get(0).toJSON().toString());
        }
    }

    @Test
    public void runValidationTest() throws IOException, OWLOntologyCreationException {
        final JSONObject correct_config = TestResources.getJSONResource("cli/gmt/base_config.json")
                // add paths to the other files to the configuration
                .put("ontology_path", TestResources.getAbsoluteResourcePath("cli/gmt/GMT_UseCase_taxonomy.owl"))
                .put("tool_annotations_path", TestResources.getAbsoluteResourcePath("cli/gmt/tool_annotations.json"))
                .put("constraints_path", TestResources.getAbsoluteResourcePath("cli/gmt/constraints_e0.json"))
                .put("solutions_dir_path", TestResources.getAbsoluteResourcePath("cli/gmt"));

        APEDomainSetup domainSetup = new APE(correct_config).getDomainSetup();

        ValidationResults results = APERunConfig.validate(correct_config, domainSetup);

        assertFalse(results.hasFails());

        /* Test missing obligatory tag  */
        List<String> tags = APERunConfig.getTags().getObligatory().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config);
            altered_config.remove(tag);

            results = APERunConfig.validate(altered_config, domainSetup);

            assertTrue(results.hasFails());
            assertTrue(results.stream().anyMatch(result -> result.isFail() && result.getTag().equals(tag)));

            success("RunConfig is missing an obligatory tag -> %s", results.getFails().get(0).toJSON().toString());
        }

        /* Test missing optional tag  */
        tags = APERunConfig.getTags().getOptional().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config);
            altered_config.remove(tag);

            results = APERunConfig.validate(altered_config, domainSetup);

            assertFalse(results.hasFails());
        }

        /* Test incorrect tag  */
        tags = APERunConfig.getTags().getAll().stream().map(info -> info.tag_name).collect(Collectors.toList());
        for (String tag : tags) {

            JSONObject altered_config = APEUtils.clone(correct_config)
                    .put(tag, "");

            results = APERunConfig.validate(altered_config, domainSetup);

            assertTrue(results.hasFails());
            assertTrue(results.stream().anyMatch(result -> result.isFail() && result.getTag().equals(tag)));

            success("RunConfig contains an incorrect tag -> %s", results.getFails().get(0).toJSON().toString());
        }
    }

}
