package com.vteba.tx.jdbc.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;

import com.vteba.utils.common.Assert;

/**
 * 单张表模拟多个sequence。
 * @author 尹雷 
 * @since 2013-12-1
 */
public class SingleMaxValueIncrementer extends AbstractColumnMaxValueIncrementer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleMaxValueIncrementer.class);
    /** The next id to serve */
    private long nextId = 0;

    /** The max id to serve */
    private long maxId = 0;

    //private String seqValueColumn;// sequence值的栏位名
    private String seqNameColumn;// sequence名的栏位名
    private String sequenceName;// 模拟sequence名字
    //private String tableName;// 模拟sequence的表名
    
    private String update;
    private String select;

    /**
     * Default constructor for bean property style usage.
     * @see #setDataSource
     * @see #setIncrementerName
     * @see #setColumnName
     */
    public SingleMaxValueIncrementer() {
    }

    /**
     * 构造一个单表模拟的sequence生成器。
     * @param dataSource 数据源
     * @param tableName 模拟sequence的表名
     * @param seqValueColumn sequence值的栏位名
     * @param seqNameColumn sequence名的栏位名
     * @param sequenceName 模拟的sequence名字，也就是seqNameColumn的值
     */
    public SingleMaxValueIncrementer(DataSource dataSource, String tableName, String seqValueColumn, String seqNameColumn, String sequenceName) {
        super(dataSource, tableName, seqValueColumn);
        this.seqNameColumn = seqNameColumn;
        this.sequenceName = sequenceName;
    }


    @Override
    protected synchronized long getNextKey() throws DataAccessException {
        if (this.maxId == this.nextId) {
            /*
            * Need to use straight JDBC code because we need to make sure that the insert and select
            * are performed on the same connection (otherwise we can't be sure that last_insert_id()
            * returned the correct value)
            */
            Connection con = DataSourceUtils.getConnection(getDataSource());
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(update);
                stmt.setString(1, sequenceName);
                DataSourceUtils.applyTransactionTimeout(stmt, getDataSource());
                // Increment the sequence column...
                stmt.executeUpdate();
                // Retrieve the new max of the sequence column...
                stmt = con.prepareStatement(select);
                stmt.setString(1, sequenceName);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) {
                        throw new DataAccessResourceFailureException("获取sequence failed after executing an update");
                    }
                    this.maxId = rs.getLong(1);
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
                this.nextId = this.maxId - getCacheSize() + 1;
            } catch (SQLException ex) {
                throw new DataAccessResourceFailureException("获取table模拟的sequence出错。", ex);
            } finally {
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(con, getDataSource());
            }
        } else {
            this.nextId++;
        }
        return this.nextId;
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        StringBuilder sb = new StringBuilder();
        Assert.notNull(seqNameColumn, "模拟sequence名的栏位[seqNameColumn]不能为空。");
        if (update == null) {// 如果
            sb.append("update ").append(getIncrementerName()).append(" set ").append(getColumnName())
                .append(" = ").append(getColumnName()).append(" + ").append(getCacheSize())
                .append(" where ").append(seqNameColumn).append(" = ?").toString();

            update = sb.toString();
            LOGGER.info("更新sequence的update语句是[{}]", update);
        }
        if (select == null) {
            sb = new StringBuilder();
            sb.append("select ").append(getColumnName()).append(" from ").append(getIncrementerName())
                .append(" where ").append(seqNameColumn).append(" = ?");
            
            select = sb.toString();
            LOGGER.info("获取sequence的select语句是[{}]", select);
        }
    }
    
    /**
     * 重写父类方法。原来这个是设置sequence名字或者表名字，
     * 这里改为设置表模拟的sequence名。
     */
    @Override
    public void setIncrementerName(String incrementerName) {
        setSequenceName(incrementerName);
    }
    
//    public String getTableName() {
//        return tableName;
//    }
//    
//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }

//    public String getSeqValueColumn() {
//        return seqValueColumn;
//    }
//    
//    public void setSeqValueColumn(String seqValueColumn) {
//        this.seqValueColumn = seqValueColumn;
//    }
    
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

    public String getUpdate() {
        return update;
    }
    
    public void setUpdate(String update) {
        this.update = update;
    }
    
    public String getSelect() {
        return select;
    }
    
    public void setSelect(String select) {
        this.select = select;
    }
    
}
