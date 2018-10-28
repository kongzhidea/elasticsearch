package com.kk.es.service;


import com.kk.es.model.BaseModel;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.JSON;
import com.kk.es.model.search.BaseSearchParam;
import com.kk.es.model.search.Sorted;
import com.kk.es.model.search.enums.Ordered;
import com.kk.es.util.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:03
 */
public class ElasticSearchClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int RETRY_TIMES = 3;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private Client client;

    // database
    private String indexName;

    // hosts 分号隔开，例如 192.168.1.1:9300;192.1658.1.2:9300
    public ElasticSearchClient(String clusterName, String hosts, String indexName) {
        this(clusterName, Arrays.asList(StringUtils.split(hosts, ";")), indexName);
    }

    public ElasticSearchClient(String clusterName, List<String> hosts, String indexName) {
        this.indexName = indexName;
        // 如果 es中配置了 cluster.name， 那么需要设置此setting，否则报 None of the configured nodes are available: [] 异常
        // 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
        // 这样做的好处是一般你不用手动设置集群里所有集群的ip到连接客户端，它会自动帮你添加，并且自动发现新加入集群的机器。
        Settings settings = ImmutableSettings.settingsBuilder().
                put("cluster.name", clusterName).
                put("client.transport.sniff", true).build();


        client = new TransportClient(settings);
        for (String host : hosts) {
            String[] conts = StringUtils.split(host, ":");
            String ip = conts[0];
            int port = Integer.valueOf(conts[1]);
            ((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(ip, port));
        }
    }

    public Client getClient() {
        return client;
    }

//    /**
//     * 更新部分字段，不存在不会自动创建文档。
//     *
//     * @return
//     */
//    public <T> String updateDoc(List<T> list, String tableName) {
//        String result = "";
//        try {
//            List<UpdateSearchTemplate<T>> ysList = new ArrayList<>();
//            for (T item : list) {
//                ysList.add(new UpdateSearchTemplate<T>(item));
//            }
//            String json = JSON.toJSONString(ysList);
//            result = pushDoc(json, tableName);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        return result;
//    }


    /**
     * 上传文档，若存在则批量更新(全部字段更新)
     */
    public <T extends BaseModel> boolean batchInsert(List<T> list, String tableName) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (T item : list) {
            try {
                IndexRequestBuilder requestBuilder = client.prepareIndex();
                requestBuilder.setIndex(indexName);
                requestBuilder.setType(tableName);
//                requestBuilder.setRefresh(true);
                requestBuilder.setId(String.valueOf(item.getId()));

                requestBuilder.setSource(JSON.toJSONString(item));

                bulkRequest.add(requestBuilder);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        boolean isFail = bulkResponse.hasFailures();

        if (isFail) {
            logger.info("push indexes failure. message:" + bulkResponse.buildFailureMessage());
        }
        bulkRequest.request().requests().clear();
        return !isFail;
    }

    /**
     * 上传文档，若存在则批量更新(全部字段更新)
     */
    public <T extends BaseModel> void insert(T item, String tableName) {
        String id = String.valueOf(item.getId());
        IndexResponse response = this.client
                .prepareIndex(indexName, tableName, id).setSource(JSON.toJSONString(item))
                .execute().actionGet();

//        IndexRequest request = new IndexRequest(indexName, tableName, id);
//        request.source(JSON.toJSONString(item));
//        this.client.index(request);
    }


    /**
     * 更新部分文档，若列表中某文档不存在则返回false，不影响其他文档更新。
     */
    public <T extends BaseModel> boolean batchUpdate(List<T> list, String tableName) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (T item : list) {
            try {
                UpdateRequestBuilder requestBuilder = client.prepareUpdate();
                requestBuilder.setIndex(indexName);
                requestBuilder.setType(tableName);
//                requestBuilder.setRefresh(true);
                requestBuilder.setId(String.valueOf(item.getId()));

                requestBuilder.setDoc(JSON.toJSONString(item));

                bulkRequest.add(requestBuilder);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        boolean isFail = bulkResponse.hasFailures();

        if (isFail) {
            logger.info("update indexes failure. message:" + bulkResponse.buildFailureMessage());
        }
        bulkRequest.request().requests().clear();
        return !isFail;
    }

    /**
     * 更新部分文档，文档不存在则抛出异常。
     */
    public <T extends BaseModel> void update(T item, String tableName) {
        String id = String.valueOf(item.getId());
        UpdateResponse response = this.client
                .prepareUpdate(indexName, tableName, id).setDoc(JSON.toJSONString(item))
                .execute().actionGet();

//        UpdateRequest request = new UpdateRequest(indexName, tableName, id);
//        request.doc(JSON.toJSONString(item));
//        this.client.update(request);
    }

    /**
     * 根据主键 删除文档。  使用的是es的_id字段。
     * 文档不存在 也会返回true。
     *
     * @return
     */
    public boolean batchDelete(List<String> list, String tableName) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for (String id : list) {
            try {
                DeleteRequestBuilder requestBuilder = client.prepareDelete();
                requestBuilder.setIndex(indexName);
                requestBuilder.setType(tableName);
//                requestBuilder.setRefresh(true);
                requestBuilder.setId(id);

                bulkRequest.add(requestBuilder);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        boolean isFail = bulkResponse.hasFailures();

        if (isFail) {
            logger.info("delete indexes failure. message:" + bulkResponse.buildFailureMessage());
        }
        bulkRequest.request().requests().clear();
        return !isFail;
    }

    /**
     * 根据主键 删除文档。  使用的是es的_id字段。
     * <p>
     * 文档不存在不会抛出异常。
     */
    public void delete(String id, String tableName) {
        DeleteResponse response = this.client
                .prepareDelete(indexName, tableName, id)
                .execute().actionGet();

//        DeleteRequest request = new DeleteRequest(indexName, tableName, id);
//        this.client.delete(request);
    }

    /**
     * 根据主键id查询，即_id字段。
     */
    public SearchHits searchById(String id, String tableName){
        QueryBuilder qb = QueryBuilders.termQuery("_id", id);

        SearchResponse searchResponse = client.prepareSearch(indexName).setTypes(tableName)
                .setQuery(qb)
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        SearchHit[] searchHists = hits.getHits();
//        if (searchHists.length > 0) {
//            for (SearchHit hit : searchHists) {
//                list.add(hit.getSourceAsString());
//            }
//        }
        return hits;
    }

//
//    /**
//     * 通用搜素。
//     *
//     * @param param
//     * @return
//     */
//    public String search(BaseSearchParam param) {
//
//        Config config = new Config(Arrays.asList(appName));
//        config.setStart(param.getPage().getStart());
//        config.setHits(param.getPage().getPageSize());
//        config.setSearchFormat(SearchFormat.JSON);
//
//        SearchParams searchParams = new SearchParams(config);
//
//        // 设定排序方式,字段必须设定为可过滤
//        if (param.getSorted() != null && param.getSorted().size() > 0) {
//            Sort sort = new Sort();
//            for (Sorted s : param.getSorted()) {
//                if (s.getOrdered() == null || s.getOrdered() == Ordered.DECREASE) {
//                    sort.addToSortFields(new SortField(s.getField(), Order.DECREASE));
//                } else {
//                    sort.addToSortFields(new SortField(s.getField(), Order.INCREASE));
//                }
//            }
//            searchParams.setSort(sort);
//        }
//
//
//        String queryParam = SearchHelper.buildQueryParam(param);
//        searchParams.setQuery(queryParam);
//
//        String filterParam = SearchHelper.buildFilterParam(param);
//        searchParams.setFilter(filterParam);
//
//        System.out.println(searchParams);
//
//        SearcherClient searcherClient = new SearcherClient(serviceClient);
//
//        String result = null;
//        try {
//            int retryTime = 0;
//            do {
//                SearchResult searchResult = searcherClient.execute(searchParams);
//                result = searchResult.getResult();
//            } while (!CloudSearchResult.isSuccess(result) && retryTime++ <= RETRY_TIMES);
//            if (!CloudSearchResult.isSuccess(result)) {
//                logger.error("OpenseachSearchError, result: {}, params:{}", result, JsonUtil.toJsonString(searchParams));
//            }
//        } catch (Exception e) {
//            logger.error("OpensearchSearchError, params:" + JsonUtil.toJsonString(searchParams), e);
//        }
//        return result;
//    }
}
