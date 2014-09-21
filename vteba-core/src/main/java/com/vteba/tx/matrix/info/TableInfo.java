package com.vteba.tx.matrix.info;

import java.util.ArrayList;
import java.util.List;

import com.vteba.tx.matrix.table.TableRuler;

/**
 * 分区表信息
 * @author yinlei 
 * @since 2013-12-5 16:33
 */
public class TableInfo {
    private String tableName;// 表名
    private String schema;// 表所在的schema
    private List<Integer> tableIndexList = new ArrayList<Integer>();//分区表信息
    private String currentTable;//现在使用的表信息
    private TableRuler tableRuler;// 分表策略
    
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

	public TableRuler getTableRuler() {
		return tableRuler;
	}

	public void setTableRuler(TableRuler tableRuler) {
		this.tableRuler = tableRuler;
	}

	public List<Integer> getTableIndexList() {
		return tableIndexList;
	}

	public void setTableIndexList(List<Integer> tableIndexList) {
		this.tableIndexList = tableIndexList;
	}
}
