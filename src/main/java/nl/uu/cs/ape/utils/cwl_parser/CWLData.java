package nl.uu.cs.ape.utils.cwl_parser;

public class CWLData {
    private String dataFormat;
    private String dataType;
    private String cwlFieldID;


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
