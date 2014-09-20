package com.vteba.tx.jdbc.params;

import java.util.Date;

/**
 * mybatis分片删除参数Bean。
 * @author yinlei
 * @date 2014-9-20
 */
public class DeleteBean extends ParamBean {
	
	public DeleteBean() {
	}
	
	public DeleteBean(Date startDate, Date endDate, Object params) {
		super(startDate, endDate, params);
	}

	public DeleteBean(Date startDate, Date endDate) {
		super(startDate, endDate);
	}

}
