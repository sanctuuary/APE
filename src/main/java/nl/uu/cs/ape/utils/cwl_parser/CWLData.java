package nl.uu.cs.ape.utils.cwl_parser;

public class CWLData {
    private String dataFormat;
    private String dataType;
    private String cwlFieldID;
    public static final String DATA_ROOT = "format_1915";
    public static final String FORMAT_ROOT = "data_0006";
    


    public CWLData(String dataFormat, String dataType, String cwlFieldID) {
        this.dataFormat = dataFormat;
        this.dataType = dataType;
        this.cwlFieldID = cwlFieldID;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCwlFieldID() {
        return cwlFieldID;
    }

    
}
