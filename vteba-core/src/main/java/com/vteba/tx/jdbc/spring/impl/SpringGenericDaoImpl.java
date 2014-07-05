package com.vteba.tx.jdbc.spring.impl;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vteba.lang.bytecode.ConstructorAccess;
import com.vteba.lang.bytecode.MethodAccess;
import com.vteba.tx.jdbc.spring.RowMapInfo;
import com.vteba.tx.jdbc.spring.RowMapInfoCache;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.spi.SpringGenericDao;
import com.vteba.utils.common.CaseUtils;
import com.vteba.utils.reflection.AsmUtils;
import com.vteba.utils.reflection.BeanCopyUtils;

/**
 * spring通用泛型dao实现。
 * @author yinlei 
 * @since 2013-7-6 16:00
 * @param <T> 泛型实体
 * @param <ID> 主键类型
 */
public abstract class SpringGenericDaoImpl<T, ID extends Serializable> extends AbstractGenericDao<T, ID> implements SpringGenericDao<T, ID> {
    private ConstructorAccess<T> constructor = AsmUtils.get().createConstructorAccess(entityClass);
    private MethodAccess method = AsmUtils.get().createMethodAccess(entityClass);
    
	public SpringGenericDaoImpl() {
        super();
    }
	
    public SpringGenericDaoImpl(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public T mapRows(ResultSet rs, int rowNum) throws SQLException {
        T entity = constructor.newInstance();
        for (int i = 0, size = setterList.size(); i < size; i++) {
            Object object = rs.getObject(columnList.get(i));//这里其实可以具体的类型，获取更具体的类型值
            if (object != null) {// 避免不必要的字节码操作，毕竟有性能损失
                method.invoke(entity, setterList.get(i), object);
            }
        }
        return entity;
    }
    
    public Object mapRows(ResultSet rs, String sql, Class<?> resultClass) throws SQLException {
        ConstructorAccess<?> constructorAccess = AsmUtils.get().createConstructorAccess(resultClass);
        Object entity = constructorAccess.newInstance();
        
        MethodAccess methodAccess = AsmUtils.get().createMethodAccess(resultClass);
        
        int columnCount = 0;
        String[] methodNames = null;
        
        RowMapInfo rowMapInfo = RowMapInfoCache.getInstance().get(sql);
        if (rowMapInfo != null) {
            columnCount = rowMapInfo.getColumnCount();
            methodNames = rowMapInfo.getMethodNames();
        }
        
        if (columnCount == 0) {
            ResultSetMetaData metaData = rs.getMetaData();
            columnCount = metaData.getColumnCount();
            methodNames = new String[columnCount];
            for (int c = 0; c < columnCount; c++) {
                String columnLabel = metaData.getColumnLabel(c + 1).toLowerCase();
                String methodName = "set" + CaseUtils.toCapCamelCase(columnLabel);
                methodNames[c] = methodName;
                methodAccess.invoke(entity, methodName, rs.getObject(c + 1));
            }
            rowMapInfo = new RowMapInfo();
            rowMapInfo.setColumnCount(columnCount);
            rowMapInfo.setMethodNames(methodNames);
            RowMapInfoCache.getInstance().put(sql, rowMapInfo);
        } else {
            for (int c = 0; c < columnCount; c++) {
                methodAccess.invoke(entity, methodNames[c], rs.getObject(c + 1));
            }
        }
        return entity;
	}
    
    public Map<String, Object> mapBean(T entity, boolean prefix, Map<String, Object> params) {
        if (prefix) {
            return BeanCopyUtils.get().toMaps(entity, params);
        } else {
            return BeanCopyUtils.get().toMap(entity);
        }
    }

    @Override
    @Autowired
    public void setSpringJdbcTemplate(SpringJdbcTemplate springJdbcTemplate) {
        this.springJdbcTemplate = springJdbcTemplate;
    }

}
