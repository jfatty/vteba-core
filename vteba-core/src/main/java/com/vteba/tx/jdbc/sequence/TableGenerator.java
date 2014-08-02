package com.vteba.tx.jdbc.sequence;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerMaxValueIncrementer;

import com.vteba.utils.common.Assert;

/**
 * 使用table模拟的sequence产生器。支持MySQL，SqlServer
 * @author yinlei 
 * @since 2013-12-1
 */
public class TableGenerator extends AbstractKeyGenerator implements InitializingBean {
    private String seqValueColumn = "seq_value";// 模拟sequence值的，表的栏位名
    
    public TableGenerator() {
        
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(seqValueColumn, "模拟sequence值的栏位不能为空");
        databaseType = databaseType.toLowerCase();
        AbstractColumnMaxValueIncrementer columnMaxValueIncrementer;
        if (databaseType.equals("mysql")) {
            columnMaxValueIncrementer = new MySQLMaxValueIncrementer(dataSource, incrementerName, seqValueColumn);
            columnMaxValueIncrementer.setCacheSize(getCacheSize());
            columnMaxValueIncrementer.setPaddingLength(getPaddingLength());
        } else if (databaseType.equals("sqlserver")) {
            columnMaxValueIncrementer = new  SqlServerMaxValueIncrementer(dataSource, incrementerName, seqValueColumn);
            columnMaxValueIncrementer.setCacheSize(getCacheSize());
            columnMaxValueIncrementer.setPaddingLength(getPaddingLength());
        } else {
            throw new IllegalArgumentException("不支持的数据库类型。");
        }
        columnMaxValueIncrementer.afterPropertiesSet();
        dataFieldIncrementer = columnMaxValueIncrementer;
    }

    public String getSeqValueColumn() {
        return seqValueColumn;
    }

    /**
     * 设置模拟sequence表的  sequence值的栏位名
     * @param seqValueColumn sequence值的栏位名
     */
    public void setSeqValueColumn(String seqValueColumn) {
        this.seqValueColumn = seqValueColumn;
    }
}
