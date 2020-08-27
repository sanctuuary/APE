package nl.uu.cs.ape.sat.utils;

import nl.uu.cs.ape.sat.configuration.APEConfig;
import nl.uu.cs.ape.sat.configuration.APEConfigTag;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class APEUtilsTest {

    @Test
    public void testStaticAttributes(){
        System.out.println(new JSONObject().put("tags", APERunConfig.JSONTagInfo()).toString(2));
        for(APEConfigTag<?> tag : APERunConfig.allTags()){
        }
    }
}
