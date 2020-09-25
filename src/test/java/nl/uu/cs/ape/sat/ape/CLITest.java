package nl.uu.cs.ape.sat.ape;

import nl.uu.cs.ape.sat.Main;
import nl.uu.cs.ape.sat.configuration.APERunConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import util.TestResources;

import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLITest {

    @Test
    public void GMTFromCLITest(){
        run(
                "cli/gmt/base_config.json",
                "cli/gmt/GMT_UseCase_taxonomy.owl",
                "cli/gmt/tool_annotations.json",
                "cli/gmt/constraints_e0.json",
                "cli/gmt"
        );
    }

    // add files to 'src/test/resources/' folder and add the relative paths here
    //@Test
    public void templateTest(){
        run(
                "relative/path/base_config.json",
                "relative/path/ontology.owl",
                "relative/path/tool_annotations.json",
                "relative/path/constraints.json",
                "relative/path"
        );
    }

    public void run(String base_config_path, String ontology_path, String tools_path, String constraints_path, String solution_dir_path){

        // get the base configuration file
        final JSONObject config_content = TestResources.getConfigResource(
                base_config_path,
                ontology_path,
                tools_path,
                constraints_path,
                solution_dir_path);

        // create a new configuration file
        final String config_path = TestResources.writeFile(Paths.get(Objects.requireNonNull(TestResources.getAbsoluteResourcePath(solution_dir_path))).resolve("config.json").toAbsolutePath().toString(), config_content.toString(2));

        Main.main(new String[] {
                config_path
        });

        // check whether images are produced correctly
        final int figures_amount_generated = Objects.requireNonNull(Paths.get(Objects.requireNonNull(TestResources.getAbsoluteResourcePath(solution_dir_path))).resolve(APERunConfig.FIGURES_FOLDER_NAME).toFile().list()).length;
        assertEquals(config_content.getInt("number_of_generated_graphs"), figures_amount_generated);

        // check whether scripts are produced correctly
        final int executables_amount_generated = Objects.requireNonNull(Paths.get(Objects.requireNonNull(TestResources.getAbsoluteResourcePath(solution_dir_path))).resolve(APERunConfig.EXECUTABLES_FOLDER_NAME).toFile().list()).length;
        assertEquals(config_content.getInt("number_of_execution_scripts"), executables_amount_generated);
    }
}
