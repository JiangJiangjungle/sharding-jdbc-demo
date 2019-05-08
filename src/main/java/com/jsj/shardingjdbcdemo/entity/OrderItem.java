package com.jsj.shardingjdbcdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long orderItemId;

    private Long orderId;

    private Integer userId;
}
