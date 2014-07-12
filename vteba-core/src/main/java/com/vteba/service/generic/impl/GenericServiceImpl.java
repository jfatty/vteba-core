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
 * date 2012-6-4 下午11:28:32
 */
public abstract class GenericServiceImpl<T, ID extends Serializable> implements IGenericService<T, ID> {

	protected IHibernateGenericDao<T, ID> baseGenericDaoImpl;
	protected SpringJdbcTemplate springJdbcTemplate;
	
	/**
	 * 子类如果要使用SpringJdbcTemplate，请重写该方法，注入相应的SpringJdbcTemplate。<br/>
	 * 并且只能用于查询，否则破坏hibernate一级和二级缓存。
	 * @param springJdbcTemplate 具体的SpringJdbcTemplate实例
	 * @author yinlei
	 * date 2013-6-5 下午9:19:15
	 */
	public void setSpringJdbcTemplate(SpringJdbcTemplate springJdbcTemplate) {
		this.springJdbcTemplate = springJdbcTemplate;
	}
	
	/**
	 * 延迟到子类中注入具体dao实例
	 * @param baseGenericDaoImpl 实现baseGenericDaoImpl具体的dao实例
	 * @author yinlei
	 * date 2012-6-5 下午4:04:41
	 */
	public abstract void setbaseGenericDaoImpl(IHibernateGenericDao<T, ID> baseGenericDaoImpl);
	
	/* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#save(T)
     */
    @Override
    public ID save(T entity) {
        return baseGenericDaoImpl.save(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#persist(T)
     */
    @Override
    public void persist(T entity) {
        baseGenericDaoImpl.persist(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.util.Map)
     */
    @Override
    public List<T> getEntityList(Map<String, ?> params) {
        return baseGenericDaoImpl.getEntityList(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(T)
     */
    @Override
    public List<T> getEntityList(T params) {
        return baseGenericDaoImpl.getEntityList(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(T, java.util.Map)
     */
    @Override
    public List<T> getEntityList(T params, Map<String, String> orderMaps) {
        return baseGenericDaoImpl.getEntityList(params, orderMaps);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> getEntityList(String propName, Object value) {
        return baseGenericDaoImpl.getEntityList(propName, value);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#update(T)
     */
    @Override
    public void update(T entity) {
        baseGenericDaoImpl.update(entity);
    }

    @Override
    public void saveOrUpdate(T entity) {
    	baseGenericDaoImpl.saveOrUpdate(entity);
    }
    
    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getEntityList(java.lang.String, java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2) {
        return baseGenericDaoImpl.getEntityList(propName1, value1, propName2, value2);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getAll()
     */
    @Override
    public List<T> getAll() {
        return baseGenericDaoImpl.getAll();
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#queryForPage(com.vteba.tx.generic.Page, java.util.Map)
     */
    @Override
    public Page<T> queryForPage(Page<T> page, Map<String, ?> params) {
        return baseGenericDaoImpl.queryForPage(page, params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#load(java.lang.Class, ID)
     */
    @Override
    public T load(Class<T> entity, ID id) {
        return baseGenericDaoImpl.load(entity, id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#load(ID)
     */
    @Override
    public T load(ID id) {
        return baseGenericDaoImpl.load(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#get(ID)
     */
    @Override
    public T get(ID id) {
        return baseGenericDaoImpl.get(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#delete(ID)
     */
    @Override
    public void delete(ID id) {
        baseGenericDaoImpl.delete(id);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#delete(T)
     */
    @Override
    public void delete(T entity) {
        baseGenericDaoImpl.delete(entity);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(java.lang.String, java.lang.String)
     */
    @Override
    public List<T> getListByLike(String propertyName, String propertyValue) {
        return baseGenericDaoImpl.getListByLike(propertyName, propertyValue);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(T)
     */
    @Override
    public List<T> getListByLike(T model) {
        return baseGenericDaoImpl.getListByLike(model);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#getListByLike(T, java.util.Map)
     */
    @Override
    public List<T> getListByLike(T model, Map<String, String> orderMaps) {
        return baseGenericDaoImpl.getListByLike(model, orderMaps);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(java.lang.String, java.lang.Object)
     */
    @Override
    public T uniqueResult(String propertyName, Object value) {
        return baseGenericDaoImpl.uniqueResult(propertyName, value);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(java.util.Map)
     */
    @Override
    public T uniqueResult(Map<String, ?> params) {
        return baseGenericDaoImpl.uniqueResult(params);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#uniqueResult(T)
     */
    @Override
    public T uniqueResult(T model) {
        return baseGenericDaoImpl.uniqueResult(model);
    }

    /* (non-Javadoc)
     * @see com.vteba.service.generic.impl.IBaseService#queryForPage(com.vteba.tx.generic.Page, T)
     */
    @Override
    public Page<T> queryForPage(Page<T> page, T entity) {
        return baseGenericDaoImpl.queryForPage(page, entity);
    }
	
	@Override
	public void saveEntityBatch(List<T> list, int batchSize) {
		for (int i = 0; i< list.size(); i++) {
			T entity = list.get(i);
			persist(entity);
			if (i != 0 && i % batchSize == 0) {
				baseGenericDaoImpl.flush();
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
		return baseGenericDaoImpl.updateBatch(setValue, params);
	}

	@Override
	public int updateBatch(T setValue, Map<String, ?> params) {
		return baseGenericDaoImpl.updateBatch(setValue, params);
	}

	@Override
	public int deleteBatch(T entity) {
		return baseGenericDaoImpl.deleteBatch(entity);
	}

	@Override
	public int deleteBatch(Map<String, ?> params) {
		return baseGenericDaoImpl.deleteBatch(params);
	}

    @Override
    public List<T> pagedQueryList(Page<T> page, Map<String, ?> params) {
        return pagedQueryList(page, params);
    }

    @Override
    public List<T> pagedQueryList(Page<T> page, T params) {
        return baseGenericDaoImpl.pagedQueryList(page, params);
    }
	
}
