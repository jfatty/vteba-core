package com.vteba.tx.jdbc.mybatis.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存mybatis分表解析后的语句。
 * @author yinlei 
 * @since 2013-12-16
 */
public class SQLCache {
    private static final ConcurrentMap<String, String> CACHE = new ConcurrentHashMap<String, String>();
    private static SQLCache instance = new SQLCache();
    
    private SQLCache() {}
    
    public static SQLCache get() {
        return instance;
    }
    
    public ConcurrentMap<String, String> getCache() {
        return CACHE;
    }
    
    public static String get(String key) {
        return CACHE.get(key);
    }
    
    public static void put(String key, String sql) {
        CACHE.put(key, sql);
    }
    
    /**
     * 将sql键对应的sql删除。
     * @param key sql键
     */
    public static void reset(String key) {
        CACHE.remove(key);
    }
    
    /**
     * 替换sql键对应的sql值
     * @param key sql键
     * @param sql sql值
     */
    public static void replace(String key, String sql) {
        CACHE.replace(key, sql);
    }
    
    /**
     * 重新解析该sql键对应的sql
     * @param key sql键
     */
    public void reload(String key) {
//        String sql = CACHE.get(key);
//        SqlConverterFactory factory = SqlConverterFactory.getInstance();
//        String parsedSQL = null;//factory.convert(sql, null, null);
//        CACHE.put(key, parsedSQL);
    }
    
    /**
     * 重新解析该缓存中对应的所有的sql
     */
    public void reloadAll() {
//        SqlConverterFactory factory = SqlConverterFactory.getInstance();
//        for (Entry<String, String> entry : CACHE.entrySet()) {
//            String parsedSQL = null;//factory.convert(entry.getValue(), null, null);
//            CACHE.put(entry.getKey(), parsedSQL);
//        }
    }
}
