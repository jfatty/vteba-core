package com.vteba.tx.matrix.info;

/**
 * 分区表详细信息
 * 
 * @author yinlei
 * @since 2013-12-9 10:53
 */
public interface TableDetail {

	public Integer getId();

	public void setId(Integer id);

	public Long getTableIndex();

	public void setTableIndex(Long tableIndex);

	public Long getShardsTableId();

	public void setShardsTableId(Long shardsTableId);

}
