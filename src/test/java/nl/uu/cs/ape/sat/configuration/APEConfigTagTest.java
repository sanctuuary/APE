package nl.uu.cs.ape.sat.configuration;

import nl.uu.cs.ape.sat.configuration.tags.APEConfigTag;
import nl.uu.cs.ape.sat.utils.APEUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class APEConfigTagTest {

    @Test
    public void test(){

        System.out.println("Test type tags..");

        for(APEConfigTag.Info<?> tag : APERunConfig.getTags().getAll()){

            if(tag.type == APEConfigTag.TagType.INTEGER){
                System.out.printf("Web API shows `%s` box for tag `%s`, with min:`%s` and max:`%s`\n",tag.type, tag.label, tag.constraints.getInt("min"), tag.constraints.getInt("max"));
            }

        }

        System.out.printf("\n### Display all tag info ####\nCORE:\n%s\n\nRUN:\n%s\n",
                APECoreConfig.getTags().toJSON().toString(3),
                APERunConfig.getTags().toJSON().toString(3));
    }
}
