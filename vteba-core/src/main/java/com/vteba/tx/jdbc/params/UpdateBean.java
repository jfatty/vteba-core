package com.vteba.tx.jdbc.params;

import java.util.Date;

/**
 * mybatis分片更新参数Bean。
 * @author yinlei
 * @date 2014-9-20
 */
public class UpdateBean extends QueryBean {

	public UpdateBean() {
	}

	public UpdateBean(Date startDate, Date endDate, Object params) {
		super(startDate, endDate, params);
	}

	public UpdateBean(Date startDate, Date endDate) {
		super(startDate, endDate);
	}

	public UpdateBean(String key, String value, Date startDate, Date endDate,
			Object params) {
		super(key, value, startDate, endDate, params);
	}

	public UpdateBean(String key, String value, Date startDate, Date endDate) {
		super(key, value, startDate, endDate);
	}

	public UpdateBean(String key, String value) {
		super(key, value);
	}

}
