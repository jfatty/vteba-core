package com.vteba.tx.jdbc.mybatis.converter.internal;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.vteba.tx.jdbc.mybatis.ShardingException;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.converter.SqlConvertFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * 基于模板的sql转换工厂。这里需要配置mybatis的代码生成工具。
 * @author yinlei
 * @since 2013-12-19
 */
public class TemplateSqlConvertFactory implements SqlConvertFactory {
	private static final TemplateSqlConvertFactory instance = new TemplateSqlConvertFactory();
	
	private TemplateSqlConvertFactory() {
	}
	
	public static TemplateSqlConvertFactory get() {
		return instance;
	}
	
	@Override
	public List<String> convert(String sql, Object params, String mapperId)
			throws ShardingException {
		
		String tableName = sql.substring(sql.indexOf(START) + 2, sql.indexOf(END));
		ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
		List<String> tableList = null;
		
		String sqlType = sql.substring(0, 6).toLowerCase();
		switch (sqlType) {
		case "select":
			tableList = strategy.getSelectTable(tableName, params, mapperId);
			break;
		case "insert":
			String insertTable = strategy.getInsertTable(tableName, params, mapperId);
			tableList = Lists.newArrayList(insertTable);
			break;
		case "update":
			tableList = strategy.getUpdateTable(tableName, params, mapperId);
			break;
		case "delete":
			tableList = strategy.getDeleteTable(tableName, params, mapperId);
			break;
		default:
			break;
		}
		List<String> sqlList = new ArrayList<String>();
		if (tableList != null) {
			for (String table : tableList) {
				String convertSql = sql.replace(START + tableName + END, table);
				sqlList.add(convertSql);
			}
		} else {
			String convertSql = sql.replace(START + tableName + END, tableName);
			sqlList = Lists.newArrayList(convertSql);
		}
		return sqlList;
	}

}
