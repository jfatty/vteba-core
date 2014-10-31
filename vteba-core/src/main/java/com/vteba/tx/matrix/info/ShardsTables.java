package com.vteba.tx.matrix.info;

import java.util.Date;
import java.util.List;

/**
 * 分区表信息
 * @author yinlei 
 * @since 2013-12-5 16:33
 */
public interface ShardsTables {
    
    public String getTableName();
    
    public void setTableName(String tableName);
    
    public String getSchemaName();
    
    public void setSchemaName(String schema);
    
    public String getCurrentTable();
    
    public void setCurrentTable(String currentTable);

	public List<Long> getTableIndexList();

	public void setTableIndexList(List<Long> tableIndexList);

	public Integer getId();

	public void setId(Integer id);

	public Date getCreateDate();

	public void setCreateDate(Date createDate);

	public Date getUpdateDate();

	public void setUpdateDate(Date updateDate);

	public Long getFromIndex();

	public void setFromIndex(Long fromIndex);

	public Long getEndIndex();

	public void setEndIndex(Long endIndex);

	public String getStrategy();

	public void setStrategy(String strategy);

}
