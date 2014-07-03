package com.vteba.tx.jdbc.spring.impl;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vteba.common.exception.NonUniqueException;
import com.vteba.tx.generic.Page;
import com.vteba.tx.jdbc.spring.SpringJdbcTemplate;
import com.vteba.tx.jdbc.spring.meta.EntityMetadata;
import com.vteba.tx.jdbc.spring.spi.SpringGenericDao;
import com.vteba.utils.reflection.ReflectUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * spring通用泛型dao实现。
 * @author yinlei 
 * @since 2013-7-6 16:00
 * @param <T> 泛型实体
 * @param <ID> 主键类型
 */
public abstract class AbstractGenericDao<T, ID extends Serializable> implements SpringGenericDao<T, ID> {
	protected String tableName;
	protected Class<T> entityClass;
	protected Class<ID> idClass;
	protected EntityMetadata metadata;
	
	protected List<String> setterList = Lists.newArrayList();
	
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
     * 将结果集映射为实体对象Bean
     * @param rs 结果集
     * @param rowNum 行号
     * @return 实体对象
     */
    public abstract T mapRows(ResultSet rs, int rowNum) throws SQLException;
    
    /**
     * 将实体 Bean转换为map，key为属性名，value为属性值。<br>
     * 以后可以抽象，延迟到子类中自己实现，避免字节码处理
     * @param entity 要转换的实体
     * @param prefix entity转换成Map的key是否要加前缀；如果为false，第三个参数不需要了
     * @param params sql参数，如果prefix == true，参数entity转成的map也要放入该参数中
     * @return map，参数entity转成的Map
     */
    public abstract Map<String, Object> mapBean(T entity, boolean prefix, Map<String, Object> params);

	protected void init() {
		// 解析实体信息
	    metadata();
	    
	    // 生成一些sql语句
	    INSERT_ALL = INSERT_ALL.replace("${tableName}", tableName);
		SELECT_BYID = SELECT_BYID.replace("${tableName}", tableName).replace("${id}", metadata.getIdName());
		SELECT_ALL = SELECT_ALL.replace("${tableName}", tableName);
		UPDATE_SET = UPDATE_SET.replace("${tableName}", tableName);
		UPDATE_BYID = UPDATE_BYID.replace("${tableName}", tableName).replaceAll("${id}", metadata.getIdName());
		DELETE_BYID = DELETE_BYID.replace("${tableName}", tableName).replace("${id}", metadata.getIdName());;
		DELETE_ALL = DELETE_ALL.replace("${tableName}", tableName);
	}
	
	public String process(Object model) {
		String sql = null;
		try {
			Configuration configuration = new Configuration();
			configuration.setEncoding(Locale.CHINA, "UTF-8");
			
			Template template = new Template("/", new StringReader(SELECT_BYID), configuration);
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("tableName", "user_");
			sql = FreeMarkerTemplateUtils.processTemplateIntoString(template, root);
		} catch (IOException e) {
			
		} catch (TemplateException e) {
			
		}
		return sql;
	}
	
	public static void main(String[] aa) {
//		String sql = null;
//		try {
//			Configuration configuration = new Configuration();
//			configuration.setEncoding(Locale.CHINA, "UTF-8");
//			
//			Template template = new Template("/", new StringReader(SELECT_BYID), configuration);
//			Map<String, Object> root = new HashMap<String, Object>();
//			root.put("tableName", "user_");
//			sql = FreeMarkerTemplateUtils.processTemplateIntoString(template, root);
//		} catch (IOException e) {
//			
//		} catch (TemplateException e) {
//			
//		}
//		System.out.println(sql);
	    
	    String sql = INSERT_ALL;
	    
	    sql = sql.substring(0, 4);
	    System.out.println(sql);
	    System.out.println(INSERT_ALL);
	}
	
	
	/**
	 * 获得clazz内所有被annotation注解标注的方法的注解信息。
	 * @param clazz 目标类
	 * @param annotation 目标注解
	 * @return 类中被标注了响应注解的方法的注解信息
	 */
	public static <T extends Annotation> List<T> getAnnotations(Class<?> clazz, Class<T> annotation){
		List<T> toReturn = new ArrayList<T>();
		for(Method m : clazz.getMethods()) {
			if (m.isAnnotationPresent(annotation)) {
				toReturn.add(m.getAnnotation(annotation));
			}
		}
		return toReturn;
	}
	
	/**
	 * 解析处理，当前dao所对应的实体信息。
	 */
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
        
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder placeholder = new StringBuilder();
	    
        Map<String, Class<?>> fieldInfo = Maps.newHashMap();
        Map<String, Class<?>> columnInfo = Maps.newHashMap();
        
        List<String> columnList = Lists.newArrayList();
        
	    Class<Column> annoColClass = Column.class;
	    Class<Id> annoIdClass = Id.class;
	    
	    for(Method method : entityClass.getMethods()) {
            if (method.isAnnotationPresent(annoColClass)) {
                Column column = method.getAnnotation(annoColClass);
                if (method.isAnnotationPresent(annoIdClass)) {
                    metadata.setIdName(column.name());
                    metadata.setIdClass(method.getReturnType());
                }
                columnBuilder.append(column.name()).append(", ");
                placeholder.append("?, ");
                columnList.add(column.name());
                
                fieldInfo.put(StringUtils.uncapitalize(method.getName().substring(3)), method.getReturnType());
                columnInfo.put(column.name(), method.getReturnType());
                
                setterList.add("set" + method.getName().substring(3));
            }
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
	
    @Override
    @SuppressWarnings("unchecked")
	public ID save(T entity) {
		Map<String, Object> params = mapBean(entity, false, null);
		String sql = INSERT_ALL;
		dynamicColumn(sql, params);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		springJdbcTemplate.update(sql, params, keyHolder);
		Number key = keyHolder.getKey();
		ID ids = null;
		if (idClass == String.class) {
		    ids = (ID) key.toString();
		} else if (idClass == Long.class) {
            ids = (ID) Long.valueOf(key.longValue());
        } else if (idClass == Integer.class) {
            ids = (ID) Integer.valueOf(key.intValue());
		}
		return ids;
	}

    /**
     * 根据栏位数据动态产生insert语句。
     * @param sql insert模板sql
     * @param params 栏位数据
     */
    private void dynamicColumn(String sql, Map<String, Object> params) {
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
    }
    
	@Override
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

	@Override
    public T unique(T entity) {
		Map<String, Object> params = mapBean(entity, false, null);
        return unique(params);
    }
	
	@Override
    public T unique(Map<String, Object> params) {
		List<T> list = query(params);
		if (list == null || list.isEmpty()) {
			throw new NonUniqueException("查询结果集为空。");
		} else if (list.size() >= 2) {
			throw new NonUniqueException("查询结果集size大于1。");
		}
        return list.get(0);
    }
	
	@Override
	public int delete(ID id) {
		return springJdbcTemplate.update(DELETE_BYID, id);
	}

    @Override
    public int deleteBatch(T entity) {
        Map<String, Object> params = mapBean(entity, false, null);
        String where = buildWhere(params);
        String sql = DELETE_ALL + where;
        return deleteBatch(sql, params);
    }

    @Override
    public int deleteBatch(String sql, T entity) {
        return deleteBatch(sql, mapBean(entity, false, null));
    }

    @Override
    public int deleteBatch(String sql, Map<String, Object> params) {
        return springJdbcTemplate.update(sql, params);
    }

    @Override
    public int deleteBatch(String sql, Object... params) {
        return springJdbcTemplate.update(sql, params);
    }
    
    @Override
    public int update(T entity) {
        Map<String, Object> params = mapBean(entity, false, null);
        Object id = params.get(metadata.getIdName());
        params.remove(metadata.getIdName());// 如果不去掉id，那么构建的set语句有id
        String sql = buildUpdateSet(params);// update set 部分
        sql = UPDATE_BYID.replace("${sets}", sql);// 生成sql语句
        params.put(metadata.getIdName(), id);//将id条件加回去
        return springJdbcTemplate.update(sql, params);
        
    }
    
    @Override
    public int updateBatch(T entity, T criteria) {
        Map<String, Object> params = mapBean(criteria, false, null);
        return updateBatch(entity, params);
    }
    
    @Override
    public int updateBatch(T entity, Map<String, Object> params) {
        //1、构造where条件
        String where = buildWhere(params);
        //2、将set参数转成Map，同时放入参数Map params中
        Map<String, Object> setMap = mapBean(entity, true, params);
        //3、构造update set语句部分
        String set = buildUpdateSet(setMap);
        
        String sql = UPDATE_SET + set + where;
        
        return springJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateBatch(String sql, Map<String, Object> params) {
        return springJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateBatch(String sql, T params) {
        Map<String, Object> paramMap = mapBean(params, false, null);
        return springJdbcTemplate.update(sql, paramMap);
    }

    @Override
    public int updateBatch(String sql, Object... params) {
        return springJdbcTemplate.update(sql, params);
    }
    
    /**
     * 根据参数构建where条件
     * @param params sql参数
     * @return where语句
     */
    private String buildWhere(Map<String, Object> params) {
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
     * @return set语句部分
     */
    private String buildUpdateSet(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            sb.append(key.substring(1)).append(" = :").append(key).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    @Override
    public List<T> query(T entity) {
        Map<String, Object> params = mapBean(entity, false, null);
        return query(params);
    }

    @Override
    public List<T> query(Map<String, Object> params) {
        String sql = SELECT_ALL + buildWhere(params);
        return query(sql, params);
    }

    @Override
    public List<T> query(String sql, Map<String, Object> params) {
        ColumnRowMapper rowMapper = new ColumnRowMapper();
        return springJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public List<T> query(String sql, Object... params) {
        return springJdbcTemplate.query(sql, entityClass, params);
    }

    @Override
    public List<T> query(String sql, T params) {
        return query(sql, mapBean(params, false, null));
    }

	@Override
	public Page<T> queryForPage(Page<T> page, T params) {
	    Map<String, Object> paramMap = mapBean(params, false, null);
		return queryForPage(page, paramMap);
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, T params) {
	    Map<String, Object> paramMap = mapBean(params, false, null);
		return queryForPage(page, sql, paramMap);
	}

	@Override
	public Page<T> queryForPage(Page<T> page, Map<String, Object> params) {
	    String sql = SELECT_ALL + buildWhere(params);
		return queryForPage(page, sql, params);
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, Map<String, Object> params) {
		int count = count(sql, params);
		if (count == 0) {
			return page;
		} else {
			page.setTotalRecordCount(count);
		}
		preparePagedQuery(page, sql, params);
		List<T> list = query(sql, params);
		page.setResult(list);
		return page;
	}

	@Override
	public Page<T> queryForPage(Page<T> page, String sql, Object... params) {
	    int count = count(sql, params);
        if (count == 0) {
            return page;
        } else {
            page.setTotalRecordCount(count);
        }
        preparePagedQuery(page, sql, params);
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
	 */
	protected String mysqlPagedQuery(String sql, Page<T> page, boolean named) {
		StringBuilder sb = new StringBuilder(sql);
		Map<String, String> orders = page.getOrders();
		if (orders != null && orders.size() >= 1 ) {
			sb.append(" order by");
			for (Entry<String, String> entry : orders.entrySet()) {
				sb.append(" ").append(entry.getKey()).append(" ").append(entry.getValue());
			}
		}
		if (named) {
		    sb.append(" limit :startIndex , :pageSize");
		} else {
		    sb.append(" limit ? , ?");
		}
		return sb.toString();
	}
	
	/**
	 * 设置分页参数
	 * @param page 分页数据
	 * @param params sql参数
	 */
	protected void setParameterToQuery(Page<T> page, Map<String, Object> params) {
		params.put("startIndex", page.getStartIndex());
		params.put("pageSize", page.getPageSize());
	}
	
	/**
     * 设置分页参数
     * @param page 分页数据
     * @param params sql参数
     */
    protected void setParameterToQuery(Page<T> page, Object... params) {
        if (params != null) {
            params = ArrayUtils.addAll(params, page.getStartIndex(), page.getPageSize());
        }
    }
	
	/**
	 * 设置分页sql，以及分页参数
	 * @param page 分页数据
	 * @param sql sql语句
	 * @param params sql参数
	 */
	protected void preparePagedQuery(Page<T> page, String sql, Map<String, Object> params) {
		sql = mysqlPagedQuery(sql, page, true);
		setParameterToQuery(page, params);
	}
	
	/**
     * 设置分页sql，以及分页参数
     * @param page 分页数据
     * @param sql sql语句
     * @param params sql参数
     */
    protected void preparePagedQuery(Page<T> page, String sql, Object... params) {
        sql = mysqlPagedQuery(sql, page, false);
        setParameterToQuery(page, params);
    }
	
	/**
	 * select显示的栏位与order by排序会影响count查询效率，进行简单的排除，未考虑union
	 * @param sql 原始sql
	 * @return 排除order by和显示栏位后的sql
	 * @author yinlei
	 * date 2012-7-14 下午11:31:21
	 */
	protected String prepareCountSql(String sql) {
		String fromSql = sql;
		StringBuilder sb = new StringBuilder("select count(*) count from ");
		fromSql = StringUtils.substringAfter(fromSql, "from");
		fromSql = StringUtils.substringBefore(fromSql, "order by");
		sb.append(fromSql);
		return sb.toString();
	}
	
	/**
	 * 计算记录数
	 * @param sql sql语句
	 * @param params 参数
	 * @return 记录数
	 */
	protected int count(String sql, Map<String, Object> params) {
		String countSQL = prepareCountSql(sql);
		return springJdbcTemplate.queryForObject(countSQL, params, Integer.class);
	}
	
	/**
     * 计算记录数
     * @param sql sql语句
     * @param params 参数
     * @return 记录数
     */
	protected int count(String sql, Object... params) {
		String countSQL = prepareCountSql(sql);
		return springJdbcTemplate.queryForObject(countSQL, Integer.class, params);
	}
}
