package com.vteba.tx.matrix.routing;

import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

/**
 * 解析查询哪张表
 * @author yinlei 
 * @see
 * @since 2013-12-5 16:27
 */
public class LocateShardingTable {

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
        String sqls = "select * from user u, address a where u.user_id = a.user_id and u.user_id = ?";
        long d = System.currentTimeMillis();
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sqls, "mysql");
        SQLSelectStatement sqlSelectStatement = parser.parseSelect();
        SQLSelect sqlSelect = sqlSelectStatement.getSelect();
        
        
        SQLSelectQueryBlock sqlSelectQuery = (SQLSelectQueryBlock) sqlSelect.getQuery();
        SQLTableSource sqlTableSource = sqlSelectQuery.getFrom();
        SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) sqlTableSource;
        
        SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlJoinTableSource.getLeft();//sqlTableSource;
        sqlExprTableSource.setExpr(new SQLIdentifierExpr("user_201409m"));
        
        
        sqlSelectQuery.setFrom(new SQLExprTableSource(new SQLIdentifierExpr("user_201409m")));
        System.out.println("druid:" + (System.currentTimeMillis() - d));
        
        System.out.println(sqlTableSource.toString());
        List<SQLSelectItem> selectList = sqlSelectQuery.getSelectList();
        for (SQLSelectItem item : selectList) {
            System.out.println(item.toString());
        }
        
        
        StringBuffer buffer = new StringBuffer();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(buffer);
        sqlSelectQuery.accept(visitor);
        System.out.println(buffer.toString());
        System.out.println();
        
        d = System.currentTimeMillis();
        
        try {
            Statement statement = CCJSqlParserUtil.parse(sqls);
            Select select = (Select) statement;
            ChangeSelectVisitor changeSelectVisitor = new ChangeSelectVisitor();
            select.getSelectBody().accept(changeSelectVisitor);
            
            System.out.println("返回的sql" + select.getSelectBody().toString());
            //System.out.println(changeSelectVisitor.toString());
            
            //select.getSelectBody();
        } catch (JSQLParserException e) {
            
        }
        System.out.println("jsqlparser:" + (System.currentTimeMillis() - d));
        
        String sqla = "select * from user u where u.user_id = a.user_id and u.user_id = ?";
        d = System.currentTimeMillis();
        String sub = sqla.substring(sqla.indexOf("from") + 4, sqla.indexOf("where")).trim();
        String[] aaa = sub.split(" ");
        String tableName = aaa[0];
        
        System.out.println(tableName + (System.currentTimeMillis() -d));
    }
    
    static class ChangeSelectVisitor implements SelectVisitor, FromItemVisitor {

        @Override
        public void visit(Table tableName) {
            //tableName.setName("user_201409m");
            
        }

        @Override
        public void visit(SubSelect subSelect) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(SubJoin subjoin) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(LateralSubSelect lateralSubSelect) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(ValuesList valuesList) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(PlainSelect plainSelect) {
            // TODO Auto-generated method stub
            System.out.println("select visit");
            //plainSelect.setInto(new Table("user_201309m"));
            
            //FromItemVisitor fromItemVisitor = new FromItemVisitorAdapter();
            //fromItemVisitor.visit(new Table("asdfasd"));
            
            //plainSelect.getFromItem().accept(fromItemVisitor);
            
            //System.out.println(fromItemVisitor.toString());
            plainSelect.setFromItem(new Table("user_201309m"));
            plainSelect.getJoins();
        }

        @Override
        public void visit(SetOperationList setOpList) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void visit(WithItem withItem) {
            // TODO Auto-generated method stub
            
        }

        
    }
}
