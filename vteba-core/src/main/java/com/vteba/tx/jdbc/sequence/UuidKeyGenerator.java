package com.vteba.tx.jdbc.sequence;

import java.math.BigInteger;
import java.util.Random;

import com.vteba.utils.common.UUIDUtils;
import com.vteba.utils.date.DateUtils;

/**
 * 基于uuid的主键生成器
 * @author yinlei
 * @since 2013-12-17
 */
public class UuidKeyGenerator implements KeyGenerator {
	private static final Random RANDOM = new Random();
	
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
        return new BigInteger(165, RANDOM).intValue();
    }

    @Override
    public long nextLong() {
        return System.nanoTime();
    }

    @Override
    public long nextValue() {
        return System.nanoTime();
    }

    @Override
    public String nextKey(String suffix) {
        return UUIDUtils.uuid() + suffix;
    }

}
