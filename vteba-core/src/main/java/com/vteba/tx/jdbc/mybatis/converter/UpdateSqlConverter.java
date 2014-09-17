package com.vteba.tx.jdbc.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

/**
 * update sql语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class UpdateSqlConverter extends AbstractSqlConverter {

    protected Statement doConvert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Update)) {
            throw new IllegalArgumentException("The argument statement must is instance of Update.");
        }
        Update update = (Update) statement;
        String name = update.getTable().getName();
        update.getTable().setName(convertTableName(name, params, mapperId));

        return update;
    }
}
