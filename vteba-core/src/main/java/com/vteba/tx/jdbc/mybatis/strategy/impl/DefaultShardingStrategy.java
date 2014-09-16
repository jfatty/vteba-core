package com.vteba.tx.jdbc.mybatis.strategy.impl;

import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;
import com.vteba.utils.date.DateUtils;

/**
 * 默认的分表策略，按月份分表。
 * @author yinlei
 * @since 2013-12-16 14:56
 */
public class DefaultShardingStrategy implements ShardingStrategy {

    @Override
    public String getTargetTableName(String baseTableName, Object paramObject, String mapperId) {
        return baseTableName + "_" + DateUtils.toDateString("yyyyMM") + "m";
    }

}
