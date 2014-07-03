package com.vteba.user;

import com.vteba.tx.jdbc.spring.impl.SpringGenericDaoImpl;

public class UserDaoImpl extends SpringGenericDaoImpl<EmpUser, Long> {

	public UserDaoImpl() {
		super(EmpUser.class);
	}

	public UserDaoImpl(Class<EmpUser> entityClass) {
		super(entityClass);
	}
	
}
