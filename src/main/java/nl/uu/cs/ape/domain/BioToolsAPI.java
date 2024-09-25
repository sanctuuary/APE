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

		JSONObject apeToolAnnotation = convertBioTools2Ape(bioToolsRAW, false);
		APEFiles.write2file(apeToolAnnotation.toString(4), new File(destinationFilePath), false);
	}

	/**
	 * Fetch the list of all the tools from the bio.tools API and save them to a
	 * file in a format that can be used by the APE library.
	 * @param destinationFilePath The path to the file where the tool annotations will be saved.
	 * @throws IOException If an error occurs while fetching the tools.
	 */
	public static void fetchBioTools(String destinationFilePath) throws IOException {

		// Fetch the Limited (predefined) set of tool
		JSONObject biotools = BioToolsAPI.getToolsFromEDAMTopic("");
		APEFiles.write2file(biotools.toString(4), new File(destinationFilePath), false);
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
	private static JSONObject getToolsFromDomain(String domainName) throws IOException {
		JSONArray toolAnnotations = null;
		if (!domainName.equals("")) {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?domain=" + domainName + "&format=json");
		} else {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?format=json");
		}
		return convertBioTools2Ape(toolAnnotations, true);
	}

	/**
	 * Retrieve tools from the bio.tools API for a given topic and convert them to
	 * the tool annotation format used by the APE library.
	 * 
	 * @param topicName The name of the topic.
	 * @return The JSONObject containing the tool annotations in the APE format.
	 * 
	 * @throws IOException If an error occurs while fetching the tools.
	 */
	public static JSONObject getToolsFromEDAMTopic(String topicName) throws IOException {
		JSONArray toolAnnotations = null;
		if (!topicName.equals("")) {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?topicID=\"" + topicName + "\"&format=json");
		} else {
			toolAnnotations = fetchToolsFromURI("https://bio.tools/api/t?format=json");
		}

		return convertBioTools2Ape(toolAnnotations, true);
	}

	/**
	 * Send Get request to get tool annotations Saves JSONArray with all the tool
	 * annotations (in tool list)
	 * 
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONArray fetchToolListFromBioTools(JSONArray toolListJson) throws JSONException, IOException {
		JSONArray bioToolAnnotations = new JSONArray();
		for (int i = 0; i < toolListJson.length(); i++) {
			String currTool = toolListJson.getString(i);
			Request request = new Request.Builder().url("https://bio.tools/api/" + currTool + "?format=json").build();
			try (Response response = client.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					log.error("The tool " + currTool + " could not be fetched from bio.tools.");
				}
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
		log.info("Fetching tools from bio.tools: " + url);
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
			log.info("bio.tools: page " + i++ + " fetched.");

		}
		log.info("All tools fetched from a given URL.");
		return bioToolAnnotations;
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
	 *                           "https://biotools.readthedocs.io/en/latest/api_usage_guide.html">bio.tools API</a>)
	 * @param excludeBadAnnotation If set to {@code true}, the method will exclude tools 
	 * that do not have both the input and the output fully specified, i.e., with data and format types and formats specified. If set to {@code false}, the method will return annotations that have at least one of the two fully specified (at least one input or or output).
	 * @return {@link JSONObject} that represents the tool annotation supported by
	 *         the APE library.
	 * @throws JSONException the json exception
	 */
	public static JSONObject convertBioTools2Ape(JSONArray bioToolsAnnotation, boolean excludeBadAnnotation)
			throws JSONException {

		int notAcceptedOperations = 0;
		
		int bioToolFunctions = 0;

		JSONArray apeToolsAnnotations = new JSONArray();

		for (JSONObject bioJsonTool : APEUtils.getJSONListFromJSONArray(bioToolsAnnotation)) {
			List<JSONObject> functions = APEUtils.getJSONListFromJson(bioJsonTool, "function");
			if (functions.isEmpty()) {
				continue;
			}
			int functionNo = 1;

			for (JSONObject function : functions) {
				bioToolFunctions++;
				JSONObject apeJsonTool = new JSONObject();
				apeJsonTool.put("label", bioJsonTool.getString("name"));

				apeJsonTool.put("id",
						bioJsonTool.getString("biotoolsID") +
								(functions.size() > 1 ? "_op" + (functionNo++) : ""));

				JSONArray apeTaxonomyTerms = new JSONArray();

				JSONArray operations = function.getJSONArray("operation");
				for (JSONObject bioOperation : APEUtils.getJSONListFromJSONArray(operations)) {
					apeTaxonomyTerms.put(bioOperation.get("uri"));
				}
				apeJsonTool.put("taxonomyOperations", apeTaxonomyTerms);
				// reading inputs
				JSONArray apeInputs = new JSONArray();
				try {
					apeInputs = calculateBioToolsInputOutput(function.getJSONArray("input"),
							bioJsonTool.getString("biotoolsID"));
					apeJsonTool.put("inputs", apeInputs);
				} catch (BioToolsAnnotationException e) {
					if (excludeBadAnnotation) {
						notAcceptedOperations++;
						continue;
					}
				}
				JSONArray apeOutputs = new JSONArray();
				try {
					apeOutputs = calculateBioToolsInputOutput(function.getJSONArray("output"),
							bioJsonTool.getString("biotoolsID"));
					apeJsonTool.put("outputs", apeOutputs);
				} catch (BioToolsAnnotationException e) {
					if (excludeBadAnnotation) {
						notAcceptedOperations++;
						continue;
					}
				}
				if (!excludeBadAnnotation ||
				(apeInputs.length() > 0 && apeOutputs.length() > 0)) {
					apeJsonTool.put("biotoolsID", bioJsonTool.getString("biotoolsID"));
					apeToolsAnnotations.put(apeJsonTool);
				}
			}
		}
		log.info("Provided bio.tools: " + bioToolsAnnotation.length());
		log.info("Total bio.tools functions: " + bioToolFunctions);
		log.info("Errored bio.tools functions: " + notAcceptedOperations);
		log.info("Created APE annotations: " + apeToolsAnnotations.length());

		return new JSONObject().put("functions", apeToolsAnnotations);
	}




	
	/**
	 * Method converts input and output tool annotations, following bio.tools schema,
	 * into the APE library tool annotation format.
	 * @param bioInputs - JSONArray with the bio.tools input/output annotations.
	 * @param toolID - ID of the tool that is being converted.
	 * 
	 * @return JSONArray with the APE library input/output annotations.
	 */
	private static JSONArray calculateBioToolsInputOutput(JSONArray bioInputs, String toolID) {
		JSONArray apeInputs = new JSONArray();
		for (JSONObject bioInput : APEUtils.getJSONListFromJSONArray(bioInputs)) {
			JSONObject apeInput = new JSONObject();
			JSONArray apeInputTypes = new JSONArray();
			JSONArray apeInputFormats = new JSONArray();
			// add all data types
			for (JSONObject bioType : APEUtils.getJSONListFromJson(bioInput, "data")) {
				apeInputTypes.put(bioType.getString("uri"));
			}
			if (apeInputTypes.length() == 0) {
				throw BioToolsAnnotationException.notExistingType(toolID);
			}
			apeInput.put("data_0006", apeInputTypes);
			// add all data formats (or just the first one)
			for (JSONObject bioType : APEUtils.getJSONListFromJson(bioInput, "format")) {
				apeInputFormats.put(bioType.getString("uri"));
			}
			if (apeInputFormats.length() == 0) {
				throw BioToolsAnnotationException.notExistingFormat(toolID);
			}
			apeInput.put("format_1915", apeInputFormats);

			apeInputs.put(apeInput);

		}
		return apeInputs;
	}

}