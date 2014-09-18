package com.vteba.tx.jdbc.sequence;

import com.vteba.utils.common.UUIDUtils;
import com.vteba.utils.date.DateUtils;

/**
 * 基于uuid的主键生成器
 * @author yinlei
 * @since 2013-12-17
 */
public class UuidKeyGenerator implements KeyGenerator {

    @Override
    public String next(String prefix) {
        return prefix + UUIDUtils.uuid();
    }

    @Override
    public String next() {
        return UUIDUtils.uuid() + "_" + DateUtils.toDateString("yyyyMM") + "m";
    }

    @Override
    public int nextInt() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long nextLong() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long nextValue() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String nextKey(String suffix) {
        return UUIDUtils.uuid() + suffix;
    }

}
