package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 * insert语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class InsertSqlConverter extends AbstractSqlConverter {

    protected Statement doConvert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Insert)) {
            throw new IllegalArgumentException("The argument statement must is instance of Insert.");
        }
        Insert insert = (Insert) statement;

        String name = insert.getTable().getName();
        insert.getTable().setName(convertTableName(name, params, mapperId));

        return insert;
    }
    
    protected String convertTableName(String tableName, Object params, String mapperId) {
        ShardingConfigFactory configFactory = ShardingConfigFactory.getInstance();
        ShardingStrategy strategy = configFactory.getStrategy(tableName);
        if (strategy == null) {
            return tableName;
        } else {
            return strategy.getInsertTable(tableName, params, mapperId);
        }
    }

	@Override
	public List<String> convert(Statement statement, Object params,
			String mapperId) {
		// TODO Auto-generated method stub
		return null;
	}
}
