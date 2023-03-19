package nl.uu.cs.ape.configuration.tags.validation;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A container for validation results for all tags.
 */
public class ValidationResults {

    private final List<ValidationResult> results = new ArrayList<>();

    /**
     * Instantiates a new empty container.
     */
    public ValidationResults() {
    }

    /**
     * Instantiates a new container based on an existing collection.
     *
     * @param results an existing collection of validation results
     */
    public ValidationResults(Collection<ValidationResult> results) {
        this.results.addAll(results);
    }

    /**
     * Gets a copy of the validation results in List format.
     *
     * @return a copy of the validation results in List format
     */
    public List<ValidationResult> list() {
        return new ArrayList<>(this.results);
    }

    /**
     * Gets a copy of the validation results in Stream format.
     *
     * @return a copy of the validation results in Stream format
     */
    public Stream<ValidationResult> stream() {
        return new ArrayList<>(this.results).stream();
    }

    /**
     * Add a new validation result.
     * The container will call the ValidationResult constructor.
     *
     * @param tag             The tag name that was being tested.
     * @param ruleDescription The description of the rule.
     * @param success         Represents a success or fail.
     */
    public void add(String tag, String ruleDescription, boolean success) {
        results.add(new ValidationResult(tag, ruleDescription, success));
    }

    /**
     * Add an existing container to this container.
     *
     * @param results an existing container
     */
    public void add(ValidationResults results) {
        this.results.addAll(results.list());
    }

    /**
     * Gets all the ValidationResults that fail.
     *
     * @return all the ValidationResults that fail
     */
    public List<ValidationResult> getFails() {
        return this.results.stream()
                .filter(ValidationResult::isFail)
                .collect(Collectors.toList());
    }

    /**
     * Gets all the ValidationResults that succeed.
     *
     * @return all the ValidationResults that succeed
     */
    public List<ValidationResult> getSuccesses() {
        return this.results.stream()
                .filter(ValidationResult::isSuccess)
                .collect(Collectors.toList());
    }

    /**
     * Indicates whether this class contains at least one fail.
     * This method is the counterpart of {@link ValidationResults#success()}
     *
     * @return a boolean indicating whether this class contains at least one fail
     */
    public boolean hasFails() {
        return results.stream().anyMatch(ValidationResult::isFail);
    }

    /**
     * Indicates whether this class passed all validation criteria.
     * This method is the counterpart of {@link ValidationResults#hasFails()}
     *
     * @return a boolean indicating whether this class passed all validation
     *         criteria
     */
    public boolean success() {
        return !hasFails();
    }

    /**
     * Mapping the list to JSONArray using {@link ValidationResult#toJSON()}.
     *
     * @return a JSONArray
     */
    public JSONArray toJSONArray() {
        return new JSONArray(results.stream().map(ValidationResult::toJSON).collect(Collectors.toList()));
    }

    /**
     * Override toString as a JSON
     * 
     * @return String representation
     */
    @Override
    public String toString() {
        return toJSONArray().toString(2);
    }
}
