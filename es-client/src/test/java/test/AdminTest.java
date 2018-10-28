//package test;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.kk.es.log.ConsoleLogger;
//import com.kk.es.model.Person;
//import org.apache.commons.lang.StringUtils;
//import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
//import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
//import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
//import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
//import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
//import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.delete.DeleteResponse;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.common.xcontent.XContentFactory;
//import org.elasticsearch.index.query.FilterBuilder;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.highlight.HighlightField;
//import org.elasticsearch.search.sort.SortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.*;
//
//
///**
// * admin接口测试
// */
//public class AdminTest {
//
//    ConsoleLogger logger = new ConsoleLogger();
//
//    // java client 会创建一个连接池，所以用完后不要关闭，下次可以直接复用。采用单例模式，可以保证应用只产生一个实例。
//    Client client = null;
//
//    // 获取client实例，连接本地9300端口
//    @Before
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
//
//    }
//
//    @After
//    public void setDown() {
//        this.client.close();
//    }
//
//    // 设置别名
//    @Test
//    public void testSetAlias() {
//        String oriIndex = "twitter";
//        String alias = "tw";
//        IndicesAliasesResponse response = client.admin().indices()
//                .prepareAliases()
//                .addAlias(oriIndex, alias)
//                .execute().actionGet();
//    }
//
//    // 获取别名对应的 index
//    @Test
//    public void testGetAlias() {
//        String alias = "tw";
//        GetAliasesResponse response = client.admin().indices()
//                .prepareGetAliases(alias)
//                .execute().actionGet();
//        logger.info(response.getAliases());
//    }
//
//    // 查询别名是否存在
//    @Test
//    public void testExistsAlias() {
//        String alias = "tw";
//        AliasesExistResponse response = client.admin().indices()
//                .prepareAliasesExist(alias)
//                .execute().actionGet();
//        logger.info(response.exists());
//    }
//
//    // 检查 index是否存在，  别名也认为是true。
//    @Test
//    public void testExistsIndex() {
//        String alias = "tw";
//        IndicesExistsResponse response = client.admin().indices()
//                .prepareExists(alias)
//                .execute().actionGet();
//        logger.info(response.isExists());
//    }
//
//    // 检查 type是否存在，  别名也认为是true。
//    @Test
//    public void testExistsType() {
//        String index = "user";
//        String type = "tb_persondd";
//        TypesExistsResponse response = client.admin().indices()
//                .prepareTypesExists(index)
//                .setTypes(type)
//                .execute().actionGet();
//        logger.info(response.isExists());
//    }
//
//    // 查看索引状态
//    @Test
//    public void testStatus() {
//        String index = "user";
//        String type = "tb_person";
//        IndicesStatsResponse response = client.admin().indices()
//                .prepareStats(index)
//                .setTypes(type)   // 指定type， 可以传多个
////                .all()    // 全部type
//                .execute().actionGet();
//        logger.info(response);
//    }
//
//    // 删除index
//    @Test
//    public void testDeleteIndex() {
//        String index = "twitter2";
//        DeleteIndexResponse response = client.admin().indices()
//                .prepareDelete(index)
//                .execute().actionGet();
//    }
//
//    // 添加索引， 设置字段属性
//    @Test
//    public void addIndex() throws IOException {
//        String index = "mp";
//        String type = "news";
//        CreateIndexResponse response = client.admin().indices()
//                .prepareCreate(index)
//                .setSettings(ImmutableSettings.settingsBuilder()
//                        .put("number_of_shards", 1))
//                .addMapping(type, XContentFactory.jsonBuilder()
//                        .startObject()
//                        .startObject(type)
//                        .startObject("properties")
//                        .startObject("title").field("analyzer", "ik_smart").field("type", "string").endObject()
//                        .startObject("description").field("type", "string").field("store", "yes").endObject()
//                        .startObject("price").field("type", "double").endObject()
//                        .endObject()
//                        .endObject()
//                        .endObject())
//                .execute().actionGet();
//    }
//
//
//    // 生成一个索引
//    @Test
//    public void testAddDocument() {
//        String index = "mp";
//        String type = "news2";
//        Map<String, Object> mat = new HashMap<String, Object>();
//        mat.put("id", 2);
//        mat.put("title", "中国很强大，不怕你们");
//        mat.put("description", "test document 2");
//        mat.put("price", 55);
//
//        //prepareIndex 参数   index(对应 数据库)  type(对应 表)  id(对应 主键)
//        IndexResponse response = this.client
//                .prepareIndex(index, type, "2").setSource(mat)
//                .execute().actionGet();
//
//    }
//
//    // 修改 index，type  的字段属性
//    @Test
//    public void testMapping() throws IOException {
//        String index = "mp";
//        String type = "news2";
//        PutMappingResponse response = client.admin().indices()
//                .preparePutMapping(index)
//                .setType(type)
//                .setSource(XContentFactory.jsonBuilder()
//                        .startObject()
//                        .startObject(type)
//                        .startObject("properties")
//                        .startObject("title").field("analyzer", "ik_smart").field("type", "string").endObject()
//                        .startObject("description").field("type", "string").field("index", "not_analyzed").field("store", "yes").endObject()
//                        .startObject("price").field("type", "double").endObject()
//                        .startObject("id").field("type", "long").endObject()
//                        .startObject("createTime").field("type", "date").endObject()
//                        .endObject()
//                        .endObject()
//                        .endObject())
//                .execute().actionGet();
//    }
//}
