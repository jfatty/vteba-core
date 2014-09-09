package com.vteba.tx.jdbc.mybatis.converter;

import java.util.Iterator;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

public class SelectSqlConverter extends AbstractSqlConverter {

    protected Statement doConvert(Statement statement, Object params, String mapperId) {
        if (!(statement instanceof Select)) {
            throw new IllegalArgumentException("The argument statement must is instance of Select.");
        }
        TableNameModifier modifier = new TableNameModifier(params, mapperId);
        ((Select) statement).getSelectBody().accept(modifier);
        return statement;
    }

    private class TableNameModifier implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor {

        private Object params;
        private String mapperId;

        TableNameModifier(Object params, String mapperId) {
            this.params = params;
            this.mapperId = mapperId;
        }

        public void visit(PlainSelect plainSelect) {
            plainSelect.getFromItem().accept(this);
            if (plainSelect.getJoins() != null) {
                Iterator<Join> joinsIt = plainSelect.getJoins().iterator();
                while (joinsIt.hasNext()) {
                    Join join = (Join) joinsIt.next();
                    join.getRightItem().accept(this);
                }
            }
            if (plainSelect.getWhere() != null) {
                plainSelect.getWhere().accept(this);
            }
        }

        // public void visit(Union union)
        // {
        // Iterator iter = union.getPlainSelects().iterator();
        // while (iter.hasNext())
        // {
        // PlainSelect plainSelect = (PlainSelect)iter.next();
        // visit(plainSelect);
        // }
        // }

        public void visit(Table tableName) {
            String table = tableName.getName();
            table = SelectSqlConverter.this.convertTableName(table, this.params, this.mapperId);

            tableName.setName(table);
        }

        public void visit(SubSelect subSelect) {
            subSelect.getSelectBody().accept(this);
        }

        public void visit(Addition addition) {
            visitBinaryExpression(addition);
        }

        public void visit(AndExpression andExpression) {
            visitBinaryExpression(andExpression);
        }

        public void visit(Between between) {
            between.getLeftExpression().accept(this);
            between.getBetweenExpressionStart().accept(this);
            between.getBetweenExpressionEnd().accept(this);
        }

        public void visit(Column tableColumn) {
        }

        public void visit(Division division) {
            visitBinaryExpression(division);
        }

        public void visit(DoubleValue doubleValue) {
        }

        public void visit(EqualsTo equalsTo) {
            visitBinaryExpression(equalsTo);
        }

        public void visit(Function function) {
        }

        public void visit(GreaterThan greaterThan) {
            visitBinaryExpression(greaterThan);
        }

        public void visit(GreaterThanEquals greaterThanEquals) {
            visitBinaryExpression(greaterThanEquals);
        }

        public void visit(InExpression inExpression) {
            inExpression.getLeftExpression().accept(this);
            inExpression.getLeftItemsList().accept(this);
            inExpression.getRightItemsList().accept(this);
            // inExpression.getItemsList().accept(this);
        }

        public void visit(IsNullExpression isNullExpression) {
        }

        public void visit(JdbcParameter jdbcParameter) {
        }

        public void visit(LikeExpression likeExpression) {
            visitBinaryExpression(likeExpression);
        }

        public void visit(ExistsExpression existsExpression) {
            existsExpression.getRightExpression().accept(this);
        }

        public void visit(LongValue longValue) {
        }

        public void visit(MinorThan minorThan) {
            visitBinaryExpression(minorThan);
        }

        public void visit(MinorThanEquals minorThanEquals) {
            visitBinaryExpression(minorThanEquals);
        }

        public void visit(Multiplication multiplication) {
            visitBinaryExpression(multiplication);
        }

        public void visit(NotEqualsTo notEqualsTo) {
            visitBinaryExpression(notEqualsTo);
        }

        public void visit(NullValue nullValue) {
        }

        public void visit(OrExpression orExpression) {
            visitBinaryExpression(orExpression);
        }

        public void visit(Parenthesis parenthesis) {
            parenthesis.getExpression().accept(this);
        }

        public void visit(Subtraction subtraction) {
            visitBinaryExpression(subtraction);
        }

        public void visitBinaryExpression(BinaryExpression binaryExpression) {
            binaryExpression.getLeftExpression().accept(this);
            binaryExpression.getRightExpression().accept(this);
        }

        public void visit(ExpressionList expressionList) {
            Iterator<Expression> iter = expressionList.getExpressions().iterator();
            while (iter.hasNext()) {
                Expression expression = (Expression) iter.next();
                expression.accept(this);
            }
        }

        public void visit(DateValue dateValue) {
        }

        public void visit(TimestampValue timestampValue) {
        }

        public void visit(TimeValue timeValue) {
        }

        public void visit(CaseExpression caseExpression) {
        }

        public void visit(WhenClause whenClause) {
        }

        public void visit(AllComparisonExpression allComparisonExpression) {
            allComparisonExpression.getSubSelect().getSelectBody().accept(this);
        }

        public void visit(AnyComparisonExpression anyComparisonExpression) {
            anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
        }

        public void visit(SubJoin subjoin) {
            subjoin.getLeft().accept(this);
            subjoin.getJoin().getRightItem().accept(this);
        }

        public void visit(Concat concat) {
            visitBinaryExpression(concat);
        }

        public void visit(Matches matches) {
            visitBinaryExpression(matches);
        }

        public void visit(BitwiseAnd bitwiseAnd) {
            visitBinaryExpression(bitwiseAnd);
        }

        public void visit(BitwiseOr bitwiseOr) {
            visitBinaryExpression(bitwiseOr);
        }

        public void visit(BitwiseXor bitwiseXor) {
            visitBinaryExpression(bitwiseXor);
        }

        public void visit(StringValue stringValue) {
        }

        @Override
        public void visit(MultiExpressionList multiExprList) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(SignedExpression signedExpression) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(JdbcNamedParameter jdbcNamedParameter) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(CastExpression cast) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(Modulo modulo) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(AnalyticExpression aexpr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(ExtractExpression eexpr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(IntervalExpression iexpr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(OracleHierarchicalExpression oexpr) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(RegExpMatchOperator rexpr) {
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
        public void visit(SetOperationList setOpList) {
            // TODO Auto-generated method stub

        }

        @Override
        public void visit(WithItem withItem) {
            // TODO Auto-generated method stub

        }
    }
}
