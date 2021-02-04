package com.example.hope.common.provider;

import java.util.Map;

/**
 * @description: 订单表sql提供类
 * @author: DHY
 * @created: 2021/02/04 17:01
 */
public class OrdersSqlProvider {

    private static final String SQL = "select " +
            "orders.*," +
            "client.name as clientName," +
            "client.phone as clientPhone," +
            "service.name as serviceName," +
            "service.phone as servicePhone," +
            "product.name as productName," +
            "product.price as productPrice," +
            "product.id as productId," +
            "store.name as storeName " +
            "from " +
            "orders," +
            "user as client," +
            "user as service," +
            "product" +
            "left join " +
            "store" +
            "on" +
            "product.storeId = store.id" +
            "where " +
            "orders.cid = client.id and " +
            "orders.sid = service.id and " +
            "orders.pid = product.id";

    public String selectByKey(Map<String, Object> para){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SQL);
        if (para != null) stringBuffer.append(" where orders."+ para.get("key") +" = " + para.get("id"));
        stringBuffer.append(";");
        return stringBuffer.toString();
    }
}
