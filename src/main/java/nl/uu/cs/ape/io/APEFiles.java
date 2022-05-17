package nl.uu.cs.ape.io;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.configuration.APEConfigException;
import nl.uu.cs.ape.utils.APEUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The type Ape files.
 */
public class APEFiles {

    /**
     * READ and WRITE enums used to verify paths.
     */
    public enum Permission { 
    	/**Read permission.*/ 
    	READ, 
    	/**Write permission.*/
    	WRITE }

    /**
     * Verify and get full path based based on the field.
     * @param tag - tag
     * @param path - path
     * @return Return the path to the file
     */
    private static Path getPath(String tag, String path){

        // check on empty values
        if (path == null) {
            throw APEConfigException.invalidValue(tag, "null", "value is null.");
        }
        if (path.equals("")) {
            throw APEConfigException.invalidValue(tag, path, "value is empty.");
        }

        // check format
        try {
            return Paths.get(path);
        }
        catch (InvalidPathException | NullPointerException e){
            throw APEConfigException.invalidValue(tag, path, String.format("The path for tag '%s' is invalid. %s", tag, e.getMessage()));
        }
    }

    /**
     * Checks whether a path has a valid format.
     * E.g. will return false if the path contains
     * forbidden character.
     *
     * @param path the path
     * @return a boolean indicating whether the path has a valid format
     */
    public static boolean validPathFormat(String path){
        try {
            Paths.get(path);
            return true;
        }
        catch (InvalidPathException | NullPointerException e){
            return false;
        }
    }

    /**
     * Method checks whether the provided value represent a correct path to a file, and returns the corresponding file if it does.
     *
     * @param tag                  Corresponding tag from the config file.
     * @param inputPath            Provided path for the file.
     * @param requestedPermissions the requested permissions
     * @return File represented by the path in the JSON object, or the default value if the tag is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public static Path readFileFromPath(String tag, String inputPath, Permission... requestedPermissions)
            throws IOException, JSONException, APEConfigException {

        final Path path = getPath(tag, inputPath);

        if (Files.notExists(path)) {
            throw APEConfigException.pathNotFound(tag, inputPath);
        }

        if (!Files.isRegularFile(path)) {
            throw APEConfigException.notAFile(tag, inputPath);
        }

        // check permissions
        checkPermissions(tag, path, requestedPermissions);

        return path.toAbsolutePath();
    }

    /**
     * Method checks whether the provided value represent a correct path, and
     * returns the path if it does.
     *
     * @param tag                  Corresponding tag from the config file.
     * @param inputPath            Path to the directory.
     * @param requestedPermissions the requested permissions
     * @return Path represented in the JSON object, or the default value if the tag         is not present.
     * @throws IOException        Error if path is cannot be found.
     * @throws JSONException      Error in parsing the value for specified tag.
     * @throws APEConfigException Error in setting up the the configuration.
     */
    public static Path readDirectoryPath(String tag, String inputPath, Permission... requestedPermissions)
            throws IOException, JSONException, APEConfigException {

        final Path path = getPath(tag, inputPath);
        final String absolutePath = path.toAbsolutePath().toString();

        // first check if the format of the string resembles a path to a folder
        if(!isFolderFormat(path)){
            throw APEConfigException.notADirectory(tag, absolutePath);
        }

        // create a new directory is necessary
        createDirectory(tag, path);

        if (!directoryExists(path)) {
            throw APEConfigException.notADirectory(tag, absolutePath);
        }

        try {
            checkPermissions(tag, path, requestedPermissions);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path.toAbsolutePath();
    }

    private static void createDirectory(String tag, Path path){

        if (Files.notExists(path)) {

            final String absolutePath = path.toAbsolutePath().toString();

            if (!isFolderFormat(path)){
                throw new APEConfigException("Path '" + absolutePath + "' for tag '" + tag + "' is not a directory!");
            }

            APEUtils.printWarning(absolutePath + "' does not exist. Directory will be created.");

            if (new File(absolutePath).mkdirs()) {
                System.out.println("Successfully created directory '" + absolutePath + "'");
            }
            else{
                throw new APEConfigException("Could not create directory '" + absolutePath + "' for tag '" + tag + "'");
            }
        }

    }

    /**
     * Check permissions.
     *
     * @param tag                  the tag
     * @param path                 the path
     * @param requestedPermissions the requested permissions
     * @throws IOException exception if the path misses requested permissions.
     */
    public static void checkPermissions(String tag, Path path, Permission... requestedPermissions) throws IOException {

        if (Arrays.stream(requestedPermissions).anyMatch(p -> p == Permission.READ) && !Files.isReadable(path)) {
            throw APEConfigException.missingPermission(tag, path.toString(), Permission.READ);
        }

        if (Arrays.stream(requestedPermissions).anyMatch(p -> p == Permission.WRITE) && !Files.isWritable(path)) {
            throw APEConfigException.missingPermission(tag, path.toString(), Permission.WRITE);
        }
    }

    /**
     * Path is a folder.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean isFolderFormat(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals("");
    }

    /**
     * Path is a file.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean isFileFormat(Path path){
        return !isFolderFormat(path);
    }

    /**
     * Directory exists boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public static boolean directoryExists(Path path){
        return Files.isDirectory(path);
    }
    
    /**
     * Directory exists boolean.
     *
     * @param jsonObject the path
     * @return the boolean
     */
    public static boolean isJSON(JSONObject jsonObject) {
        return jsonObject != null;
    }

    /**
     * String is a valid IRI.
     *
     * @param uri the uri
     * @return String is a valid IRI
     */
    public static boolean isIRI(String uri){
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e) {
            return false;
        }
        return url.getProtocol().equals("http");
    }

}
