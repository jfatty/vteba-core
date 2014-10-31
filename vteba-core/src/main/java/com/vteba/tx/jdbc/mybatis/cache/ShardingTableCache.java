package com.vteba.tx.jdbc.mybatis.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Named;

import org.springframework.beans.factory.InitializingBean;

import com.vteba.tx.matrix.info.ShardsTables;

/**
 * 维护分区表缓存。这个应该在系统启动的时候维护缓存中的信息。
 * @author yinlei 
 * @since 2013-12-17
 */
@Named
public class ShardingTableCache implements InitializingBean {
    private static final ConcurrentMap<String, ShardsTables> shardingTableCache = new ConcurrentHashMap<String, ShardsTables>();
    
    public ShardingTableCache() {}
    
    public ConcurrentMap<String, ShardsTables> getCache() {
        return shardingTableCache;
    }
    
    public static ShardsTables get(String tableName) {
        return shardingTableCache.get(tableName);
    }
    
    public static void put(String tableName, ShardsTables tableInfo) {
        shardingTableCache.put(tableName, tableInfo);
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
