package com.vteba.tx.jdbc.params;

/**
 * mybatis分片查询、更新、删除抽象参数Bean。亦可直接使用。
 * @author yinlei
 * @date 2014-9-20
 */
public class ParamBean {
	private Integer startDate;// 开始时间，数字型 201409,20140902
	private Integer endDate;// 结束时间
	private Object params;// 其他查询参数
	
	/**
	 * 构造参数Bean
	 */
	public ParamBean() {
	}
	
	/**
	 * 构造参数Bean
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 */
	public ParamBean(Object params) {
		super();
		this.params = params;
	}

	/**
	 * 构造参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 */
	public ParamBean(Integer startDate, Integer endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * 构造参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 */
	public ParamBean(Integer startDate, Integer endDate, Object params) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.params = params;
	}
	
	public Integer getStartDate() {
		return startDate;
	}

	/**
	 * 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param startDate 日期，数字型
	 */
	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public Integer getEndDate() {
		return endDate;
	}

	/**
	 * 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param startDate 日期，数字型
	 */
	public void setEndDate(Integer endDate) {
		this.endDate = endDate;
	}

	public Object getParams() {
		return params;
	}

	/**
	 * 设置查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 * @param params
	 */
	public void setParams(Object params) {
		this.params = params;
	}

}
