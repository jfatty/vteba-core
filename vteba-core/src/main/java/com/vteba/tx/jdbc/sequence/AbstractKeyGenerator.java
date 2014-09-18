package com.vteba.tx.jdbc.sequence;

import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;

import com.vteba.utils.common.Assert;

/**
 * 主键生成器的抽象基类
 * @author 尹雷 
 * @since 2013-12-1
 */
public class AbstractKeyGenerator implements KeyGenerator,NamedKeyGenerator, InitializingBean {
    private static final Random RANDOM = new Random();
    protected String databaseType;// 数据库类型
    protected DataSource dataSource;// 数据源
    protected String incrementerName;// sequence名或者 table名
    private int cacheSize = 1;// 主键缓存size
    /** 左侧添加0的长度，0表示不添加 */
    private int paddingLength = 0;
    
    protected AbstractDataFieldMaxValueIncrementer dataFieldIncrementer;
    
    @Override
    public String next(String prefix) {
        String currval = dataFieldIncrementer.nextStringValue();
        return prefix + currval;
    }
    
    @Override
    public String next() {
        return dataFieldIncrementer.nextStringValue();
    }
    
    @Override
    public String nextKey(String suffix) {
        return dataFieldIncrementer.nextStringValue() + suffix;
    }
    
    @Override
    public int nextInt() {
        return dataFieldIncrementer.nextIntValue();
    }
    
    @Override
    public long nextLong() {
        return dataFieldIncrementer.nextLongValue();
    }
    
    @Override
    public String nextString(String seqName) {
        dataFieldIncrementer.setIncrementerName(seqName);
        return dataFieldIncrementer.nextStringValue();
    }
    
    @Override
    public int nextInt(String seqName) {
        dataFieldIncrementer.setIncrementerName(seqName);
        return dataFieldIncrementer.nextIntValue();
    }
    
    @Override
    public long nextLong(String seqName) {
        dataFieldIncrementer.setIncrementerName(seqName);
        return dataFieldIncrementer.nextLongValue();
    }

    @Override
    public long nextValue() {
        return System.nanoTime() + RANDOM.nextInt();
    }

    /**
     * @param databaseType 数据库类型，如oracle，postgresql
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    
    /**
     * @param dataSource 要查询的数据源
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param incrementerName sequence名字
     */
    public void setIncrementerName(String incrementerName) {
        this.incrementerName = incrementerName;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getIncrementerName() {
        return incrementerName;
    }
    
    public int getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getPaddingLength() {
        return paddingLength;
    }
    
    public void setPaddingLength(int paddingLength) {
        this.paddingLength = paddingLength;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dataSource, "数据源不能为空。");
        Assert.notNull(incrementerName, "模拟sequence的表名不能为空");
        Assert.notNull(databaseType, "数据库类型不能为空");
        
    }
}
