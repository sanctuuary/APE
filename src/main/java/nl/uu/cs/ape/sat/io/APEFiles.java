package nl.uu.cs.ape.sat.io;

import nl.uu.cs.ape.sat.configuration.APEConfigException;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class APEFiles {

    /**
     * READ and WRITE enums used to verify paths.
     */
    public enum Permission {
        READ, WRITE
    }

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
     * @param tag    Corresponding tag from the config file.
     * @param inputPath Provided path for the file.
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
     * @param tag    Corresponding tag from the config file.
     * @param inputPath Path to the directory.
     * @return Path represented in the JSON object, or the default value if the tag
     *         is not present.
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

    private static void createFile(String tag, Path path) {

        if (Files.notExists(path)) {

            final String absolutePath = path.toAbsolutePath().toString();

            if (!isFileFormat(path)) {
                throw new APEConfigException("Path '" + absolutePath + "' for tag '" + tag + "' is not a file, but a directory!");
            }

            try{
                APEUtils.printWarning(absolutePath + "' does not exist. File will be created.");
                if(new File(absolutePath).createNewFile()){
                    System.out.println("Successfully created file '" + absolutePath + "'");
                }
                else{
                    throw new IOException("File already exists.");
                }
            }
            catch (IOException e){
                throw new APEConfigException("Could not create file '" + absolutePath + "' for tag '" + tag + "'\n" + e.getMessage());
            }
        }

    }

    public static void checkPermissions(String tag, Path path, Permission... requestedPermissions) throws IOException {

        for (Permission permission : Arrays.stream(requestedPermissions).distinct().collect(Collectors.toList())) {

            if (permission == Permission.READ && !Files.isReadable(path)) {
                throw APEConfigException.missingPermission(tag, path.toString(), permission);
            }

            if (permission == Permission.WRITE && !Files.isWritable(path)) {
                throw APEConfigException.missingPermission(tag, path.toString(), permission);
            }
        }
    }

    private static boolean isFolderFormat(Path path) {
        return FilenameUtils.getExtension(path.toString()).equals("");
    }

    public static boolean isFileFormat(Path path){
        return !isFolderFormat(path);
    }

    public static boolean fileExists(String stringPath){
        try{
            final Path p = Paths.get(stringPath);
            return Files.exists(p) && Files.isRegularFile(p);
        }
        catch (InvalidPathException | NullPointerException e){
            return false;
        }
    }

    public static boolean directoryExists(Path path){
        return Files.isDirectory(path);
    }

    public static boolean isURI(String uri){
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e) {
            return false;
        }
        return url.getProtocol().equals("http");
    }
}
