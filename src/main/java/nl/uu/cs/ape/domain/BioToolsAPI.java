package nl.uu.cs.ape.domain;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.uu.cs.ape.configuration.ToolAnnotationTag;
import nl.uu.cs.ape.utils.APEFiles;
import nl.uu.cs.ape.utils.APEUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The {@code BioToolsAPI} class provides methods for fetching tool annotations
 * from bio.tools API.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BioToolsAPI {

	/** Http-Client */
	private static final OkHttpClient client = new OkHttpClient();

	/**
	 * Send Get request to get tool annotations for each elements in JSONArray from
	 * bio.tools API. It writes the result to a file.
	 * 
	 * @param listFilePath        Path to the file with the list of tools.
	 * @param destinationFilePath Path to the file where the result will be written.
	 * @throws IOException - If the file cannot be read or written.
	 */
	public static void fetchToolSet(String listFilePath, String destinationFilePath) throws IOException {

		// Fetch the Limited (predefined) set of tool
		JSONArray bioToolsRAW = readListOfTools(listFilePath);

		JSONObject apeToolAnnotation = convertBioTools2Ape(bioToolsRAW);
		APEFiles.write2file(apeToolAnnotation.toString(4), new File(destinationFilePath), false);
	}

	/**
	 * Send Get request to get tool annotations for each elements in JSONArray from
	 * bio.tools API. It writes the result to a JSONArray.
	 * 
	 * @param filePath Path to the file with the list of tools.
	 * @return JSONArray with the tool annotations.
	 * @throws IOException - If the file cannot be read or written.
	 */
	private static JSONArray readListOfTools(String filePath) throws IOException {

		File toolList = new File(filePath);
		JSONArray toolListJson = new JSONArray(FileUtils.readFileToString(toolList, "UTF-8"));
		/* Fetch tool annotations */
		return fetchToolListFromBioTools(toolListJson);
	}

	/**
	 * Send Get request to get tool annotations for each elements in JSONArray from
	 * bio.tools API. It writes the result to a JSONArray.
	 * 
	 * @param domainName Path to the file with the list of tools.
	 * @return JSONArray with the tool annotations.
	 * @throws IOException - If the file cannot be read or written.
	 */
	private static JSONArray getToolsFromDomain(String domainName) throws IOException {
		JSONArray toolAnnotations = null;
		if (!domainName.equals("")) {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?domain=" + domainName + "&format=json");
		} else {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?format=json");
		}
		return toolAnnotations;
	}

	/**
	 * Send Get request to get tool annotations Saves JSONArray with all
	 * bio.tools that belong to a certain EDAM topic.
	 * 
	 * @throws IOException
	 */
	public static JSONObject getToolsFromEDAMTopic(String topicName) throws IOException {
		JSONArray toolAnnotations = null;
		if (!topicName.equals("")) {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?topicID=\"" + topicName + "\"&format=json");
		} else {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?format=json");
		}

		return convertBioTools2ApeStrict(toolAnnotations);
	}

	/**
	 * Send Get request to get tool annotations Saves JSONArray with all the tool
	 * annotations (in tool list)
	 * 
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONArray fetchToolListFromBioTools(JSONArray toolListJson) throws JSONException, IOException {
		JSONArray bioToolAnnotations = new JSONArray();
		for (int i = 0; i < toolListJson.length(); i++) {
			String currTool = toolListJson.getString(i);
			Request request = new Request.Builder().url("https://bio.tools/api/" + currTool + "?format=json").build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful())
					throw new IOException("Unexpected code when trying to fetch" + response);
				// Get response body
				JSONObject responseJson = new JSONObject(response.body().string());
				bioToolAnnotations.put(i, responseJson);
			}
		}
		log.debug("The list of tools successfully fetched from bio.tools.");
		return bioToolAnnotations;
	}

	/**
	 * Send Get request to get tool annotations Saves JSONArray with all the tool
	 * annotations (in tool list)
	 * 
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONArray fetchToolsFromURI(String url) throws JSONException, IOException {
		JSONArray bioToolAnnotations = new JSONArray();
		String next = "";
		int i = 1;
		while (next != null) {
			Request request = new Request.Builder().url(url + "&format=json" + next.replace('?', '&')).build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful())
					throw new IOException("Unexpected code when trying to fetch" + response);
				// Get response body
				JSONObject responseJson = new JSONObject(response.body().string());
				JSONArray toolListJson = responseJson.getJSONArray("list");
				for (int j = 0; j < toolListJson.length(); j++) {
					JSONObject tool = toolListJson.getJSONObject(j);
					bioToolAnnotations.put(tool);
				}
				try {
					next = responseJson.getString("next");
				} catch (JSONException e) {
					next = null;
				}
			}
			log.trace("bio.tools: page " + i + " fetched.");

		}
		log.debug("All tools fetched from a given URL.");
		return bioToolAnnotations;
	}

	/**
	 * Method converts tools annotated using 'bio.tools' standard (see <a href=
	 * "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 * API</a>), into standard supported by the APE library.
	 * <p>
	 * In practice, the method takes a {@link JSONArray} as an argument, where each
	 * {@link JSONObject} in the array represents a tool annotated using 'bio.tools'
	 * standard, and returns a {@link JSONObject} that represents tool annotations
	 * that can be used by the APE library.
	 *
	 * @param bioToolsAnnotation A {@link JSONArray} object, that contains list of
	 *                           annotated tools ({@link JSONObject}s) according the
	 *                           bio.tools specification (see <a href=
	 *                           "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 *                           API</a>)
	 * @return {@link JSONObject} that represents the tool annotation supported by
	 *         the APE library.
	 * @throws JSONException the json exception
	 */
	private static JSONObject convertBioTools2Ape(JSONArray bioToolsAnnotation) throws JSONException {
		JSONArray apeToolsAnnotations = new JSONArray();
		for (int i = 0; i < bioToolsAnnotation.length(); i++) {

			JSONObject bioJsonTool = bioToolsAnnotation.getJSONObject(i);
			List<JSONObject> functions = APEUtils.getListFromJson(bioJsonTool, "function", JSONObject.class);
			int functionNo = 1;
			for (JSONObject function : functions) {
				JSONObject apeJsonTool = new JSONObject();
				apeJsonTool.put(ToolAnnotationTag.LABEL.toString(), bioJsonTool.getString("name"));
				apeJsonTool.put(ToolAnnotationTag.ID.toString(), bioJsonTool.getString("biotoolsID") + functionNo++);

				JSONArray apeTaxonomyTerms = new JSONArray();

				JSONArray operations = function.getJSONArray("operation");
				for (int j = 0; j < operations.length(); j++) {
					JSONObject bioOperation = operations.getJSONObject(j);
					apeTaxonomyTerms.put(bioOperation.get("uri"));
				}
				apeJsonTool.put(ToolAnnotationTag.TAXONOMY_OPERATIONS.toString(), apeTaxonomyTerms);
				// reading inputs
				JSONArray apeInputs = new JSONArray();
				JSONArray bioInputs = function.getJSONArray("input");
				// for each input
				for (int j = 0; j < bioInputs.length(); j++) {
					JSONObject bioInput = bioInputs.getJSONObject(j);
					JSONObject apeInput = new JSONObject();
					JSONArray apeInputTypes = new JSONArray();
					JSONArray apeInputFormats = new JSONArray();
					// add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "data", JSONObject.class)) {
						apeInputTypes.put(bioType.getString("uri"));
					}
					apeInput.put("data_0006", apeInputTypes);
					// add all data formats (or just the first one)
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "format", JSONObject.class)) {
						apeInputFormats.put(bioType.getString("uri"));
					}
					apeInput.put("format_1915", apeInputFormats);

					apeInputs.put(apeInput);
				}
				apeJsonTool.put(ToolAnnotationTag.INPUTS.toString(), apeInputs);

				// reading outputs
				JSONArray apeOutputs = new JSONArray();
				JSONArray bioOutputs = function.getJSONArray("output");
				// for each output
				for (int j = 0; j < bioOutputs.length(); j++) {

					JSONObject bioOutput = bioOutputs.getJSONObject(j);
					JSONObject apeOutput = new JSONObject();
					JSONArray apeOutputTypes = new JSONArray();
					JSONArray apeOutputFormats = new JSONArray();
					// add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "data", JSONObject.class)) {
						apeOutputTypes.put(bioType.getString("uri"));
					}
					apeOutput.put("data_0006", apeOutputTypes);
					// add all data formats
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "format", JSONObject.class)) {
						apeOutputFormats.put(bioType.getString("uri"));
					}
					apeOutput.put("format_1915", apeOutputFormats);

					apeOutputs.put(apeOutput);
				}
				apeJsonTool.put(ToolAnnotationTag.OUTPUTS.toString(), apeOutputs);

				apeToolsAnnotations.put(apeJsonTool);
			}
		}

		return new JSONObject().put("functions", apeToolsAnnotations);
	}


	/**
	 * Method converts tools annotated using 'bio.tools' standard (see <a href=
	 * "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 * API</a>), into standard supported by the APE library. It is a strict
	 * conversion, where only tools that have inputs and outputs types and formats
	 * are accepted.
	 * <p>
	 * In practice, the method takes a {@link JSONArray} as an argument, where each
	 * {@link JSONObject} in the array represents a tool annotated using 'bio.tools'
	 * standard, and returns a {@link JSONObject} that represents tool annotations
	 * that can be used by the APE library.
	 *
	 * @param bioToolsAnnotation A {@link JSONArray} object, that contains list of
	 *                           annotated tools ({@link JSONObject}s) according the
	 *                           bio.tools specification (see <a href=
	 *                           "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools
	 *                           API</a>)
	 * @return {@link JSONObject} that represents the tool annotation supported by
	 *         the APE library.
	 * @throws JSONException the json exception
	 */
	public static JSONObject convertBioTools2ApeStrict(JSONArray bioToolsAnnotation) throws JSONException {
		
		Set<String> notAcceptedTools = new HashSet<>();
		Set<String> noFunctionAnnotation = new HashSet<>();
		Set<String> toolsMissingDimension = new HashSet<>();
		Set<String> annotatedBioTools = new HashSet<>();
		int notAcceptedOperations = 0;
		int bioToolFunctions = 0;

		JSONArray apeToolsAnnotations = new JSONArray();
		Set<String> toolsList = new HashSet<>();

		for (int i = 0; i < bioToolsAnnotation.length(); i++) {

			JSONObject bioJsonTool = bioToolsAnnotation.getJSONObject(i);
			List<JSONObject> functions = APEUtils.getListFromJson(bioJsonTool, "function", JSONObject.class);
			if (functions.isEmpty()) {
				noFunctionAnnotation.add(bioJsonTool.getString("biotoolsID"));
				notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
				continue;
			}
			int functionNo = 1;
			functionloop: for (JSONObject function : functions) {
				bioToolFunctions++;
				JSONObject apeJsonTool = new JSONObject();
				apeJsonTool.put("label", bioJsonTool.getString("name"));
				if (functions.size() > 1) {
					apeJsonTool.put("id", bioJsonTool.getString("biotoolsID") + "_op" + (functionNo++));
				} else {
					apeJsonTool.put("id", bioJsonTool.getString("biotoolsID"));
				}

				JSONArray apeTaxonomyTerms = new JSONArray();

				JSONArray operations = function.getJSONArray("operation");
				for (int j = 0; j < operations.length(); j++) {
					JSONObject bioOperation = operations.getJSONObject(j);
					apeTaxonomyTerms.put(bioOperation.get("uri"));
				}
				apeJsonTool.put("taxonomyOperations", apeTaxonomyTerms);
//			reading inputs
				JSONArray apeInputs = new JSONArray();
				JSONArray bioInputs = function.getJSONArray("input");
//			for each input
				for (int j = 0; j < bioInputs.length(); j++) {
					JSONObject bioInput = bioInputs.getJSONObject(j);
					JSONObject apeInput = new JSONObject();
					JSONArray apeInputTypes = new JSONArray();
					JSONArray apeInputFormats = new JSONArray();
//				add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "data", JSONObject.class)) {
						apeInputTypes.put(bioType.getString("uri"));
					}
					if (apeInputTypes.length() == 0) {
						notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
						toolsMissingDimension.add(bioJsonTool.getString("biotoolsID"));
						notAcceptedOperations++;
						continue functionloop;
					}
					apeInput.put("data_0006", apeInputTypes);
//				add all data formats (or just the first one)
					for (JSONObject bioType : APEUtils.getListFromJson(bioInput, "format", JSONObject.class)) {
						apeInputFormats.put(bioType.getString("uri"));
					}
					if (apeInputFormats.length() == 0) {
						notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
						toolsMissingDimension.add(bioJsonTool.getString("biotoolsID"));
						notAcceptedOperations++;
						continue functionloop;
					}
					apeInput.put("format_1915", apeInputFormats);

					apeInputs.put(apeInput);
				}
				apeJsonTool.put("inputs", apeInputs);

//			reading outputs
				JSONArray apeOutputs = new JSONArray();
				JSONArray bioOutputs = function.getJSONArray("output");
//			for each output
				for (int j = 0; j < bioOutputs.length(); j++) {

					JSONObject bioOutput = bioOutputs.getJSONObject(j);
					JSONObject apeOutput = new JSONObject();
					JSONArray apeOutputTypes = new JSONArray();
					JSONArray apeOutputFormats = new JSONArray();
//				add all data types
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "data", JSONObject.class)) {
						apeOutputTypes.put(bioType.getString("uri"));
					}
					if (apeOutputTypes.length() == 0) {
						notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
						toolsMissingDimension.add(bioJsonTool.getString("biotoolsID"));
						notAcceptedOperations++;
						continue functionloop;
					}
					apeOutput.put("data_0006", apeOutputTypes);
//				add all data formats
					for (JSONObject bioType : APEUtils.getListFromJson(bioOutput, "format", JSONObject.class)) {
						apeOutputFormats.put(bioType.getString("uri"));
					}
					if (apeOutputFormats.length() == 0) {
						notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
						toolsMissingDimension.add(bioJsonTool.getString("biotoolsID"));
						notAcceptedOperations++;
						continue functionloop;
					}
					apeOutput.put("format_1915", apeOutputFormats);

					apeOutputs.put(apeOutput);
				}
				apeJsonTool.put("outputs", apeOutputs);

				// if the tool has outputs add it to the tool annotation
				if (apeInputs.length() > 0 && apeOutputs.length() > 0) {
					toolsList.add(bioJsonTool.getString("biotoolsID"));
					apeToolsAnnotations.put(apeJsonTool);
					annotatedBioTools.add(bioJsonTool.getString("biotoolsID"));
				} else {
					notAcceptedTools.add(bioJsonTool.getString("biotoolsID"));
					notAcceptedOperations++;
				}
			}
		}
		log.info("Provide bio.tools: " + bioToolsAnnotation.length());
		log.info("Total bio.tools functions: " + bioToolFunctions);
		log.info("Errored bio.tools functions: " + notAcceptedOperations);
		log.info("No functions: " + noFunctionAnnotation.size());
		log.info("Created APE annotations: " + apeToolsAnnotations.length());

		JSONArray tools = new JSONArray();
		for (String tool : toolsList) {
			tools.put(tool);
		}
		return new JSONObject().put("functions", tools);
	}

}