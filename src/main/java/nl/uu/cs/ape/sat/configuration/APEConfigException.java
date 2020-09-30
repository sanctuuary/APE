package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This {@link APEConfigException} will be thrown if the configuration is incorrect.
 * Additional info will be provided to help the user fix the problem.
 * The static methods in this class can also be used to throw other kinds of exceptions regarding the core- and run configuration.
 * <p>
 * The configuration problems can be classified in three exceptions:
 * <ul>
 *   <li>APEConfigException: Application specific configuration exceptions.</li>
 *   <li>JSONException: Values or files that cannot be parsed to a value.</li>
 *   <li>IOException: Errors in in reading from the file system.</li>
 * </ul>
 */
public class APEConfigException extends RuntimeException {

    /**
     * Instantiates a new Ape config exception.
     *
     * @param message The message that will be passed to the {@link Exception} super class.
     */
    public APEConfigException(String message) {
        super(message);
    }

    /**
     * Invalid value ape config exception.
     *
     * @param tag   Corresponding JSON tag in the configuration file.
     * @param value The value for the tag, provided by the user.
     * @param info  Application specific information that may help the user solve the problem.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static APEConfigException invalidValue(String tag, Object value, String info) {
        return new APEConfigException(String.format("'%s' is not a valid value for tag '%s', %s", value, tag, info));
    }

    /**
     * Invalid value ape config exception.
     *
     * @param tag    Corresponding JSON tag in the configuration file.
     * @param config The configuration provided by the user.
     * @param info   Application specific information that may help the user solve the problem.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static APEConfigException invalidValue(String tag, JSONObject config, String info) {
        return new APEConfigException(String.format("'%s' is not a valid value for tag '%s', %s", config.get(tag), tag, info));
    }

    /**
     * Missing tag ape config exception.
     *
     * @param tag Corresponding JSON tag in the configuration file.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static APEConfigException missingTag(String tag) {
        return new APEConfigException(String.format("Tag '%s' is not provided, cannot setup the configuration.", tag));
    }

    /**
     * Cannot parse ape config exception.
     *
     * @param <T>          The type that APE cannot parse to.
     * @param tag          Corresponding JSON tag in the configuration file.
     * @param value        The value for the tag, provided by the user.
     * @param expectedType The type that APE cannot parse to.
     * @param info         Application specific information that may help the user solve the problem.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static <T> JSONException cannotParse(String tag, Object value, Class<T> expectedType, String info) {
        return new JSONException(String.format("Value '%s' cannot be parsed to type '%s' for tag '%s', %s", value, expectedType.getSimpleName(), tag, info));
    }

    /**
     * Required validation tag json exception.
     *
     * @param <T>         the type parameter
     * @param tag         the tag
     * @param requiredTag the required tag
     * @param info        the info
     * @return the json exception
     */
    public static <T> JSONException requiredValidationTag(String tag, String requiredTag, String info) {
        return new JSONException(String.format("Cannot parse tag '%s' without '%s', %s", tag, requiredTag, info));
    }

    /**
     * Path not found ape config exception.
     *
     * @param tag  Corresponding JSON tag in the configuration file.
     * @param path The relative- or absolute path to a JSON- or OWL file.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static IOException pathNotFound(String tag, String path) {
        return new IOException(String.format("Provided path '%s' for tag '%s' does not exist.", path, tag));
    }

    /**
     * Path is found, but it is not a file.
     *
     * @param tag  Corresponding JSON tag in the configuration file.
     * @param path The relative- or absolute path to a JSON- or OWL file.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static IOException notAFile(String tag, String path) {
        return new IOException(String.format("Provided path '%s' for tag '%s' is not a file.", path, tag));
    }

    /**
     * Path is found, but it is not a directory.
     *
     * @param tag  Corresponding JSON tag in the configuration file.
     * @param path The relative- or absolute path to a JSON- or OWL file.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static IOException notADirectory(String tag, String path) {
        return new IOException(String.format("Provided path '%s' for tag '%s' is not a directory.", path, tag));
    }

    /**
     * Missing permission ape config exception.
     *
     * @param tag               Corresponding JSON tag in the configuration file.
     * @param path              The relative- or absolute path to a JSON- or OWL file.
     * @param missingPermission The missing READ or WRITE permission for the file described by the path.
     * @return Configuration exception with information that may help the user solve the problem.
     */
    public static IOException missingPermission(String tag, String path, Object missingPermission) {
        return new IOException(String.format("You are missing [%s] permission for path '%s' for tag '%s'", missingPermission, path, tag));
    }

    /**
     * List all the failed validation criteria in one exception.
     *
     * @param validationResults the validation results
     * @return the json exception
     */
    public static APEConfigException ruleViolations(ValidationResults validationResults) {
        StringBuilder sb = new StringBuilder();
        validationResults.getFails().forEach(fail ->
                sb.append(String.format("Tag '%s' is incorrect: %s\n", fail.getTag(), fail.getRuleDescription()))
        );
        return new APEConfigException(sb.toString());
    }
}
