//package test;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.kk.es.log.ConsoleLogger;
//import com.kk.es.model.Person;
//import com.kk.es.util.DateUtil;
//import com.kk.es.util.JsonUtil;
//import org.apache.commons.lang.StringUtils;
//import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.delete.DeleteRequestBuilder;
//import org.elasticsearch.action.delete.DeleteResponse;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.action.update.UpdateResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.Requests;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.XContentFactory;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.node.Node;
//import org.elasticsearch.node.NodeBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.highlight.HighlightField;
//import org.elasticsearch.search.sort.SortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.*;
//
//import java.io.IOException;
//import java.util.*;
//
//
///**
// * 原生的 es客户端
// * <p/>
// * 通过TransportClient这个接口，我们可以不启动节点就可以和es集群进行通信，它需要指定es集群中其
// * 中一台或多台机的ip地址和端口
// */
//public class DocumentTest {
//
//    ConsoleLogger logger = new ConsoleLogger();
//
//    // java client 会创建一个连接池，所以用完后不要关闭，下次可以直接复用。采用单例模式，可以保证应用只产生一个实例。
//    Client client = null;
//
//    // 获取client实例，连接本地9300端口
//    @Before
////    @Ignore
//    public void setUpByTransportClient() {
//        // 如果 es中配置了 cluster.name， 那么需要设置此setting，否则报 None of the configured nodes are available: [] 异常
//        // 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
//        // 这样做的好处是一般你不用手动设置集群里所有集群的ip到连接客户端，它会自动帮你添加，并且自动发现新加入集群的机器。
//        Map<String, String> m = new HashMap<String, String>(); // 参数 可以放到map中 ， 也可以 直接通过setting.put方法，不用map。
//        Settings settings = ImmutableSettings.settingsBuilder().put(m).
//                put("cluster.name", "kongzhidea").
//                put("client.transport.sniff", true).build();
//
//
//        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
//                "localhost", 9300));
//
//        //如果有 多个节点
////         client = new TransportClient(settings)
////                .addTransportAddress(new  InetSocketTransportAddress("localhost",
////                        9300))
////                .addTransportAddress(new  InetSocketTransportAddress("localhost",
////                        9600));
//
//
//    }
//
//    @After
//    public void setDown() {
//        this.client.close();
//    }
//
//    // 批量添加， 对象转json
//    @Test
//    public void testBulkIndex() throws Exception {
//
//        List<IndexRequest> requests = new ArrayList<IndexRequest>();
//
//        List<Person> persons = new ArrayList<Person>();
//
//        for (int i = 0; i < 100; i++) {
//            Person person = new Person();
//            person.setAge(20 + i);
//            person.setId(i);
//            person.setStudent(true);
//            person.setName("张三" + i);
//            person.setSex("男");
//            persons.add(person);
//
//            String index = "user"; // 相当于数据库名
//            String type = "tb_person2"; // 相当于表名
//
//            String json = JSON.toJSONString(person);
//
//            IndexRequest request = client
//                    .prepareIndex(index, type, "" + person.getId()).setSource(json)
//                    .request();
//
//            requests.add(request);
//        }
//
//        BulkRequestBuilder bulkRequest = client.prepareBulk();
//
//        for (IndexRequest request : requests) {
//            bulkRequest.add(request);
//        }
//
//        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
//        if (bulkResponse.hasFailures()) {
//            Assert.fail("批量创建索引错误！");
//        }
//    }
//
//    @Test
//    public void searchPerson() {
//        String index = "user"; // 相当于数据库名
//        String type = "tb_person"; // 相当于表名
//
//        // 匹配全部
////        QueryBuilder qb = QueryBuilders.matchAllQuery();
//        QueryBuilder qb = QueryBuilders.rangeQuery("id").from(0).to(100);
//
//        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type)
//                .setQuery(qb)
//                .execute()
//                .actionGet();
//        SearchHits hits = searchResponse.getHits();
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        SearchHit[] searchHists = hits.getHits();
//        if (searchHists.length > 0) {
//            for (SearchHit hit : searchHists) {
//                Person person = JSONObject.parseObject(hit.getSourceAsString(), Person.class);
//                logger.info(JSON.toJSONString(person));
//            }
//        }
//    }
//
//    // 生成一个索引
//    @Test
//    public void testAdd() {
//        Map<String, Object> mat = new HashMap<String, Object>();
//        mat.put("user", "kk");
//        mat.put("age", 30);
//        mat.put("postDate", new Date());
//        mat.put("message", "trying out Elastic Search");
//
//        //prepareIndex 参数   index(对应 数据库)  type(对应 表)  id(对应 主键)
//        IndexResponse response = this.client
//                .prepareIndex("twitter", "tweet", "2").setSource(mat)
//                .execute().actionGet();
//
//        //  json格式
////        IndexResponse response = this.client
////                .prepareIndex("twitter", "tweet2", "2").setSource(JsonUtil.map2Json(mat))
////                .execute().actionGet();
//
//        // 可通过 http://localhost:9200/twitter/tweet/1 查看此内容
//
//    }
//
//    // 查询索引
//    @Test
//    public void getIndex() {
//        //prepareGet 参数   index  type  id
//        GetResponse response = client.prepareGet("twitter", "tweet", "1")
//                .execute().actionGet();
//        Map<String, Object> rpMap = response.getSource();
//        if (rpMap == null) {
//            logger.info("empty");
//            return;
//        }
//        //索引名称
//        String _index = response.getIndex();
//        // type名称
//        String _type = response.getType();
//        // 文档ID
//        String _id = response.getId();
//        // 索引版本
//        long _version = response.getVersion();
//
//        logger.info("index=" + _index + ",type=" + _type + ",id=" + _id + ",version=" + _version);
//        logger.info("map=" + rpMap);
//
//        logger.info("str=" + response.getSourceAsString()); //  json格式
//    }
//
//    // 搜索， 游标
//    @Test
//    public void searchIndexScroll() {
//        // termQuery 要求 单词不能分割
////        QueryBuilder qb = QueryBuilders.termQuery("user", "kk");
//        QueryBuilder qb = QueryBuilders.termQuery("message", "out");
//
//        // prepareSearch 传入 index， 可以传 多个index
//        // setTypes  设置要查询的类型， 如果不设置，则搜索全部 类型
//        SearchResponse scrollResp = client.prepareSearch("twitter")
//                .setTypes("tweet")
//                .setSearchType(SearchType.SCAN)
//                .addHighlightedField("message")
//                .setHighlighterPreTags("<span style=\"color:red\">")
//                .setHighlighterPostTags("</span>")
//                .setScroll(new TimeValue(60000))
//                .setQuery(qb)
//                .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll
//        //Scroll until no hits are returned
//        while (true) {
//            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
//            for (SearchHit hit : scrollResp.getHits()) {
//                logger.info(hit.getId() + "," + hit.getSource());
//
//                // 高亮显示
//                Map<String, HighlightField> result = hit.getHighlightFields();
//                HighlightField titleField = result.get("message");
//                Text[] titleTexts = titleField.fragments();
//                String msg = "";
//
//                for (Text text : titleTexts) {
//                    msg += text;
//                }
//                logger.info("highlight.message=" + msg);
//            }
//            //Break condition: No hits are returned
//            if (scrollResp.getHits().hits().length == 0) {
//                break;
//            }
//        }
//    }
//
//    // termQuery
//    @Test
//    public void searchIndex() {
//        QueryBuilder qb = QueryBuilders.termQuery("message", "out");
//        List<String> list = searcher(qb, "twitter", "tweet");
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    // query， filter
//    @Test
//    public void searchIndexByFilter() {
//        QueryBuilder qb = QueryBuilders.termQuery("message", "out");
//        // rangeFilter  过滤器，  from  to 默认均包含左右边界。  includeLower和includeUpper 设置左右边界 默认true。
//        FilterBuilder filterBuilder = FilterBuilders.rangeFilter("age")
//                .from(20).to(30).includeLower(true).includeUpper(true);
//        List<String> list = searcher(qb, filterBuilder, "twitter", "tweet");
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    // query filter -> filterQuery
//    @Test
//    public void searchIndexByFilterQuery() {
//        QueryBuilder qb = QueryBuilders.termQuery("message", "out");
//        // rangeFilter  过滤器，  from  to 默认均包含左右边界。  includeLower和includeUpper 设置左右边界 默认true。
//        FilterBuilder filterBuilder = FilterBuilders.rangeFilter("age")
//                .from(20).to(30).includeLower(true).includeUpper(true);
//        QueryBuilder queryBuilder = QueryBuilders.filteredQuery(qb, filterBuilder);
//        List<String> list = searcher(queryBuilder, "twitter", "tweet");
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    // boolQuery   must, should, mustnot
//    @Test
//    public void searchIndexByBooleanQuery() {
//        QueryBuilder qb = QueryBuilders.boolQuery()
//                .must(QueryBuilders.termQuery("message", "out"))
//                .should(QueryBuilders.termQuery("user", "kongzhidea"));
//
//        List<String> list = searcher(qb, "twitter", "tweet");
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    // RangeQuery
//    @Test
//    public void searchIndexByRangeQuery() {
//        QueryBuilder qb = QueryBuilders.rangeQuery("age").
//                from(21).to(30).includeLower(true).includeUpper(true);
//
//        List<String> list = searcher(qb, "twitter", "tweet");
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    @Test
//    public void searchCountTest() {
//        QueryBuilder qb = QueryBuilders.termQuery("message", "out");
//        long count = searcherCount(qb, "twitter", "tweet");
//    }
//
//    /**
//     * 执行搜索  count
//     *
//     * @param queryBuilder
//     * @param indexname
//     * @param type
//     * @return
//     */
//
//    public long searcherCount(QueryBuilder queryBuilder, String indexname, String type) {
//        List<String> list = new ArrayList<String>();
//        SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type)
//                .setQuery(queryBuilder)
//                .setSearchType(SearchType.COUNT)
//                .execute()
//                .actionGet();
//        SearchHits hits = searchResponse.getHits();
//        // searchType=Count,  hits.getHits() 没有记录，只有totalHits
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        return hits.getTotalHits();
//    }
//
//
//    /**
//     * 执行搜索
//     *
//     * @param queryBuilder
//     * @param indexname
//     * @param type
//     * @return
//     */
//    public List<String> searcher(QueryBuilder queryBuilder, String indexname, String type) {
//        List<String> list = new ArrayList<String>();
//        SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type)
//                .setQuery(queryBuilder)
//                .execute()
//                .actionGet();
//        SearchHits hits = searchResponse.getHits();
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        SearchHit[] searchHists = hits.getHits();
//        if (searchHists.length > 0) {
//            for (SearchHit hit : searchHists) {
//                list.add(hit.getSourceAsString());
//            }
//        }
//        return list;
//    }
//
//    /**
//     * 执行搜索
//     *
//     * @param queryBuilder
//     * @param indexname
//     * @param type
//     * @return
//     */
//    public List<String> searcher(QueryBuilder queryBuilder, FilterBuilder filterBuilder, String indexname, String type) {
//        List<String> list = new ArrayList<String>();
//        SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type)
//                .setQuery(queryBuilder)
//                .setPostFilter(filterBuilder)
//                .execute()
//                .actionGet();
//        SearchHits hits = searchResponse.getHits();
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        SearchHit[] searchHists = hits.getHits();
//        if (searchHists.length > 0) {
//            for (SearchHit hit : searchHists) {
//                list.add(hit.getSourceAsString());
//            }
//        }
//        return list;
//    }
//
//    // 异步 删除索引
//    @Test
//    public void deleteIndex() {
//        // prepareDelete 参数   index  type  id
//        DeleteResponse response = client.prepareDelete("twitter", "tweet2", "2")
//                .execute()
//                .actionGet();
//    }
//
//    // 直接 删除索引
//    @Test
//    public void deleteIndexImmedetely() {
//        // prepareDelete 参数   index  type  id
//        String index = "user"; // 相当于数据库名
//        String type = "tb_person"; // 相当于表名
//        DeleteRequest req = new DeleteRequest(index, type, "5");
//        client.delete(req);
//    }
//
//    // 根据query删除索引
//    @Test
//    public void delbyquery() {
//        QueryBuilder query = QueryBuilders.termQuery("user", "kongzhidea");
//        client.prepareDeleteByQuery("twitter").setTypes("tweet2").setQuery(query).execute().actionGet();
//    }
//
//    // 批量删除
//    @Test
//    public void testDeleteBulk() throws Exception {
//
//        List<DeleteRequest> requests = new ArrayList<DeleteRequest>();
//
//        String index = "user"; // 相当于数据库名
//        String type = "tb_person2"; // 相当于表名
//
//
//        for (int i = 30; i < 40; i++) {
//            DeleteRequest req = new DeleteRequest(index, type, "" + i);
//
//            requests.add(req);
//        }
//
//        BulkRequestBuilder bulkRequest = client.prepareBulk();
//
//        for (DeleteRequest request : requests) {
//            bulkRequest.add(request);
//        }
//
//        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
//        if (bulkResponse.hasFailures()) {
//            Assert.fail("批量删除索引错误！");
//        }
//    }
//
//
//    //sort
//    @Test
//    public void searchIndexOrderById() {
//        QueryBuilder qb = QueryBuilders.termQuery("name", "张");
//
//        SortBuilder sortBuilder = SortBuilders.fieldSort("id").order(SortOrder.ASC);
//
//        List<String> list = new ArrayList<String>();
//        SearchResponse searchResponse = client.prepareSearch("user").setTypes("tb_person")
//                .setQuery(qb)
//                .addSort(sortBuilder)
//                .execute()
//                .actionGet();
//        SearchHits hits = searchResponse.getHits();
//        logger.info("查询到记录数=" + hits.getTotalHits());
//        SearchHit[] searchHists = hits.getHits();
//        if (searchHists.length > 0) {
//            for (SearchHit hit : searchHists) {
//                list.add(hit.getSourceAsString());
//            }
//        }
//
//        logger.info(StringUtils.join(list, "\n"));
//    }
//
//    @Test
//    public void testJson() {
//        QueryBuilder query = QueryBuilders.matchQuery("firmName", "清泉");
//        FilterBuilder stationFilter = FilterBuilders.termFilter("stationId", 5);
//        FilterBuilder staffFilter = FilterBuilders.termFilter("staffId", 936);
//        query = QueryBuilders.filteredQuery(query, FilterBuilders.andFilter(stationFilter).add(staffFilter));
//        SortBuilder dealSorter = SortBuilders.fieldSort("dealCount").order(SortOrder.ASC);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        String json = searchSourceBuilder.query(query).toString();
//        System.out.println(json);
//    }
//
//    @Test
//    public void testJson2() {
//        QueryBuilder query = QueryBuilders.matchQuery("name", "张 9");
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        String json = searchSourceBuilder.query(query).toString();
//        System.out.println(json);
//    }
//}
