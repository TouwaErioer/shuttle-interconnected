package com.example.hope.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 订单实体类
 * @author: DHY
 * @created: 2020/10/23 22:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private long id;

    // 消费者id
    private long cid;
    // 生产者id
    private long uid;
    // 产品id
    private long pid;

    private Date create_time;
    private String address;
    private String note;
    private String file_url;
    private boolean complete;
    private boolean order_status;
}