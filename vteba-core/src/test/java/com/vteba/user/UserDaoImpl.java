package com.vteba.user;

import com.vteba.tx.jdbc.spring.SpringGenericDaoImpl;

public class UserDaoImpl extends SpringGenericDaoImpl<EmpUser, Long> {

	public UserDaoImpl() {
		super("emp_user", EmpUser.class);
	}

	public UserDaoImpl(String tableName, Class<EmpUser> entityClass) {
		super(tableName, entityClass);
	}
	
}
