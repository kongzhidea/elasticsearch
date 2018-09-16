package com.kk.opensearch.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.kk.opensearch.model.AddSearchTemplate;
import com.kk.opensearch.model.DeleteSearchTemplate;
import com.kk.opensearch.model.UpdateSearchTemplate;
import com.kk.opensearch.model.search.BaseSearchParam;
import com.kk.opensearch.model.search.Sorted;
import com.kk.opensearch.model.search.enums.Ordered;
import com.kk.opensearch.util.CloudSearchResult;
import com.kk.opensearch.util.JsonUtil;
import com.kk.opensearch.util.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:03
 */
public class CloudSearchClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int RETRY_TIMES = 3;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private OpenSearchClient serviceClient;

    private String appName;

    public CloudSearchClient(String accessKey, String accessSecret, String host, String appName) {
        this.appName = appName;
        OpenSearch openSearch = new OpenSearch(accessKey, accessSecret, host);
        serviceClient = new OpenSearchClient(openSearch);
    }

    public OpenSearchClient getServiceClient() {
        return serviceClient;
    }

    public String getAppName() {
        return appName;
    }

    /**
     * 上传文档，
     * add 若存在则批量更新
     * update 部分字段更新，不支持标准版
     *
     * @param docs      文档内容 ,数据格式为json格式的string。
     *                  例如格式为：[{"cmd":"add","fields":{"field1":"value1",...}},...]
     * @param tableName 推送索引对应的表名
     * @return String
     */
    public String pushDoc(String docs, String tableName) {
        DocumentClient documentClient = new DocumentClient(serviceClient);
        String result = null;
        try {
            int retryTime = 0;
            do {
                OpenSearchResult osr = documentClient.push(docs, appName, tableName);
                result = osr.getResult();
            } while (!CloudSearchResult.isPushSuccess(result) && retryTime++ <= RETRY_TIMES);
            if (!CloudSearchResult.isPushSuccess(result)) {
                logger.error("推送到云搜失败, result:{}, docs:{}, tableName:{} ", new Object[]{result, docs, tableName});
            }
        } catch (Exception e) {
            logger.error(String.format("推送到云搜失败, docs:{}, tableName:{}, ", new Object[]{docs, tableName}) + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 上传文档，若存在则批量更新(全部字段更新)
     *
     * @return
     */
    public <T> String pushDoc(List<T> list, String tableName) {
        String result = "";
        try {
            List<AddSearchTemplate<T>> ysList = new ArrayList<>();
            for (T item : list) {
                ysList.add(new AddSearchTemplate<T>(item));
            }
            String json = JsonUtil.toJsonString(ysList);
            result = pushDoc(json, tableName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }


    /**
     * 更新部分字段，不存在不会自动创建文档。
     *
     * @return
     */
    public <T> String updateDoc(List<T> list, String tableName) {
        String result = "";
        try {
            List<UpdateSearchTemplate<T>> ysList = new ArrayList<>();
            for (T item : list) {
                ysList.add(new UpdateSearchTemplate<T>(item));
            }
            String json = JSON.toJSONString(ysList);
            result = pushDoc(json, tableName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除文档，通过主键删除，需要设置主键字段
     *
     * @return
     */
    public <T> String deleteDoc(List<T> list, String tableName) {
        String result = "";
        try {
            List<DeleteSearchTemplate<T>> ysList = new ArrayList<>();
            for (T item : list) {
                ysList.add(new DeleteSearchTemplate<T>(item));
            }
            String json = JSON.toJSONString(ysList);
            result = pushDoc(json, tableName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 通用搜素，最多反馈5000条数据。
     *
     * @param param
     * @return
     */
    public String search(BaseSearchParam param) {

        Config config = new Config(Arrays.asList(appName));
        config.setStart(param.getPage().getStart());
        config.setHits(param.getPage().getPageSize());
        config.setSearchFormat(SearchFormat.JSON);

        SearchParams searchParams = new SearchParams(config);

        // 设定排序方式,字段必须设定为可过滤
        if (param.getSorted() != null && param.getSorted().size() > 0) {
            Sort sort = new Sort();
            for (Sorted s : param.getSorted()) {
                if (s.getOrdered() == null || s.getOrdered() == Ordered.DECREASE) {
                    sort.addToSortFields(new SortField(s.getField(), Order.DECREASE));
                } else {
                    sort.addToSortFields(new SortField(s.getField(), Order.INCREASE));
                }
            }
            searchParams.setSort(sort);
        }


        String queryParam = SearchHelper.buildQueryParam(param);
        searchParams.setQuery(queryParam);

        String filterParam = SearchHelper.buildFilterParam(param);
        searchParams.setFilter(filterParam);

        System.out.println(searchParams);

        SearcherClient searcherClient = new SearcherClient(serviceClient);

        String result = null;
        try {
            int retryTime = 0;
            do {
                SearchResult searchResult = searcherClient.execute(searchParams);
                result = searchResult.getResult();
            } while (!CloudSearchResult.isSuccess(result) && retryTime++ <= RETRY_TIMES);
            if (!CloudSearchResult.isSuccess(result)) {
                logger.error("OpenseachSearchError, result: {}, params:{}", result, JsonUtil.toJsonString(searchParams));
            }
        } catch (Exception e) {
            logger.error("OpensearchSearchError, params:" + JsonUtil.toJsonString(searchParams), e);
        }
        return result;
    }
}
