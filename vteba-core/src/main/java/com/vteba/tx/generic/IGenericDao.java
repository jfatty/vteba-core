package com.vteba.tx.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 公共DAO泛型接口
 * @author yinlei 
 * 2012-4-1 下午7:24:07
 * @param <T> 实体类型
 * @param <ID> 主键类型，一般是String或者Long
 */
public interface IGenericDao<T, ID extends Serializable> {
	/**
	 * 保存实体entity，立即持久化，session.save(entity)
	 * @param entity
	 * @return 实体主键
	 */
	public ID save(T entity);
	
	/**
	 * 持久化实体，session.persist(entity)，尽可能延迟到事务结束
	 * @param entity
	 */
	public void persist(T entity);
	
	/**
	 * 保存或更新实体entity，session.saveOrUpdate(entity)
	 * @param entity
	 */
	public void saveOrUpdate(T entity);
	
	/**
	 * 更新实体entity，session.update(entity)
	 * @param entity
	 */
	public void update(T entity);
	
	/**
     * 批量更新实体entity，使用命名参数
     * @param setValue set参数
     * @param params where参数
     */
    public int updateBatch(T setValue, T params);
    
    /**
     * 批量更新实体entity，使用命名参数
     * @param setValue set参数
     * @param params where参数
     */
    public int updateBatch(T setValue, Map<String, ?> params);
	
	/**
	 * 合并实体entity，session.merge(entity)，同JPA merge()
	 * @param entity 实体
	 * @return 实体
	 */
	public T merge(T entity);
	
	/**
	 * 根据ID load指定entity实体，总尝试返回代理，为空时抛出异常，同JPA getReference()
	 * @param entity 实体类型
	 * @param id 主键
	 * @return 实体
	 */
	public T load(Class<T> entity, ID id);
	
	/**
	 * 根据ID load实体，总尝试返回代理，为空时抛出异常，session.load(entityClass, id, lockMode)
	 * @param id 实体主键
	 * @return 实体
	 */
	public T load(ID id);
	
	/**
	 * 根据ID get实体，立即命中数据库，为空时返回null，同JPA find()
	 * @param id 主键
	 * @return 实体
	 */
	public T get(ID id);
	
	public List<T> getEntityList(Map<String, ?> params);
	public List<T> getEntityList(Map<String, ?> params, Map<String, String> orderMaps);
	public List<T> getEntityList(T params);
	public List<T> getEntityList(T params, Map<String, String> orderMaps);
	public List<T> getEntityList(String propName, Object value);    
	public List<T> getEntityList(String propName, Object value, Map<String, String> orderMaps);    
	public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2);
	public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2, Map<String, String> orderMaps);
	public List<T> getAll();
	
	/**
	 * 根据ID删除实体
	 * @param id
	 */
	public void delete(ID id);
	
	/**
	 * 根据entity(带主键)删除实体，同JPA remove()
	 * @param entity
	 */
	public void delete(T entity);
	
    
	/**
     * 根据entity携带的条件删除实体，命名参数
     * @param entity 条件
     */
    public int deleteBatch(T entity);
    
    /**
     * 根据条件删除实体，使用命名参数
     * @param params sql参数
     */
    public int deleteBatch(Map<String, ?> params);
	
    /**
     * 分页查询，使用查询语句实现
     * @param page 分页数据
     * @param params 携带查询条件，一般简单“等于”条件
     * @return Page&lt;T&gt;分页，携带查询结果
     * @author yinlei
     * date 2012-7-8 下午10:34:23
     */
    public Page<T> queryForPage(Page<T> page, Map<String, ?> params);
    
    /**
     * 分页查询，使用criteria实现
     * @param page 分页数据
     * @param entity 携带查询条件，一般简单“等于”条件
     * @return Page&lt;T&gt;分页，携带查询结果
     * @author yinlei
     * date 2012-7-8 下午10:34:23
     */
    public Page<T> queryForPage(Page<T> page, T entity);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件
     * @return 结果List
     * @author yinlei
     * date 2013-10-4 17:27
     */
    public List<T> pagedQueryList(Page<T> page, Map<String, ?> params);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件
     * @return 结果List
     * @author yinlei
     * date 2013-10-4 17:27
     */
    public List<T> pagedQueryList(Page<T> page, T params);
    
    /**
     * String属性like查询，使用QBE实现
     * @param propertyName 属性名
     * @param propertyValue 属性值
     * @return list 查询结果List&lt;T&gt;
     */
    public List<T> getListByLike(String propertyName, String propertyValue);
    
    /**
     * String属性like查询，其它等于，使用QBE实现
     * @param model 携带查询条件model
     * @return list 查询结果List&lt;X&gt;
     */
    public List<T> getListByLike(T model);
	
    /**
     * String属性like查询，其它等于，使用QBE实现
     * @param model 携带查询条件model
     * @param orderMaps 使用Map传参，key是排序字段，value是asc或desc
     * @return list 查询结果List&lt;X&gt;
     */
    public List<T> getListByLike(T model, Map<String, String> orderMaps);
    
    /**
     * QBC条件查询获得唯一实体，请确保属性具有唯一性
     * @param propertyName 属性名
     * @param value 属性值
     * @return 实体&lt;T&gt;
     */
    public T uniqueResult(String propertyName, Object value);
    
    /**
     * QBC条件查询获得唯一实体，请确保属性具有唯一性
     * @param params 携带查询参数，key为属性名，value为值
     * @return 实体&lt;X&gt;
     * @author yinlei
     * date 2013-6-11 下午5:19:04
     */
    public T uniqueResult(Map<String, ?> params);
    
    /**
     * QBE条件查询获得唯一实体，请确保属性具有唯一性
     * @param model 携带查询参数实体
     * @return 实体&lt;T&gt;实例
     * @author yinlei
     * date 2013-6-11 下午5:21:11
     */
    public T uniqueResult(T model);
    
}
