package com.vteba.tx.jdbc.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * sql转换接口的抽象实现。
 * @author yinlei 
 * @since 2013-12-17 12:55
 */
public abstract class AbstractSqlConverter implements SqlConverter {

    public String convert(Statement statement, Object params, String mapperId) {
        return doDeParse(doConvert(statement, params, mapperId));
    }

    /**
     * sql逆向转换，将转换后的sql语句，转换成字符串。
     * @param statement sql语句Statement
     * @return sql字符串
     */
    protected String doDeParse(Statement statement) {
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        statement.accept(deParser);
        return deParser.getBuffer().toString();
    }

    protected String convertTableName(String tableName, Object params, String mapperId) {
        ShardingConfigFactory configFactory = ShardingConfigFactory.getInstance();
        ShardingStrategy strategy = configFactory.getStrategy(tableName);
        if (strategy == null) {
            return tableName;
        }
        return strategy.getTargetTableName(tableName, params, mapperId);
    }

    /**
     * 子类实现具体的转换工作
     * @param statement sql语句
     * @param paramObject sql参数
     * @param mapperId mybatis sql映射id
     * @return
     */
    protected abstract Statement doConvert(Statement statement, Object paramObject, String mapperId);
}
