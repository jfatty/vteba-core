package com.vteba.service.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;

/**
 * 基于SpringJdbcTemplate实现的通用泛型service。
 * @author yinlei
 * @date 2014-7-5
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface CommonService<T, ID extends Serializable> {

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
    //public ID saveAll(T entity);
	
	/**
	 * 根据id更新实体entity非空栏位（主键属性非空）
	 * @param entity
	 */
	public int update(T entity);
	
	/**
     * 根据id更新实体entity所有栏位（主键属性非空）
     * @param entity
     */
    //public int updateAll(T entity);
	
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
    public T unique(T entity);
    
    /**
     * 根据条件查询唯一实体，多于一个报异常
     * @param params 参数
     * @return 实体
     */
    public T unique(Map<String, Object> params);
	
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
    
    /**
     * 根据条件查询VO list，sql为命名sql，一般用于多表连接
     * @param sql 命名sql语句
     * @param params 携带sql参数的VO
     * @return VO bean list
     */
    public <VO> List<VO> queryList(String sql, VO params);
    
    /**
     * 根据条件查询VO list，sql为命名sql，一般用于多表连接
     * @param sql 命名sql语句
     * @param params 携带sql参数的VO
     * @param resultClass VO class，结果对象类型
     * @return VO bean list
     */
    public <VO> List<VO> queryList(String sql, Class<VO> resultClass, Map<String, Object> params);
    
    /**
     * 根据条件查询VO list，sql为普通sql，一般用于多表连接
     * @param sql sql语句
     * @param resultClass VO class，结果对象类型
     * @param params sql参数
     * @return VO bean list
     */
    public <VO> List<VO> queryList(String sql, Class<VO> resultClass, Object... params);
    
    /**
     * 根据条件分页查询实体list，使用命名sql
     * @param page 分页条件以及排序条件
     * @param params 查询条件参数
     * @return 实体分页数据
     */
    public Page<T> queryForPage(Page<T> page, T params);
    
    /**
     * 根据条件分页查询实体list，使用命名sql
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param params 查询条件参数
     * @return 实体分页数据
     */
    public Page<T> queryForPage(Page<T> page, String sql, T params);
    
    /**
     * 根据条件分页查询实体list，使用命名sql
     * @param page 分页条件以及排序条件
     * @param params 查询条件参数
     * @return 实体分页数据
     */
    public Page<T> queryForPage(Page<T> page, Map<String, Object> params);
    
    /**
     * 根据条件分页查询实体list，使用命名sql
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param params 查询条件参数
     * @return 实体分页数据
     */
    public Page<T> queryForPage(Page<T> page, String sql, Map<String, Object> params);
    
    /**
     * 根据条件分页查询实体list，sql为普通sql，？为占位符
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param params sql参数
     * @return 实体分页数据
     */
    public Page<T> queryForPage(Page<T> page, String sql, Object... params);
    
    /**
     * 根据条件分页查询vo bean list，使用命名sql，一般用于多表连接
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param params 查询条件参数
     * @return vo bean list
     */
    public <VO> Page<VO> queryPageList(Page<VO> page, String sql, VO params);
    
    /**
     * 根据条件分页查询vo bean list，使用命名sql，一般用于多表连接
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param resultClass 结果VO类型
     * @param params 查询条件参数
     * @return vo bean list
     */
    public <VO> Page<VO> queryPageList(Page<VO> page, String sql, Class<VO> resultClass, Map<String, Object> params);
    
    /**
     * 根据条件分页查询vo bean list，使用命名sql，一般用于多表连接
     * @param page 分页条件以及排序条件
     * @param sql sql语句
     * @param resultClass 结果VO类型
     * @param params 查询条件参数
     * @return vo bean list
     */
    public <VO> Page<VO> queryPageList(Page<VO> page, String sql, Class<VO> resultClass, Object... params);
    
    /**
	 * 执行查询语句。只能查询基本类型。
	 * @param sql 命名参数sql语句
	 * @param paramMap sql参数，key为命名参数名
	 * @param requiredType 参数基本类型类
	 * @return 基本类型
	 */
    public <X> X queryForObject(String sql, Map<String, ?> paramMap, Class<X> requiredType);
    
    /**
	 * 执行查询语句。只能查询基本类型。
	 * @param sql sql语句，以？为占位符
	 * @param requiredType 基本类型类
	 * @param params sql参数
	 * @return 基本类型
	 */
	public <X> X queryForObject(String sql, Class<X> requiredType, Object... params);
}
