package nl.uu.cs.ape.sat.configuration.tags;

import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import org.json.JSONObject;

import javax.inject.Provider;

public class APEConfigDependentTag {

    public abstract static class One<T, D1> extends APEConfigTag<T> {

        private final Provider<D1> provider1;

        public One(Provider<D1> provider1) {
            this.provider1 = provider1;
        }

        @Override
        protected T constructFromJSON(JSONObject obj) {
            return constructFromJSON(obj, provider1.get());
        }

        protected abstract T constructFromJSON(JSONObject obj, D1 dependency);

        @Override
        protected ValidationResults validate(T value, ValidationResults results) {
            return validate(value, provider1.get(), results);
        }

        protected abstract ValidationResults validate(T value, D1 dependency1, ValidationResults results);
    }

    public abstract static class Two<T, D1, D2> extends One<T, D1> {

        private final Provider<D2> provider2;

        public Two(Provider<D1> provider1, Provider<D2> provider2) {
            super(provider1);
            this.provider2 = provider2;
        }

        @Override
        protected T constructFromJSON(JSONObject obj, D1 dependency1) {
            return constructFromJSON(obj, dependency1, provider2.get());
        }

        protected abstract T constructFromJSON(JSONObject obj, D1 dependency, D2 dependency2);

        @Override
        protected ValidationResults validate(T value, D1 dependency1, ValidationResults results) {
            return validate(value, dependency1, provider2.get(), results);
        }

        protected abstract ValidationResults validate(T value, D1 dependency1, D2 dependency2, ValidationResults results);
    }

    public abstract static class Three<T, D1, D2, D3> extends Two<T, D1, D2> {

        private final Provider<D3> provider3;

        public Three(Provider<D1> provider1, Provider<D2> provider2, Provider<D3> provider3) {
            super(provider1, provider2);
            this.provider3 = provider3;
        }

        @Override
        protected T constructFromJSON(JSONObject obj, D1 dependency1, D2 dependency2) {
            return constructFromJSON(obj, dependency1, dependency2, provider3.get());
        }

        protected abstract T constructFromJSON(JSONObject obj, D1 dependency, D2 dependency2, D3 dependency3);

        @Override
        protected ValidationResults validate(T value, D1 dependency1, D2 dependency2, ValidationResults results) {
            return validate(value, dependency1, dependency2, provider3.get(), results);
        }

        protected abstract ValidationResults validate(T value, D1 dependency1, D2 dependency2, D3 dependency3, ValidationResults results);
    }
}
