package com.vteba.service.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;

public interface BaseService<T, ID extends Serializable> {

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
     * @param params where参数，key为属性名
     */
    public int updateBatch(T setValue, Map<String, ?> params);
	
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
	
	/**
     * 重载的便捷方法，条件查询获得唯一实体，请确保属性具有唯一性
     * @param propName 属性名
     * @param value 属性值
     * @return 实体&lt;T&gt;
     */
    public T uniqueResult(String propName, Object value);
    
    /**
     * 重载的便捷方法，条件查询获得唯一实体，请确保属性具有唯一性
     * @param propName1 属性名1
     * @param value1 属性值1
     * @param propName2 属性名2
     * @param value2 属性值2
     * @return 实体&lt;T&gt;
     */
    public T uniqueResult(String propName1, Object value1, String propName2, Object value2);
    
    /**
     * QBC条件查询获得唯一实体，请确保属性具有唯一性
     * @param params 携带查询参数，key为属性名，value为值
     * @return 实体&lt;X&gt;
     */
    public T uniqueResult(Map<String, ?> params);
    
    /**
     * QBC条件查询获得唯一实体，请确保属性具有唯一性
     * @param model 携带查询参数实体
     * @return 实体&lt;T&gt;实例
     */
    public T uniqueResult(T model);
	
	/**
	 * 查询实体List
	 * @param params 查询条件，key是属性名，value是属性值
	 * @return 实体List
	 */
	public List<T> getEntityList(Map<String, ?> params);
	
	/**
     * 查询实体List
     * @param params 查询条件，key是属性名，value是属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public List<T> getEntityList(Map<String, ?> params, Map<String, String> orderMaps);
	
	/**
     * 查询实体List
     * @param params 查询条件
     * @return 实体List
     */
	public List<T> getEntityList(T params);
	
	/**
     * 查询实体List
     * @param params 查询条件
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public List<T> getEntityList(T params, Map<String, String> orderMaps);
	
	/**
     * 重载的便捷方法，查询实体List
     * @param propName 查询条件，属性名
     * @param value 查询条件，属性值
     * @return 实体List
     */
	public List<T> getEntityList(String propName, Object value);
	
	/**
     * 重载的便捷方法，查询实体List
     * @param propName 查询条件，属性名
     * @param value 查询条件，属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public List<T> getEntityList(String propName, Object value, Map<String, String> orderMaps);
	
	/**
     * 重载的便捷方法，查询实体List
     * @param propName1 查询条件1，属性名
     * @param value1 查询条件1，属性值
     * @param propName2 查询条件2，属性名
     * @param value2 查询条件2，属性值
     * @return 实体List
     */
	public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2);
	
	/**
     * 重载的便捷方法，查询实体List
     * @param propName1 查询条件1，属性名
     * @param value1 查询条件1，属性值
     * @param propName2 查询条件2，属性名
     * @param value2 查询条件2，属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public List<T> getEntityList(String propName1, Object value1, String propName2, Object value2, Map<String, String> orderMaps);
	
	/** 
     * hql查询JavaBean List<br> 
     * 用法：<br>
     * 1、使用select new查询VO Bean，select new com.vteba.model.AUser(i.sbillno,u) from Inventory i, User u 
     *   &nbsp;&nbsp;&nbsp;&nbsp;where i.scustomerno = u.userAccount，VO中要有对应的构造函数，且要使用包名全路径。<br>
     * 2、直接select i.sbillno,u from Inventory i, User u...，则返回List&lt;Object[]&gt;，其中Object[]是{"billno", User}<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;，不建议这么用<br> 
     * 3、直接查询PO也是可以的，select u from User where u.userName = :userName<br> 
     * @param hql 可用Jpa风格参数： ?1、?2，命名参数： :subjectName，Hibernate参数： ? (deprecated)
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     * @author yinlei
     * date 2012-12-17 下午10:35:09
     */
	public <E> List<E> getListByHql(String hql, Object... values);
	
	/**
     * 查询实体list，<em>慎用</em>，确保不会返回很多对象。
     * @return 实体List
     */
	public List<T> getAll();
	
	/**
	 * 根据ID删除实体
	 * @param id
	 */
	public void delete(ID id);
	
	/**
	 * 根据entity（带主键）删除实体，同JPA remove()
	 * @param entity
	 */
	public void delete(T entity);
	
    
	/**
     * 根据entity携带的条件删除实体
     * @param entity 条件
     * @return 删除的实体数
     */
    public int deleteBatch(T entity);
    
    /**
     * 根据条件删除实体
     * @param params 条件参数，key为属性名，value为属性值
     * @return 删除的实体数
     */
    public int deleteBatch(Map<String, ?> params);
    
    /**
     * 重载的便捷方法，根据条件删除实体
     * @param propName 参数属性名
     * @param value 参数属性值
     * @return 删除的实体数
     */
    public int deleteBatch(String propName, Object value);
    
    /**
     * 重载的便捷方法，根据条件删除实体
     * @param propName1 参数属性名1
     * @param value1 参数属性值1
     * @param propName2 参数属性名2
     * @param value2 参数属性值2
     * @return 删除的实体数
     */
    public int deleteBatch(String propName1, Object value1, String propName2, Object value2);
	
    /**
     * 分页查询，使用查询语句实现
     * @param page 分页数据
     * @param params 查询条件，key为属性名
     * @return Page&lt;T&gt;分页，携带查询结果
     */
    public Page<T> queryForPage(Page<T> page, Map<String, ?> params);
    
    /**
     * 分页查询，使用criteria实现
     * @param page 分页数据
     * @param entity 携带查询条件
     * @return Page&lt;T&gt;分页，携带查询结果
     */
    public Page<T> queryForPage(Page<T> page, T entity);
    
    /**
     * 重载的便捷方法，分页查询
     * @param page 分页数据和排序字段
     * @param propName 查询条件，属性名
     * @param value 查询条件属性值
     * @return Page&lt;T&gt;分页，携带查询结果
     */
    public Page<T> queryForPage(Page<T> page, String propName, Object value);
    
    /**
     * 重载的便捷方法。分页查询
     * @param page 分页数据和排序字段
     * @param propName1 查询条件，属性名1
     * @param value1 查询条件，属性值1
     * @param propName2 查询条件，属性名2
     * @param value2 查询条件，属性值2
     * @return Page&lt;T&gt;分页，携带查询结果
     */
    public Page<T> queryForPage(Page<T> page, String propName1, Object value1, String propName2, Object value2);
    
    /**
	 * 使用hql进行分页查询
	 * @param page 分页条件
	 * @param hql hql语句
	 * @param values hql参数值
	 * @return Page&lt;T&gt;分页，携带查询结果
	 */
    public Page<T> queryForPageByHql(Page<T> page, String hql, Object... values);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件，key为属性名
     * @return 结果List
     */
    public List<T> pagedQueryList(Page<T> page, Map<String, ?> params);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件
     * @return 结果List
     */
    public List<T> pagedQueryList(Page<T> page, T params);
    
    /**
     * 重载的便捷方法。分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param propName 查询条件，属性名
     * @param value 查询条件属性值
     * @return 结果List
     */
    public List<T> pagedQueryList(Page<T> page, String propName, Object value);
    
    /**
     * 重载的便捷方法。分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param propName1 查询条件，属性名1
     * @param value1 查询条件，属性值1
     * @param propName2 查询条件，属性名2
     * @param value2 查询条件，属性值2
     * @return 结果List
     */
    public List<T> pagedQueryList(Page<T> page, String propName1, Object value1, String propName2, Object value2);
    
    /**
	 * 分页查询但是不返回总记录数。
	 * @param page 分页参数
	 * @param hql hql语句
	 * @param values hql参数
	 * @return 结果List
	 */
    public List<T> pagedQueryByHql(Page<T> page, String hql, Object... values);
    
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
     * @param orderMaps 排序条件，key是排序字段，value是asc或desc
     * @return list 查询结果List&lt;X&gt;
     */
    public List<T> getListByLike(T model, Map<String, String> orderMaps);
    
//    /**
//     * 查询实体中某一属性的值，该属性是基本类型
//     * @param field 要查询哪个属性值
//     * @param resultClass 结果类型
//     * @param params 参数条件
//     * @return 属性值List
//     */
//    @Deprecated
//    public <X> List<X> queryPrimitiveList(String field, Class<X> resultClass, Map<String, ?> params);
//    
//    /**
//     * 查询实体中某一属性的值，该属性是基本类型
//     * @param field 属性名
//     * @param resultClass 基本类型类
//     * @param params 参数
//     * @return 基本类型
//     */
//    @Deprecated
//    public <X> X queryForPrimitive(String field, Class<X> resultClass, Map<String, ?> params);
    
//    /**
//     * 对某一属性执行统计函数
//     * @param statsField 要被统计的属性和函数，如：sum(salary)，avg(age)，count(userId)，max(salary)，min(age)
//     * @param resultClass 返回的基本类型类
//     * @param params 条件参数
//     * @return 统计结果list
//     */
    //@Deprecated
    //public <X extends Number> List<X> statsForList(String statsField, Class<X> resultClass, Map<String, ?> params);
    
//    /**
//     * 对某一属性执行统计函数
//     * @param statsField 要被统计的属性和函数，如：sum(salary)，avg(age)，count(userId)，max(salary)，min(age)
//     * @param resultClass 返回的基本类型类
//     * @param params 条件参数
//     * @return 统计结果
//     */
    //@Deprecated
    //public <X extends Number> X statsPrimitive(String statsField, Class<X> resultClass, Map<String, ?> params);
    
    /**
	 * hql查询标量值，一般用于统计查询，或者查询某几列（建议使用getListByHql方法，hql用select new），返回List&lt;Object[]&gt;
	 * @param hql hql语句
	 * @param values hql参数
	 * @return List&lt;Object[]&gt;
	 */
    public List<Object[]> queryForObject(String hql, Object... values);
    
    /**
	 * hql查询基本类型及其封装类，String，Date和大数值
	 * @param hql hql语句
	 * @param clazz 要转换的基本类型
	 * @param values hql参数
	 * @return 基本类型值
	 */
	public <X> X queryForObject(String hql, Class<X> clazz, Object... values);
	
	/**
	 * hql查询基本类型及其封装类，String，Date和大数值List&lt;X&gt;
	 * @param hql hql语句
	 * @param clazz 基本类型
	 * @param values hql参数值
	 * @return List&lt;X&gt;
	 */
	public <X> List<X> queryForList(String hql, Class<X> clazz, Object... values);
    
	/**
	 * sql查询标量值，返回List&lt;Object[]&gt;
	 * @param sql sql语句
	 * @param values sql参数值
	 * @return List&lt;Object[]&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:10:53
	 */
	public List<Object[]> sqlQueryForObject(String sql, Object... values);
	
	/**
	 * sql查询基本类型及其封装类，String，Date和大数值List&lt;X&gt;
	 * @param sql sql语句
	 * @param clazz 要转换的基本类型
	 * @param values sql参数值
	 * @return 基本类型List&lt;X&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:09:08
	 */
	public <X> List<X> sqlQueryForList(String sql, Class<X> clazz, Object... values);
	
	/**
	 * sql查询基本类型及其封装类，String，Date和大数值
	 * @param sql sql语句
	 * @param clazz 要转换的基本类型，String，Date和大数值
	 * @param values sql参数
	 * @return 基本类型值
	 * @author yinlei
	 * date 2013-6-4 下午10:17:32
	 */
	public <X> X sqlQueryForObject(String sql, Class<X> clazz, Object... values);
	
	/**
     * 批量保存一批数据
     * @param list 实体list
     * @param batchSize 批次大小，每batchSize flush一次
     */
    public abstract void saveEntityBatch(List<T> list, int batchSize);

    /**
     * 根据id list批量删除实体
     * @param list ids列表
     */
    public abstract void deleteEntityBatch(List<ID> list);

}
