package com.kk.opensearch.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class CloudSearchResult {
    private static final Logger log = LoggerFactory.getLogger(CloudSearchResult.class);

    public static boolean isSuccess(String result) {
        result = StringUtils.trimToEmpty(result);
        boolean success = false;
        try {
            JSONObject jo = null;
            if ((jo = JSON.parseObject(result)) != null) {                
                success = "OK".equals(jo.get("status"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return success;
    }

    public static boolean isPushSuccess(String result) {
        return "true".equalsIgnoreCase(result);
    }
}
