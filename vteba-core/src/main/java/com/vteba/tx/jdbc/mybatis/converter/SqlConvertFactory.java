package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import com.vteba.tx.jdbc.mybatis.ShardingException;

public interface SqlConvertFactory {
	/**sql中插值开始标记*/
	public static final String START = "{{";
	/**sql中插值结束标记*/
	public static final String END = "}}";
	
	/**sql中select*/
	public static final String SELECT = "select";
	/**sql中update*/
	public static final String UPDATE = "update";
	/**sql中insert*/
	public static final String INSERT = "insert";
	/**sql中delete*/
	public static final String DELETE = "delete";
	/**sql中from*/
	public static final String FROM = "from";
	/**sql中where*/
	public static final String WHERE = "where";
	
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
