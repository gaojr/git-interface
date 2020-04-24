package cn.gjr.gitinterface.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * json util
 *
 * @author GaoJunru
 */
public final class JsonUtil {
    /**
     * gson构造器
     * <ul>
     *     <li>根据Expose注解过滤成员</li>
     *     <li>序列化时输出null</li>
     * </ul>
     */
    private static final GsonBuilder BUILDER = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls();

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
        return BUILDER.create().fromJson(string, token.getType());
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

    /**
     * 对象list -> json数组
     *
     * @param list 列表
     * @param token token
     * @param <T> 类型
     * @return json数组
     */
    public static <T> JsonArray list2Array(List<T> list, TypeToken<List<T>> token) {
        return BUILDER.create().toJsonTree(list, token.getType()).getAsJsonArray();
    }

    /**
     * json数组 -> json字符串
     *
     * @param array json数组
     * @return json字符串
     */
    public static String array2String(JsonArray array) {
        return array == null ? "[]" : array.toString();
    }
}
