package util;

import nl.uu.cs.ape.sat.Main;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static util.Evaluation.fail;

/**
 * The {@code TestUtil} class is used to read contents of resource files more easily, as functional tests will make use of json files in the test resource folders.
 *
 * @author Maurin Voshol
 */
public class TestResources {

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return the absolute path of a resource that can be used by the library.
     */
    public static String getAbsoluteResourcePath(String resource) {
        try {
            return Paths.get(Objects.requireNonNull(TestResources.class.getClassLoader().getResource(resource)).toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            fail("Could not retrieve resource '%s'", resource);
            return null;
        }
    }

    /**
     * @return the absolute path of a the resource root that can be used by the library.
     */
    public static String getAbsoluteRoot() {
        return getAbsoluteResourcePath("");
    }

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @param charset:  charset of the file, default is UTF-8
     * @return contents of a resource in String format
     */
    public static String getTextResource(String resource, Charset charset) {
        try {
            return IOUtils.toString(Objects.requireNonNull(TestResources.class.getClassLoader().getResourceAsStream(resource)), charset);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not retrieve %s resource '%s' ", charset, resource);
            return null;
        }
    }

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return contents of a resource in String format
     */
    public static String getTextResource(String resource) {
        return getTextResource(resource, StandardCharsets.UTF_8);
    }

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return JSON resource in JSONObject format
     */
    public static JSONObject getJSONResource(String resource) {
        return new JSONObject(getTextResource(resource));
    }

    public static String writeFile(String relativePath, String content) {
        Path absolutePath = Paths.get(TestResources.getAbsoluteRoot()).resolve(relativePath).toAbsolutePath();
        Path absoluteParentPath = absolutePath.getParent();
        try {
            File folder = absoluteParentPath.toFile();
            if (!folder.exists() && folder.mkdirs()) {
                System.out.printf("Directories created for file '%s'\n", relativePath);
            }
            File file = absolutePath.toFile();
            if (!file.exists() && file.createNewFile()) {
                System.out.printf("File '%s' was created\n", relativePath);
            }
            Files.write(absolutePath, content.getBytes());

            System.out.printf("Wrote content to '%s'\n", absolutePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return absolutePath.toString();
    }

    public static JSONObject getConfigResource(String base_config_path, String ontology_path, String tools_path, String constraints_path, String solution_dir_path){
        return getJSONResource(base_config_path)
                // add paths to the other files to the configuration
                .put("ontology_path", getAbsoluteResourcePath(ontology_path))
                .put("tool_annotations_path", getAbsoluteResourcePath(tools_path))
                .put("constraints_path", getAbsoluteResourcePath(constraints_path))
                .put("solutions_dir_path", getAbsoluteResourcePath(solution_dir_path));
    }
}
