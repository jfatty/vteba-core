package com.vteba.tx.jdbc.mybatis.converter;

import net.sf.jsqlparser.statement.Statement;

public interface SqlConverter {
    public String convert(Statement statement, Object params, String mapperId);
}
