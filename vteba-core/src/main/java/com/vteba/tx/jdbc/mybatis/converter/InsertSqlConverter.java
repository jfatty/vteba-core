package com.vteba.tx.jdbc.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

public class InsertSqlConverter extends AbstractSqlConverter {

    protected Statement doConvert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Insert)) {
            throw new IllegalArgumentException("The argument statement must is instance of Insert.");
        }
        Insert insert = (Insert) statement;

        String name = insert.getTable().getName();
        insert.getTable().setName(convertTableName(name, params, mapperId));

        return insert;
    }
}
