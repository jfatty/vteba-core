package com.vteba.tx.hibernate.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.vteba.tx.generic.Page;
import com.vteba.tx.hibernate.BaseGenericDao;
import com.vteba.utils.ofbiz.LangUtils;

@SuppressWarnings("unchecked")
public class BaseGenericDaoImpl<T, ID extends Serializable> extends
		HibernateGenericDaoImpl<T, ID> implements BaseGenericDao<T, ID> {

	public BaseGenericDaoImpl() {

	}

	public BaseGenericDaoImpl(Class<T> entityClass) {
		super(entityClass);
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {

	}

	@Override
	public Page<T> queryForPage(Page<T> page, String propName, Object value) {
		StringBuilder sb = new StringBuilder(selectAll);
        sb.append(" where ").append(propName).append(" = :").append(propName);
        sb.append(buildOrderBy(page.getOrders()));
        String hql = sb.toString();
        long totalRecordCount = countHqlResult(hql, LangUtils.toMap(propName, value));
        if (totalRecordCount <= 0) {
        	return page;
        }
        Query query = getSession().createQuery(hql);
        query.setParameter(propName, value);
        page.setTotalRecordCount(totalRecordCount);
        setParameterToQuery(page, query);
        List<T> result = query.list();
        page.setResult(result);
	    return page;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String propName1, Object value1,
			String propName2, Object value2) {
		StringBuilder sb = new StringBuilder(selectAll);
        sb.append(" where ").append(propName1).append(" = :").append(propName1);
        sb.append(" and ").append(propName2).append(" = :").append(propName2);
        sb.append(buildOrderBy(page.getOrders()));
        String hql = sb.toString();
        long totalRecordCount = countHqlResult(hql, LangUtils.toMap(propName1, value1, propName2, value2));
        if (totalRecordCount <= 0) {
        	return page;
        }
        Query query = getSession().createQuery(hql);
        query.setParameter(propName1, value1);
        query.setParameter(propName2, value2);
        page.setTotalRecordCount(totalRecordCount);
        setParameterToQuery(page, query);
        List<T> result = query.list();
        page.setResult(result);
	    return page;
	}
	
	@Override
	public List<Object[]> queryForObject(String hql, Object... values) {
		return hqlQueryForObject(hql, false, values);
	}

	@Override
	public <X> X queryForObject(String hql, Class<X> clazz, Object... values) {
		return hqlQueryForObject(hql, clazz, values);
	}

	@Override
	public <X> List<X> queryForList(String hql, Class<X> clazz,
			Object... values) {
		return hqlQueryForList(hql, clazz, values);
	}

}
