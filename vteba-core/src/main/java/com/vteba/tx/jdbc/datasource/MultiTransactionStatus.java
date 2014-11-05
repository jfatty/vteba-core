package com.vteba.tx.jdbc.datasource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

/**
 * 多数据源的一阶段提交的事务管理，实现spring的TransactionStatus
 * @author yinlei
 * @since 2013-12-9
 */
public class MultiTransactionStatus implements TransactionStatus {

	private final PlatformTransactionManager mainTransactionManager;
	private final Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = Collections
			.synchronizedMap(new HashMap<PlatformTransactionManager, TransactionStatus>());

	private boolean newSynchonization;

	/**
	 * Creates a new {@link MultiTransactionStatus} for the given {@link PlatformTransactionManager}.
	 * 
	 * @param mainTransactionManager must not be {@literal null}.
	 */
	public MultiTransactionStatus(PlatformTransactionManager mainTransactionManager) {

		Assert.notNull(mainTransactionManager, "TransactionManager must not be null!");
		this.mainTransactionManager = mainTransactionManager;
	}

	public Map<PlatformTransactionManager, TransactionStatus> getTransactionStatuses() {
		return transactionStatuses;
	}

	public void setNewSynchonization() {
		this.newSynchonization = true;
	}

	public boolean isNewSynchonization() {
		return newSynchonization;
	}

	public void registerTransactionManager(TransactionDefinition definition, PlatformTransactionManager transactionManager) {
		getTransactionStatuses().put(transactionManager, transactionManager.getTransaction(definition));
	}

	public void commit(PlatformTransactionManager transactionManager) {
		TransactionStatus transactionStatus = getTransactionStatus(transactionManager);
		// 因为会动态添加事务管理器，添加时可能已经开启了事务，提交时，新添加的事务管理是没有事务状态的。
		if (transactionStatus != null) {
			transactionManager.commit(transactionStatus);
		}
	}

	/**
	 * Rolls back the {@link TransactionStatus} registered for the given {@link PlatformTransactionManager}.
	 * 
	 * @param transactionManager must not be {@literal null}.
	 */
	public void rollback(PlatformTransactionManager transactionManager) {
		transactionManager.rollback(getTransactionStatus(transactionManager));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#isRollbackOnly()
	 */
	public boolean isRollbackOnly() {
		return getMainTransactionStatus().isRollbackOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#isCompleted()
	 */
	public boolean isCompleted() {
		return getMainTransactionStatus().isCompleted();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#isNewTransaction()
	 */
	public boolean isNewTransaction() {
		return getMainTransactionStatus().isNewTransaction();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#hasSavepoint()
	 */
	public boolean hasSavepoint() {
		return getMainTransactionStatus().hasSavepoint();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#setRollbackOnly()
	 */
	public void setRollbackOnly() {
		for (TransactionStatus ts : transactionStatuses.values()) {
			ts.setRollbackOnly();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.SavepointManager#createSavepoint()
	 */
	public Object createSavepoint() throws TransactionException {

		SavePoints savePoints = new SavePoints();

		for (TransactionStatus transactionStatus : transactionStatuses.values()) {
			savePoints.save(transactionStatus);
		}
		return savePoints;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.SavepointManager#rollbackToSavepoint(java.lang.Object)
	 */
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		SavePoints savePoints = (SavePoints) savepoint;
		savePoints.rollback();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.SavepointManager#releaseSavepoint(java.lang.Object)
	 */
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		((SavePoints) savepoint).release();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.transaction.TransactionStatus#flush()
	 */
	public void flush() {
		for (TransactionStatus transactionStatus : transactionStatuses.values()) {
			transactionStatus.flush();
		}
	}

	private TransactionStatus getMainTransactionStatus() {
		return transactionStatuses.get(mainTransactionManager);
	}

	private TransactionStatus getTransactionStatus(PlatformTransactionManager transactionManager) {
		return this.getTransactionStatuses().get(transactionManager);
	}

	private static class SavePoints {

		private final Map<TransactionStatus, Object> savepoints = new HashMap<TransactionStatus, Object>();

		private void addSavePoint(TransactionStatus status, Object savepoint) {

			Assert.notNull(status, "TransactionStatus must not be null!");
			this.savepoints.put(status, savepoint);
		}

		private void save(TransactionStatus transactionStatus) {
			Object savepoint = transactionStatus.createSavepoint();
			addSavePoint(transactionStatus, savepoint);
		}

		public void rollback() {
			for (TransactionStatus transactionStatus : savepoints.keySet()) {
				transactionStatus.rollbackToSavepoint(savepointFor(transactionStatus));
			}
		}

		private Object savepointFor(TransactionStatus transactionStatus) {
			return savepoints.get(transactionStatus);
		}

		public void release() {
			for (TransactionStatus transactionStatus : savepoints.keySet()) {
				transactionStatus.releaseSavepoint(savepointFor(transactionStatus));
			}
		}
	}
}