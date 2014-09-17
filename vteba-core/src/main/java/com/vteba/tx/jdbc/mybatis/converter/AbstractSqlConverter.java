package com.vteba.tx.jdbc.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

public abstract class AbstractSqlConverter implements SqlConverter {

    public String convert(Statement statement, Object params, String mapperId) {
        return doDeParse(doConvert(statement, params, mapperId));
    }

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

    protected abstract Statement doConvert(Statement paramStatement, Object paramObject, String paramString);
}
