package com.vteba.tx.jdbc.mybatis.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.vteba.tx.matrix.info.TableInfo;

/**
 * 维护分区表缓存。这个应该在系统启动的时候维护缓存中的信息。
 * @author yinlei 
 * @since 2013-12-17
 */
public class ShardingTableCache {
    private static final ConcurrentMap<String, TableInfo> shardingTableCache = new ConcurrentHashMap<String, TableInfo>();
    private static ShardingTableCache instance = new ShardingTableCache();
    
    private ShardingTableCache() {}
    
    public static ShardingTableCache get() {
        return instance;
    }
    
    public ConcurrentMap<String, TableInfo> getCache() {
        return shardingTableCache;
    }
    
    public static TableInfo get(String tableName) {
        return shardingTableCache.get(tableName);
    }
    
    public static void put(String tableName, TableInfo tableInfo) {
        shardingTableCache.put(tableName, tableInfo);
    }
}