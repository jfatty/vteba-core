package com.vteba.tx.jdbc.params;

/**
 * mybatis分区表，删除参数Bean。
 * @author yinlei
 * @date 2014-9-20
 */
public class DeleteBean extends ParamBean {
	
	/**
	 * 构造删除参数Bean
	 */
	public DeleteBean() {
	}

	/**
	 * 构造删除参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 */
	public DeleteBean(Integer startDate, Integer endDate, Object params) {
		super(startDate, endDate, params);
	}

	/**
	 * 构造删除参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 */
	public DeleteBean(Integer startDate, Integer endDate) {
		super(startDate, endDate);
	}
	
}
