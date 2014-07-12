package com.vteba.tx.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vteba.tx.generic.Page;

public interface BaseGenericDao<T, ID extends Serializable> extends IHibernateGenericDao<T, ID> {
	
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
	 * 使用hql进行分页查询
	 * @param page 分页条件
	 * @param hql hql语句
	 * @param values hql参数值
	 * @return Page&lt;T&gt;分页，携带查询结果
	 */
    public Page<T> queryForPageByHql(Page<T> page, String hql, Object... values);
    
    /**
	 * 分页查询但是不返回总记录数。
	 * @param page 分页参数
	 * @param hql hql语句
	 * @param values hql参数
	 * @return 结果List
	 */
    public List<T> pagedQueryByHql(Page<T> page, String hql, Object... values);
    
    /**
     * 查询实体中某一属性的值，该属性是基本类型
     * @param field 要查询哪个属性值
     * @param resultClass 结果类型
     * @param params 参数条件
     * @return 属性值List
     */
    public <X> List<X> queryPrimitiveList(String field, Class<X> resultClass, Map<String, ?> params);
    
    /**
     * 查询实体中某一属性的值，该属性是基本类型
     * @param field 属性名
     * @param resultClass 结果类型
     * @param params 参数
     * @return 属性值
     */
    public <X> X queryForPrimitive(String field, Class<X> resultClass, Map<String, ?> params);
    
    /**
     * 对某一属性执行统计函数
     * @param statsField 要被统计的属性和函数，如：sum(salary)，avg(age)，count(userId)，max(salary)，min(age)
     * @param resultClass 返回的基本类型类
     * @param params 条件参数
     * @return 统计结果list
     */
    @Deprecated
    public <X extends Number> List<X> statsForList(String statsField, Class<X> resultClass, Map<String, ?> params);
    
    /**
     * 对某一属性执行统计函数
     * @param statsField 要被统计的属性和函数，如：sum(salary)，avg(age)，count(userId)，max(salary)，min(age)
     * @param resultClass 返回的基本类型类
     * @param params 条件参数
     * @return 统计结果
     */
    @Deprecated
    public <X extends Number> X statsPrimitive(String statsField, Class<X> resultClass, Map<String, ?> params);
    
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
	
}
