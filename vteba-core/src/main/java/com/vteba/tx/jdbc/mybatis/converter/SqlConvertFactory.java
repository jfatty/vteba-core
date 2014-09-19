package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import com.vteba.tx.jdbc.mybatis.ShardingException;

public interface SqlConvertFactory {
	/**sql中插值开始标记*/
	public static final String START = "{{";
	/**sql中插值结束标记*/
	public static final String END = "}}";
	
	/**
	 * 转换sql，替换其中的插值，返回sql list
	 * @param sql 原始sql语句
	 * @param params sql参数
	 * @param mapperId mybatis sql映射id
	 * @return 转换后的sql list
	 * @throws ShardingException
	 */
	public List<String> convert(String sql, Object params, String mapperId) throws ShardingException;
}