package com.vteba.tx.matrix.info;

import java.util.ArrayList;
import java.util.List;

/**
 * 分区表信息
 * @author yinlei 
 * @see
 * @since 2013-12-5 16:33
 */
public class TableInfo {
    private String tableName;// 表名
    private String schema;// 表所在的schema
    private List<String> tableList = new ArrayList<String>();//分区表信息
    private String currentTable;//现在使用的表信息
    
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
    
    public List<String> getTableList() {
        return tableList;
    }
    
    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }
    
    public String getCurrentTable() {
        return currentTable;
    }
    
    public void setCurrentTable(String currentTable) {
        this.currentTable = currentTable;
    }
}
