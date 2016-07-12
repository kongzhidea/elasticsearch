package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kk.es.log.ConsoleLogger;
import com.kk.es.model.Person;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;


/**
 * suggestion
 */
public class SuggestionTest {

    ConsoleLogger logger = new ConsoleLogger();

    // java client 会创建一个连接池，所以用完后不要关闭，下次可以直接复用。采用单例模式，可以保证应用只产生一个实例。
    Client client = null;

    // 获取client实例，连接本地9300端口
    @Before
//    @Ignore
    public void setUpByTransportClient() {
        // 如果 es中配置了 cluster.name， 那么需要设置此setting，否则报 None of the configured nodes are available: [] 异常
        // 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
        // 这样做的好处是一般你不用手动设置集群里所有集群的ip到连接客户端，它会自动帮你添加，并且自动发现新加入集群的机器。
        Map<String, String> m = new HashMap<String, String>(); // 参数 可以放到map中 ， 也可以 直接通过setting.put方法，不用map。
        Settings settings = ImmutableSettings.settingsBuilder().put(m).
                put("cluster.name", "kongzhidea").
                put("client.transport.sniff", true).build();


        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
                "localhost", 9300));

        //如果有 多个节点
//        Client client = new TransportClient()
//                .addTransportAddress(new  InetSocketTransportAddress("host1",
//                        9300))
//                .addTransportAddress(new  InetSocketTransportAddress("host2",
//                        9300));


    }

    @After
    public void setDown() {
        this.client.close();
    }

    // 当索引不存在时候，创建索引
    @Test
    public void testGen() throws IOException {
        String index = "ms";
        String type = "sug";
        CreateIndexResponse response = client.admin().indices()
                .prepareCreate(index)
                .setSettings(ImmutableSettings.settingsBuilder()
                        .put("number_of_shards", 1))
                .addMapping(type, XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject(type)
                        .startObject("properties")
                        .startObject("id").field("type", "long").field("store", "yes").field("index", "not_analyzed").endObject()
                        .startObject("name").field("type", "string").field("store", "no").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                        .startObject("suggest").field("type", "completion").field("index_analyzer", "simple").field("search_analyzer", "simple").field("payloads", "true").endObject()
                        .endObject()
                        .endObject()
                        .endObject())
                .execute().actionGet();
    }

    // 当索引存在时候，更新索引， 当类型不存在时候会自动创建类型。
    @Test
    public void testGenUpdate() throws IOException {
        String index = "ms";
        String type = "sug2";
        PutMappingResponse response = client.admin().indices()
                .preparePutMapping(index)
                .setType(type)
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject(type)
                        .startObject("properties")
                        .startObject("id").field("type", "long").field("store", "yes").field("index", "not_analyzed").endObject()
                        .startObject("name").field("type", "string").field("store", "no").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                        .startObject("suggest").field("type", "completion").field("index_analyzer", "simple").field("search_analyzer", "simple").field("payloads", "true").endObject()
                        .endObject()
                        .endObject()
                        .endObject())
                .execute().actionGet();
    }

    // 生成一个索引
    @Test
    public void testAddDocument() {
        String index = "ms";
        String type = "sug2";
        Map<String, Object> mat = new HashMap<String, Object>();
        int id = 4;
        mat.put("id", id);
        mat.put("name", "老狼-haha");
        mat.put("suggest", "老狼-哈哈流浪歌手的请人");

        //prepareIndex 参数   index(对应 数据库)  type(对应 表)  id(对应 主键)
        IndexResponse response = this.client
                .prepareIndex(index, type, "" + id).setSource(mat)
                .execute().actionGet();

    }

    @Test
    public void testSuggestion() {
        String index = "ms";
        String field = "suggest";
        List<String> list = getCompletionSuggest(index, field, "老狼-哈");
        System.out.println(StringUtils.join(list, "\n"));

        System.out.println("...............");
        List<String> list2 = getCompletionSuggestNew(index, field, "老狼-哈");
        System.out.println(StringUtils.join(list2, "\n"));
    }

    /**
     * 搜索建议，自动补全搜索结结果
     * <p/>
     * 查询的是 全部type
     */
    public List<String> getCompletionSuggestNew(String indices,
                                                String field,
                                                String prefix) {
        CompletionSuggestionBuilder suggestionsBuilder = new CompletionSuggestionBuilder(
                "complete");
        suggestionsBuilder.text(prefix);
        suggestionsBuilder.field(field);
        suggestionsBuilder.size(10); // size!!!
        SuggestResponse resp = client.prepareSuggest(indices)
                .addSuggestion(suggestionsBuilder).execute().actionGet();
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> list = resp.getSuggest()
                .getSuggestion("complete").getEntries();
        List<String> suggests = new ArrayList<String>();
        if (list == null) {
            return null;
        } else {
            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> e : list) {
                CompletionSuggestion.Entry entry = (CompletionSuggestion.Entry) e;
                for (CompletionSuggestion.Entry.Option option : entry) {
                    suggests.add(option.getText().toString());
                }
            }
            return suggests;
        }
    }

    /**
     * 搜索建议，自动补全搜索结结果
     * <p/>
     * 查询的是 全部type
     */
    public List<String> getCompletionSuggest(String indices,
                                             String field,
                                             String prefix) {
        CompletionSuggestionBuilder suggestionsBuilder = new CompletionSuggestionBuilder(
                "complete");
        suggestionsBuilder.text(prefix);
        suggestionsBuilder.field(field);
        suggestionsBuilder.size(10); // size!!!
        SuggestResponse resp = client.prepareSuggest(indices)
                .addSuggestion(suggestionsBuilder).execute().actionGet();
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> list = resp.getSuggest()
                .getSuggestion("complete").getEntries();
        List<String> suggests = new ArrayList<String>();
        if (list == null) {
            return null;
        } else {
            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> e : list) {
                for (Suggest.Suggestion.Entry.Option option : e) {
                    suggests.add(option.getText().toString());
                }
            }
            return suggests;
        }
    }
}
