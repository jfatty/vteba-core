package com.vteba.tx.jdbc.spring.impl;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vteba.lang.bytecode.ConstructorAccess;
import com.vteba.lang.bytecode.MethodAccess;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.spi.SpringGenericDao;
import com.vteba.utils.reflection.AsmUtils;
import com.vteba.utils.reflection.BeanCopyUtils;

/**
 * spring通用泛型dao实现。
 * @author yinlei 
 * @since 2013-7-6 16:00
 * @param <T> 泛型实体
 * @param <ID> 主键类型
 */
public class SpringGenericDaoImpl<T, ID extends Serializable> extends AbstractGenericDao<T, ID> implements SpringGenericDao<T, ID> {
    private ConstructorAccess<T> constructorAccess = AsmUtils.get().createConstructorAccess(entityClass);
    private MethodAccess methodAccess = AsmUtils.get().createMethodAccess(entityClass);
    
	public SpringGenericDaoImpl() {
        super();
    }
	
    public SpringGenericDaoImpl(Class<T> entityClass) {
        super(entityClass);
    }

    public T mapRows(ResultSet rs, int rowNum) throws SQLException {
        T entity = constructorAccess.newInstance();
        for (int i = 0, size = setterList.size(); i < size; i++) {
            Object object = rs.getObject(i + 1);//这里其实可以具体的类型，获取更具体的类型值
            if (object != null) {// 避免不必要的字节码操作，毕竟有性能损失
                methodAccess.invoke(entity, setterList.get(i), object);
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
