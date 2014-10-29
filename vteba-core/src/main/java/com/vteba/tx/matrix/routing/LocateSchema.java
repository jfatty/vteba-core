package com.vteba.tx.matrix.routing;

import com.vteba.service.tenant.SchemaHolder;

/**
 * 获取查询所对应的schema
 * @author yinlei 
 * @see
 * @since 2013-12-5 16:23
 */
public class LocateSchema {
    
    /**
     * 获取对应的数据源
     * @param sql
     * @return 数据源schema
     */
    public String getSchema(String sql) {
        return SchemaHolder.getSchema();
    }
}
