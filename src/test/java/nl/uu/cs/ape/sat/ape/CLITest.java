package nl.uu.cs.ape.sat.ape;

import nl.uu.cs.ape.sat.Main;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import util.TestResources;

import java.nio.file.Paths;
import java.util.Objects;

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
    }
}
