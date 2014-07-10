package com.vteba.tx.generic.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造统计条件（实质上是拼接ql）
 * @author yinlei 
 * @since 2014-7-10
 */
public class Stats {
    private String entityName;
    private StringBuilder sb;
    private List<String> fieldList;
    private List<String> entityList;
    
    private Stats() {
    }
    
    private Stats(String entityName) {
        this.entityName = entityName;
        sb = new StringBuilder("select");
        fieldList = new ArrayList<String>();
        entityList = new ArrayList<String>();
        entityList.add(entityName);
    }
    
    /**
     * 切换查询实体
     * @param entityName 实体名
     * @return this，当前对象
     */
    public Stats from(String entityName) {
        this.entityName = entityName;
        entityList.add(entityName);
        return this;
    }
    
    public static Stats inti(String entityName) {
        return new Stats(entityName);
    }
    
    public Stats sum(String fieldName) {
        sb.append(" sum(").append(entityName).append(".").append(fieldName).append(") as _").append(fieldName);
        fieldList.add(fieldName);
        return this;
    }
    
    public Stats avg(String fieldName) {
        sb.append(" avg(").append(entityName).append(".").append(fieldName).append(") as _").append(fieldName);
        fieldList.add(fieldName);
        return this;
    }
    
    public Stats count(String fieldName) {
        sb.append(" count(").append(entityName).append(".").append(fieldName).append(") as _").append(fieldName);
        fieldList.add(fieldName);
        return this;
    }
    
    public Stats max(String fieldName) {
        sb.append(" max(").append(entityName).append(".").append(fieldName).append(") as _").append(fieldName);
        fieldList.add(fieldName);
        return this;
    }
    
    public Stats min(String fieldName) {
        sb.append(" min(").append(entityName).append(".").append(fieldName).append(") as _").append(fieldName);
        fieldList.add(fieldName);
        return this;
    }
    
    public Stats where() {
        
        return this;
    }
    
    
    
    List<String> getFieldList() {
        return this.fieldList;
    }
    
    public String toString() {
        sb.append(" from ").append(entityName);
        return sb.toString();
    }
}
