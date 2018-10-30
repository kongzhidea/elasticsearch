package com.kk.es.test.operator;

import com.kk.es.model.search.Sorted;
import com.kk.es.model.search.enums.Ordered;
import com.kk.es.test.param.OrderParam;
import com.kk.es.util.SearchHelper;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
    public void testQueryParam(){
        OrderParam param = new OrderParam();
//        param.setId(1L);
//        param.setIdList(Arrays.asList(1L,2L));
//        param.setPhone("186");

//        param.setCreator("kk2");
        param.setDealUser("k1");

        param.setTitle("测试");
        param.setRemark("hello");

        BoolQueryBuilder qb = SearchHelper.buildQueryParam(param);
        System.out.println(qb.toString());

        BoolFilterBuilder fb = SearchHelper.buildFilterParam(param);
        System.out.println(fb.toString());

        client.search(tableName, null, fb);
    }
}
