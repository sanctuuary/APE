package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.utils.APEConfigException;

import javax.annotation.Nullable;

public class APEDefaultValue<T> {

    private final boolean hasValue;
    private final T value;

    private APEDefaultValue(@Nullable T t){
        hasValue = true;
        value = t;
    }

    private APEDefaultValue(){
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

    public static <T> APEDefaultValue<T> noDefault() {
        return new APEDefaultValue<>();
    }

    public static <T> APEDefaultValue<T> withDefault(@Nullable T value){
        return new APEDefaultValue<>(value);
    }

}
