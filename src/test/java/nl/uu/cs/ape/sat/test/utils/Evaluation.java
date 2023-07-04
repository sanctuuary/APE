package nl.uu.cs.ape.sat.test.utils;

import org.junit.jupiter.api.Assertions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Evaluation {

    private static boolean debugMode = true;

    public static void success(String message, Object... params) {
        if (debugMode) {
            log.info("\u001B[32mSUCCESS: " + String.format(message, params) + "\u001B[0m");
        }
    }

    public static void fail(String message, Object... params) {
        Assertions.fail(String.format(message, params));
    }

    private static void evaluateResult(boolean result, String message, Object... params) {
        if (result) {
            success(message, params);
        } else {
            fail(message, params);
        }
    }

    public void result(boolean result, Object e) {
        evaluateResult(result, (!result || debugMode) ? formatMessage(result, e) : "");
    }

    public void result(boolean result) {
        result(result, null);
    }

    protected abstract String formatMessage(boolean result, Object info);

    public static class TagTypeEvaluation extends Evaluation {

        private String type, tag;
        private Boolean reverse;

        public TagTypeEvaluation(String type, boolean reversed) {
            this.type = type;
            this.reverse = reversed;
        }

        public TagTypeEvaluation forTag(String tag) {
            this.tag = tag;
            return this;
        }

        @Override
        protected String formatMessage(boolean result, Object e) {

            StringBuilder sb = new StringBuilder();

            sb.append("Exception was ");

            if (e == null)
                sb.append("NOT ");

            sb.append("thrown for APECoreConfig with a ");

            if (!reverse && e != null)
                sb.append("non-");

            sb.append(type).append(" value for the ");

            if (reverse)
                sb.append("non-");

            sb.append(type).append(" tag '");

            sb.append(tag).append("'");

            if (e != null) {
                sb.append("\nAPE message was: \u001B[0m").append(e.toString());
            }

            return sb.toString();
        }
    }
}
