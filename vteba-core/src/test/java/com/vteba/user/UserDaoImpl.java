package com.vteba.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.impl.AbstractGenericDao;
import com.vteba.tx.jdbc.spring.impl.SqlType;
import com.vteba.utils.reflection.BeanCopyUtils;

/**
 * spring generic dao使用例子，以后会使用代码生成工具生成。
 * @author  yinlei 
 * @since 2014-7-2
 */
public class UserDaoImpl extends AbstractGenericDao<EmpUser, Long> {

	public UserDaoImpl() {
		super(EmpUser.class);
	}

	public UserDaoImpl(Class<EmpUser> entityClass) {
		super(entityClass);
	}

    @Override
    public void setSpringJdbcTemplate(SpringJdbcTemplate skmbwJdbcTemplate) {
        this.springJdbcTemplate = skmbwJdbcTemplate;
    }

    // 完全可以使用代码生成工具来生成
    @Override
    public EmpUser mapRows(ResultSet rs, int rowNum) throws SQLException {
        EmpUser user = new EmpUser();
        user.setUserName(rs.getString("user_name"));
        user.setAge(rs.getInt("age"));
        return user;
    }

    // 完全可以使用代码生成工具来生成
    @Override
    public Map<String, Object> mapBean(EmpUser entity, boolean prefix, SqlType sqlType) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (entity != null) {
            if (prefix) {
                if (entity.getUserName() != null) {
                    resultMap.put("_user_name", entity.getUserName());
                }
                if (entity.getAge() != null) {
                    resultMap.put("_age", entity.getAge());
                }
            } else {
                if (entity.getUserName() != null) {
                    resultMap.put("user_name", entity.getUserName());
                }
                if (entity.getAge() != null) {
                    resultMap.put("age", entity.getAge());
                }
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> mapBean(Object entity) {
    	return BeanCopyUtils.get().toMap(entity, false, false, null);
    }
    
    @Override
    public EmpUser mapRows(ResultSet rs, String sql, Class<?> resultClass) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
