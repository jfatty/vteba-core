package com.vteba.tx.matrix.info;

/**
 * 分区表详细信息
 * 
 * @author yinlei
 * @since 2013-12-9 10:53
 */
public class TableDetail {
	private Integer id;
	private Long tableIndex;// 分区表索引值
	private Long shardsTableId;// 分区表id，外键，关联shards_table

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getTableIndex() {
		return tableIndex;
	}

	public void setTableIndex(Long tableIndex) {
		this.tableIndex = tableIndex;
	}

	public Long getShardsTableId() {
		return shardsTableId;
	}

	public void setShardsTableId(Long shardsTableId) {
		this.shardsTableId = shardsTableId;
	}

}
