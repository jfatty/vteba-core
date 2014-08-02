package com.vteba.tx.jdbc.sequence;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer;

/**
 * sequence产生器。支持Oracle，PostgreSQL，DB2
 * 
 * @author yinlei
 * @since 2013-12-1
 */
public class SequenceGenerator extends AbstractKeyGenerator implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        databaseType = databaseType.toLowerCase();
        if (databaseType.equals("oracle")) {
            dataFieldIncrementer = new OracleSequenceMaxValueIncrementer(dataSource, incrementerName);
            dataFieldIncrementer.setPaddingLength(getPaddingLength());
        } else if (databaseType.equals("postgresql")) {
            dataFieldIncrementer = new PostgreSQLSequenceMaxValueIncrementer(dataSource, incrementerName);
            dataFieldIncrementer.setPaddingLength(getPaddingLength());
        } else if (databaseType.equals("db2")) {
            dataFieldIncrementer = new  DB2SequenceMaxValueIncrementer(dataSource, incrementerName);
            dataFieldIncrementer.setPaddingLength(getPaddingLength());
        } else {
            throw new IllegalArgumentException("不支持的数据库类型。");
        }
        dataFieldIncrementer.afterPropertiesSet();
    }
    
}
