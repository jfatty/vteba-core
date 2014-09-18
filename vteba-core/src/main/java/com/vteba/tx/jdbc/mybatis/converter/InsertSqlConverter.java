package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.google.common.collect.Lists;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * insert语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class InsertSqlConverter extends AbstractSqlConverter {

	@Override
	public List<String> convert(Statement statement, Object params,
			String mapperId) {
		if (!(statement instanceof Insert)) {
            throw new IllegalArgumentException("The argument statement must is instance of Insert.");
        }
        Insert insert = (Insert) statement;

        String name = insert.getTable().getName();
        ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(name);
        String sql = null;
        if (strategy != null) {
            String tableName = strategy.getInsertTable(name, params, mapperId);
            sql = deParse(insert, tableName);
        } 
		return Lists.newArrayList(sql);
	}
	
	private String deParse(Insert insert, String tableName) {
		insert.getTable().setName(tableName);
		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
		insert.accept(deParser);
		return deParser.getBuffer().toString();
	}
}
