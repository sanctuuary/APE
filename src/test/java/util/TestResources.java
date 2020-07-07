package util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
}
