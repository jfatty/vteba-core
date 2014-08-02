package com.vteba.tx.jdbc.sequence;

import org.springframework.beans.factory.InitializingBean;

/**
 * 通过单个表模拟多个sequence，性能会低。sequence名字栏位默认seq_name，sequence值的栏位默认seq_value。
 * @author 尹雷 
 * @since 2012-12-1
 */
public class SingleTableGenerator extends AbstractKeyGenerator implements KeyGenerator, InitializingBean {
    private String seqValueColumn = "seq_value";// sequence值的栏位名
    private String seqNameColumn = "seq_name";// sequence名的栏位名
    private String sequenceName;// 模拟sequence名字
    //private String tableName;// 模拟sequence的表名
    
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        setIncrementerName(incrementerName);
        SingleMaxValueIncrementer singleMaxValueIncrementer = new SingleMaxValueIncrementer(getDataSource(), 
            incrementerName, seqValueColumn, seqNameColumn, sequenceName);
        singleMaxValueIncrementer.afterPropertiesSet();
        dataFieldIncrementer = singleMaxValueIncrementer;
    }

    public String getSeqValueColumn() {
        return seqValueColumn;
    }

    public void setSeqValueColumn(String seqValueColumn) {
        this.seqValueColumn = seqValueColumn;
    }

    public String getSeqNameColumn() {
        return seqNameColumn;
    }

    public void setSeqNameColumn(String seqNameColumn) {
        this.seqNameColumn = seqNameColumn;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

//    public String getTableName() {
//        return tableName;
//    }
//
//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }

}
