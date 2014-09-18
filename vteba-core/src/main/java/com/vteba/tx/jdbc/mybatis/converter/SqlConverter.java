package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import net.sf.jsqlparser.statement.Statement;

/**
 * sql转换接口。
 * @author yinlei 
 * @since 2013-12-17 12:49
 */
public interface SqlConverter {
    
    /**
     * 根据分表分片策略，进行sql转换
     * @param statement sql语句
     * @param params sql参数
     * @param mapperId mybatis sql映射id
     * @return 转换后的sql
     */
    public List<String> convert(Statement statement, Object params, String mapperId);
    
    /**
     * 根据分表分片策略，进行sql转换
     * @param statement sql语句
     * @param params sql参数
     * @param mapperId mybatis sql映射id
     * @return 转换后的sql
     */
    //public String insert(Statement statement, Object params, String mapperId);
}
