package com.vteba.tx.jdbc.params;

import java.util.Date;

/**
 * mybatis分片查询参数Bean。
 * @author yinlei
 * @date 2014-9-20
 */
public class QueryBean extends ParamBean {
	private String key;// 主键名称
	private String value;// 主键值
	
	public QueryBean() {
	}

	public QueryBean(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public QueryBean(Date startDate, Date endDate) {
		super(startDate, endDate);
	}

	public QueryBean(Date startDate, Date endDate, Object params) {
		super(startDate, endDate, params);
	}
	
	public QueryBean(String key, String value, Date startDate, Date endDate) {
		super(startDate, endDate);
		this.key = key;
		this.value = value;
	}
	
	public QueryBean(String key, String value, Date startDate, Date endDate, Object params) {
		super(startDate, endDate, params);
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
