package com.vteba.tx.jdbc.mybatis.converter;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * update sql语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class UpdateSqlConverter extends AbstractSqlConverter {

    public List<String> convert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Update)) {
            throw new IllegalArgumentException("The argument statement must is instance of Update.");
        }
        Update update = (Update) statement;
        List<String> sqlList = new ArrayList<String>();
        String name = update.getTable().getName();
        ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(name);
        if (strategy != null) {
            List<String> tableList = strategy.getUpdateTable(name, params, mapperId);
            for (String tableName : tableList) {
        		String sql = deParse(update, tableName);
        		sqlList.add(sql);
        	}
        }

        return sqlList;
    }
    
    private String deParse(Update update, String tableName) {
		update.getTable().setName(tableName);
		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
		update.accept(deParser);
		return deParser.getBuffer().toString();
	}
}
