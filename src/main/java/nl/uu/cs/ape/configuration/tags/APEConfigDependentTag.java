package nl.uu.cs.ape.configuration.tags;

import org.json.JSONObject;

import nl.uu.cs.ape.configuration.tags.validation.ValidationResults;

import javax.inject.Provider;

/**
 * The Dependent Tags act the same way as an {@link APEConfigTag},
 * but they take a Provider as an argument. This way, Tags can access
 * data from other tags when they need it (dependency).
 * <p>
 * Instead of overriding the method constructFromJSON(JSONObject json);
 * dependent tags need you to override constructFromJSON(JSONObject json, D1 dependency1, .., DN dependencyN);
 *     <ul>
 *         <li>Create instance of {@literal APEConfigDependentTag.One<T, D1>} for one dependency.</li>
 *         <li>Create instance of {@literal APEConfigDependentTag.Two<T, D1, D2>} for two dependencies..</li>
 *         <li>Create instance of {@literal APEConfigDependentTag.Three<T, D1, D2, D3>} for three dependencies.</li>
 *     </ul>
 * <p>
 * Example, tag2(string type) is dependent on tag1(int type):
 * {@literal APEConfigTag<Integer> tag1 = ... ;}
 * {@literal APEConfigTag<String> tag2 = new APEConfigDependentTag.One<String, Integer>(() -> tag1.getValue()) { ... };}
 * <p>
 * Providers (callback functions) can be instantiated like this: {@literal () -> tag1.getValue()}
 * or like this: tag1::getValue
 * <p>
 * Using providers instead of tag references makes sure tags can be dependent on other data as well (e.g. APEDomainSetup)
 */
public class APEConfigDependentTag {

    /**
     * APEConfigTag that has a dependency on type D1.
     *
     * @param <T>  any type that represents the tag
     * @param <D1> any type that the tag has a dependency on
     */
    public abstract static class One<T, D1> extends APEConfigTag<T> {

        private final Provider<D1> provider1;

        /**
         * Instantiates a new APEConfigTag with one dependency (D1).
         *
         * @param provider1 the provider of dependency1
         */
        public One(Provider<D1> provider1) {
            this.provider1 = provider1;
        }

        @Override
        protected T constructFromJSON(JSONObject obj) {
            return constructFromJSON(obj, provider1.get());
        }

        /**
         * Construct object T from JSON.
         *
         * @param obj        the configuration object
         * @param dependency the dependency
         * @return the constructed object from JSON
         */
        protected abstract T constructFromJSON(JSONObject obj, D1 dependency);

        @Override
        protected ValidationResults validate(T value, ValidationResults results) {
            return validate(value, provider1.get(), results);
        }

        /**
         * Validate object T using the dependencies.
         *
         * @param value       the tag value that must be validated
         * @param dependency1 the first dependency
         * @param results     the validation results
         * @return the validation results
         */
        protected abstract ValidationResults validate(T value, D1 dependency1, ValidationResults results);
    }

    /**
     * APEConfigTag that has dependencies on type D1 and D2.
     *
     * @param <T>  any type that represents the tag
     * @param <D1> first type that the tag has a dependency on
     * @param <D2> second type that the tag has a dependency on
     */
    public abstract static class Two<T, D1, D2> extends One<T, D1> {

        private final Provider<D2> provider2;

        /**
         * Instantiates a new APEConfigTag with two dependencies (D1, D2).
         *
         * @param provider1 the provider of dependency1
         * @param provider2 the provider of dependency2
         */
        public Two(Provider<D1> provider1, Provider<D2> provider2) {
            super(provider1);
            this.provider2 = provider2;
        }

        @Override
        protected T constructFromJSON(JSONObject obj, D1 dependency1) {
            return constructFromJSON(obj, dependency1, provider2.get());
        }

        /**
         * Construct object T from JSON.
         *
         * @param obj         the configuration object
         * @param dependency1 the first dependency
         * @param dependency2 the second dependency
         * @return the constructed object from JSON
         */
        protected abstract T constructFromJSON(JSONObject obj, D1 dependency1, D2 dependency2);

        @Override
        protected ValidationResults validate(T value, D1 dependency1, ValidationResults results) {
            return validate(value, dependency1, provider2.get(), results);
        }

        /**
         * Validate object T using the dependencies.
         *
         * @param value       the tag value that must be validated
         * @param dependency1 the first dependency
         * @param dependency2 the second dependency
         * @param results     the validation results
         * @return the validation results
         */
        protected abstract ValidationResults validate(T value, D1 dependency1, D2 dependency2, ValidationResults results);
    }

    /**
     * APEConfigTag that has dependencies on type D1, D2 and D3.
     *
     * @param <T>  any type that represents the tag
     * @param <D1> first type that the tag has a dependency on
     * @param <D2> second type that the tag has a dependency on
     * @param <D3> third type that the tag has a dependency on
     */
    public abstract static class Three<T, D1, D2, D3> extends Two<T, D1, D2> {

        private final Provider<D3> provider3;

        /**
         * Instantiates a new APEConfigTag with three dependencies (D1, D2, D3).
         *
         * @param provider1 the provider of dependency1
         * @param provider2 the provider of dependency2
         * @param provider3 the provider of dependency3
         */
        public Three(Provider<D1> provider1, Provider<D2> provider2, Provider<D3> provider3) {
            super(provider1, provider2);
            this.provider3 = provider3;
        }

        @Override
        protected T constructFromJSON(JSONObject obj, D1 dependency1, D2 dependency2) {
            return constructFromJSON(obj, dependency1, dependency2, provider3.get());
        }

        /**
         * Construct object T from JSON.
         *
         * @param obj         the configuration object
         * @param dependency1 the first dependency
         * @param dependency2 the second dependency
         * @param dependency3 the third dependency
         * @return the constructed object from JSON
         */
        protected abstract T constructFromJSON(JSONObject obj, D1 dependency1, D2 dependency2, D3 dependency3);

        @Override
        protected ValidationResults validate(T value, D1 dependency1, D2 dependency2, ValidationResults results) {
            return validate(value, dependency1, dependency2, provider3.get(), results);
        }

        /**
         * Validate object T using the dependencies.
         *
         * @param value       the tag value that must be validated
         * @param dependency1 the first dependency
         * @param dependency2 the second dependency
         * @param dependency3 the third dependency
         * @param results     the validation results
         * @return the validation results
         */
        protected abstract ValidationResults validate(T value, D1 dependency1, D2 dependency2, D3 dependency3, ValidationResults results);
    }
}
