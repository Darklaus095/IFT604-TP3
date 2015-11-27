package Common.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Michaël on 11/26/2015.
 */
public class JsonSerializer {

    public static String serialize(Object o){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(o);
    }
}
