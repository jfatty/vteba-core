package com.vteba.tx.jdbc.spring.meta;

import java.util.Map;


public class EntityMetadata {
    private Map<String, Class<?>> fieldInfo;

    
    public Map<String, Class<?>> getFieldInfo() {
        return fieldInfo;
    }
    
    public void setFieldInfo(Map<String, Class<?>> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }
    
    
}
