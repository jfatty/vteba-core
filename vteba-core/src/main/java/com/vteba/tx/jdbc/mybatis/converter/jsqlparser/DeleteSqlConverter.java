package com.vteba.tx.jdbc.mybatis.converter.jsqlparser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * delete sql语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class DeleteSqlConverter extends AbstractSqlConverter {

	@Override
    public List<String> convert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Delete)) {
            throw new IllegalArgumentException("The argument statement must is instance of Delete.");
        }
        
        List<String> sqlList = new ArrayList<String>();
        
        Delete delete = (Delete) statement;

        String name = delete.getTable().getName();
        ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(name);
        if (strategy != null) {
            List<String> tableList = strategy.getDeleteTable(name, params, mapperId);
            for (String tableName : tableList) {
        		String sql = deParse(delete, tableName);
        		sqlList.add(sql);
        	}
        } 
        return sqlList;
    }

	private String deParse(Delete delete, String tableName) {
		delete.getTable().setName(tableName);
		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
		delete.accept(deParser);
		return deParser.getBuffer().toString();
	}
    
}
