package com.vteba.tx.hibernate.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.vteba.common.exception.NonUniqueException;
import com.vteba.tx.generic.Page;
import com.vteba.tx.hibernate.BaseGenericDao;
import com.vteba.tx.hibernate.transformer.PrimitiveResultTransformer;
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
	
	/**
	 * {@link #queryForObject(String, Class, Object...)}
	 */
	@Deprecated
	public <X> List<X> queryPrimitiveList(String field, Class<X> resultClass,
			Map<String, ?> params) {
		StringBuilder sb = new StringBuilder("select distinct ");
		sb.append(field).append(" from ").append(entityName);
		sb.append(buildWhere(params));
		Query query = createQuery(sb.toString(), params);
		query.setResultTransformer(new PrimitiveResultTransformer(resultClass));
		List<X> list = query.list();
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	/**
	 * {@link #queryForList(String, Class, Object...)}
	 */
	@Deprecated
	public <X> X queryForPrimitive(String field, Class<X> resultClass,
			Map<String, ?> params) {
		List<X> list = queryPrimitiveList(field, resultClass, params);
		if (list.size() == 0 || list.size() >= 2) {
			throw new NonUniqueException("查询结果不唯一。");
		}
		return list.get(0);
	}

	@Deprecated
	@Override
	public <X extends Number> List<X> statsForList(String statsField,
			Class<X> resultClass, Map<String, ?> params) {
		return queryPrimitiveList(statsField, resultClass, params);
	}

	@Deprecated
	@Override
	public <X extends Number> X statsPrimitive(String statsField,
			Class<X> resultClass, Map<String, ?> params) {
		return queryForPrimitive(statsField, resultClass, params);
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
