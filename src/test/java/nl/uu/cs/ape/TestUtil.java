package nl.uu.cs.ape;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * The {@code TestUtil} class is used to read contents of resource files more easily, as functional tests will make use of json files in the test resource folders.
 *
 * @author Maurin Voshol
 *
 */
public class TestUtil {

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return the absolute path of a resource that can be used by the library.
     */
    public static String getAbsoluteResourcePath(String resource) {
        try {
            URI uri = Objects.requireNonNull(TestUtil.class.getClassLoader().getResource(resource)).toURI();
            return Paths.get(uri).toString();
        } catch (URISyntaxException | NullPointerException e) {
            return null;
        }
    }

    /**
     * @return the absolute path of a the resource root that can be used by the library.
     */
    public static String getAbsoluteResourceRoot() {
        return getAbsoluteResourcePath("");
    }

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return contents of a resource in String format
     */
    public static String getTextResource(String resource) throws IOException {
        InputStream inputStream = Objects.requireNonNull(TestUtil.class.getClassLoader().getResourceAsStream(resource));
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * @param resource: relative path of a resource in the test resource folder
     * @return JSON resource in JSONObject format
     */
    public static JSONObject getJSONResource(String resource) throws IOException {
        return new JSONObject(getTextResource(resource));
    }

    public static void success(String message, Object ... params){
        System.out.println("\u001B[32mSUCCESS:\u001B[0m " + String.format(message, params));
    }

    private static boolean debugMode = true;

    public static void testResult(boolean success, String message){

        if(!success){
            fail(message);
            return;
        }

        if(success && debugMode){
            success(message);
        }
    }
}
