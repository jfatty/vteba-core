package com.vteba.tx.hibernate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * hibernate使用jdbc时，返回实体的回调接口
 * @author yinlei
 * @date 2014-10-26
 * @param <T> 实体类型
 */
public interface RowMapper<T> {
	T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
