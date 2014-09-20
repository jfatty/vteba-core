package com.vteba.tx.jdbc.params;

import java.util.Date;

/**
 * mybatis分片查询、更新、删除抽象参数Bean。亦可直接使用。
 * @author yinlei
 * @date 2014-9-20
 */
public class ParamBean {
	private Date startDate;// 开始时间
	private Date endDate;// 结束时间
	private Object params;// 其他查询参数
	
	public ParamBean() {
	}
	
	public ParamBean(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public ParamBean(Date startDate, Date endDate, Object params) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.params = params;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

}
