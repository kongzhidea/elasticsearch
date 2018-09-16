package com.kk.test.operator;

import com.kk.opensearch.service.CloudSearchClient;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:41
 */
public class BaseMode {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static String accessKey;

    private static String accessSecret;

    // 应用名称
    private static String indexName;

    // 日常使用
    private static String hosts = "http://opensearch-cn-corp.aliyuncs.com";

    // 线上使用
//    private static String hosts = "http://opensearch-cn-internal.aliyuncs.com";

    static {
        accessKey = System.getProperty("accessKey");
        accessSecret = System.getProperty("accessSecret");
        indexName = System.getProperty("indexName");
    }

    protected CloudSearchClient client;
    protected String tableName = "order";

    @Before
    public void init() {
        client = new CloudSearchClient(accessKey, accessSecret, hosts, indexName);
    }

}
