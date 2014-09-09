package com.vteba.tx.matrix.info;

/**
 * 表主键分布信息
 * @author yinlei 
 * @since 2013-12-9 10:53
 */
public class Table {
    private String tableName;
    private Long from;// id开始记录
    private Long end;// id结束记录
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public Long getFrom() {
        return from;
    }
    
    public void setFrom(Long from) {
        this.from = from;
    }
    
    public Long getEnd() {
        return end;
    }
    
    public void setEnd(Long end) {
        this.end = end;
    }
    
}
