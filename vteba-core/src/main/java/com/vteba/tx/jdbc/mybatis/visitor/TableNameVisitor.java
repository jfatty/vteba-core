package com.vteba.tx.jdbc.mybatis.visitor;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
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
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;


/**
 * 如果需要做sql某一方面的转换，实现对应的访问者模式的方法即可。
 * @author yinlei
 * @since 2013-12-17
 */
public class TableNameVisitor implements SelectVisitor, FromItemVisitor {
//    private String tableName;
    private Object params;
    private String mapperId;
    
    private Select select;
    
    private List<String> sqlList;

    public TableNameVisitor(Select select, Object params, String mapperId) {
        this.params = params;
        this.mapperId = mapperId;
//        this.tableName = tableName;
        this.select = select;
    }

	public List<String> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<String> sqlList) {
		this.sqlList = sqlList;
	}

	public void visit(PlainSelect plainSelect) {
        plainSelect.getFromItem().accept(this);
//        if (plainSelect.getJoins() != null) {
//            Iterator<Join> joinsIt = plainSelect.getJoins().iterator();
//            while (joinsIt.hasNext()) {
//                Join join = (Join) joinsIt.next();
//                join.getRightItem().accept(this);
//            }
//        }
//		FromItem fromItem = plainSelect.getFromItem();
//		
//		plainSelect.setFromItem(new Table("user_201309m"));
    }

    public void visit(Table tableName) {
    	ShardingStrategy strategy = ShardingConfigFactory.getInstance().getStrategy(tableName.getName());
    	List<String> tableList = strategy.getSelectTable(tableName.getName(), params, mapperId);
    	List<String> sqlList = new ArrayList<String>();
    	for (String table : tableList) {
    		tableName.setName(table);
    		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
    		select.accept(deParser);
    		sqlList.add(deParser.getBuffer().toString());
    	}
    	this.sqlList = sqlList;
    }

    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    public void visit(Column tableColumn) {
    }

    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        subjoin.getJoin().getRightItem().accept(this);
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {

    }

    @Override
    public void visit(ValuesList valuesList) {

    }

    @Override
    public void visit(SetOperationList setOpList) {

    }

    @Override
    public void visit(WithItem withItem) {

    }
}
