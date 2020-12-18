package nl.uu.cs.ape.sat.configuration.tags;

import nl.uu.cs.ape.sat.configuration.APEConfigException;
import nl.uu.cs.ape.sat.configuration.tags.validation.ValidationResults;
import nl.uu.cs.ape.sat.io.APEFiles;
import nl.uu.cs.ape.sat.models.Range;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Provider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static nl.uu.cs.ape.sat.configuration.tags.APEConfigTag.TagType.*;

public class APEConfigTagFactory {

    public static class TYPES {

        public static abstract class ExistingFile extends APEConfigTag<Path> {

            @Override
            public TagType getType() {
                return FILE_PATH;
            }

            @Override
            protected Path constructFromJSON(JSONObject obj) {
                final String input = obj.getString(getTagName());
                try {
                    return APEFiles.readFileFromPath(getTagName(), input, getRequiredPermissions());
                } catch (IOException e) {
                    throw APEConfigException.invalidValue(getTagName(), input, e.getMessage());
                }
            }

            protected abstract APEFiles.Permission[] getRequiredPermissions();

            @Override
            public ValidationResults validate(Path path, ValidationResults results) {
                results.add(getTagName(), "The file should exist.", Files.exists(path));
                return results;
            }
        }

        public static abstract class DataDimensions extends APEConfigDependentTag.One<List<String>, String> {

            public DataDimensions(Provider<String> provider) {
                super(provider);
            }

            @Override
            protected List<String> constructFromJSON(JSONObject obj, String ontology_prefix) {

                if (ontology_prefix == null) {
                    throw APEConfigException.requiredValidationTag(getTagName(), new TAGS.ONTOLOGY_PREFIX().getTagName(), "");
                }

                List<String> dataDimensionRoots = new ArrayList<>();
                try {
                    for (String subTaxonomy : APEUtils.getListFromJson(obj, getTagName(), String.class)) {
                        dataDimensionRoots.add(APEUtils.createClassURI(subTaxonomy, ontology_prefix));
                    }
                } catch (ClassCastException e) {
                    throw APEConfigException.invalidValue(getTagName(), obj, "expected a list in correct format.");
                } catch (IllegalArgumentException e) {
                    throw APEConfigException.invalidValue(getTagName(), obj, "elements of the list cannot be empty.");
                }
                return dataDimensionRoots;
            }

            @Override
            protected ValidationResults validate(List<String> value, String ontology_prefix, ValidationResults results) {
                // TODO
                return results;
            }

            @Override
            public TagType getType() {
                return DATA_DIMENSIONS;
            }

        }

        public static abstract class DataInstances extends APEConfigDependentTag.One<List<Type>, APEDomainSetup> {

            public DataInstances(Provider<APEDomainSetup> provider) {
                super(provider);
            }

            @Override
            public TagType getType() {
                return DATA_INSTANCES;
            }

            @Override
            protected ValidationResults validate(List<Type> value, APEDomainSetup apeDomainSetup, ValidationResults results) {
                // TODO: check data instances
                return results;
            }

            @Override
            public APEConfigDefaultValue<List<Type>> getDefault() {
                return APEConfigDefaultValue.withDefault(new ArrayList<>());
            }

        }

        public static abstract class Bool extends APEConfigTag<Boolean> {

            @Override
            public TagType getType() {
                return BOOLEAN;
            }

            @Override
            protected Boolean constructFromJSON(JSONObject obj) {
                return obj.getBoolean(getTagName());
            }

            @Override
            protected ValidationResults validate(Boolean value, ValidationResults results) {
                return results;
            }
        }

        public static abstract class Int extends APEConfigTag<Integer> {

            private final Range range;

            public Int(Range range) {
                this.range = range;
            }

            @Override
            public TagType getType() {
                return INTEGER;
            }

            @Override
            protected Integer constructFromJSON(JSONObject obj) {
                return obj.getInt(getTagName());
            }

            @Override
            public APEConfigDefaultValue<Integer> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }

            @Override
            protected JSONObject getTagConstraints() {
                return range.toJSON();
            }
        }

        public static abstract class IntRange extends APEConfigTag<Range> {

            private final Range boundaries;

            public IntRange(Range boundaries) {
                this.boundaries = boundaries;
            }

            @Override
            protected Range constructFromJSON(JSONObject obj) {
                return Range.from(obj.getJSONObject(getTagName()));
            }

            @Override
            public APEConfigDefaultValue<Range> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }

            @Override
            public TagType getType() {
                return INTEGER_RANGE;
            }

            @Override
            protected ValidationResults validate(Range range, ValidationResults results) {
                results.add(getTagName(),
                        "Maximal solution length should be greater or equal to the minimal solution length.",
                        range.getMax() >= range.getMin());
                return results;
            }

            @Override
            protected JSONObject getTagConstraints() {
                return boundaries.toJSON();
            }
        }

        public static abstract class Directory extends APEConfigTag<Path> {

            protected abstract APEFiles.Permission[] getRequiredPermissions();

            @Override
            public TagType getType() {
                return FOLDER_PATH;
            }

            @Override
            protected Path constructFromJSON(JSONObject obj) {
                final String input = obj.getString(getTagName());
                try {
                    return APEFiles.readDirectoryPath(getTagName(), input, getRequiredPermissions());
                } catch (IOException e) {
                    throw APEConfigException.invalidValue(getTagName(), input, e.getMessage());
                }
            }

            @Override
            public APEConfigDefaultValue<Path> getDefault() {
                return APEConfigDefaultValue.withDefault(null);
            }

            @Override
            protected ValidationResults validate(Path value, ValidationResults results) {
                return results;
            }
        }

        public static abstract class Option<E extends Enum<E>> extends APEConfigTag<E> {

            @Override
            public TagType getType() {
                return ENUM;
            }

            public abstract Class<E> getEnumClass();

            public E[] getOptions() {
                return getEnumClass().getEnumConstants();
            }

            @Override
            protected E constructFromJSON(JSONObject json) {
                return E.valueOf(getEnumClass(), json.getString(getTagName()).toUpperCase());
            }

            @Override
            protected ValidationResults validate(E value, ValidationResults results) {
                return results;
            }

            @Override
            protected JSONObject getTagConstraints() {
                return new JSONObject().put("options", new JSONArray(getOptions()));
            }
        }

        public static abstract class URI extends APEConfigTag<String> {

            @Override
            public TagType getType() {
                return URI;
            }

            @Override
            protected String constructFromJSON(JSONObject obj) {
                return obj.getString(getTagName());
            }

            @Override
            protected ValidationResults validate(String uri, ValidationResults results) {
                results.add(getTagName(), "Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).", APEFiles.isURI(uri));
                return results;
            }
        }
        
        public static abstract class JSON extends APEConfigTag<JSONObject> {

            @Override
            public TagType getType() {
                return JSON;
            }

            @Override
            protected JSONObject constructFromJSON(JSONObject obj) {
            	String constrintsPath = obj.getString(getTagName());
            	JSONObject constraints = null;
            	try {
            	constraints = APEUtils.readFileToJSONObject(new File(constrintsPath));
            	} catch (IOException e) {
            		throw APEConfigException.invalidValue(getTagName(), constrintsPath, e.getMessage());
				} catch (JSONException e) {
					throw APEConfigException.invalidValue(getTagName(), constrintsPath, e.getMessage());
				}
            	
            
                return constraints;
            }

            @Override
            protected ValidationResults validate(JSONObject jsonObject, ValidationResults results) {
                results.add(getTagName(), "Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).", APEFiles.isJSON(jsonObject));
                return results;
            }
        }
    }

    public static class TAGS {

        public static class ONTOLOGY extends TYPES.ExistingFile {

            @Override
            public String getTagName() {
                return "ontology_path";
            }

            @Override
            public String getLabel() {
                return "Ontology";
            }


            @Override
            public String getDescription() {
                return "This tag should be a path to an existing .owl file.";
            }

            @Override
            public APEConfigDefaultValue<Path> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }

            @Override
            protected APEFiles.Permission[] getRequiredPermissions() {
                return new APEFiles.Permission[]{APEFiles.Permission.READ};
            }
        }

        public static class ONTOLOGY_PREFIX extends TYPES.URI {

            @Override
            public String getTagName() {
                return "ontologyPrexifIRI";
            }

            @Override
            public String getLabel() {
                return "Ontology Prefix";
            }

            @Override
            public String getDescription() {
                return "Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).";
            }

            @Override
            public APEConfigDefaultValue<String> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }

        }

        public static class DIMENSIONS_ONTOLOGY extends TYPES.DataDimensions {

            public DIMENSIONS_ONTOLOGY(Provider<String> provider) {
                super(provider);
            }

            @Override
            public String getTagName() {
                return "dataDimensionsTaxonomyRoots";
            }

            @Override
            public String getLabel() {
                return "Data Dimensions";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<List<String>> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }
        }

        public static class PROGRAM_INPUTS extends TYPES.DataInstances {

            public PROGRAM_INPUTS(Provider<APEDomainSetup> provider) {
                super(provider);
            }

            @Override
            public String getTagName() {
                return "inputs";
            }

            @Override
            public String getLabel() {
                return "Input data";
            }

            @Override
            public String getDescription() {
                //TODO
                return "";
            }
            
            @Override
            protected List<Type> constructFromJSON(JSONObject obj, APEDomainSetup apeDomainSetup) {
                final ArrayList<Type> instances = new ArrayList<>();

                if (apeDomainSetup == null) {
                    throw APEConfigException.requiredValidationTag(getTagName(), "core configuration", "");
                }

                try {
                    for (JSONObject jsonType : APEUtils.getListFromJson(obj, getTagName(), JSONObject.class)) {
                        Type input;
                        if ((input = Type.taxonomyInstanceFromJson(jsonType, apeDomainSetup, true)) != null) {
                            instances.add(input);
                        }
                    }
                } catch (ClassCastException e) {
                    instances.clear();
                    throw APEConfigException.cannotParse(getTagName(), obj.get(getTagName()).toString(), JSONObject[].class,
                            "please provide the correct format.");
                }

                return instances;
            }
        }

        public static class PROGRAM_OUTPUTS extends TYPES.DataInstances {

            public PROGRAM_OUTPUTS(Provider<APEDomainSetup> provider) {
                super(provider);
            }

            @Override
            public String getTagName() {
                return "outputs";
            }

            @Override
            public String getLabel() {
                return "Output data";
            }

            @Override
            public String getDescription() {
                //TODO
                return "";
            }
            
            @Override
            protected List<Type> constructFromJSON(JSONObject obj, APEDomainSetup apeDomainSetup) {
                final ArrayList<Type> instances = new ArrayList<>();

                if (apeDomainSetup == null) {
                    throw APEConfigException.requiredValidationTag(getTagName(), "core configuration", "");
                }

                try {
                    for (JSONObject jsonType : APEUtils.getListFromJson(obj, getTagName(), JSONObject.class)) {
                        Type output;
                        if ((output = Type.taxonomyInstanceFromJson(jsonType, apeDomainSetup, false)) != null) {
                            instances.add(output);
                        }
                    }
                } catch (ClassCastException e) {
                    instances.clear();
                    throw APEConfigException.cannotParse(getTagName(), obj.get(getTagName()).toString(), JSONObject[].class,
                            "please provide the correct format.");
                }

                return instances;
            }
        }

        public static class TOOL_ONTOLOGY_ROOT extends APEConfigDependentTag.One<String, String> {

            public TOOL_ONTOLOGY_ROOT(Provider<String> prefix_provider) {
                super(prefix_provider);
            }

            @Override
            public String getTagName() {
                return "toolsTaxonomyRoot";
            }

            @Override
            public String getLabel() {
                return "Tools root";
            }

            @Override
            public TagType getType() {
                return MODULE;
            }

            @Override
            public String getDescription() {
                //TODO
                return "";
            }

            @Override
            public APEConfigDefaultValue<String> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }

            @Override
            protected String constructFromJSON(JSONObject obj, String prefix) {
                return APEUtils.createClassURI(obj.getString(getTagName()), prefix);
            }

            @Override
            protected ValidationResults validate(String value, String prefix, ValidationResults results) {
                return results;
            }
        }

        public static class TOOL_ANNOTATIONS extends TYPES.ExistingFile {

            @Override
            protected APEFiles.Permission[] getRequiredPermissions() {
                return new APEFiles.Permission[]{APEFiles.Permission.READ};
            }

            @Override
            public String getTagName() {
                return "tool_annotations_path";
            }

            @Override
            public String getLabel() {
                return "Tool annotations";
            }

            @Override
            public String getDescription() {
                return "This tag should be a path to an existing .json file.";
            }

            @Override
            public APEConfigDefaultValue<Path> getDefault() {
                return APEConfigDefaultValue.noDefault();
            }
        }

        public static class CONSTRAINTS extends TYPES.JSON {

            @Override
            public String getTagName() {
                return "constraints_path";
            }

            @Override
            public String getLabel() {
                return "Constraints";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<JSONObject> getDefault() {
                return APEConfigDefaultValue.withDefault(null);
            }
        }

        public static class STRICT_TOOL_ANNOTATIONS extends TYPES.Bool {

            @Override
            public String getTagName() {
                return "strict_tool_annotations";
            }

            @Override
            public String getLabel() {
                return "Implement strict tool annotations";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<Boolean> getDefault() {
                return APEConfigDefaultValue.withDefault(true);
            }
        }

        public static class SOLUTION_LENGTH_RANGE extends TYPES.IntRange {

            public SOLUTION_LENGTH_RANGE() {
                super(Range.of(1, 50));
            }

            @Override
            public String getTagName() {
                return "solution_length";
            }

            @Override
            public String getLabel() {
                return "Solution min/max length";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            protected ValidationResults validate(Range range, ValidationResults results) {
                results.add(super.validate(range, results));
                results.add(getTagName(),
                        "Minimal solution length should be greater or equal to 0.",
                        range.getMin() > 0);
                return results;
            }
        }

        public static class MAX_NO_SOLUTIONS extends TYPES.Int {

            public MAX_NO_SOLUTIONS() {
                super(Range.of(0, Integer.MAX_VALUE));
            }

            @Override
            public String getTagName() {
                return "max_solutions";
            }

            @Override
            public String getLabel() {
                return "Maximum number of solutions";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            protected ValidationResults validate(Integer i, ValidationResults results) {
                results.add(getTagName(), "The maximum number of generated solutions should be greater or equal to 0.", i >= 0);
                return results;
            }
        }

        public static class SOLUTION_DIR_PATH extends TYPES.Directory {

            @Override
            protected APEFiles.Permission[] getRequiredPermissions() {
                return new APEFiles.Permission[]{APEFiles.Permission.READ, APEFiles.Permission.WRITE};
            }

            @Override
            public String getTagName() {
                return "solutions_dir_path";
            }

            @Override
            public String getLabel() {
                return "Solution directory";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }
        }

        public static class NO_EXECUTIONS extends TYPES.Int {

            public NO_EXECUTIONS() {
                super(Range.of(0, Integer.MAX_VALUE));
            }

            @Override
            public String getTagName() {
                return "number_of_execution_scripts";
            }

            @Override
            public String getLabel() {
                return "Number of executions scripts";
            }

            @Override
            public String getDescription() {
                //TODO
                return "";
            }

            @Override
            protected ValidationResults validate(Integer value, ValidationResults results) {
                return results;
            }
        }

        public static class NO_GRAPHS extends TYPES.Int {

            public NO_GRAPHS() {
                super(Range.of(0, Integer.MAX_VALUE));
            }

            @Override
            public String getTagName() {
                return "number_of_generated_graphs";
            }

            @Override
            public String getLabel() {
                return "Number of generated graphs";
            }

            @Override
            public String getDescription() {
                //TODO
                return "";
            }

            @Override
            protected ValidationResults validate(Integer value, ValidationResults results) {
                return results;
            }
        }

        public static class USE_WORKFLOW_INPUT extends TYPES.Option<ConfigEnum> {

            @Override
            public Class<ConfigEnum> getEnumClass() {
                return ConfigEnum.class;
            }

            @Override
            public String getTagName() {
                return "use_workflow_input";
            }

            @Override
            public String getLabel() {
                return "Use workflow input";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<ConfigEnum> getDefault() {
                return APEConfigDefaultValue.withDefault(ConfigEnum.ALL);
            }
        }

        public static class USE_ALL_GENERATED_DATA extends TYPES.Option<ConfigEnum> {

            @Override
            public Class<ConfigEnum> getEnumClass() {
                return ConfigEnum.class;
            }

            @Override
            public String getTagName() {
                return "use_all_generated_data";
            }

            @Override
            public String getLabel() {
                return "Use all generated data";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<ConfigEnum> getDefault() {
                return APEConfigDefaultValue.withDefault(ConfigEnum.ONE);
            }
        }

        public static class DEBUG_MODE extends TYPES.Bool {

            @Override
            public String getTagName() {
                return "debug_mode";
            }

            @Override
            public String getLabel() {
                return "Use debug mode";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<Boolean> getDefault() {
                return APEConfigDefaultValue.withDefault(false);
            }
        }

        public static class TOOL_SEQ_REPEAT extends TYPES.Bool {

            @Override
            public String getTagName() {
                return "tool_seq_repeat";
            }

            @Override
            public String getLabel() {
                return "Tool sequence repeat";
            }

            @Override
            public String getDescription() {
                // TODO
                return "TODO";
            }

            @Override
            public APEConfigDefaultValue<Boolean> getDefault() {
                return APEConfigDefaultValue.withDefault(true);
            }
        }
    }
}
