package com.vteba.tx.matrix.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vteba.tx.matrix.table.TableRuler;

/**
 * 分区表信息
 * @author yinlei 
 * @since 2013-12-5 16:33
 */
public class ShardsTable {
	private Integer id;
    private String tableName;// 表名
    private String schema;// 表所在的schema
    private List<Long> tableIndexList = new ArrayList<Long>();//分区表信息
    private String currentTable;//现在使用的表信息
    private TableRuler strategy;// 分表策略
    private Date createDate;
    private Date updateDate;
    private Long fromIndex;// id开始记录
    private Long endIndex;// id结束记录
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    public String getCurrentTable() {
        return currentTable;
    }
    
    public void setCurrentTable(String currentTable) {
        this.currentTable = currentTable;
    }

	public List<Long> getTableIndexList() {
		return tableIndexList;
	}

	public void setTableIndexList(List<Long> tableIndexList) {
		this.tableIndexList = tableIndexList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(Long fromIndex) {
		this.fromIndex = fromIndex;
	}

	public Long getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Long endIndex) {
		this.endIndex = endIndex;
	}

	public TableRuler getStrategy() {
		return strategy;
	}

	public void setStrategy(TableRuler strategy) {
		this.strategy = strategy;
	}

}
