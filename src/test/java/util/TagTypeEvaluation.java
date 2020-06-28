package util;

public class TagTypeEvaluation extends Evaluation {

    private String type, tag;
    private Boolean reverse = false;

    public TagTypeEvaluation(String type, boolean reversed){
        this.type = type;
        this.reverse = reversed;
    }

    public TagTypeEvaluation forTag(String tag){
        this.tag = tag;
        return this;
    }

    @Override
    protected String formatMessage(boolean result, Exception e) {

        StringBuilder sb = new StringBuilder();

        sb.append("Exception was ");

        if(e == null)
            sb.append("NOT ");

        sb.append("thrown for APECoreConfig with a ");

        if(!reverse && e != null)
            sb.append("non-");

        sb.append(type).append(" value for the ");

        if(reverse)
            sb.append("non-");

        sb.append(type).append(" tag '");

        sb.append(tag).append("'");

        if(e != null){
            sb.append("\nAPE message was:").append(e.getMessage());
        }

        return sb.toString();
    }
}
