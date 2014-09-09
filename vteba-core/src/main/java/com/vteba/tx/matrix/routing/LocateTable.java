package com.vteba.tx.matrix.routing;

import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 * 解析查询哪张表
 * @author yinlei 
 * @see
 * @since 2013-12-5 16:27
 */
public class LocateTable {

    public String selectTable(String sql) {
        
        return sql;
    }
    
    public String insertTable(String sql) {
        
        return sql;
    }
    
    public String updateTable(String sql) {
        
        return sql;
    }
    
    public String deleteTable(String sql) {
        
        return sql;
    }
    
    public String createTable(String sql) {
        
        return sql;
    }
    
    public static void main(String[] aa) {
        String sqls = "select * from user where user_id = ?";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sqls, "mysql");
        SQLSelectStatement sqlSelectStatement = parser.parseSelect();
        SQLSelect sqlSelect = sqlSelectStatement.getSelect();
        
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock) sqlSelect.getQuery();
        SQLTableSource sqlTableSource = sqlSelectQuery.getFrom();
        System.out.println(sqlTableSource.toString());
        List<SQLSelectItem> selectList = sqlSelectQuery.getSelectList();
        for (SQLSelectItem item : selectList) {
            System.out.println(item.toString());
        }
    }
}
