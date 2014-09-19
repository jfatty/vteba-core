package com.vteba.tx.jdbc.mybatis.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.vteba.tx.jdbc.mybatis.cache.ShardingTableCache;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;
import com.vteba.tx.jdbc.uuid.StandardRandomStrategy;
import com.vteba.tx.matrix.info.TableInfo;
import com.vteba.utils.date.DateUtils;

/**
 * 默认的分表策略，按月份分表。
 * @author yinlei
 * @since 2013-12-16 14:56
 */
public class DefaultShardingStrategy implements ShardingStrategy {
    private static final String DELETE = "deleteById";
    private static final String UPDATE = "updateById";
    private static final String GET = "get";
    
    //private TableRuler tableRuler;
    
    @Override
    public String getTableName(String baseTableName, Object params, String mapperId) {
        return baseTableName + "_" + DateUtils.toDateString("yyyyMM") + "m";
    }
    
    /**
     * insert，直接insert当前表，这个最简单
     * @param baseTableName 原表名
     * @param params sql参数，是参数原始的对象，例如User，Account等POJO
     * @param mapperId mybatis sql映射id
     * @return 分区表名
     */
    public String getInsertTable(String baseTableName, Object params, String mapperId) {
        TableInfo tableInfo = ShardingTableCache.get(baseTableName);
        String tableName = tableInfo.getCurrentTable();
        
        return tableName;
    }
    
    public List<String> getDeleteTable(String baseTableName, Object params, String mapperId) {
        List<String> tables = new ArrayList<String>();
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(DELETE)) {
            String id = (String) params;
            String tableSuffix = id.substring(id.lastIndexOf("_"));
            tables.add(baseTableName + tableSuffix);
        } else {
            tables.add(getTableName(baseTableName, params, mapperId));
        }
        return tables;
    }
    
    public List<String> getUpdateTable(String baseTableName, Object params, String mapperId) {
        List<String> tables = new ArrayList<String>();
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(UPDATE)) {
            String id = (String) params;
            String tableSuffix = id.substring(id.lastIndexOf("_"));
            tables.add(baseTableName + tableSuffix);
        } else {
            tables.add(getTableName(baseTableName, params, mapperId));
        }
        return tables;
    }
    
    public List<String> getSelectTable(String baseTableName, Object params, String mapperId) {
        List<String> tables = new ArrayList<String>();
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(GET)) {
            String id = params.toString();
            String tableSuffix = id.substring(id.lastIndexOf("_"));
            tables.add(baseTableName + tableSuffix);
        } else {
            tables.add(getTableName(baseTableName, params, mapperId));
        }
        return tables;
    }
    
    // insert，直接insert当前表，这个最简单
    
    // update，如果是根据id，那么也可以直接定位到当前表，如果批量，可以考虑在各个表中并行执行。
    // 如果是外键更新，是否有好的方法呢？
    // 传入id参数，value（id的值），data参数，sql参数
    
    // delete，处理方式同update
    
    // select，如果是根据id查询，那么直接定位表。如果是批量查询，那么也要在各个表中并行执行，然后合并结果
    
    // 根据分表策略，定时任务创建各个表，在表没有创建完成之前，那么存到上一个表中
    
    // 对于批量处理，要添加时间范围参数，以便确定查询或者更新的表
    
    // 手工替换sql，使用模板插值
    
    // 每一个分表都会提前创建好
    
    // 每一个sql中只有一个分表
    
    // 只能有两个表做连接，且只有第一个表示分表
    
    // 如果sql解析有缓存，更新sql的时间，统一按一个服务器的时间进行协调，比如数据库
    
    public static void main(String[] aa) {
        String a = "asdf.asa";
        System.out.println(a.substring(a.lastIndexOf(".") + 1));
        
        long d = System.currentTimeMillis();
        //UUIDUtils.uuid();
        System.out.println(System.currentTimeMillis() - d);
        
        d = System.currentTimeMillis();
        UUID id = UUID.randomUUID();
        id.toString();
        System.out.println(System.currentTimeMillis() - d);
        
        d = System.currentTimeMillis();
        StandardRandomStrategy strategy = StandardRandomStrategy.INSTANCE;
        strategy.generateUUID(null).toString();
        System.out.println(System.currentTimeMillis() - d);
        
        d = System.currentTimeMillis();
        DateUtils.toDateString("yyyyMM");
        System.out.println(System.currentTimeMillis() - d);
        
        d = System.currentTimeMillis();
        DateTime dateTime = new DateTime();
        dateTime.getYear();
        dateTime.getMonthOfYear();
        System.out.println(System.currentTimeMillis() - d);
        
    }
}
