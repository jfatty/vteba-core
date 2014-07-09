package com.vteba.service.generic.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.service.generic.IGenericService;
import com.vteba.tx.generic.Page;
import com.vteba.tx.hibernate.IHibernateGenericDao;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;

/**
 * 通用泛型Service实现，供其他Service继承，简化service实现。
 * @author yinlei 
 * date 2012-6-29 下午11:28:32
 */
public abstract class GenericServiceImpl<T, ID extends Serializable> implements IGenericService<T, ID> {

	protected IHibernateGenericDao<T, ID> hibernateGenericDaoImpl;
	protected SpringJdbcTemplate springJdbcTemplate;
	
	/**
	 * 子类如果要使用SpringJdbcTemplate，请重写该方法，注入相应的SpringJdbcTemplate。<br/>
	 * 并且只能用于查询，否则破坏hibernate一级和二级缓存。
	 * @param springJdbcTemplate 具体的SpringJdbcTemplate实例
	 * @author yinlei
	 * date 2013-6-27 下午9:19:15
	 */
	public void setSpringJdbcTemplate(SpringJdbcTemplate springJdbcTemplate) {
		this.springJdbcTemplate = springJdbcTemplate;
	}
	
	/**
	 * 延迟到子类中注入具体dao实例
	 * @param hibernateGenericDaoImpl 实现IHibernateGenericDao具体的dao实例
	 * @author yinlei
	 * date 2012-6-22 下午4:04:41
	 */
	public abstract void setHibernateGenericDaoImpl(IHibernateGenericDao<T, ID> hibernateGenericDaoImpl);
	
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

    @Override
    public void saveOrUpdate(T entity) {
    	hibernateGenericDaoImpl.saveOrUpdate(entity);
    }
    
    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.lang.String, java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2) {
        return hibernateGenericDaoImpl.getEntityList(propName1, value1, propName2, value2);
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
	
	@Override
	public void saveEntityBatch(List<T> list, int batchSize) {
		for (int i = 0; i< list.size(); i++) {
			T entity = list.get(i);
			persist(entity);
			if (i != 0 && i % batchSize == 0) {
				hibernateGenericDaoImpl.flush();
			}
		}
	}
	
	@Override
	public void deleteEntityBatch(List<ID> list) {
		for (ID id : list) {
			delete(id);
		}
	}

	@Override
	public int updateBatch(T setValue, T params) {
		return hibernateGenericDaoImpl.updateBatch(setValue, params);
	}

	@Override
	public int updateBatch(T setValue, Map<String, ?> params) {
		return hibernateGenericDaoImpl.updateBatch(setValue, params);
	}

	@Override
	public int deleteBatch(T entity) {
		return hibernateGenericDaoImpl.deleteBatch(entity);
	}

	@Override
	public int deleteBatch(Map<String, ?> params) {
		return hibernateGenericDaoImpl.deleteBatch(params);
	}
	
}
