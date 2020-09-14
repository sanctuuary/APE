package nl.uu.cs.ape.sat.configuration.tags;

import nl.uu.cs.ape.sat.configuration.APEConfigException;

import javax.annotation.Nullable;

public class APEConfigDefaultValue<T> {

    private final boolean hasValue;
    private final T value;

    private APEConfigDefaultValue(@Nullable T t){
        hasValue = true;
        value = t;
    }

    private APEConfigDefaultValue(){
        hasValue = false;
        value = null;
    }

    public boolean hasValue(){
        return this.hasValue;
    }

    public T get(){
        if(!this.hasValue){
            throw new APEConfigException("No default value present");
        }
        return this.value;
    }

    public static <T> APEConfigDefaultValue<T> noDefault() {
        return new APEConfigDefaultValue<>();
    }

    public static <T> APEConfigDefaultValue<T> withDefault(@Nullable T value){
        return new APEConfigDefaultValue<>(value);
    }

}
