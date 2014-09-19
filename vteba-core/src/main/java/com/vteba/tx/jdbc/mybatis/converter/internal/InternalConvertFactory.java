package com.vteba.tx.jdbc.mybatis.converter.internal;

import java.util.ArrayList;
import java.util.List;

import com.vteba.tx.jdbc.mybatis.ShardingException;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.converter.SqlConvertFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

public class InternalConvertFactory implements SqlConvertFactory {
	private static final InternalConvertFactory instance = new InternalConvertFactory();

	
	
	private InternalConvertFactory() {
		
	}
	
	public static InternalConvertFactory get() {
		return instance;
	}
	
	@Override
	public List<String> convert(String sql, Object params, String mapperId)
			throws ShardingException {
		String tableName = sql.substring(sql.indexOf(START), sql.indexOf(END));
		ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(tableName);
		
		List<String> sqlList = new ArrayList<String>();
		
		String sqlType = sql.substring(0, 6).toLowerCase();
		switch (sqlType) {
		case "select":
			List<String> tableList = strategy.getSelectTable(tableName, params, mapperId);
			for (String table : tableList) {
				String convertSql = sql.replace(START + tableName + END, table);
				sqlList.add(convertSql);
			}
			break;

		case "insert":
			
			break;
		case "update":
			
			break;
		case "delete":
			
			break;
		default:
			break;
		}
		return null;
	}

}
