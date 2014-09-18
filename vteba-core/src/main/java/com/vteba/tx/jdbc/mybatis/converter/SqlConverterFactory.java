package com.vteba.tx.jdbc.mybatis.converter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.google.common.collect.Lists;
import com.vteba.tx.jdbc.mybatis.ShardingException;

/**
 * sql转换工厂
 * @author yinlei 
 * @since 2013-12-17 12:51
 */
public class SqlConverterFactory {

    private static final Log           log     = LogFactory.getLog(SqlConverterFactory.class);
    private static SqlConverterFactory factory = new SqlConverterFactory();
    private Map<String, SqlConverter>  converterMap;
    private CCJSqlParserManager        pm;

    public static SqlConverterFactory getInstance() {
        return factory;
    }

    private SqlConverterFactory() {
        this.converterMap = new HashMap<String, SqlConverter>();
        this.pm = new CCJSqlParserManager();
        register();
    }

    private void register() {
        this.converterMap.put(Select.class.getName(), new SelectSqlConverter());
        this.converterMap.put(Insert.class.getName(), new InsertSqlConverter());
        this.converterMap.put(Update.class.getName(), new UpdateSqlConverter());
        this.converterMap.put(Delete.class.getName(), new DeleteSqlConverter());
    }

    public List<String> convert(String sql, Object params, String mapperId) throws ShardingException {
        Statement statement = null;
        try {
            statement = this.pm.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            log.error(e.getMessage(), e);
            throw new ShardingException(e);
        }
        SqlConverter converter = this.converterMap.get(statement.getClass().getName());
        if (converter != null) {
            return converter.convert(statement, params, mapperId);
        }
        return Lists.newArrayList(sql);
    }
}
