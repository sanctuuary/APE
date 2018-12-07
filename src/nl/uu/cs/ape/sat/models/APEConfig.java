package nl.uu.cs.ape.sat.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import nl.uu.cs.ape.sat.StaticFunctions;

/**
 * The {@code APEConfig} (singleton) class is used to define the configuration
 * variables required for the proper execution of the library.
 * 
 * @author Vedran Kasalica
 *
 */
public class APEConfig {

	/**
	 * Singleton instance of the class.
	 */
	private static final APEConfig configAPE = new APEConfig();
	/**
	 * Tags used in the config file
	 */
	private final String CONFIGURATION_FILE = "ape.configuration";
	private final String ONTOLOGY_TAG = "ontology_path";
	private final String MODULE_ONTOLOGY_TAG = "modulesTaxonomyRoot";
	private final String DATA_ONTOLOGY_TAG = "dataTaxonomyRoot";
	private final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";
	private final String CONSTRAINTS_TAG = "constraints_path";
	private final String SOLUTION_TAG = "solutions_path";
	private final String SOLUTIION_MIN_LENGTH_TAG = "solution_min_length";
	private final String PILEPINE_TAG = "pipeline";
	private final String MAX_NO_SOLUTIONS_TAG = "max_solutions";

	/*
	 * Max number of solution that the solver will return.
	 */
	private Integer MAX_NO_SOLUTIONS;

	/**
	 * Path to the taxonomy file
	 */
	private String ONTOLOGY_PATH;

	/**
	 * Nodes in the ontology that correspond to the roots of module and data
	 * taxonomies.
	 */
	private String MODULE_TAXONOMY_ROOT, DATA_TAXONOMY_ROOT;
	
	/**
	 * List of nodes in the ontology that correspond to the roots of data type and data format
	 * taxonomies.
	 */
	private List<String> DATA_SUB_ROOTS = new ArrayList<>();

	private String TOOL_ANNOTATIONS_PATH;
	/**
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	private String SOLUTION_PATH;

	/**
	 * Path to the file with all workflow constraints.
	 */
	private String CONSTRAINTS_PATH;
	/**
	 * Length of the solutions (length of the automaton).
	 */
	private Integer SOLUTIION_MIN_LENGTH;
	/**
	 * Output branching factor (max number of outputs per tool).
	 */
	private Integer MAX_NO_TOOL_OUTPUTS = 3;
	/**
	 * {@code true} if THE pipeline approach should be used, {@code false} in case
	 * of general memory approach.
	 */
	private Boolean PILEPINE;

	/**
	 * Configurations used to read/update the "ape.configuration" file.
	 */

	private Document document;
	private Node configNode;

	/**
	 * Initialize the configuration of the project.
	 */
	private APEConfig() {
		File inputFile = new File(CONFIGURATION_FILE);
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(inputFile);

			configNode = document.selectSingleNode("/configuration");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the singleton class representing the library configuration.
	 * 
	 * @return
	 */
	public static APEConfig getConfig() {
		return configAPE;
	}

	/**
	 * Setting up the configuration of the library.
	 * 
	 * @return {@code true} if the method successfully set-up the configuration,
	 *         {@code false} otherwise.
	 */
	public boolean defaultConfigSetup() {

		ONTOLOGY_PATH = configNode.selectSingleNode(ONTOLOGY_TAG).valueOf("@value");
		if (!StaticFunctions.isValidConfigReadFile(ONTOLOGY_TAG, ONTOLOGY_PATH)) {
			return false;
		}

		MODULE_TAXONOMY_ROOT = configNode.selectSingleNode(MODULE_ONTOLOGY_TAG).valueOf("@value");
		if (MODULE_TAXONOMY_ROOT == null || MODULE_TAXONOMY_ROOT == "") {
			return false;
		}

		Node dataTaxonomy = configNode.selectSingleNode(DATA_ONTOLOGY_TAG);
		DATA_TAXONOMY_ROOT = dataTaxonomy.valueOf("@value");
		if (DATA_TAXONOMY_ROOT == null || DATA_TAXONOMY_ROOT == "") {
			return false;
		}
		
		for(Node dataSubRoots: dataTaxonomy.selectNodes("*")) {
			DATA_SUB_ROOTS.add(dataSubRoots.valueOf("@value"));
		}

		TOOL_ANNOTATIONS_PATH = configNode.selectSingleNode(TOOL_ANNOTATIONS_TAG).valueOf("@value");
		if (!StaticFunctions.isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, TOOL_ANNOTATIONS_PATH)) {
			return false;
		}

		CONSTRAINTS_PATH = configNode.selectSingleNode(CONSTRAINTS_TAG).valueOf("@value");
		if (!StaticFunctions.isValidConfigReadFile(CONSTRAINTS_TAG, CONSTRAINTS_PATH)) {
			return false;
		}

		SOLUTION_PATH = configNode.selectSingleNode(SOLUTION_TAG).valueOf("@value");
		if (!StaticFunctions.isValidConfigWriteFile(SOLUTION_TAG, SOLUTION_PATH)) {
			return false;
		}

		SOLUTIION_MIN_LENGTH = StaticFunctions.isValidConfigInt(SOLUTIION_MIN_LENGTH_TAG,
				configNode.selectSingleNode(SOLUTIION_MIN_LENGTH_TAG).valueOf("@value"));
		if (SOLUTIION_MIN_LENGTH == null) {
			return false;
		}

		MAX_NO_SOLUTIONS = StaticFunctions.isValidConfigInt(MAX_NO_SOLUTIONS_TAG,
				configNode.selectSingleNode(MAX_NO_SOLUTIONS_TAG).valueOf("@value"));
		if (MAX_NO_SOLUTIONS == null) {
			return false;
		}

		PILEPINE = StaticFunctions.isValidConfigBoolean(PILEPINE_TAG,
				configNode.selectSingleNode(PILEPINE_TAG).valueOf("@value"));
		if (PILEPINE == null) {
			return false;
		}
		return true;
	}

	public Integer getMAX_NO_SOLUTIONS() {
		return MAX_NO_SOLUTIONS;
	}

	public void setMAX_NO_SOLUTIONS(Integer mAX_NO_SOLUTIONS) {
		MAX_NO_SOLUTIONS = mAX_NO_SOLUTIONS;
	}

	public String getONTOLOGY_PATH() {
		return ONTOLOGY_PATH;
	}

	public void setONTOLOGY_PATH(String oNTOLOGY_PATH) {
		ONTOLOGY_PATH = oNTOLOGY_PATH;
	}

	public String getMODULE_TAXONOMY_ROOT() {
		return MODULE_TAXONOMY_ROOT;
	}

	public void setMODULE_TAXONOMY_ROOT(String mODULE_TAXONOMY_ROOT) {
		MODULE_TAXONOMY_ROOT = mODULE_TAXONOMY_ROOT;
	}
	

	public String getDATA_TAXONOMY_ROOT() {
		return DATA_TAXONOMY_ROOT;
	}

	public void setDATA_TAXONOMY_ROOT(String dATA_TAXONOMY_ROOT) {
		DATA_TAXONOMY_ROOT = dATA_TAXONOMY_ROOT;
	}

	public List<String> getData_Taxonomy_SubRoots() {
		return DATA_SUB_ROOTS;
	}

	public String getTOOL_ANNOTATIONS_PATH() {
		return TOOL_ANNOTATIONS_PATH;
	}

	public void setTOOL_ANNOTATIONS_PATH(String tOOL_ANNOTATIONS_PATH) {
		TOOL_ANNOTATIONS_PATH = tOOL_ANNOTATIONS_PATH;
	}

	public String getSOLUTION_PATH() {
		return SOLUTION_PATH;
	}

	public void setSOLUTION_PATH(String sOLUTION_PATH) {
		SOLUTION_PATH = sOLUTION_PATH;
	}

	public String getCONSTRAINTS_PATH() {
		return CONSTRAINTS_PATH;
	}

	public void setCONSTRAINTS_PATH(String cONSTRAINTS_PATH) {
		CONSTRAINTS_PATH = cONSTRAINTS_PATH;
	}

	public Integer getSOLUTIION_MIN_LENGTH() {
		return SOLUTIION_MIN_LENGTH;
	}

	public void setSOLUTIION_MIN_LENGTH(Integer sOLUTIION_MIN_LENGTH) {
		SOLUTIION_MIN_LENGTH = sOLUTIION_MIN_LENGTH;
	}

	public Integer getMAX_NO_TOOL_OUTPUTS() {
		return MAX_NO_TOOL_OUTPUTS;
	}

	public void setMAX_NO_TOOL_OUTPUTS(Integer mAX_NO_TOOL_OUTPUTS) {
		MAX_NO_TOOL_OUTPUTS = mAX_NO_TOOL_OUTPUTS;
	}

	public Boolean getPILEPINE() {
		return PILEPINE;
	}

	public void setPILEPINE(Boolean pILEPINE) {
		PILEPINE = pILEPINE;
	}

	public String getConfigurationFile() {
		return CONFIGURATION_FILE;
	}

}
