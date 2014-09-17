package com.vteba.tx.jdbc.mybatis.converter;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

/**
 * delete sql语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class DeleteSqlConverter extends AbstractSqlConverter {

    protected Statement doConvert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Delete)) {
            throw new IllegalArgumentException("The argument statement must is instance of Delete.");
        }
        Delete delete = (Delete) statement;

        String name = delete.getTable().getName();
        delete.getTable().setName(convertTableName(name, params, mapperId));

        return delete;
    }
    
    protected String convertTableName(String tableName, Object params, String mapperId) {
        ShardingConfigFactory configFactory = ShardingConfigFactory.getInstance();
        ShardingStrategy strategy = configFactory.getStrategy(tableName);
        if (strategy == null) {
            return tableName;
        } else {
            return strategy.getDeleteTable(tableName, params, mapperId);
        }
    }
}
