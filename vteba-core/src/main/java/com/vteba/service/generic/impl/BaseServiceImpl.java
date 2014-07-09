package com.vteba.service.generic.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;
import com.vteba.tx.hibernate.IHibernateGenericDao;


public class BaseServiceImpl<T, ID extends Serializable> implements IBaseService<T, ID> {
    private IHibernateGenericDao<T, ID> hibernateGenericDaoImpl;

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#save(T)
     */
    @Override
    public ID save(T entity) {
        return hibernateGenericDaoImpl.save(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#persist(T)
     */
    @Override
    public void persist(T entity) {
        hibernateGenericDaoImpl.persist(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.util.Map)
     */
    @Override
    public List<T> getEntityList(Map<String, ?> params) {
        return hibernateGenericDaoImpl.getEntityList(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(T)
     */
    @Override
    public List<T> getEntityList(T params) {
        return hibernateGenericDaoImpl.getEntityList(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(T, java.util.Map)
     */
    @Override
    public List<T> getEntityList(T params, Map<String, String> orderMaps) {
        return hibernateGenericDaoImpl.getEntityList(params, orderMaps);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> getEntityList(String propName, Object value) {
        return hibernateGenericDaoImpl.getEntityList(propName, value);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#update(T)
     */
    @Override
    public void update(T entity) {
        hibernateGenericDaoImpl.update(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.lang.String, java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2) {
        return hibernateGenericDaoImpl.getEntityList(propName1, value1, propName2, value2);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#merge(T)
     */
    @Override
    public T merge(T entity) {
        return hibernateGenericDaoImpl.merge(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getAll()
     */
    @Override
    public List<T> getAll() {
        return hibernateGenericDaoImpl.getAll();
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#queryForPage(com.vteba.tx.generic.Page, java.util.Map)
     */
    @Override
    public Page<T> queryForPage(Page<T> page, Map<String, ?> params) {
        return hibernateGenericDaoImpl.queryForPage(page, params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#load(java.lang.Class, ID)
     */
    @Override
    public T load(Class<T> entity, ID id) {
        return hibernateGenericDaoImpl.load(entity, id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#load(ID)
     */
    @Override
    public T load(ID id) {
        return hibernateGenericDaoImpl.load(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#get(ID)
     */
    @Override
    public T get(ID id) {
        return hibernateGenericDaoImpl.get(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#delete(ID)
     */
    @Override
    public void delete(ID id) {
        hibernateGenericDaoImpl.delete(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#delete(T)
     */
    @Override
    public void delete(T entity) {
        hibernateGenericDaoImpl.delete(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(java.lang.String, java.lang.String)
     */
    @Override
    public List<T> getListByLike(String propertyName, String propertyValue) {
        return hibernateGenericDaoImpl.getListByLike(propertyName, propertyValue);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(T)
     */
    @Override
    public List<T> getListByLike(T model) {
        return hibernateGenericDaoImpl.getListByLike(model);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(T, java.util.Map)
     */
    @Override
    public List<T> getListByLike(T model, Map<String, String> orderMaps) {
        return hibernateGenericDaoImpl.getListByLike(model, orderMaps);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(java.lang.String, java.lang.Object)
     */
    @Override
    public T uniqueResult(String propertyName, Object value) {
        return hibernateGenericDaoImpl.uniqueResult(propertyName, value);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(java.util.Map)
     */
    @Override
    public T uniqueResult(Map<String, Object> params) {
        return hibernateGenericDaoImpl.uniqueResult(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(T)
     */
    @Override
    public T uniqueResult(T model) {
        return hibernateGenericDaoImpl.uniqueResult(model);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#queryForPage(com.vteba.tx.generic.Page, T)
     */
    @Override
    public Page<T> queryForPage(Page<T> page, T entity) {
        return hibernateGenericDaoImpl.queryForPage(page, entity);
    }
    
}
