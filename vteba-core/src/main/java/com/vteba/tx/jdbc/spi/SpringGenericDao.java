package com.vteba.tx.jdbc.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Spring泛型Dao抽象。
 * @author yinlei
 * date 2013-7-6 下午4:40:09
 */
public interface SpringGenericDao<T, ID extends Serializable> {
    
    /**
	 * 保存实体entity（新增，insert非空栏位）
	 * @param entity
	 * @return 实体主键
	 */
	public ID save(T entity);
	
	/**
     * 保存实体entity（新增，insert所有栏位）
     * @param entity
     * @return 实体主键
     */
    public ID saveAll(T entity);
	
	/**
	 * 根据id更新实体entity非空栏位（主键属性非空）
	 * @param entity
	 */
	public int update(T entity);
	
	/**
     * 根据id更新实体entity所有栏位（主键属性非空）
     * @param entity
     */
    public int updateAll(T entity);
	
	/**
     * 批量更新实体entity，使用命名sql
     * @param entity set参数
     * @param criteria where参数
     */
    public int updateBatch(T entity, T criteria);
	
    /**
     * 批量更新实体entity，使用命名sql
     * @param entity set参数
     * @param params where参数
     */
    public int updateBatch(T entity, Map<String, Object> params);
    
    /**
     * 批量更新实体entity，使用命名sql
     * @param sql 命名sql语句
     * @param params sql参数
     */
    public int updateBatch(String sql, Map<String, Object> params);
    
    /**
     * 批量更新实体entity，使用命名sql
     * @param sql 命名sql语句
     * @param params 实体携带的sql参数
     */
    public int updateBatch(String sql, T params);
    
    /**
     * 批量更新实体entity，使用普通sql，？为占位符
     * @param sql 普通sql语句
     * @param params sql参数
     */
    public int updateBatch(String sql, Object... params);
    
	/**
	 * 根据ID查询entity实体
	 * @param id 主键
	 * @return 实体
	 */
	public T get(ID id);
	
	/**
     * 根据T查询entity实体
     * @param entity 含主键的实体
     * @return 实体
     */
    public T get(T entity);
	
	/**
	 * 根据主键删除实体
	 * @param entity 参数含主键
	 */
	//public int delete(T entity);
	
	/**
	 * 根据主键删除实体
	 * @param id 主键
	 */
	public int delete(ID id);
	
	/**
     * 根据entity携带的条件删除实体
     * @param entity 条件
     */
    public int deleteBatch(T entity);
    
    /**
     * 根据entity携带的条件删除实体，使用命名sql
     * @param sql sql语句
     * @param entity 实体携带的sql参数
     */
    public int deleteBatch(String sql, T entity);
    
    /**
     * 根据条件删除实体，使用命名sql
     * @param sql sql语句
     * @param params sql参数
     */
    public int deleteBatch(String sql, Map<String, Object> params);
    
    /**
     * 根据条件删除实体，使用普通sql，？为占位符
     * @param sql sql语句
     * @param params sql参数
     */
    public int deleteBatch(String sql, Object... params);
    
    /**
     * 根据entity携带条件查询实体所有栏位list
     * @param entity 条件参数
     * @return 实体list
     */
    public List<T> query(T entity);
    
    /**
     * 根据条件查询实体所有栏位list
     * @param params 条件参数
     * @return 实体list
     */
    public List<T> query(Map<String, Object> params);
    
    /**
     * 根据条件查询实体指定栏位list，sql为命名sql
     * @param sql 命名sql语句
     * @param params 实体携带的sql参数
     * @return 实体list
     */
    public List<T> query(String sql, T params);
    
    /**
     * 根据条件查询实体指定栏位list，sql为命名sql
     * @param sql 命名sql语句
     * @param params 条件参数
     * @return 实体list
     */
    public List<T> query(String sql, Map<String, Object> params);
    
    /**
     * 根据条件查询实体list，sql为普通sql，？为占位符
     * @param sql sql语句
     * @param params 条件参数
     * @return 实体list
     */
    public List<T> query(String sql, Object... params);
}
