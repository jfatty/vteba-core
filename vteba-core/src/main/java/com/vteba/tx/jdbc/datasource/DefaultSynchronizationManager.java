package com.vteba.tx.jdbc.datasource;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 多数据源的事务同步管理器，主要依赖于TransactionSynchronizationManager的同步管理器来实现。
 * 因为在新的spring3+中，aop以及开启事务同步，再次开启会出错。所以需要检查。
 * @author yinlei
 * @since 2014-1-9
 */
public class DefaultSynchronizationManager implements SynchronizationManager {
	
    @Override
    public void initSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @Override
    public boolean isSynchronizationActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }

    @Override
    public void clearSynchronization() {
        TransactionSynchronizationManager.clear();
    }
}
