package com.vteba.tx.jdbc.params;

/**
 * mybatis分片更新参数Bean。
 * @author yinlei
 * @date 2014-9-20
 */
public class UpdateBean extends ParamBean {
	/**
	 * 主键值，用来定位分区表
	 */
	private String keyValue;
	/**
	 * update的set语句参数值，是一个实体类
	 */
	private Object record;
	
	/**
	 * 构造更新参数Bean
	 */
	public UpdateBean() {
	}

	/**
	 * 构造更新参数Bean
	 * @param record where set参数，是一个实体类
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 */
	public UpdateBean(Object record, Integer startDate, Integer endDate, Object params) {
		super(startDate, endDate, params);
		this.record = record;
	}

	/**
	 * 构造更新参数Bean
	 * @param record where set参数，是一个实体类
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 */
	public UpdateBean(Object record, Integer startDate, Integer endDate) {
		super(startDate, endDate);
		this.record = record;
	}

	/**
	 * 构造更新参数Bean
	 * @param record where set参数，是一个实体类
	 * @param keyValue 主键值，用来定位分区表
	 */
	public UpdateBean(Object record, String keyValue) {
		this.record = record;
		this.keyValue = keyValue;
	}
	
	/**
	 * 构造更新参数Bean
	 * @param record where set参数，是一个实体类
	 * @param params 更新参数值，where条件
	 * @param keyValue 主键值，用来定位分区表
	 */
	public UpdateBean(Object record, Object params, String keyValue) {
		super(params);
		this.record = record;
		this.keyValue = keyValue;
	}
	
	public String getKeyValue() {
		return keyValue;
	}

	/**
	 * 设置主键值，用来定位分区表
	 * @param keyValue 主键值
	 */
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public Object getRecord() {
		return record;
	}

	/**
	 * 设置update的set语句参数值，是一个实体类
	 * @param record set参数值
	 */
	public void setRecord(Object record) {
		this.record = record;
	}

}
