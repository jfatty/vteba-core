package com.vteba.tx.hibernate;

import java.io.Serializable;

import com.vteba.tx.generic.IGenericDao;

/**
 * Hibernate 泛型 DAO接口，简化Entity DAO实现。
 * @author yinlei 
 * date 2012-5-6 下午10:42:35
 * @param <T> 实体类型
 * @param <ID> 主键类型，一般是String或者Long或Integer
 */
public interface IHibernateGenericDao<T, ID extends Serializable> extends IGenericDao<T, ID> {
	/**
	 * 强制hibernate将对象与数据库同步。
	 */
	public void flush();
	
	/**
	 * 清空hibernate的session缓存，慎用。
	 */
	public void clear();
	
	/**
     * 初始化延迟加载的对象，load默认延迟加载
     */
    public void initProxyObject(Object proxy);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param detachedCriteria 携带查询条件，DetachedCriteria实例，复杂条件
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByCriteria(DetachedCriteria detachedCriteria);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param model 携带查询条件，实体实例，简单条件，一般是等于
	 * @param detachedCriteria 携带查询条件，DetachedCriteria实例，复杂条件
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByCriteria(T model, DetachedCriteria detachedCriteria);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param propertyName 属性名
	 * @param propertyValue 属性值
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByCriteria(String propertyName, Object propertyValue);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param model 携带查询条件model
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByCriteria(T model);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param model 携带查询条件model
	 * @param orderMaps 使用Map传参，key是排序字段，value是asc或desc。
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByCriteria(T model, Map<String, String> orderMaps);
	
	/**
	 * 根据属性equal查询，使用QBE实现
	 * @param entityClass 要查询的实体类
	 * @param model 携带查询条件model
	 * @param orderMaps 使用Map传参，key是排序字段，value是asc或desc。
	 * @return list 查询结果List&lt;X&gt;
	 */
	//public <X> List<X> getListByCriteria(Class<X> entityClass, X model, Map<String, String> orderMaps);
	
	/**
	 * String属性like查询，使用QBE实现
	 * @param propertyName 属性名
	 * @param propertyValue 属性值
	 * @return list 查询结果List&lt;T&gt;
	 */
	//public List<T> getListByLike(String propertyName, String propertyValue);
	
	/**
	 * String属性like查询，其它等于，使用QBE实现
	 * @param model 携带查询条件model
	 * @return list 查询结果List&lt;X&gt;
	 */
	//public List<T> getListByLike(T model);
	
	/**
	 * String属性like查询，其它等于，使用QBE实现
	 * @param model 携带查询条件model
	 * @param orderMaps 使用Map传参，key是排序字段，value是asc或desc
	 * @return list 查询结果List&lt;X&gt;
	 */
	//public List<T> getListByLike(T model, Map<String, String> orderMaps);
	
	/**
	 * String属性like查询，其它等于，使用QBE实现
	 * @param entityClass 要查询的实体类
	 * @param model 携带查询条件model
	 * @param orderMaps 使用Map传参，key是排序字段，value是asc或desc
	 * @return list 查询结果List&lt;X&gt;
	 */
	//public <X> List<X> getListByCriteriaLike(Class<X> entityClass, X model, Map<String, String> orderMaps);

	/**
	 * sql查询标量值，返回List&lt;Object[]&gt;
	 * @param sql sql语句
	 * @param values sql参数值
	 * @return List&lt;Object[]&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:10:53
	 */
	//public List<Object[]> sqlQueryForObject(String sql, Object... values);
	
	/**
	 * sql查询基本类型及其封装类，String，Date和大数值List&lt;X&gt;
	 * @param sql sql语句
	 * @param clazz 要转换的基本类型
	 * @param values sql参数值
	 * @return 基本类型List&lt;X&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:09:08
	 */
	//public <X> List<X> sqlQueryForList(String sql, Class<X> clazz, Object... values);
	
	/**
	 * sql查询基本类型及其封装类，String，Date和大数值
	 * @param sql sql语句
	 * @param clazz 要转换的基本类型，String，Date和大数值
	 * @param values sql参数
	 * @return 基本类型值
	 * @author yinlei
	 * date 2013-6-4 下午10:17:32
	 */
	//public <X> X sqlQueryForObject(String sql, Class<X> clazz, Object... values);

	/**
	 * hql查询标量值，返回List&lt;Object[]&gt;
	 * @param hql hql语句
	 * @param namedQuery 是否命名hql
	 * @param values hql参数
	 * @return List&lt;Object[]&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:12:18
	 */
	//public List<Object[]> hqlQueryForObject(String hql, boolean namedQuery, Object... values);
	
	/**
	 * hql查询基本类型及其封装类，String，Date和大数值List&lt;X&gt;
	 * @param hql hql语句
	 * @param clazz 基本类型
	 * @param values hql参数值
	 * @return List&lt;X&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:13:42
	 */
	//public <X> List<X> hqlQueryForList(String hql, Class<X> clazz, Object... values);
	
	/**
	 * hql查询基本类型及其封装类，String，Date和大数值
	 * @param hql hql语句
	 * @param clazz 要转换的基本类型
	 * @param values hql参数
	 * @return 基本类型值
	 * @author yinlei
	 * date 2013-6-4 下午10:15:36
	 */
	//public <X> X hqlQueryForObject(String hql, Class<X> clazz, Object... values);
	
	/**
	 * QBC条件查询获得唯一实体，请确保属性具有唯一性
	 * @param propertyName 属性名
	 * @param value 属性值
	 * @return 实体&lt;T&gt;
	 */
	//public T uniqueResult(String propertyName, Object value);
	
	/**
	 * QBC条件查询获得唯一实体，请确保属性具有唯一性
	 * @param entityClass 要查询的实体类
	 * @param propertyName 属性名
	 * @param value 属性值
	 * @return 实体&lt;X&gt;
	 */
	//public <X> X uniqueResult(Class<X> entityClass, String propertyName, Object value);
	
	/**
	 * QBC条件查询获得唯一实体，请确保属性具有唯一性
	 * @param params 携带查询参数，key为属性名，value为值
	 * @return 实体&lt;X&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:19:04
	 */
	//public T uniqueResult(Map<String, Object> params);
	
	/**
	 * QBE条件查询获得唯一实体，请确保属性具有唯一性
	 * @param entityClass 要查询的实体类
	 * @param params 携带查询参数，key为属性名，value为值
	 * @return 实体&lt;X&gt;
	 * @author yinlei
	 * date 2013-6-11 下午5:19:04
	 */
	//public <X> X uniqueResult(Class<X> entityClass, Map<String, Object> params);
	
	/**
	 * QBE条件查询获得唯一实体，请确保属性具有唯一性
	 * @param model 携带查询参数实体
	 * @return 实体&lt;T&gt;实例
	 * @author yinlei
	 * date 2013-6-11 下午5:21:11
	 */
	//public T uniqueResult(T model);
	
	/**
	 * QBE条件查询获得唯一实体，请确保属性具有唯一性
	 * @param entityClass 要查询的实体类
	 * @param model 携带查询参数实体
	 * @return 实体&lt;X&gt;实例
	 * @author yinlei
	 * date 2013-6-11 下午5:22:34
	 */
	//public <X> X uniqueResult(Class<X> entityClass, X model);
	
	/**
	 * 使用hql获得唯一实体。<br>
	 * 1、hql应查询Class&lt;T&gt;实例所有的属性，如：select s from Subject s where .... 。<br>
	 * 2、使用new T()构造函数指定属性，如：select new Subject(id, subjectCode, subjectName, level) 
	 *    from Subject s where .... 同时Subject实体中要有对应的构造函数才行。<br>
	 * @param hql 查询语句
	 * @param values hql中绑定的参数值
	 * @return 当前实体&lt;T&gt;
	 */
	//public T uniqueResultByHql(String hql, Object... values);
	
	/**
	 * 使用命名hql获得唯一实体。<br>
	 * 1、hql应查询Class&lt;T&gt;实例所有的属性，如：select s from Subject s where .... 。<br>
	 * 2、使用new T()构造函数指定属性，如：select new Subject(id, subjectCode, subjectName, level) 
	 *    from Subject s where .... 同时Subject实体中要有对应的构造函数才行。<br>
	 * @param hql 查询语句
	 * @param values hql中绑定的参数值
	 * @return 当前实体&lt;T&gt;
	 */
	//public T uniqueResultByNamedHql(String hql, Object... values);
	
	/**
	 * 通过hql获得唯一实体，hql语句可进行多实体连接。<br>
	 * 1、hql应查询Class&lt;X&gt;实例所有的属性，如：select s from Subject s where .... 。<br>
	 *    这种方式建议使用{@link #uniqueResultByHql(String, Object...)}<br>
	 * 2、使用new X()构造函数指定属性，如：select new Subject(id, subjectCode) <br>
	 *    from Subject s where .... 同时Subject中要有对应的构造函数才行。<br>
	 *    Subject如果不是实体类，Subject要带上包名路径。<br>
	 *    如果Subject是实体类，建议使用{@link #uniqueResultByHql(String, Object...)}<br>
	 * 3、查询任意栏位，hql中的栏位名或别名要和Class&lt;X&gt;实例中的属性名一致。<br>
	 * @param <X> 查询的实体
	 * @param hql 要执行的hql语句
	 * @param resultClass 结果类型Class&lt;X&gt;，对于第一和第二点，两种用法，该参数要设为null。
	 * @param values hql参数值
	 * @return 实体&lt;X&gt;
	 */
	//public <X> X uniqueResultByHql(String hql, Class<X> resultClass, Object... values);
	
	/**
	 * 通过hql获得唯一实体，hql语句可进行多实体连接。<br>
	 * 1、hql应查询Class&lt;X&gt;实例所有的属性，如：select s from Subject s where .... 。<br>
	 *    这种方式建议使用{@link #uniqueResultByHql(String, Object...)}<br>
	 * 2、使用new X()构造函数指定属性，如：select new Subject(id, subjectCode) <br>
	 *    from Subject s where .... 同时Subject中要有对应的构造函数才行。<br>
	 *    Subject如果不是实体类，Subject要带上包名路径。<br>
	 *    如果Subject是实体类，建议使用{@link #uniqueResultByHql(String, Object...)}<br>
	 * 3、查询任意栏位，hql中的栏位名或别名要和Class&lt;X&gt;实例中的属性名一致。<br>
	 * @param <X> 查询的实体
	 * @param hql 要执行的hql语句/命名hql名称
	 * @param resultClass 结果类型Class&lt;X&gt;。对于第一和第二点，两种用法，该参数要设为null。
	 * @param namedQuery 是否命名查询
	 * @param values hql参数值
	 * @return 实体&lt;X&gt;
	 */
	//public <X> X uniqueResultByHql(String hql, Class<X> resultClass, boolean namedQuery, Object... values);
	
	/**
	 * 使用sql获得唯一实体<br>
	 * 1、sql栏位或者别名要和实体的属性一致，栏位和实体属性名不一致要指定别名。<br>
	 * 如：select id, subject_code subjectCode, subject_name subjectName from subject s where ....<br>
	 * 其中id属性和sql栏位一样，不需要指定别名。<br>
	 * 2、基于别名，使用AliasedResultTransformer，可转换任意列。
	 * @param sql 要执行的sql
	 * @param values sql绑定的参数
	 * @return 当前实体&lt;T&gt;
	 */
	//public T uniqueResultBySql(String sql, Object...values);
	
	/**
	 * 通过sql获得唯一实体<br>
	 * 1、sql栏位或者别名要和实体的属性一致，栏位和实体属性名不一致要指定别名。<br>
	 * 如：select id, subject_code subjectCode, subject_name subjectName from subject s where ....<br>
	 * 其中id属性和sql栏位一样，不需要指定别名。<br>
	 * 2、基于别名，使用AliasedResultTransformer，可转换任意列。
	 * @param <X> 要查询的实体
	 * @param sql 要执行的sql语句
	 * @param resultClass 结果类型Class&lt;X&gt;
	 * @param values sql参数值
	 * @return 实体&lt;X&gt;
	 */
	//public <X> X uniqueResultBySql(String sql, Class<X> resultClass, Object... values);
	
	/**
	 * 执行任意hql，常用于update，delete，insert
	 * @param hql 要执行的hql
	 * @param values hql中绑定的参数值
	 * @param namedQuery 是否命名查询
	 * @return 影响的实体数
	 */
	//public int executeHqlUpdate(String hql, boolean namedQuery, Object... values);
	
	/**
	 * 执行任意sql，常用于update，delete，insert
	 * @param sql 要执行的sql
	 * @param values sql中绑定的参数值
	 * @return 影响的记录数
	 */
	//public int executeSqlUpdate(String sql, Object... values);
	
	/**
	 * 分页查询，使用criteria实现
	 * @param page 分页数据
	 * @param entity 携带查询条件，一般简单“等于”条件
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2012-7-8 下午10:34:23
	 */
	//public Page<T> queryForPage(Page<T> page, T entity);
	
	/**
	 * 分页查询，使用criteria实现
	 * @param page 分页数据
	 * @param detachedCriteria 携带查询条件，DetachedCriteria实例，复杂条件
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2012-7-8 下午10:35:13
	 */
	//public Page<T> queryForPage(Page<T> page, DetachedCriteria detachedCriteria);
	
	/**
	 * 分页查询，使用criteria实现
	 * @param page 分页数据
	 * @param entity 携带查询条件，实体实例，一般简单条件
	 * @param detachedCriteria 携带查询条件，DetachedCriteria实例，复杂条件
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2012-7-8 下午10:37:22
	 */
	//public Page<T> queryForPage(Page<T> page, T entity, DetachedCriteria detachedCriteria);
	
	/**
	 * 分页查询，使用criteria实现，左外连接立即初始化延迟加载的集合
	 * @param page 分页数据
	 * @param entity 实体
	 * @param objects 实体中延迟加载的集合的名字
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2012-6-26 下午4:50:03
	 */
	//public Page<T> queryForPageByLeftJoin(Page<T> page, T entity, Object... objects);
	
	/**
	 * 分页查询，使用criteria实现，第二个select立即初始化延迟加载的集合
	 * @param page 分页数据
	 * @param entity 实体
	 * @param objects 实体中延迟加载的集合的名字
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2012-6-26 下午4:50:03
	 */
	//public Page<T> queryForPageBySubSelect(Page<T> page, T entity, Object... objects);
	
	/**
	 * 使用hql进行分页查询
	 * @param page 分页条件
	 * @param hql hql语句
	 * @param values hql参数值
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2013-6-11 下午5:28:27
	 */
	//public Page<T> queryForPageByHql(Page<T> page, String hql, Object... values);
	
	/**
	 * 分页查询但是不返回总记录数。
	 * @param page 分页参数
	 * @param hql hql语句
	 * @param values hql参数
	 * @return 结果List
	 * @author yinlei
	 * date 2013-10-4 17:27
	 */
	//public List<T> pagedQueryByHql(Page<T> page, String hql, Object... values);
	
	/**
	 * 使用sql进行分页查询
	 * @param page 分页条件
	 * @param sql sql语句
	 * @param values sql参数值
	 * @return Page&lt;T&gt;分页，携带查询结果
	 * @author yinlei
	 * date 2013-6-11 下午5:28:32
	 */
	//public Page<T> queryForPageBySql(Page<T> page, String sql, Object... values);

	/**
	 * 统计hql查询返回多少条记录，分页查询使用
	 * @param hql 要执行的hql
	 * @param values hql绑定的参数值
	 * @return 记录数
	 * @author yinlei
	 * date 2012-5-14 下午11:39:33
	 */
	//public long countHqlResult(String hql, Object... values);
	/**
	 * 统计sql查询返回多少条记录，分页查询使用
	 * @param sql 要执行的sql
	 * @param values sql绑定的参数值
	 * @return 记录数
	 * @author yinlei
	 * date 2012-5-14 下午11:40:33
	 */
	//public long countSqlResult(String sql, Object... values);
	
	/**
	 * table模拟sequence，使用mysql function实现
	 * @param sequenceName sequence表中，seq_name ID
	 * @return Long sequence值
	 * @author yinlei
	 * date 2012-7-3 下午3:33:12
	 */
	//public Long getSequenceLongValue(String sequenceName);
}
