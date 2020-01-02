package cn.gjr.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * json util
 *
 * @author GaoJunru
 */
public final class JsonUtil {
    private JsonUtil() {
    }

    /**
     * json字符串 -> bean对象
     *
     * @param string json字符串
     * @param token token
     * @return bean对象
     */
    public static <T> T string2Bean(String string, TypeToken<T> token) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(string, token.getType());
    }

    /**
     * json字符串 -> json对象
     *
     * @param string json字符串
     * @return json对象
     */
    public static JsonObject string2Json(String string) {
        return JsonParser.parseString(string).getAsJsonObject();
    }
}
