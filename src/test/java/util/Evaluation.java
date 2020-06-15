package util;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class Evaluation {

    private static boolean debugMode = true;

    public static void setDebug(boolean debug){
        debugMode = debug;
    }

    public void result(boolean result, Exception e){

        String message = "";

        if(!result || debugMode){
            message = formatMessage(result, e);
        }

        evaluateResult(result, message);
    }

    public void result(boolean result){
        result(result,null);
    }

    protected abstract String formatMessage(boolean result, Exception e);

    private static void evaluateResult(boolean result, String message){

        if(!result){
            fail(message);
        }
        else if(debugMode){
            System.out.println("\u001B[32mSUCCESS:\u001B[0m " + message);
        }
    }
}
