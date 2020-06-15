package util;

public class TagInfo {

    private String[] tags;
    private String tagType;
    private Object correctExample;
    private Object[] wrongExamples;

    public TagInfo(String tagType, Object correctExample, Object[] wrongExamples, String[] tags) {
        this.tagType = tagType;
        this.tags = tags;
        this.correctExample = correctExample;
        this.wrongExamples = wrongExamples;
    }

    public String getTagType() {
        return tagType;
    }

    public String[] getTags() {
        return tags;
    }

    public Object getCorrectExample() {
        return correctExample;
    }

    public Object[] getWrongExample() {
        return wrongExamples;
    }
}
