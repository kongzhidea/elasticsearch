package com.kk.es.util;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    public static String map2Json(Map<String, Object> map) {
        String jsonData = null;
        try {
            //使用XContentBuilder创建json数据
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            XContentBuilder builder = jsonBuild.startObject();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                builder.field(key, value);
            }
            builder.endObject();
            jsonData = jsonBuild.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    public static String map2Json(Map<String, Object> headerMap, Map<String, Object> footerMap) {
        String jsonData = null;
        try {
            //使用XContentBuilder创建json数据
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            XContentBuilder builder = jsonBuild.startObject();

            XContentBuilder header = builder.startObject("header");
            for (String key : headerMap.keySet()) {
                Object value = headerMap.get(key);
                header.field(key, value);
            }
            header.endObject();

            XContentBuilder footer = builder.startObject("footer");
            for (String key : footerMap.keySet()) {
                Object value = footerMap.get(key);
                footer.field(key, value);
            }
            footer.endObject();


            builder.endObject();
            jsonData = jsonBuild.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }


    public static void main(String[] args) {
        Map<String, Object> mat = new HashMap<String, Object>();
        mat.put("a", "1");
        mat.put("b", "2");
        mat.put("c", "3");
        Map<String, Object> foot = new HashMap<String, Object>();
        foot.put("f", "4");
        System.out.println(map2Json(mat));
        System.out.println(map2Json(mat, foot));
    }

}