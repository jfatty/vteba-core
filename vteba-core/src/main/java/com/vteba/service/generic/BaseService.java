package com.vteba.service.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;

public interface BaseService<T, ID extends Serializable> {

	/**
     * @param entity
     * @return
     * @see com.vteba.tx.generic.IGenericDao#save(java.lang.Object)
     */
    public abstract ID save(T entity);

    /**
     * @param entity
     * @see com.vteba.tx.generic.IGenericDao#persist(java.lang.Object)
     */
    public abstract void persist(T entity);

    /**
     * @param params
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getEntityList(java.util.Map)
     */
    public abstract List<T> getEntityList(Map<String, ?> params);
    
    /**
     * 查询实体List
     * @param params 查询条件，key是属性名，value是属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public abstract List<T> getEntityList(Map<String, ?> params, Map<String, String> orderMaps);

    /**
     * @param params
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getEntityList(java.lang.Object)
     */
    public abstract List<T> getEntityList(T params);

    /**
     * @param params
     * @param orderMaps
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getEntityList(java.lang.Object, java.util.Map)
     */
    public abstract List<T> getEntityList(T params, Map<String, String> orderMaps);

    /**
     * @param propName
     * @param value
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getEntityList(java.lang.String, java.lang.Object)
     */
    public abstract List<T> getEntityList(String propName, Object value);
    
    /**
     * 重载的便捷方法，查询实体List
     * @param propName 查询条件，属性名
     * @param value 查询条件，属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public abstract List<T> getEntityList(String propName, Object value, Map<String, String> orderMaps);
    
    /**
     * 重载的便捷方法，查询实体List
     * @param propName1 查询条件1，属性名
     * @param value1 查询条件1，属性值
     * @param propName2 查询条件2，属性名
     * @param value2 查询条件2，属性值
     * @return 实体List
     */
	public abstract List<T> getEntityList(String propName1, Object value1, String propName2, Object value2);
	
	/**
     * 重载的便捷方法，查询实体List
     * @param propName1 查询条件1，属性名
     * @param value1 查询条件1，属性值
     * @param propName2 查询条件2，属性名
     * @param value2 查询条件2，属性值
     * @param orderMaps 排序条件，key是要排序的属性名，value是"desc"或"asc"
     * @return 实体List
     */
	public abstract List<T> getEntityList(String propName1, Object value1, String propName2, Object value2, Map<String, String> orderMaps);
    
    /** 
     * hql查询VO Bean List，一般用于多实体连接查询部分栏位。主要基于别名进行结果集转换。<br> 
     * 用法：<br>
     * 1、使用select new查询VO Bean，select new com.vteba.model.AUser(i.sbillno,u) from Inventory i, User u 
     *   &nbsp;&nbsp;&nbsp;&nbsp;where i.scustomerno = u.userAccount，VO中要有对应的构造函数，且要使用包名全路径。<br>
     * 2、直接select i.sbillno,u from Inventory i, User u...，则返回List&lt;Object[]&gt;，其中Object[]是{"billno", User}<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;，不建议这么用，建议使用{@link #getListByHql(String, Class, Object...)}根据别名进行结果集转换 <br> 
     * 3、直接查询PO也是可以的，但是建议使用{@link #getEntityListByHql(String, Object...)}代替<br> 
     * @param hql 可用Jpa风格参数： ?1、?2，命名参数： :subjectName，Hibernate参数： ? (deprecated)
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     * @author yinlei
     * date 2012-12-17 下午10:35:09
     */
	public abstract <E> List<E> getListByHql(String hql, Object... values);

    /**
     * @param entity
     * @see com.vteba.tx.generic.IGenericDao#update(java.lang.Object)
     */
    public abstract void update(T entity);
    
    /**
     * @param entity
     * @see com.vteba.tx.generic.IGenericDao#saveOrUpdate(java.lang.Object)
     */
    public abstract void saveOrUpdate(T entity);

    /**
     * 批量更新实体entity
     * @param setValue set参数
     * @param params where参数
     */
    public abstract int updateBatch(T setValue, T params);
    
    /**
     * 批量更新实体entity
     * @param setValue set参数
     * @param params where参数，key为属性名，value为属性值
     */
    public abstract int updateBatch(T setValue, Map<String, ?> params);
    
    /**
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getAll()
     */
    public abstract List<T> getAll();

    /**
     * @param page
     * @param params
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#queryForPage(com.vteba.tx.generic.Page, java.util.Map)
     */
    public abstract Page<T> queryForPage(Page<T> page, Map<String, ?> params);

    /**
     * @param entity
     * @param id
     * @return
     * @see com.vteba.tx.generic.IGenericDao#load(java.lang.Class, java.io.Serializable)
     */
    public abstract T load(Class<T> entity, ID id);

    /**
     * @param id
     * @return
     * @see com.vteba.tx.generic.IGenericDao#load(java.io.Serializable)
     */
    public abstract T load(ID id);

    /**
     * @param id
     * @return
     * @see com.vteba.tx.generic.IGenericDao#get(java.io.Serializable)
     */
    public abstract T get(ID id);

    /**
     * @param id
     * @see com.vteba.tx.generic.IGenericDao#delete(java.io.Serializable)
     */
    public abstract void delete(ID id);

    /**
     * @param entity
     * @see com.vteba.tx.generic.IGenericDao#delete(java.lang.Object)
     */
    public abstract void delete(T entity);

    /**
     * 根据entity携带的条件删除实体，命名参数
     * @param entity 条件
     */
    public abstract int deleteBatch(T entity);
    
    /**
     * 根据条件删除实体，使用命名参数
     * @param params 条件参数，key为属性名，value为属性值
     */
    public abstract int deleteBatch(Map<String, ?> params);
    
    /**
     * 重载的便捷方法，根据条件删除实体
     * @param propName 参数属性名
     * @param value 参数属性值
     * @return 删除的实体数
     */
    public abstract int deleteBatch(String propName, Object value);
    
    /**
     * 重载的便捷方法，根据条件删除实体
     * @param propName1 参数属性名1
     * @param value1 参数属性值1
     * @param propName2 参数属性名2
     * @param value2 参数属性值2
     * @return 删除的实体数
     */
    public abstract int deleteBatch(String propName1, Object value1, String propName2, Object value2);
    
    /**
     * @param propertyName
     * @param propertyValue
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getListByLike(java.lang.String, java.lang.String)
     */
    public abstract List<T> getListByLike(String propertyName, String propertyValue);

    /**
     * @param model
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getListByLike(java.lang.Object)
     */
    public abstract List<T> getListByLike(T model);

    /**
     * @param model
     * @param orderMaps
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#getListByLike(java.lang.Object, java.util.Map)
     */
    public abstract List<T> getListByLike(T model, Map<String, String> orderMaps);

    /**
     * 重载的便捷方法，条件查询获得唯一实体，请确保属性具有唯一性
     * @param propName 属性名
     * @param value 属性值
     * @return 实体&lt;T&gt;
     */
    public abstract T uniqueResult(String propName, Object value);
    
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
     * @param params
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#uniqueResult(java.util.Map)
     */
    public abstract T uniqueResult(Map<String, ?> params);

    /**
     * @param model
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#uniqueResult(java.lang.Object)
     */
    public abstract T uniqueResult(T model);

    /**
     * @param page
     * @param entity
     * @return
     * @see com.vteba.tx.hibernate.BaseGenericDao#queryForPage(com.vteba.tx.generic.Page, java.lang.Object)
     */
    public abstract Page<T> queryForPage(Page<T> page, T entity);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件，key为属性名
     * @return 结果List
     */
    public abstract List<T> pagedQueryList(Page<T> page, Map<String, ?> params);
    
    /**
     * 分页查询但是不返回总记录数。
     * @param page 分页参数，以及排序参数
     * @param params 参数，where条件
     * @return 结果List
     */
    public abstract List<T> pagedQueryList(Page<T> page, T params);
    
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
    
	public Page<T> queryForPage(Page<T> page, String propName, Object value);

	public Page<T> queryForPage(Page<T> page, String propName1, Object value1,
			String propName2, Object value2);
	
	public Page<T> queryForPageByHql(Page<T> page, String hql, Object... values);
	
	public <X extends Number> List<X> queryPrimitiveList(String field, Class<X> resultClass,
			Map<String, ?> params);

	public <X extends Number> X queryForPrimitive(String field, Class<X> resultClass,
			Map<String, ?> params);

	public <X extends Number> List<X> statsForList(String statsField,
			Class<X> resultClass, Map<String, ?> params);

	public <X extends Number> X statsPrimitive(String statsField,
			Class<X> resultClass, Map<String, ?> params);
	
	public List<T> pagedQueryByHql(Page<T> page, String hql, Object... values);
	
	public <X> List<X> sqlQueryForList(String sql, Class<X> clazz, Object... values);
	
	public <X> X sqlQueryForObject(String sql, Class<X> clazz, Object... values);
	
	public List<Object[]> sqlQueryForObject(String sql, Object... values);

}
