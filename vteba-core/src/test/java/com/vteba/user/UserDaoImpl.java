package com.vteba.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.impl.AbstractGenericDao;

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
    public Map<String, Object> mapBean(EmpUser entity, boolean prefix, Map<String, Object> params) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (entity != null) {
            if (prefix) {
                if (entity.getUserName() != null) {
                    resultMap.put("getUserName", entity.getUserName());
                    params.put("getUserName", entity.getUserName());
                }
                if (entity.getAge() != null) {
                    resultMap.put("getAge", entity.getAge());
                    params.put("getAge", entity.getAge());
                }
            } else {
                if (entity.getUserName() != null) {
                    resultMap.put("userName", entity.getUserName());
                }
                if (entity.getAge() != null) {
                    resultMap.put("age", entity.getAge());
                }
            }
        }
        return resultMap;
    }

}
