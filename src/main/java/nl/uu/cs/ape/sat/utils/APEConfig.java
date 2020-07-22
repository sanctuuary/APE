package nl.uu.cs.ape.sat.utils;

import com.sun.tools.javac.code.Attribute;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import org.apache.logging.log4j.util.Activator;
import org.json.JSONObject;

import java.io.File;

public class APEConfig {

    public static class TagInfo<T> {

        private String name;
        private Class<T> type;
        private boolean optional;
        private String description;

        public TagInfo(String tag, Class<T> type, boolean optional){
            this.name = tag;
            this.type = type;
            this.optional = optional;
        }

        public JSONObject toJSON(){
            return new JSONObject()
                    .put("name", name)
                    .put("type", type.getSimpleName())
                    .put("optional", optional);
        }

        public String getName(){
            return this.name;
        }

        public Class<T> getType(){
            return this.type;
        }

        public boolean isOptional(){
            return this.optional;
        }

        public String getDescription(){
            return this.description;
        }
    }

    public abstract static class Field<T> {

        private T value;
        private TagInfo<T> tag;

        public Field(TagInfo<T> info){
            this.tag = info;
        }

        protected abstract T readFromJSON(JSONObject obj);

        public void setValueFromJSON(JSONObject obj) {
            this.value = readFromJSON(obj);
        }

        public void setValueFromJSON(T value) {
            this.value = value;
        }

        public T getValue(){
            return this.value;
        }

        public TagInfo<T> getTagInfo(){
            return this.tag;
        }

        public static class OnOff extends Field<Boolean> {

            public OnOff(TagInfo<Boolean> info) {
                super(info);
            }

            @Override
            protected Boolean readFromJSON(JSONObject obj) {
                return obj.getBoolean(getTagInfo().getName());
            }
        }

        public static class Path extends Field<String>{

            public Path(TagInfo<String> info) {
                super(info);
            }

            @Override
            protected String readFromJSON(JSONObject obj) {
                //TODO: verify path
                return obj.getString(getTagInfo().getName());
            }
        }

        public static class Option<E extends Enum<E>> extends Field<E> {

            public Option(TagInfo<E> info) {
                super(info);
            }

            @Override
            protected E readFromJSON(JSONObject json) {
                final String tag = getTagInfo().getName();
                final String value = json.getString(tag).toUpperCase();
                final Class<E> type = getTagInfo().getType();
                return E.valueOf(type, value);
            }
        }
    }
}
