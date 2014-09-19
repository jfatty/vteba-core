package com.vteba.tx.jdbc.mybatis.converter.internal;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.vteba.tx.jdbc.mybatis.ShardingException;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.converter.SqlConvertFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * 手工转换sql，截取字符串的方式。性能很高，缺点是可能考虑的问题不够全面。
 * @author yinlei
 * @since 2013-12-19
 */
public enum ManualSqlConvertFactory implements SqlConvertFactory {
	INSTANCE;
	
	private ManualSqlConvertFactory() {
	}
	
	@Override
	public List<String> convert(String sql, Object params, String mapperId)
			throws ShardingException {
		sql = sql.toLowerCase();
		List<String> tableList = null;
		String tableName = null;
		ShardingStrategy strategy = null;
		String sqlType = sql.substring(0, 6);
		switch (sqlType) {
		case "select":
			int start = sql.indexOf(FROM) + 4;
			int where = sql.indexOf(WHERE);
			String temp = null;
			if (where > 0) {
				temp = sql.substring(start, where);
			} else {
				int limit = sql.indexOf("limit");
				if (limit > 0) {
					temp = sql.substring(start, limit);
				} else {
					temp = sql.substring(start);
				}
			}
			if (temp == null) {
				throw new IllegalStateException("select语句错误，没有获取到要查询的表。");
			}
			
			String[] tables = temp.trim().split(",");// 处理多表连接的情况
			if (tables == null) {
				throw new IllegalStateException("select语句错误，多表连接，没有获取到要查询的表。");
			}
			tables = tables[0].trim().split(" ");// 处理有别名的情况
			tableName = tables[0].trim();
			if (tableName == null) {
				throw new IllegalStateException("select语句错误，表别名，没有获取到要查询的表。");
			}
			strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
			tableList = strategy.getSelectTable(tableName, params, mapperId);
			break;
		case "insert":
			start = sql.indexOf("into");
			int end = sql.indexOf("(");
			tableName = sql.substring(start, end).trim();
			if (tableName == null) {
				throw new IllegalStateException("insert语句错误，没有获取到要插入的表。");
			}
			strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
			String insertTable = strategy.getInsertTable(tableName, params, mapperId);
			tableList = Lists.newArrayList(insertTable);
			break;
		case "update":
			end = sql.indexOf("set");
			tableName = sql.substring(6, end).trim();
			if (tableName == null) {
				throw new IllegalStateException("update语句错误，没有获取到要更新的表。");
			}
			strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
			tableList = strategy.getUpdateTable(tableName, params, mapperId);
			break;
		case "delete":
			start = sql.indexOf(FROM);
			where = sql.indexOf(WHERE);
			if (where > 0) {
				tableName = sql.substring(start, where).trim();
			} else {
				tableName = sql.substring(start).trim();
			}
			if (tableName == null) {
				throw new IllegalStateException("delete语句错误，没有获取到要delete的表。");
			}
			
			strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
			tableList = strategy.getDeleteTable(tableName, params, mapperId);
			break;
		default:
			break;
		}
		List<String> sqlList = new ArrayList<String>();
		if (tableList != null) {
			for (String table : tableList) {
				String convertSql = sql.replaceFirst(tableName, table);
				sqlList.add(convertSql);
			}
		} else {
			String convertSql = sql.replaceFirst(tableName, tableName);
			sqlList = Lists.newArrayList(convertSql);
		}
		return sqlList;
	}
}
