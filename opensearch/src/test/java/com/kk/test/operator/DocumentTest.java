package com.kk.test.operator;

import com.alibaba.fastjson.JSON;
import com.kk.opensearch.model.AddSearchTemplate;
import com.kk.opensearch.model.UpdateSearchTemplate;
import com.kk.opensearch.util.JsonUtil;
import org.junit.Test;
import com.kk.test.model.Order;

import java.util.Arrays;

/**
 * @author zhihui.kzh
 * @create 3/9/1821:39
 */
public class DocumentTest extends BaseMode{

    // 上传文档，若存在则批量更新(全部字段更新)
    @Test
    public void testAdd(){
        Order order = new Order();
        order.setId(3L);
        order.setCreator("kk3");
        order.setPhone("186");
        order.setEmail("111@qq.com");
        order.setGmtCreated(System.currentTimeMillis());
        order.setTitle("测试标题");
        order.setRemark("测试备注");
        order.setGroupIds(Arrays.asList(1L,3L,5L));
        order.setAllDealUser(Arrays.asList("k1","k2","k3"));

        logger.info(JSON.toJSONString(order));
        logger.info(JsonUtil.toJsonString(order));

        client.pushDoc(Arrays.asList(order), tableName);

    }

    // 部分字段更新，不存在不会自动创建文档。
    @Test
    public void testUpdate() {
        Order order = new Order();
        order.setId(3L);
        order.setCreator("k2");
        order.setPhone("1533");
        order.setTitle("测试的标题");

        client.updateDoc(Arrays.asList(order), tableName);

    }

    // 删除文档，通过主键删除
    @Test
    public void testDelete(){
        Order order = new Order();
        order.setId(3L);

        client.deleteDoc(Arrays.asList(order), tableName);
    }
}
