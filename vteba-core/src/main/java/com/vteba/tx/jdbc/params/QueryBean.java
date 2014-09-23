package com.vteba.tx.jdbc.params;

/**
 * mybatis分片查询参数Bean。用来定位分区表，并不是实际的where条件
 * @author yinlei
 * @date 2014-9-20
 */
public class QueryBean extends ParamBean {
	/**
	 * 主键值，用来定位分区表
	 */
	private String keyValue;
	/**
     * order by 排序语句
     */
	private String orderBy;
	
	private static final int PAGE_NO = 1;

    /**
     * 分页开始记录
     */
	private int pageNo = PAGE_NO;
	
	private static final int PAGE_SIZE = 10;

    /**
     * 分页大小
     */
	private int pageSize = PAGE_SIZE;

    /**
     * 是否去重
     */
    private boolean distinct;
    
    /**
     * 是否统计查询
     */
    private boolean stats;
	
    /**
	 * 构造查询参数Bean
	 */
	public QueryBean() {
	}

	/**
	 * 构造查询参数Bean
	 * @param keyValue 主键值，用来定位分区表
	 */
	public QueryBean(String keyValue) {
		super();
		this.keyValue = keyValue;
	}
	
	/**
	 * 构造查询参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 * @param keyValue 主键值，用来定位分区表
	 */
	public QueryBean(Object params, String keyValue) {
		super(params);
		this.keyValue = keyValue;
	}
	
	/**
	 * 构造查询参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param params 查询参数，一般是实体Bean和mybatis查询bean（自动生成的）
	 */
	public QueryBean(Integer startDate, Integer endDate, Object params) {
		super(startDate, endDate, params);
	}

	/**
	 * 构造查询参数Bean
	 * @param startDate 开始日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 * @param endDate 结束日期，数字型，例如： 201409（2014年9月），20140902（2014年9月2号）
	 */
	public QueryBean(Integer startDate, Integer endDate) {
		super(startDate, endDate);
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

	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序语句。例如 [create_date desc, user_name asc]。
	 * @param orderBy 排序语句
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置查询页码，默认1
	 * @param pageNo 页码
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	public int getStart() {
		return (pageNo - 1) * pageSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置pageSize，每页大小，默认10
	 * @param pageSize 每页大小
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		if (pageSize < 1) {
			this.pageSize = PAGE_SIZE;
		}
	}

	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * 设置是否distinct去重，默认false
	 * @param distinct true是
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isStats() {
		return stats;
	}

	/**
	 * 设置为统计查询
	 * @param stats true是
	 */
	public void setStats(boolean stats) {
		this.stats = stats;
	}
	
}
