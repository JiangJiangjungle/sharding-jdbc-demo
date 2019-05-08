package com.jsj.shardingjdbcdemo.dao;


import com.jsj.shardingjdbcdemo.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiangshenjie
 * @date 2019-5-8
 */
@Repository
public interface OrderMapper {

    boolean insertOrder(@Param("userId")Integer userId,@Param("status")String status);

    List<Order> selectByUserId(@Param("userId")Integer userId);

    Order selectByOrderId(@Param("orderId")Long orderId);

    List<Order> selectByOrderIdBetween(@Param("id1") Long startOrderId, @Param("id2") Long endOrderId);
}
