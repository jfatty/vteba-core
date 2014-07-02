package com.vteba.tx.jdbc.spring;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.vteba.tx.jdbc.spi.SpringGenericDao;
import com.vteba.utils.reflection.BeanCopyUtils;
import com.vteba.utils.reflection.ReflectUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SpringGenericDaoImpl<T, ID extends Serializable> implements SpringGenericDao<T, ID> {
	protected String tableName;
	protected Class<T> entityClass;
	protected Class<ID> idClass;
	
	private List<String> sqlColumnList = new ArrayList<String>();
	
	private static String INSERT = "insert into ${tableName}(${columns}) values(${placeholder})";
	private static String DELETE = "delete from ${tableName} where id = ?";
	private static String DELETE_WHERE = "delete from ${tableName} ";
	private static String UPDATE = "update ${tableName} set ${?} where id = ?";
	private static String UPDATE_SET = "update ${tableName} set ";
	private static String SELECT = "select * from ${tableName} where id = ?";
	private static String SELECT_ALL = "select * from ";
	
	private SpringJdbcTemplate springJdbcTemplate;
	
	public SpringGenericDaoImpl() {
		super();
		entityClass = ReflectUtils.getClassGenericType(this.getClass());
		idClass = ReflectUtils.getClassGenericType(getClass(), 1);
		init();
	}

	public SpringGenericDaoImpl(String tableName) {
		super();
		this.tableName = tableName;
		entityClass = ReflectUtils.getClassGenericType(this.getClass());
		init();
	}
	
	public SpringGenericDaoImpl(String tableName, Class<T> entityClass) {
		super();
		this.tableName = tableName;
		this.entityClass = entityClass;
		init();
	}

	protected void init() {
		Table table = entityClass.getAnnotation(Table.class);
		tableName = table.name();
		List<Column> columnList = getAnnotations(entityClass, Column.class);
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder placeholders = new StringBuilder();
		for (Column col : columnList) {
			columnBuilder.append(col.name()).append(", ");
			placeholders.append("?, ");
			sqlColumnList.add(col.name());
		}
		String columns = columnBuilder.substring(0, columnBuilder.length() - 2);
		String placeholder = placeholders.substring(0, placeholders.length() - 2);
		
		SELECT = SELECT.replace("${tableName}", tableName);
		SELECT_ALL = SELECT_ALL + tableName;
		
		INSERT = INSERT.replace("${tableName}", tableName)
				.replace("${columns}", columns)
				.replace("${placeholder}", placeholder);

		UPDATE_SET = UPDATE_SET.replace("${tableName}", tableName);
		
		DELETE = DELETE.replace("${tableName}", tableName);
		
		DELETE_WHERE = DELETE_WHERE.replace("${tableName}", tableName);
	}
	
	public String process(Object model) {
		String sql = null;
		try {
			Configuration configuration = new Configuration();
			configuration.setEncoding(Locale.CHINA, "UTF-8");
			
			Template template = new Template("/", new StringReader(SELECT), configuration);
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("tableName", "user_");
			sql = FreeMarkerTemplateUtils.processTemplateIntoString(template, root);
		} catch (IOException e) {
			
		} catch (TemplateException e) {
			
		}
		return sql;
	}
	
	public static void main(String[] aa) {
		String sql = null;
		try {
			Configuration configuration = new Configuration();
			configuration.setEncoding(Locale.CHINA, "UTF-8");
			
			Template template = new Template("/", new StringReader(SELECT), configuration);
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("tableName", "user_");
			sql = FreeMarkerTemplateUtils.processTemplateIntoString(template, root);
		} catch (IOException e) {
			
		} catch (TemplateException e) {
			
		}
		System.out.println(sql);
	}
	
	
	/**
	 * 获得clazz内所有被annotation注解标注的方法的注解信息。
	 * @param clazz 目标类
	 * @param annotation 目标注解
	 * @return 类中被标注了响应注解的方法的注解信息
	 */
	public static <T extends Annotation> List<T> getAnnotations(Class<?> clazz, Class<T> annotation){
		List<T> toReturn = new ArrayList<T>();
		for(Method m : clazz.getMethods()){
			if (m.isAnnotationPresent(annotation)) {
				toReturn.add(m.getAnnotation(annotation));
			} else if (m.isAnnotationPresent(Id.class)) {
				//toReturn.add(m.getAnnotation(annotation));
			}
		}
		return toReturn;
	}
	
	@Override
	public ID save(T entity) {
		Map<String, Object> paramMap = maps(entity);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		springJdbcTemplate.update(INSERT, paramMap, keyHolder);
		Number key = keyHolder.getKey();
		if (idClass == String.class) {
		    @SuppressWarnings("unchecked")
            ID id = (ID) key.toString();
		    return id;
		} else {
		    @SuppressWarnings("unchecked")
            ID id = (ID) key;
		    return id;
		}
	}

	@Override
    public ID saveAll(T entity) {
        // TODO Auto-generated method stub
        return null;
    }
	
	@Override
	public T get(ID id) {
		GenericRowMapper<T> rowMapper = new GenericRowMapper<T>(entityClass, SELECT);
		return springJdbcTemplate.queryForObject(SELECT, rowMapper, id);
	}

	@Override
    public T get(T entity) {
        // TODO Auto-generated method stub
        return null;
    }
	
	@Override
	public int delete(ID id) {
		return springJdbcTemplate.update(DELETE, id);
	}

    @Override
    public int deleteBatch(T entity) {
        Map<String, Object> params = maps(entity);
        String where = buildWhere(params);
        String sql = DELETE_WHERE + where;
        return deleteBatch(sql, params);
    }

    @Override
    public int deleteBatch(String sql, T entity) {
        return deleteBatch(sql, maps(entity));
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
        Map<String, Object> toMap = new HashMap<String, Object>();
        BeanCopyUtils.get().beanToMap(entity, toMap);
        return springJdbcTemplate.update(UPDATE, toMap);
        
    }
    
    @Override
    public int updateAll(T entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int updateBatch(T entity, T criteria) {
        Map<String, Object> params = maps(criteria);
        return updateBatch(entity, params);
    }
    
    @Override
    public int updateBatch(T entity, Map<String, Object> params) {
        Map<String, Object> setMap = BeanCopyUtils.get().toMapPrefix(entity);
        String set = buildUpdateSet(setMap);
        String where = buildWhere(params);
        BeanCopyUtils.get().beanToMapPrefix(entity, params);
        
        String sql = UPDATE_SET + set + where;
        
        return springJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateBatch(String sql, Map<String, Object> params) {
        return springJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateBatch(String sql, T params) {
        Map<String, Object> paramMap = maps(params);
        return springJdbcTemplate.update(sql, paramMap);
    }

    @Override
    public int updateBatch(String sql, Object... params) {
        return springJdbcTemplate.update(sql, params);
    }
    
    private Map<String, Object> maps(T entity) {
        return BeanCopyUtils.get().beanToMaps(entity);
    }
    
    private String buildWhere(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder().append(" where 1=1");
        for (Entry<String, ?> entry : params.entrySet()) {
            sb.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
        }
        return sb.toString();
    }
    
    private String buildUpdateSet(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            sb.append(StringUtils.uncapitalize(key.substring(3))).append(" = :").append(key).append(" ");
        }
        return sb.toString();
    }
    
    @Override
    public List<T> query(T entity) {
        Map<String, Object> params = maps(entity);
        return query(params);
    }

    @Override
    public List<T> query(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(SELECT_ALL).append(" where 1=1");
        for (Entry<String, ?> entry : params.entrySet()) {
            sb.append(" and ").append(entry.getKey()).append(" = :").append(entry.getKey());
        }
        return query(sb.toString(), params);
    }

    @Override
    public List<T> query(String sql, Map<String, Object> params) {
        RowMapper<T> rowMapper = new GenericRowMapper<T>(entityClass, sql);
        return springJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public List<T> query(String sql, Object... params) {
        return springJdbcTemplate.query(sql, entityClass, params);
    }

    @Override
    public List<T> query(String sql, T params) {
        return query(sql, maps(params));
    }

}
