package com.vteba.tx.matrix.shards.impl;

import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;
import com.vteba.tx.matrix.shards.spi.ShardsGenericDao;

/**
 * 分区表通用Dao实现。
 * @author yinlei
 * @since 2013-12-22
 * @param <T> 分区表对应的实体
 */
public class ShardsGenericDaoImpl<T> implements ShardsGenericDao<T> {

	@Override
	public String save(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int persist(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBatch(T setValue, T params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBatch(T setValue, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBatch(String sql, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBatch(String sql, T params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateBatch(String sql, Object... params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public T get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T unique(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T unique(Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteBatch(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteBatch(String sql, T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteBatch(String sql, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteBatch(String sql, Object... params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<T> query(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> query(Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> query(String sql, T params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> query(String sql, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> query(String sql, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> List<VO> queryList(String sql, VO params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, T params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, T params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql, VO params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Map<String, ?> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X queryForObject(String sql, Map<String, ?> paramMap,
			Class<X> requiredType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X queryForObject(String sql, Class<X> requiredType,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
