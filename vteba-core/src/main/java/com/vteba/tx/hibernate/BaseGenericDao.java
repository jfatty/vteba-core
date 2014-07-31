package com.vteba.tx.hibernate;

import java.io.Serializable;
import java.util.List;

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
     * <p>查询当前PO List，一般查询单实体。多实体关联查询，请使用{@link #getListByHql(String, Class, Object...)}。<br>
     * 用法：<br>
     * <p>1、查询全部栏位，select u from User u where...<br>
     * <p>2、使用select new查询部分栏位，select new User(u.id,u.name) from User u where...，<br>
     *   &nbsp;&nbsp;&nbsp;&nbsp;实体类中要有相应的构造函数<br>
     * <p>3、直接查询部分栏位，则返回List&lt;Object[]&gt;，不建议这么使用。建议使用第二点中的<br>
     *   select new语法；或使用{@link #getListByHql(String, Class, Object...)}可直接返回JavaBean<br>
     * @param hql 可用Jpa风格参数： ?1、?2。命名参数： :subjectName。Hibernate参数： ? (deprecated)。
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     */
    public List<T> getEntityListByHql(String hql, Object... values);
    
    /**
     * <p>命名hql查询当前实体Class&lt;T&gt;实例List。是{@link #getEntityListByHql(String, Object...)}的命名参数版。 <br>
     * 用法：<br>
     * <p>1、hql应查询Class&lt;T&gt;实例所有的属性，如：select s from Subject s where .... 。<br>
     * <p>2、使用select new T()查询指定属性，如：select new Subject(id, subjectCode) from Subject s where ....<br>
     *   同时Subject实体中要有对应的构造函数。<br>
     * <p>3、直接查询部分栏位，返回List&lt;Object[]&gt;。不建议这么使用。建议使用第二点中的<br>
     *   select new语法；或使用{@link #getListByHql(String, Class, Object...)}可直接返回JavaBean<br>
     *   
     * @param namedQuery 命名hql语句名，可用Jpa风格参数： ?1、?2，命名参数： :subjectCode
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     */
    public List<T> getEntityListByNamedHql(String namedQuery, Object... values);
    
    /** 
     * <p>hql查询JavaBean List。 用法：<br>
     * <p>1、使用select new查询VO Bean，select new com.vteba.model.AUser(i.sbillno,u) from Inventory i, User u 
     *   where i.scustomerno = u.userAccount，VO中要有对应的构造函数，且要使用包名全路径。<br>
     * <p>2、直接select i.sbillno,u from Inventory i,User u...，则返回List&lt;Object[]&gt;，其中Object[]是{"billno", User}<br>
     *   ，不建议这么用<br>
     * <p>3、直接查询PO也是可以的，select u from User where u.userName = :userName<br> 
     * @param hql 可用Jpa风格参数： ?1、?2，命名参数： :subjectName，Hibernate参数： ? (deprecated)
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     * @author yinlei
     * date 2012-12-17 下午10:35:09
     */
	public <E> List<E> getListByHql(String hql, Object... values);
    
	/** 
     * <p>命名hql查询VO Bean List，一般用于多实体连接查询部分栏位。主要基于别名进行结果集转换。<br>
     * 是{@link #getListByHql(String, Object...)}的命名参数版。 <br> 
     * 用法：<br>
     * <p>1、使用select new查询VO Bean，select new com.vteba.model.AUser(i.sbillno,u) from Inventory i, User u 
     *   where i.scustomerno = u.userAccount，VO中要有对应的构造函数，且要使用包名全路径。<br>
     * <p>2、直接select i.sbillno,u from Inventory i, User u...，则返回List&lt;Object[]&gt;，其中Object[]是{"billno", User}<br>
     *   ，不建议这么用，建议使用{@link #getListByHql(String, Class, Object...)}根据别名进行结果集转换 <br> 
     * <p>3、直接查询PO也是可以的，但是建议使用{@link #getEntityListByHql(String, Object...)}代替<br> 
     * @param hql 可用Jpa风格参数： ?1、?2，命名参数： :subjectName，Hibernate参数： ? (deprecated)
     * @param values hql参数，可以使用单个参数，Map，List，AstModel实例，传参。
     * @author yinlei
     * date 2012-12-17 下午10:35:09
     */
    public <E> List<E> getListByNamedHql(String namedQuery, Object... values);
	
    /**
	 * 使用hql进行分页查询
	 * @param page 分页条件
	 * @param hql hql语句
	 * @param values hql参数值
	 * @return Page&lt;T&gt;分页，携带查询结果
	 */
    public <X> Page<X> queryForPageByHql(Page<X> page, String hql, Object... values);
    
    /**
	 * 分页查询但是不返回总记录数。
	 * @param page 分页参数
	 * @param hql hql语句
	 * @param values hql参数
	 * @return 结果List
	 */
    public <X> List<X> pagedQueryByHql(Page<X> page, String hql, Object... values);
    
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
