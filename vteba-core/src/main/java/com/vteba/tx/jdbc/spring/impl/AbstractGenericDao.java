package com.vteba.tx.jdbc.spring.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vteba.common.exception.NonUniqueException;
import com.vteba.tx.generic.Page;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.meta.EntityMetadata;
import com.vteba.tx.jdbc.spring.spi.SpringGenericDao;
import com.vteba.utils.reflection.ReflectUtils;

/**
 * spring通用泛型dao实现。
 * @author yinlei 
 * @since 2013-7-6 16:00
 * @param <T> 泛型实体
 * @param <ID> 主键类型
 */
public abstract class AbstractGenericDao<T, ID extends Serializable> implements SpringGenericDao<T, ID> {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericDao.class);
    
    protected String tableName;
	protected Class<T> entityClass;
	protected Class<ID> idClass;
	protected EntityMetadata metadata;
	
	// setter方法list
	protected List<String> setterList = Lists.newArrayList();
	// sql字段list
    List<String> columnList = Lists.newArrayList();
	
	private static String INSERT_ALL = "insert into ${tableName}(${columns}) values(${placeholder})";
	private static String DELETE_BYID = "delete from ${tableName} where ${id} = ?";
	private static String DELETE_ALL = "delete from ${tableName} ";
	private static String UPDATE_BYID = "update ${tableName} set ${sets} where ${id} = :${id}";
	private static String UPDATE_SET = "update ${tableName} set ";
	private static String SELECT_BYID = "select * from ${tableName} where ${id} = ?";
	private static String SELECT_ALL = "select * from ${tableName} ";
	
	protected SpringJdbcTemplate springJdbcTemplate;
	
	public AbstractGenericDao() {
        super();
        entityClass = ReflectUtils.getClassGenericType(this.getClass());
        idClass = ReflectUtils.getClassGenericType(this.getClass(), 1);
        init();
    }

    public AbstractGenericDao(Class<T> entityClass) {
        super();
        this.entityClass = entityClass;
        idClass = ReflectUtils.getClassGenericType(this.getClass(), 1);
        init();
    }
	
	//***************以下为方法****************//
	
	/**
	 * 延迟到子类中注入相应的SpringJdbcTemplate
	 * @param springJdbcTemplate SpringJdbcTemplate，基于JdbcTemplate封装
	 */
    public abstract void setSpringJdbcTemplate(SpringJdbcTemplate springJdbcTemplate);
    
    /**
     * 将结果集映射为泛型实体T对象，当前dao的泛型参数实体类型
     * @param rs 结果集
     * @param rowNum 行号
     * @return 泛型参数T的对象
     * @see #mapBean(Object, boolean)
     */
    public abstract T mapRows(ResultSet rs, int rowNum) throws SQLException;
    
    /**
     * 将结果集映射为resultClass参数所对应的VO对象。实现时，如果有多个VO要映射，可根据resultClass区分。（这个方法多表连接会回调）
     * @param rs 结果集
     * @param sql sql语句
     * @param resultClass 结果对象类，一般为VO对象
     * @return resultClass对应的对象
     * @see #mapBean(Object)
     * @throws SQLException
     */
    public abstract Object mapRows(ResultSet rs, String sql, Class<?> resultClass) throws SQLException;
    
    /**
     * 将实体 Bean转换为map，key为属性名 下划线命名法，value为属性值。<br>
     * 为提高性能，建议子类重写该方法，避免字节码处理。
     * @param entity 要转换的实体
     * @param prefix entity转换成Map的key是否要加前缀；前缀为 _
     * @return map，参数entity转成的Map
     * @see #mapRows(ResultSet, int)
     */
    public abstract Map<String, Object> mapBean(T entity, boolean prefix);
    
    /**
	 * 使用字节码将JavaBean转成Map，key为属性的下划线命名法，如果想要更高性能，建议子类重写该方法。（多表连接时会使用）
	 * @param params 要转换的对象
	 * @return params转成的Map
	 * @see #mapRows(ResultSet, String, Class)
	 */
	public abstract Map<String, Object> mapBean(Object params);
    
    //OK
	protected void init() {
		// 解析实体信息
	    metadata();
	    
	    // 生成一些sql语句
	    INSERT_ALL = INSERT_ALL.replace("${tableName}", tableName);
		SELECT_BYID = SELECT_BYID.replace("${tableName}", tableName).replace("${id}", metadata.getIdName());
		SELECT_ALL = SELECT_ALL.replace("${tableName}", tableName);
		UPDATE_SET = UPDATE_SET.replace("${tableName}", tableName);
		UPDATE_BYID = UPDATE_BYID.replace("${tableName}", tableName).replace("${id}", metadata.getIdName());
		DELETE_BYID = DELETE_BYID.replace("${tableName}", tableName).replace("${id}", metadata.getIdName());;
		DELETE_ALL = DELETE_ALL.replace("${tableName}", tableName);
		
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("实体类[{}] Dao，初始化成功。", entityClass.getName());
		}
	}
	
	/**
	 * 获得clazz内所有被annotation注解标注的方法的注解信息。
	 * @param clazz 目标类
	 * @param annotation 目标注解
	 * @return 类中被标注了响应注解的方法的注解信息
	 *///OK
	protected <A extends Annotation> List<A> getAnnotations(Class<?> clazz, Class<A> annotation){
		List<A> toReturn = new ArrayList<A>();
		for(Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(annotation)) {
				toReturn.add(m.getAnnotation(annotation));
			}
		}
		return toReturn;
	}
	
	/**
	 * 解析处理，当前dao所对应的实体信息。
	 *///OK
	private void metadata() {
	    EntityMetadata metadata = new EntityMetadata();
	    
	    if (entityClass == null) {
	        throw new NullPointerException("初始化DAO时，泛型实体类不能为null。");
	    }
	    
	    Table table = entityClass.getAnnotation(Table.class);
	    if (table == null) {
	        throw new NullPointerException("请使用@Table注解标示实体类，提供元数据信息。");
	    }
        tableName = table.name();
        metadata.setTableName(tableName);
        metadata.setSchema(table.schema());
        
        // 逗号分隔的 栏位名 字符串
        StringBuilder columnBuilder = new StringBuilder();
        // 逗号分隔的？占位符 字符串
        StringBuilder placeholder = new StringBuilder();
	    // java属性信息，key为属性名，value为属性类型
        Map<String, Class<?>> fieldInfo = Maps.newHashMap();
        // sql字段信息，key为字段名，value为栏字段类型
        Map<String, Class<?>> columnInfo = Maps.newHashMap();
        
	    Class<Column> annoColClass = Column.class;
	    Class<Id> annoIdClass = Id.class;
	    
	    for(Method method : entityClass.getMethods()) {
            if (method.isAnnotationPresent(annoColClass)) {
                Column column = method.getAnnotation(annoColClass);
                if (method.isAnnotationPresent(annoIdClass)) {// 获取id的信息
                    metadata.setIdName(column.name());
                    metadata.setIdClass(method.getReturnType());
                }
                columnBuilder.append(column.name()).append(", ");
                placeholder.append("?, ");
                columnList.add(column.name());
                
                fieldInfo.put(StringUtils.uncapitalize(method.getName().substring(3)), method.getReturnType());
                columnInfo.put(column.name(), method.getReturnType());
                
                //组建setter方法list
                if (method.getReturnType() == boolean.class) {// boolean类型的getter方法是is开头的
                    setterList.add("set" + method.getName().substring(2));
                } else {
                    setterList.add("set" + method.getName().substring(3));
                }
            }
        }
	    
	    if (metadata.getIdName() == null) {
	    	throw new NullPointerException("实体类[" + entityClass.getName() + "]，没有提供ID属性，请使用@Id注解标注。");
	    }
	    
	    //以逗号分隔的栏位字符串，如 ："id, userName, age, salary"
        String columns = columnBuilder.substring(0, columnBuilder.length() - 2);
        //sql问好占位符，如："?, ?, ?, ?"
        String placeholders = placeholder.substring(0, placeholder.length() - 2);
	    
        metadata.setColumns(columns);
        metadata.setColumnList(columnList);
        metadata.setPlaceholders(placeholders);
        metadata.setFieldInfo(fieldInfo);
        metadata.setColumnInfo(columnInfo);
        
	    this.metadata = metadata;
	}
	
    @Override//OK
	public ID save(T entity) {
		Map<String, Object> params = mapBean(entity, false);
		String sql = dynamicColumn(INSERT_ALL, params);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("保存实体对象sql=[{}]", sql);
		}
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		springJdbcTemplate.update(sql, params, keyHolder);
		Number key = keyHolder.getKey();// 自增主键或者序列，如果是应用程序自己生成的id，这里要调整
		return convertId(key, idClass);
	}

    public int persist(T entity) {
        Map<String, Object> params = mapBean(entity, false);
        String sql = dynamicColumn(INSERT_ALL, params);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("保存实体对象sql=[{}]", sql);
        }
        return springJdbcTemplate.update(sql, params);
    }
    
    /**
     * 转换ID
     * @param value key值
     * @param toType String，Integer，Long
     * @return 转换后的key
     *///OK
    private ID convertId(Number value, Class<ID> toType) {
        Object result = null;
        if (value != null) {
            if (toType == String.class) {
                result = value.toString();
            } else if ((toType == Integer.class)) {
                result = value.intValue();
            } else if ((toType == Long.class)) {
                result = value.longValue();
            }
        }
        @SuppressWarnings("unchecked")
        ID id = (ID) result;
        return id;
    }
    
    /**
     * 根据栏位数据动态产生insert语句。
     * @param sql insert模板sql
     * @param params 栏位数据
     *///OK
    private String dynamicColumn(String sql, Map<String, Object> params) {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (Entry<String, Object> entry : params.entrySet()) {
            columns.append(entry.getKey()).append(", ");
            placeholders.append(":").append(entry.getKey()).append(", ");
        }
        String temp = columns.substring(0, columns.length() - 2);
        sql = sql.replace("${columns}", temp);
        temp = placeholders.substring(0, placeholders.length() - 2);
        sql = sql.replace("${placeholder}", temp);
        return sql;
    }
    
	@Override//OK
	public T get(ID id) {
	    ColumnRowMapper rowMapper = new ColumnRowMapper();
		return springJdbcTemplate.queryForObject(SELECT_BYID, rowMapper, id);
	}

	class ColumnRowMapper implements RowMapper<T> {
	    
	    @Override
	    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapRows(rs, rowNum);
	    }

	}

	class BeanRowMapper<X> implements RowMapper<X> {
        private Class<X> clazz;
        private String sql;
        
        public BeanRowMapper(String sql, Class<X> clazz) {
            this.clazz = clazz;
            this.sql = sql;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public X mapRow(ResultSet rs, int rowNum) throws SQLException {
            return (X) mapRows(rs, sql, clazz);
        }

    }
	
	@Override//OK
    public T unique(T entity) {
		Map<String, Object> params = mapBean(entity, false);
        return unique(params);
    }
	
	@Override//OK
    public T unique(Map<String, ?> params) {
		List<T> list = query(params);
		if (list == null || list.isEmpty()) {
			throw new NonUniqueException("查询结果集为空。");
		} else if (list.size() >= 2) {
			throw new NonUniqueException("查询结果集size大于1。");
		}
        return list.get(0);
    }
	
	@Override//OK
	public int delete(ID id) {
		return springJdbcTemplate.update(DELETE_BYID, id);
	}

    @Override//OK
    public int deleteBatch(T entity) {
        Map<String, Object> params = mapBean(entity, false);
        String where = buildWhere(params);
        String sql = DELETE_ALL + where;
        return deleteBatch(sql, params);
    }

    @Override//OK
    public int deleteBatch(String sql, T entity) {
        return deleteBatch(sql, mapBean(entity, false));
    }

    @Override//OK
    public int deleteBatch(String sql, Map<String, ?> params) {
        return springJdbcTemplate.update(sql, params);
    }

    @Override//OK
    public int deleteBatch(String sql, Object... params) {
        return springJdbcTemplate.update(sql, params);
    }
    
    @Override//OK
    public int update(T entity) {
        Map<String, Object> params = mapBean(entity, false);
        Object id = params.remove(metadata.getIdName());// 如果不去掉id，那么构建的set语句有id
        if (id == null) {
            throw new NullPointerException("update方法是根据ID更新实体，ID属性为空，请设置ID属性值；要么使用updateBatch。");
        }
        
        String sql = buildUpdateSet(params, false);// update set 部分
        sql = UPDATE_BYID.replace("${sets}", sql);// 生成sql语句
        params.put(metadata.getIdName(), id);//将id条件加回去
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update实体对象sql=[{}]", sql);
        }
        return springJdbcTemplate.update(sql, params);
        
    }
    
    @Override//OK
    public int updateBatch(T entity, T criteria) {
        Map<String, Object> params = mapBean(criteria, false);
        return updateBatch(entity, params);
    }
    
    @Override//OK
    public int updateBatch(T entity, Map<String, ?> params) {
        StringBuilder sb = new StringBuilder(UPDATE_SET);
        //1、构造where条件
        String where = buildWhere(params);
        //2、将set参数转成Map，同时放入参数Map params中
        Map<String, Object> setMap = mapBean(entity, true);
        //3、构造update set语句部分
        String set = buildUpdateSet(setMap, true);
        
        sb.append(set).append(where);
        String sql = sb.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateBatch sql = [{}]", sql);
        }
        setMap.putAll(params);
        return springJdbcTemplate.update(sql, setMap);
    }

    @Override// OK
    public int updateBatch(String sql, Map<String, ?> params) {
        return springJdbcTemplate.update(sql, params);
    }

    @Override//OK
    public int updateBatch(String sql, T params) {
        Map<String, Object> paramMap = mapBean(params, false);
        return springJdbcTemplate.update(sql, paramMap);
    }

    @Override//OK
    public int updateBatch(String sql, Object... params) {
        return springJdbcTemplate.update(sql, params);
    }
    
    /**
     * 根据参数构建where条件
     * @param params sql参数
     * @return where语句
     *///OK
    private String buildWhere(Map<String, ?> params) {
        StringBuilder sb = new StringBuilder().append(" where 1=1");
        for (Entry<String, ?> entry : params.entrySet()) {
            sb.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
        }
        return sb.toString();
    }
    
    /**
     * 构建update语句的set语句部分，为防止和where中条件冲突，
     * set语句的命名参数，使用 _ 开头。
     * @param params sql参数
     * @param prefix 是否截取前缀，这个参数和mapBean的prefix参数一致
     * @return set语句部分
     *///OK
    private String buildUpdateSet(Map<String, Object> params, boolean prefix) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            if (prefix) {
                key = key.substring(1);
            }
            sb.append(key).append(" = :").append(entry.getKey()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    @Override//OK
    public List<T> query(T entity) {
        Map<String, Object> params = mapBean(entity, false);
        return query(params);
    }

    @Override//OK
    public List<T> query(Map<String, ?> params) {
        String sql = SELECT_ALL + buildWhere(params);
        return query(sql, params);
    }

    @Override//OK
    public List<T> query(String sql, Map<String, ?> params) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("查询实体对象sql=[{}]", sql);
        }
        ColumnRowMapper rowMapper = new ColumnRowMapper();
        return springJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override//OK
    public List<T> query(String sql, Object... params) {
        ColumnRowMapper rowMapper = new ColumnRowMapper();
        return springJdbcTemplate.query(sql, rowMapper, params);
        //return springJdbcTemplate.query(sql, entityClass, params);
    }

    @Override//OK
    public List<T> query(String sql, T params) {
        return query(sql, mapBean(params, false));
    }

	@Override//OK
	public Page<T> queryForPage(Page<T> page, T params) {
	    Map<String, Object> paramMap = mapBean(params, false);
		return queryForPage(page, paramMap);
	}

	@Override//OK
	public Page<T> queryForPage(Page<T> page, String sql, T params) {
	    Map<String, Object> paramMap = mapBean(params, false);
		return queryForPage(page, sql, paramMap);
	}

	@Override//OK
	public Page<T> queryForPage(Page<T> page, Map<String, ?> params) {
	    String sql = SELECT_ALL + buildWhere(params);
		return queryForPage(page, sql, params);
	}

	@Override//OK
	public Page<T> queryForPage(Page<T> page, String sql, Map<String, ?> params) {
		int count = count(sql, params);
		if (count == 0) {
			return page;
		} else {
			page.setTotalRecordCount(count);
		}
		//sql = preparePagedQuery(page, sql, params);
		
		sql = mysqlPagedQuery(sql, page, true);
        params = setParameterToQuery(page, params);
		
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("分页查询的sql语句=[{}]", sql);
		}
		List<T> list = query(sql, params);
		page.setResult(list);
		return page;
	}

	@Override//OK
	public Page<T> queryForPage(Page<T> page, String sql, Object... params) {
	    int count = count(sql, params);
        if (count == 0) {
            return page;
        } else {
            page.setTotalRecordCount(count);
        }
        //sql = preparePagedQuery(page, sql, params);
        sql = mysqlPagedQuery(sql, page, false);
        params = setParameterToQuery(page, params);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("分页查询的sql语句=[{}]", sql);
        }
        List<T> list = query(sql, params);
        page.setResult(list);
        return page;
	}

	/**
	 * 构造mysql的分页查询sql
	 * @param sql sql语句
	 * @param page 分页数据
	 * @param named 是否命名sql
	 * @return 分页sql语句
	 *///OK
	protected <X> String mysqlPagedQuery(String sql, Page<X> page, boolean named) {
		StringBuilder sb = new StringBuilder(sql);
		Map<String, String> orders = page.getOrders();
		if (orders != null && orders.size() >= 1 ) {
			sb.append(" order by ");
			for (Entry<String, String> entry : orders.entrySet()) {
				sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
			}
		}
		sql = sb.substring(0, sb.length() -2);
		if (named) {
		    sql += " limit :startIndex , :pageSize";
		} else {
		    sql += " limit ? , ?";
		}
		return sql;
	}
	
	/**
	 * 设置分页参数
	 * @param page 分页数据
	 * @param params sql参数
	 *///OK
	protected <X> Map<String, Object> setParameterToQuery(Page<X> page, Map<String, ?> params) {
	    Map<String, Object> map = new HashMap<String, Object>();
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		map.putAll(params);
		return map;
	}
	
	/**
     * 设置分页参数
     * @param page 分页数据
     * @param params sql参数
     *///OK
    protected <X> Object[] setParameterToQuery(Page<X> page, Object... params) {
        if (params != null) {
            params = ArrayUtils.addAll(params, page.getStartIndex(), page.getPageSize());
        }
        return params;
    }
	
	/**
	 * 设置分页sql，以及分页参数
	 * @param page 分页数据
	 * @param sql sql语句
	 * @param params sql参数
	 *///OK
    @Deprecated
	protected <X> String preparePagedQuery(Page<X> page, String sql, Map<String, ?> params) {
		sql = mysqlPagedQuery(sql, page, true);
		setParameterToQuery(page, params);
		return sql;
	}
	
	/**
     * 设置分页sql，以及分页参数。因为要返回两个参数，还要使用Object数据，在代码中直接调用算了。
     * @param page 分页数据
     * @param sql sql语句
     * @param params sql参数
     *///OK
	@Deprecated
    protected <X> Object[] preparePagedQuery(Page<X> page, String sql, Object... params) {
        Object[] result = new Object[2];
	    sql = mysqlPagedQuery(sql, page, false);
	    result[0] = sql;
        params = setParameterToQuery(page, params);
        result[1] = params;
        return result;
    }
	
	/**
	 * select显示的栏位与order by排序会影响count查询效率，进行简单的排除，未考虑union
	 * @param sql 原始sql
	 * @return 排除order by和显示栏位后的sql
	 * @author yinlei
	 * date 2012-7-14 下午11:31:21
	 *///OK
	protected String prepareCountSql(String sql) {
		String subsql = sql;
		StringBuilder sb = new StringBuilder("select count(*) count from ");
		subsql = StringUtils.substringAfter(subsql, "from");
		subsql = StringUtils.substringBefore(subsql, "order by");
		sb.append(subsql);
		return sb.toString();
	}
	
	/**
	 * 计算记录数
	 * @param sql sql语句
	 * @param params 参数
	 * @return 记录数
	 *///OK
	protected int count(String sql, Map<String, ?> params) {
		String countSQL = prepareCountSql(sql);
		return springJdbcTemplate.queryForObject(countSQL, params, Integer.class);
	}
	
	/**
     * 计算记录数
     * @param sql sql语句
     * @param params 参数
     * @return 记录数
     *///OK
	protected int count(String sql, Object... params) {
		String countSQL = prepareCountSql(sql);
		return springJdbcTemplate.queryForObject(countSQL, Integer.class, params);
	}
	
	@Override//OK
	public <VO> List<VO> queryList(String sql, VO params) {
		@SuppressWarnings("unchecked")
		Class<VO> resultClass = (Class<VO>) params.getClass();
		return queryList(sql, resultClass, mapBean(params));
	}

	@Override//OK
	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Map<String, ?> params) {
	    BeanRowMapper<VO> rowMapper = new BeanRowMapper<VO>(sql, resultClass);
	    return springJdbcTemplate.query(sql, params, rowMapper);
		//return springJdbcTemplate.query(sql, resultClass, params);
	}

	@Override//OK
	public <VO> List<VO> queryList(String sql, Class<VO> resultClass,
			Object... params) {
	    BeanRowMapper<VO> rowMapper = new BeanRowMapper<VO>(sql, resultClass);
	    return springJdbcTemplate.query(sql, rowMapper, params);
		//return springJdbcTemplate.query(sql, resultClass, params);
	}

	@Override//OK
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql, VO params) {
		@SuppressWarnings("unchecked")
		Class<VO> resultClass = (Class<VO>) params.getClass();
		return queryPageList(page, sql, resultClass, mapBean(params));
	}

	@Override//OK
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Map<String, ?> params) {
		int count = count(sql, params);
		if (count == 0) {
			return page;
		} else {
			page.setTotalRecordCount(count);
		}
		//sql = preparePagedQuery(page, sql, params);
		
		sql = mysqlPagedQuery(sql, page, true);
        params = setParameterToQuery(page, params);
		
		List<VO> list = queryList(sql, resultClass, params);
		page.setResult(list);
		return page;
	}

	@Override//OK
	public <VO> Page<VO> queryPageList(Page<VO> page, String sql,
			Class<VO> resultClass, Object... params) {
		int count = count(sql, params);
		if (count == 0) {
			return page;
		} else {
			page.setTotalRecordCount(count);
		}
		//sql = preparePagedQuery(page, sql, params);
		sql = mysqlPagedQuery(sql, page, false);
        params = setParameterToQuery(page, params);
		if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("分页查询的sql语句=[{}]", sql);
        }
		List<VO> list = queryList(sql, resultClass, params);
		page.setResult(list);
		return page;
	}
	
	@Override//OK
	public <X> X queryForObject(String sql, Map<String, ?> paramMap,
			Class<X> requiredType) {
		return springJdbcTemplate.queryForObject(sql, paramMap, requiredType);
	}

	@Override//OK
	public <X> X queryForObject(String sql, Class<X> requiredType,
			Object... params) {
		return springJdbcTemplate.queryForObject(sql, requiredType, params);
	}
}
