package com.example.hope.common.provider;

import java.util.Map;

/**
 * @description: 商店sql提供类
 * @author: DHY
 * @created: 2021/02/03 21:00
 */
public class StoreSqlProvider {

    public String selectByKey(Map<String, Object> para) {
        String sql = "select store.*,service.name as serviceName,service.color as serviceColor,category.name as categoryName from store left join service on store.serviceId = service.id left join category on store.categoryId = category.id";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sql);
        if (para != null) {
            if (para.get("key").equals("search"))
                stringBuffer.append(" where store.name like %" + para.get("keyword") + "%");
            else stringBuffer.append(" where store." + para.get("key") + " = " + para.get("id"));
        }
        stringBuffer.append(";");
        return stringBuffer.toString();
    }
}
