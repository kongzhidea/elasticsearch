package test;

import com.kk.es.log.ConsoleLogger;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequestBuilder;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 模板
 */
public class TemplateTest {

    ConsoleLogger logger = new ConsoleLogger();

    // java client 会创建一个连接池，所以用完后不要关闭，下次可以直接复用。采用单例模式，可以保证应用只产生一个实例。
    Client client = null;

    // 获取client实例，连接本地9300端口
    @Before
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


    }

    @After
    public void setDown() {
        this.client.close();
    }

    // 创建模板
    @Test
    public void testCreateTemplate() throws IOException {
        String type = "blog";
        String templateName = "content_*";
        String templateMatch = "content_tpl";
        PutIndexTemplateResponse response = client.admin().indices()
                .preparePutTemplate(templateMatch)
                .setTemplate(templateName)
                .setSettings(ImmutableSettings.builder()
                        .put("index.number_of_shards", 5)
                        .put("index.number_of_replicas", 1))
                .addMapping(type, XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject(type)
                        .startObject("properties")
                        .startObject("id").field("type", "long").endObject()
                        .startObject("title").field("analyzer", "ik_smart").field("type", "string").endObject()
                        .startObject("description").field("type", "string").field("store", "yes").endObject()
                        .endObject()
                        .endObject()
                        .endObject())
                .execute().actionGet();
    }


    // 获取模板
    @Test
    public void testGetTemplate() {
        String templateName = "content_*";

        GetIndexTemplatesResponse response = client.admin().indices().getTemplates(new GetIndexTemplatesRequest(templateName))
                .actionGet();

        if (response.getIndexTemplates() != null) {
            for (IndexTemplateMetaData tmp : response.getIndexTemplates()) {
                logger.info("name=" + tmp.getTemplate()); // content_*
                logger.info("mapping=" + tmp.getMappings()); //  properties
            }
        }
    }

    // 删除模板
    @Test
    public void testDeleteTemplate() {
        String templateMatch = "content_tpl";
        DeleteIndexTemplateResponse response = client.admin().indices()
                .prepareDeleteTemplate(templateMatch)
                .execute().actionGet();
    }


    // 生成一个索引
    @Test
    public void testAdd() {
        String index = "1content_my";
        String type = "blog";

        int id = 1;
        Map<String, Object> mat = new HashMap<String, Object>();
        mat.put("id", id);
        mat.put("title", "java很强大");
        mat.put("description", "i like java");

        //prepareIndex 参数   index(对应 数据库)  type(对应 表)  id(对应 主键)
        IndexResponse response = this.client
                .prepareIndex(index, type, "" + id).setSource(mat)
                .execute().actionGet();

    }
}
