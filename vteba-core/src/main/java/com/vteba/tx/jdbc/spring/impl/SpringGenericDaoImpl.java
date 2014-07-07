package com.vteba.tx.jdbc.spring.impl;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
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

/**
 * spring通用泛型dao实现。
 * @author yinlei 
 * @since 2013-7-6 16:00
 * @param <T> 泛型实体
 * @param <ID> 主键类型
 */
public class SpringGenericDaoImpl<T, ID extends Serializable> extends AbstractGenericDao<T, ID> implements SpringGenericDao<T, ID> {
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
    
    @Override
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
    
	@Override
    public Map<String, Object> mapBean(T entity, boolean prefix, SqlType sqlType) {
        return toMap(entity, prefix, sqlType, tableName);
    }

	@Override
	public Map<String, Object> mapBean(Object params) {
		return toMap(params, false, SqlType.NULL, null);
	}
	
	/**
     * 将Bean转换为Map，map key使用下划线命名法（prefix == true，将以 _ 开头的），性能最好。
     * @param fromBean 源JavaBean
     * @param prefix key是否加 _ 前缀
     * @return fromBean转化成的Map
     */
    protected Map<String, Object> toMap(Object fromBean, boolean prefix, SqlType sqlType, String table) {
        MethodAccess methodAccess = AsmUtils.get().createMethodAccess(fromBean.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        
        StringBuilder columns = new StringBuilder();
        StringBuilder updateSets = new StringBuilder();
        String column = null;
        boolean append = true;
        
        switch (sqlType) {
            case INSERT:
                columns.append("insert into ").append(table).append("(");
                
                StringBuilder holders = new StringBuilder(") values(");
                
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            column = getColumn(prefix, methodName);
                            if (append) {
                                columns.append(column);
                                holders.append(":").append(column);
                                //holders.append("?");
                                append = false;
                            } else {
                                columns.append(",").append(column);
                                holders.append(",").append(":").append(column);
                                //holders.append(",").append("?");
                            }
                            resultMap.put(column, value);
                        }
                    } 
                }
                columns.append(holders).append(")");
                resultMap.put(SQL_KEY, columns.toString());
                break;
            case SELECT:
                columns.append("select * from ").append(table);
                
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            column = getColumn(prefix, methodName);
                            append = buildWhere(columns, column, append);
                            resultMap.put(column, value);
                        }
                    } 
                }
                resultMap.put(SQL_KEY, columns.toString());
                break;
            case DELETE:
                columns.append("delete from ").append(table);
                
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            column = getColumn(prefix, methodName);
                            append = buildWhere(columns, column, append);
                            resultMap.put(column, value);
                        }
                    } 
                }
                resultMap.put(SQL_KEY, columns.toString());
                break;
            case UPDATE:
                updateSets.append("update ").append(table);
                
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            column = getColumn(prefix, methodName);
                            if (append) {
                                columns.append(" where ").append(column).append(" = :").append(column);
                                updateSets.append(" set ").append(column).append(" = :").append(column);
                                append = false;
                            } else {
                                columns.append(" and ").append(column).append(" = :").append(column);
                                updateSets.append(", ").append(column).append(" = :").append(column);
                            }
                            resultMap.put(column, value);
                        }
                    } 
                }
                updateSets.append(columns);
                resultMap.put(SQL_KEY, updateSets.toString());
                break;
            case WHERE:
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            column = getColumn(prefix, methodName);
                            append = buildWhere(columns, column, append);
                            resultMap.put(column, value);
                        }
                    } 
                }
                resultMap.put(SQL_KEY, columns.toString());
                break;
            case UPDATESET:
                updateSets.append("update ").append(table);
                String subKey = "";
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            if (prefix) {
                                column = CaseUtils.toUnderCase(methodName.substring(3));
                                subKey = column.substring(1);
                            } else {
                                column = CaseUtils.underCase(methodName.substring(3));
                                subKey = column;
                            }
                            
                            if (append) {
                                updateSets.append(" set ").append(subKey).append(" = :").append(column);
                                append = false;
                            } else {
                                updateSets.append(", ").append(subKey).append(" = :").append(column);
                            }
                            resultMap.put(column, value);
                        }
                    } 
                }
                resultMap.put(SQL_KEY, updateSets.toString());
                break;
            case NULL:
                
            default:
                for (String methodName : methodNames) {
                    if (methodName.startsWith("get")) {
                        Object value = methodAccess.invoke(fromBean, methodName, (Object[])null);
                        if (value != null) {
                            if (prefix) {
                                resultMap.put(CaseUtils.toUnderCase(methodName.substring(3)), value);
                            } else {
                                resultMap.put(CaseUtils.underCase(methodName.substring(3)), value);
                            }
                        }
                    } 
                }
                break;
        }
        return resultMap;
    }

    private boolean buildWhere(StringBuilder columns, String column, boolean append) {
        if (append) {
            columns.append(" where ").append(column).append(" = :").append(column);
            append = false;
        } else {
            columns.append(" and ").append(column).append(" = :").append(column);
        }
        return append;
    }

    private String getColumn(boolean prefix, String methodName) {
        String column;
        if (prefix) {
            column = CaseUtils.toUnderCase(methodName.substring(3));
        } else {
            column = CaseUtils.underCase(methodName.substring(3));
        }
        return column;
    }
	
    @Override
    @Autowired
    public void setSpringJdbcTemplate(SpringJdbcTemplate springJdbcTemplate) {
        this.springJdbcTemplate = springJdbcTemplate;
    }

}
