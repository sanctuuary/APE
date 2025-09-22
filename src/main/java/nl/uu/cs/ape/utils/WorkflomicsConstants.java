package nl.uu.cs.ape.utils;

/**
 * Constants for Workflomics repository URLs and paths.
 */
public final class WorkflomicsConstants {
    
    /**
     * Base URL for the Workflomics tools-and-domains repository.
     */
    private static final String WORKFLOMICS_BASE_URL = "https://raw.githubusercontent.com/Workflomics/tools-and-domains";
    
    /**
     * Main branch reference for the repository.
     */
    private static final String MAIN_BRANCH = "main";
    
    /**
     * Bio.tools configuration URL.
     */
    public static final String BIOTOOLS_CONFIG_URL = WORKFLOMICS_BASE_URL + "/refs/heads/" + MAIN_BRANCH + "/domains/non-executable-domains/bio.tools/config.json";
    
    /**
     * Base URL for CWL tools.
     */
    public static final String CWL_TOOLS_BASE_URL = WORKFLOMICS_BASE_URL + "/" + MAIN_BRANCH + "/cwl-tools";
    
    /**
     * Creates a CWL tool URL for a given biotoolsID.
     * 
     * @param biotoolsID the biotools ID
     * @return the complete CWL tool URL
     */
    public static String getCwlToolUrl(String biotoolsID) {
        return String.format("%s/%s/%s.cwl", CWL_TOOLS_BASE_URL, biotoolsID, biotoolsID);
    }
    
    // Private constructor to prevent instantiation
    private WorkflomicsConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}