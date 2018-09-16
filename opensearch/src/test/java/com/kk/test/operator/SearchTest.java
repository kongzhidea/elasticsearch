package com.kk.test.operator;

import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.aliyun.opensearch.sdk.generated.search.Order;
import com.aliyun.opensearch.sdk.generated.search.Sort;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.aliyun.opensearch.search.SearchParamsBuilder;
import com.kk.opensearch.model.search.*;
import com.kk.opensearch.model.search.enums.Ordered;
import com.kk.opensearch.util.SearchHelper;
import com.kk.test.param.OrderParam;
import org.junit.Test;

import java.util.Arrays;

/**
 * filter中 数值类型不使用引号，字符串使用双引号。
 *
 * query中针对text类型 单引号和双引号有区别，单引号会进行分词，双引号表示keyword不能分开。
 *
 * @author zhihui.kzh
 * @create 3/9/1821:39
 */
public class SearchTest extends BaseMode {

    @Test
    public void testSearch0() {

        Config config = new Config(Arrays.asList(client.getAppName()));
        config.setStart(0);
        config.setHits(20);
        config.setSearchFormat(SearchFormat.JSON);

        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        // 设置查询子句，若需多个索引组合查询，需要setQuery处合并，否则若设置多个setQuery后面的会替换前面查询
//        searchParams.setQuery(" idx_title:'测试' AND idx_keyword:'标题'");

        searchParams.setFilter(" in(id,\"1|2\") ");


        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        sorter.addToSortFields(new SortField("RANK", Order.INCREASE)); //若id相同则以RANK升序
        //添加Sort对象参数
//        searchParams.setSorted(sorter);

        System.out.println(searchParams);

        SearcherClient searcherClient = new SearcherClient(client.getServiceClient());
        try {
            SearchResult searchResult = searcherClient.execute(searchParams);
            String result = searchResult.getResult();

            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testQueryParam(){
        OrderParam param = new OrderParam();
        param.setId(1L);
        param.setPhone("186");
        param.setPhone2("153");

        param.setCreator("k");
        param.setTitle("测试");
        param.setRemark("标题");

        param.setSorted(Arrays.asList(new Sorted("id", Ordered.DECREASE)));

        System.out.println(SearchHelper.buildQueryParam(param));

        System.out.println();

        System.out.println(SearchHelper.buildFilterParam(param));

    }

    @Test
    public void testSearchParam(){
        OrderParam param = new OrderParam();
//        param.setId(1L);
        param.setPhone("186");
        param.setPhone2("153");

        param.setCreator("k");
        param.setTitle("测试");
        param.setRemark("标题");

        param.setStartTimeLong(1535982786790L);
        param.setEndTimeLong(1535983325570L);

        param.setIdList(Arrays.asList(1L,2L));

        param.setEmailList(Arrays.asList("111@qq.com","222@qq.com"));

        param.setSorted(Arrays.asList(new Sorted("id", Ordered.DECREASE)));

        System.out.println(client.search(param));

    }
}
