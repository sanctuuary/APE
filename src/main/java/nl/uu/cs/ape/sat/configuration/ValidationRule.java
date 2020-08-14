package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.io.APEFiles;

import java.util.function.Predicate;

public class ValidationRule<T> {

    protected final String tagName, ruleDescription;
    protected final Predicate<T> predicate;

    public ValidationRule(String tagName, String ruleDescription, Predicate<T> predicate){
        this.tagName = tagName;
        this.ruleDescription = ruleDescription;
        this.predicate = predicate;
    }

    public ValidationResult test(T t){
        return new ValidationResult(tagName, ruleDescription, predicate.test(t));
    }

    public static final Predicate<String> FILEEXISTS = APEFiles::fileExists;
}
