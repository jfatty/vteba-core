package com.vteba.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * spring实现的一阶段提交，链式事务管理器。
 * 
 * @author yinlei
 * @since 2013-12-8
 */
public class ChainedTransactionManager extends
		AbstractPlatformTransactionManager {

	private static final long serialVersionUID = 5806177265950745203L;
	private List<PlatformTransactionManager> transactionManagers = new ArrayList<PlatformTransactionManager>();
	private ArrayList<PlatformTransactionManager> reversed;

	public void setTransactionManagers(
			List<PlatformTransactionManager> transactionManagers) {
		this.transactionManagers = transactionManagers;
		reversed = new ArrayList<PlatformTransactionManager>(transactionManagers);
		Collections.reverse(reversed);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition)
			throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) transaction;
		for (PlatformTransactionManager transactionManager : transactionManagers) {
			DefaultTransactionStatus element = (DefaultTransactionStatus) transactionManager
					.getTransaction(definition);
			list.add(0, element);
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status)
			throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status.getTransaction();
		int i = 0;
		for (PlatformTransactionManager transactionManager : reversed) {
			TransactionStatus local = list.get(i++);
			try {
				transactionManager.commit(local);
			} catch (TransactionException e) {
				logger.error("Error in commit", e);
				// Rollback will ensue as long as rollbackOnCommitFailure=true
				throw e;
			}
		}
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return new ArrayList<DefaultTransactionStatus>();
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status)
			throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status.getTransaction();
		int i = 0;
		TransactionException lastException = null;
		for (PlatformTransactionManager transactionManager : reversed) {
			TransactionStatus local = list.get(i++);
			try {
				transactionManager.rollback(local);
			} catch (TransactionException e) {
				// Log exception and try to complete rollback
				lastException = e;
				logger.error("Error in rollback", e);
			}
		}
		if (lastException != null) {
			throw lastException;
		}
	}

}
