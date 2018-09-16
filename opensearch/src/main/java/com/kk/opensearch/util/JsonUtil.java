package com.kk.opensearch.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {

    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
    }

    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty, // 字符类型字段如果为null，输出为""，而不是null
    };

    /**
     * 适用于全量更新，不能使用于更新部分字段
     *
     * 更新部分字段直接使用：JSON.toJSONString()
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj, config, features);
    }

    public static JSONObject parseJSONObject(String str) {
        return JSON.parseObject(str);
    }

    public static boolean isJson(String str) {
        try {
            return parseJSONObject(str) != null;
        } catch (Exception e) {
        }

        return false;
    }

    public static <T> T parse(String jsonData, Class<? extends T> objClass) {
        return JSON.parseObject(jsonData, objClass);
    }
}
