package nl.uu.cs.ape.configuration.tags;

import javax.annotation.Nullable;

import nl.uu.cs.ape.configuration.APEConfigException;

/**
 * In APE, a default value could be null. Simply checking
 * value==null to see if a tag has a default value is not possible.
 * This class contains a boolean specifying whether the the tag has
 * a default value or not.
 *
 * @param <T> any type
 */
public class APEConfigDefaultValue<T> {

    private final boolean hasValue;
    private final T value;

    private APEConfigDefaultValue(@Nullable T t) {
        hasValue = true;
        value = t;
    }

    private APEConfigDefaultValue() {
        hasValue = false;
        value = null;
    }

    /**
     * Call this constructor if the tag does not have a default value.
     *
     * @param <T> any type
     * @return default value container
     */
    public static <T> APEConfigDefaultValue<T> noDefault() {
        return new APEConfigDefaultValue<>();
    }

    /**
     * Call this constructor if the tag has a default value.
     * The value is allowed to be null.
     *
     * @param <T>   any type
     * @param value the default value
     * @return default value container
     */
    public static <T> APEConfigDefaultValue<T> withDefault(@Nullable T value) {
        return new APEConfigDefaultValue<>(value);
    }

    /**
     * Returns a boolean indicating whether the container has a default value set or not.
     *
     * @return a boolean indicating whether the container has a default value set or not
     */
    public boolean hasValue() {
        return this.hasValue;
    }

    /**
     * Get the default value that has been set by the tag.
     * Make sure to call {@link APEConfigDefaultValue#hasValue()}
     * to check whether a value has been set by the tag.
     *
     * @return the default value
     */
    public T get() {
        if (!this.hasValue) {
            throw new APEConfigException("No default value present");
        }
        return this.value;
    }

}
