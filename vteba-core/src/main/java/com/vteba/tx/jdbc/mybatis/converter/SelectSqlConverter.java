package com.vteba.tx.jdbc.mybatis.converter;

import java.util.List;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

import com.vteba.tx.jdbc.mybatis.visitor.TableNameVisitor;

/**
 * select sql语句转换
 * @author yinlei
 * @since 2013-12-17
 */
public class SelectSqlConverter extends AbstractSqlConverter {

    public List<String> convert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Select)) {
            throw new IllegalArgumentException("The argument statement must is instance of Select.");
        }
        Select select = (Select) statement;
        TableNameVisitor visitor = new TableNameVisitor(select, params, mapperId);
        select.getSelectBody().accept(visitor);
        
        return visitor.getSqlList();
    }

}
