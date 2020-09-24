package nl.uu.cs.ape.sat.configuration.tags.validation;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationResults {

    private final List<ValidationResult> results = new ArrayList<>();

    public ValidationResults() {
    }

    public ValidationResults(Collection<ValidationResult> results) {
        this.results.addAll(results);
    }

    public void add(String tag, String ruleDescription, boolean success) {
        results.add(new ValidationResult(tag, ruleDescription, success));
    }

    public void add(ValidationResults results) {
        this.results.addAll(results.toList());
    }

    public List<ValidationResult> toList() {
        return new ArrayList<>(this.results);
    }

    public ValidationResults getFails() {
        return new ValidationResults(this.results.stream()
                .filter(ValidationResult::isFail)
                .collect(Collectors.toList()));
    }

    public boolean fail() {
        return results.stream().anyMatch(ValidationResult::isFail);
    }

    public boolean success() {
        return results.stream().allMatch(ValidationResult::isSuccess);
    }

    public ValidationResults getSuccesses() {
        return new ValidationResults(this.results.stream()
                .filter(ValidationResult::isSuccess)
                .collect(Collectors.toList()));
    }

    public JSONArray toJSONArray() {
        return new JSONArray(results.stream().map(ValidationResult::toJSON).collect(Collectors.toList()));
    }

    public Stream<ValidationResult> stream() {
        return results.stream();
    }

    @Override
    public String toString() {
        return toJSONArray().toString(2);
    }
}
