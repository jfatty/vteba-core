package com.vteba.tx.jdbc.spring.meta;

import java.util.List;
import java.util.Map;

public class EntityMetadata {
    // 表名
    private String tableName;
    // 数据库schema
    private String schema;
    // 属性信息，key属性名，value属性类型
    private Map<String, Class<?>> fieldInfo;
    // 栏位信息，key为栏位名，value为栏位类型（java）
    private Map<String, Class<?>> columnInfo;
    // 以逗号分隔的栏位名
    private String                columns;
    // 以逗号分隔的sql问号占位符
    private String                placeholders;
    // sql栏位列表
    private List<String>          columnList;
    // setter方法列表
    private List<String>          setterList;
    // id名字
    private String                idName;
    // id类型
    private Class<?>              idClass;

    public Map<String, Class<?>> getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(Map<String, Class<?>> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public String getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(String placeholders) {
        this.placeholders = placeholders;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Class<?> getIdClass() {
        return idClass;
    }
    
    public void setIdClass(Class<?> idClass) {
        this.idClass = idClass;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public Map<String, Class<?>> getColumnInfo() {
        return columnInfo;
    }
    
    public void setColumnInfo(Map<String, Class<?>> columnInfo) {
        this.columnInfo = columnInfo;
    }

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

    public List<String> getSetterList() {
        return setterList;
    }
    
    public void setSetterList(List<String> setterList) {
        this.setterList = setterList;
    }

}
