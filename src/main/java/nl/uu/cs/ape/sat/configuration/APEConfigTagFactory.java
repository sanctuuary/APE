package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.io.APEFiles;
import nl.uu.cs.ape.sat.utils.APEConfigException;
import nl.uu.cs.ape.sat.utils.APECoreConfig;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class APEConfigTagFactory {

    public static class Number extends APEConfigTag<Integer> {

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        public Number(String tagName) {
            super(tagName, Integer.class);
        }

        @Override
        protected Integer constructFromJSON(JSONObject obj) {
            return obj.getInt(getName());
        }
    }

    public static class Option<E extends Enum<E>> extends APEConfigTag<E> {

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         * @param type    the type
         */
        public Option(String tagName, Class<E> type) {
            super(tagName, type);
        }

        @Override
        protected E constructFromJSON(JSONObject json) {
            final String s = json.getString(getName()).toUpperCase();
            return E.valueOf(getTagType(), s);
        }
    }

    public static class Bool extends APEConfigTag<Boolean>{

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        public Bool(String tagName) {
            super(tagName, Boolean.class);
        }

        @Override
        protected Boolean constructFromJSON(JSONObject obj) {
            return obj.getBoolean(getName());
        }
    }

    public static class StringTag extends APEConfigTag<String>{

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        public StringTag(String tagName) {
            super(tagName, String.class);
        }

        @Override
        protected String constructFromJSON(JSONObject obj) {
            return obj.getString(getName());
        }
    }

    public static class Path extends StringTag{

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        public Path(String tagName) {
            super(tagName);
            this.addValidationFunction(APEFiles::validPathFormat, "The path should be a correctly formatted.");
        }
    }

    public static class ExistingFile extends APEConfigTag<File>{

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        public ExistingFile(String tagName) {
            super(tagName, File.class);
            this.addValidationFunction(file -> APEFiles.validPathFormat(file.getPath()), "The path should be a correctly formatted.");
            this.addValidationFunction(File::exists, "The file should exist in the system files.");
        }

        @Override
        protected File constructFromJSON(JSONObject obj) {
            return new File(obj.getString(getName()));
        }
    }

    public static class DataDimensions extends APEConfigTag<List<String>>{

        /**
         * Instantiates a new Ape config field info.
         *
         * @param tagName the tag name
         */
        @SuppressWarnings("all")
        public DataDimensions(String tagName) {
            super(tagName, (Class<List<String>>) new ArrayList<String>().getClass());
        }

        @Override
        protected List<String> constructFromJSON(JSONObject obj) {
            List<String> dataDimensionRoots = new ArrayList<>();
            try{
                final String prefix = obj.getString(APECoreConfig.ONTOLOGY_PREFIX.getName());
                for (String subTaxonomy : APEUtils.getListFromJson(obj, getName(), String.class)) {
                    dataDimensionRoots.add(APEUtils.createClassURI(subTaxonomy, prefix));
                }
            }
            catch (ClassCastException e){
                throw APEConfigException.invalidValue(getName(), obj, "expected a list in correct format.");
            } catch (IllegalArgumentException e) {
                throw APEConfigException.invalidValue(getName(), obj, "elements of the list cannot be empty.");
            }
            return dataDimensionRoots;
        }
    }
}
