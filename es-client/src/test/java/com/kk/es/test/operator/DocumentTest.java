package com.kk.es.test.operator;

import com.alibaba.fastjson.JSON;
import com.kk.es.test.model.Order;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentTest extends BaseMode {

    @Test
    public void testAdd() {
        Order order = new Order();
        order.setId(1L);
        order.setCreator("kk3");
        order.setPhone("123");
        order.setEmail("111@qq.com");
        order.setGmtCreated(System.currentTimeMillis());
        order.setTitle("测试标题1");
        order.setRemark("测试备注");
        order.setGroupIds(Arrays.asList(1L, 3L, 5L));
        order.setAllDealUser(Arrays.asList("k1", "k2", "k3"));

        logger.info(JSON.toJSONString(order));

        boolean ret = client.batchInsert(Arrays.asList(order), tableName);
        logger.info("ret=" + ret);
    }

    @Test
    public void testAdd0() {
        Order order = new Order();
        order.setId(5L);
        order.setCreator("kk2");
        order.setPhone("123");
        order.setEmail("111@qq.com");
        order.setGmtCreated(System.currentTimeMillis());
        order.setTitle("测试标题1");
        order.setRemark("测试备注");
        order.setGroupIds(Arrays.asList(1L, 3L, 5L));
        order.setAllDealUser(Arrays.asList("k1", "k2", "k3"));

        logger.info(JSON.toJSONString(order));

        client.insert(order, tableName);
    }

    @Test
    public void testUpdate() {
        List<Order> orderList = new ArrayList<>();
        {
            Order order = new Order();
            order.setId(5L);
            order.setCreator("kk22");
            orderList.add(order);
        }
        {
            Order order = new Order();
            order.setId(2L);
            order.setCreator("kk2");
            orderList.add(order);
        }

        logger.info(JSON.toJSONString(orderList));

        boolean ret = client.batchUpdate(orderList, tableName);
        logger.info("ret=" + ret);
    }

    @Test
    public void testUpdate0() {
        Order order = new Order();
        order.setId(3L);
        order.setCreator("kk202");
        order.setRemark("i am hello, world");

        logger.info(JSON.toJSONString(order));

        client.update(order, tableName);
    }

    @Test
    public void testDelete() {
        boolean ret = client.batchDelete(Arrays.asList("5","7"), tableName);
        logger.info("ret=" + ret);
    }
    @Test
    public void testDelete0() {
        client.delete("5", tableName);
    }


    @Test
    public void testGet0() {
        SearchHits ret = client.searchById("1", tableName);
        logger.info(JSON.toJSONString(ret, true));
    }

    @Test
    public void testGet1() {
        SearchHits ret = client.searchById("abc", tableName);
        logger.info(JSON.toJSONString(ret, true));
    }
}
