package com.vteba.tx.jdbc.mybatis.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;

/**
 * 分片配置工厂，持有配置的信息。
 * @author yinlei 
 * @since 2013-12-17 12:45
 */
public class ShardingConfigFactory {

    private static final ShardingConfigFactory instance = new ShardingConfigFactory();

    public static ShardingConfigFactory getInstance() {
        return instance;
    }

    private Map<String, ShardingStrategy> strategyRegister = new HashMap<String, ShardingStrategy>();
    private Set<String>                   ignoreSet;
    private Set<String>                   parseSet;

    public void register(String table, ShardingStrategy strategy) {
        this.strategyRegister.put(table.toLowerCase(), strategy);
    }

    public ShardingStrategy getStrategy(String table) {
        return this.strategyRegister.get(table.toLowerCase());
    }

    public synchronized void addIgnoreId(String id) {
        if (this.ignoreSet == null) {
            this.ignoreSet = new HashSet<String>();
        }
        this.ignoreSet.add(id);
    }

    public synchronized void addParseId(String id) {
        if (this.parseSet == null) {
            this.parseSet = new HashSet<String>();
        }
        this.parseSet.add(id);
    }

    public boolean isConfigParseId() {
        return this.parseSet != null;
    }

    public boolean isParseId(String id) {
        return this.parseSet != null && this.parseSet.contains(id);
    }

    public boolean isIgnoreId(String id) {
        return this.ignoreSet != null && this.ignoreSet.contains(id);
    }
}
