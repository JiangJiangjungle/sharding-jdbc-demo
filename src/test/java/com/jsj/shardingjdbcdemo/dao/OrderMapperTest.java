package com.jsj.shardingjdbcdemo.dao;

import com.jsj.shardingjdbcdemo.Application;
import com.jsj.shardingjdbcdemo.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)//这里是启动类
public class OrderMapperTest {
    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void test1() {
        for (int i=11;i<20;i++){
            System.out.println(orderMapper.insertOrder(i,"success"));
        }
    }

    @Test
    public void test2() {
        int userId = 5;
        List<Order> orderList = orderMapper.selectByUserId(userId);
        for (Order order : orderList) {
            System.out.println(order.toString());
        }
    }

    @Test
    public void test3() {
        long orderId = 332918184766078976L;
        Order order = orderMapper.selectByOrderId(orderId);
        System.out.println(order.toString());
    }

    @Test
    public void test4() {
        long startOrderId = 332918184766078976L;
        long endOrderId=332918419831652352L;
        List<Order> orderList = orderMapper.selectByOrderIdBetween(startOrderId,endOrderId);
        for (Order order : orderList) {
            System.out.println(order.toString());
        }
    }
}
