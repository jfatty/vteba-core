package com.vteba.tx.jdbc.datasource;

/**
 * 多数据源的事务同步管理器，接口规范。后期可能会扩展。
 * @author yinlei
 * @since 2014-1-9
 */
public interface SynchronizationManager {
	
    void initSynchronization();

    boolean isSynchronizationActive();

    void clearSynchronization();
}
