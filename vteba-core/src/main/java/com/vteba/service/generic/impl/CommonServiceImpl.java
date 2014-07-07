package com.vteba.service.generic.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.service.generic.CommonService;
import com.vteba.tx.generic.Page;
import com.vteba.tx.jdbc.spring.spi.SpringGenericDao;

/**
 * 基于SpringJdbcTemplate的通用泛型Service实现。
 * @author yinlei
 * @date 2014-7-5
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public abstract class CommonServiceImpl<T, ID extends Serializable> implements CommonService<T, ID> {
	protected SpringGenericDao<T, ID> springGenericDaoImpl;
	
	public abstract void setSpringGenericDaoImpl(SpringGenericDao<T, ID> springGenericDaoImpl);

	public ID save(T entity) {
		return springGenericDaoImpl.save(entity);
	}

	public int persist(T entity) {
	    return springGenericDaoImpl.persist(entity);
	}
	
	public int update(T entity) {
		return springGenericDaoImpl.update(entity);
	}

//	public int saveOrUpdate(T entity) {
//	    return springGenericDaoImpl.saveOrUpdate(entity);
//	}
	
	public int updateBatch(T setValue, T params) {
		return springGenericDaoImpl.updateBatch(setValue, params);
	}

	public int updateBatch(T setValue, Map<String, ?> params) {
		return springGenericDaoImpl.updateBatch(setValue, params);
	}

	public int updateBatch(String sql, Map<String, ?> params) {
		return springGenericDaoImpl.updateBatch(sql, params);
	}

	public int updateBatch(String sql, T params) {
		return springGenericDaoImpl.updateBatch(sql, params);
	}

	public int updateBatch(String sql, Object... params) {
		return springGenericDaoImpl.updateBatch(sql, params);
	}

	public T get(ID id) {
		return springGenericDaoImpl.get(id);
	}

	public T unique(T entity) {
		return springGenericDaoImpl.unique(entity);
	}

	public T unique(Map<String, ?> params) {
		return springGenericDaoImpl.unique(params);
	}

	public int delete(ID id) {
		return springGenericDaoImpl.delete(id);
	}

	public int deleteBatch(T entity) {
		return springGenericDaoImpl.deleteBatch(entity);
	}

	public int deleteBatch(String sql, T entity) {
		return springGenericDaoImpl.deleteBatch(sql, entity);
	}

	public int deleteBatch(String sql, Map<String, ?> params) {
		return springGenericDaoImpl.deleteBatch(sql, params);
	}

	public int deleteBatch(String sql, Object... params) {
		return springGenericDaoImpl.deleteBatch(sql, params);
	}

	public List<T> query(T entity) {
		return springGenericDaoImpl.query(entity);
	}

	public List<T> query(Map<String, ?> params) {
		return springGenericDaoImpl.query(params);
	}

	public List<T> query(String sql, T params) {
		return springGenericDaoImpl.query(sql, params);
	}

	public List<T> query(String sql, Map<String, ?> params) {
		return springGenericDaoImpl.query(sql, params);
	}

	public List<T> query(String sql, Object... params) {
		return springGenericDaoImpl.query(sql, params);
	}

	public <VO> List<VO> queryList(String sql, VO params) {
		return springGenericDaoImpl.queryList(sql, params);
	}

	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Map<String, ?> params) {
		return springGenericDaoImpl.queryList(sql, resultClass, params);
	}

	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Object... params) {
		return springGenericDaoImpl.queryList(sql, resultClass, params);
	}

	public Page<T> queryForPage(Page<T> page, T params) {
		return springGenericDaoImpl.queryForPage(page, params);
	}

	public Page<T> queryForPage(Page<T> page, String sql, T params) {
		return springGenericDaoImpl.queryForPage(page, sql, params);
	}

	public Page<T> queryForPage(Page<T> page, Map<String, ?> params) {
		return springGenericDaoImpl.queryForPage(page, params);
	}

	public Page<T> queryForPage(Page<T> page, String sql,
			Map<String, ?> params) {
		return springGenericDaoImpl.queryForPage(page, sql, params);
	}

	public Page<T> queryForPage(Page<T> page, String sql, Object... params) {
		return springGenericDaoImpl.queryForPage(page, sql, params);
	}

	public <VO> Page<VO> queryPageList(Page<VO> page, String sql, VO params) {
		return springGenericDaoImpl.queryPageList(page, sql, params);
	}

	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Map<String, ?> params) {
		return springGenericDaoImpl.queryPageList(page, sql, resultClass,
				params);
	}

	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Object... params) {
		return springGenericDaoImpl.queryPageList(page, sql, resultClass,
				params);
	}

	public <X> X queryForObject(String sql, Map<String, ?> paramMap,
			Class<X> requiredType) {
		return springGenericDaoImpl.queryForObject(sql, paramMap, requiredType);
	}

	public <X> X queryForObject(String sql, Class<X> requiredType,
			Object... params) {
		return springGenericDaoImpl.queryForObject(sql, requiredType, params);
	}

}
