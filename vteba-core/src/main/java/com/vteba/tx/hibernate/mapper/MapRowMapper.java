package com.vteba.tx.hibernate.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * RowMapper的基本实现，返回Map，key是字段名，value是字段值。
 * @author yinlei
 * @date 2014-10-26
 */
public class MapRowMapper implements RowMapper<Map<String, Object>> {

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		String label = null;
		for (int i = 1; i <= columnCount; i++) {
			label = metaData.getColumnLabel(i);
			result.put(label, rs.getObject(label));
		}
		return result;
	}

}
