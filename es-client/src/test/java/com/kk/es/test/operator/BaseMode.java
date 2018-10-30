package com.kk.es.test.operator;

import com.kk.es.service.ElasticSearchClient;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:41
 */
public class BaseMode {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static String clusterName = "elasticsearch";

    // database
    private static String indexName = "workorder";

    // 日常使用
    private static String hosts = "192.168.0.100:9300";

    protected ElasticSearchClient client;
    protected String tableName = "order";

    @Before
    public void init() {
        client = new ElasticSearchClient(clusterName, hosts, indexName);
    }

}
