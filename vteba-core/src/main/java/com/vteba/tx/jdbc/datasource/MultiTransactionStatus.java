package com.vteba.tx.jdbc.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * 多数据源的一阶段提交的事务管理，实现spring的TransactionStatus
 * @author yinlei
 * @since 2013-12-9
 */
public class MultiTransactionStatus implements TransactionStatus {

	private PlatformTransactionManager transactionManager;

	private ConcurrentMap<PlatformTransactionManager, TransactionStatus> transactionStatusMap = new ConcurrentHashMap<PlatformTransactionManager, TransactionStatus>();

	private boolean newSynchonization;

	public MultiTransactionStatus(
			PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	private Map<PlatformTransactionManager, TransactionStatus> getTransactionStatusMap() {
		return transactionStatusMap;
	}

	private TransactionStatus getTransactionStatus() {
		return transactionStatusMap.get(transactionManager);
	}

	public void setNewSynchonization() {
		this.newSynchonization = true;
	}

	public boolean isNewSynchonization() {
		return newSynchonization;
	}

	@Override
	public boolean isNewTransaction() {
		return getTransactionStatus().isNewTransaction();
	}

	@Override
	public boolean hasSavepoint() {
		return getTransactionStatus().hasSavepoint();
	}

	@Override
	public void setRollbackOnly() {
		for (TransactionStatus ts : transactionStatusMap.values()) {
			ts.setRollbackOnly();
		}
	}

	@Override
	public boolean isRollbackOnly() {
		return getTransactionStatus().isRollbackOnly();
	}

	@Override
	public boolean isCompleted() {
		return getTransactionStatus().isCompleted();
	}

	private static class SavePoints {
		Map<TransactionStatus, Object> savepoints = new HashMap<TransactionStatus, Object>();

		private void addSavePoint(TransactionStatus status, Object savepoint) {
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

	@Override
	public Object createSavepoint() throws TransactionException {
		SavePoints savePoints = new SavePoints();

		for (TransactionStatus transactionStatus : transactionStatusMap.values()) {
			savePoints.save(transactionStatus);
		}
		return savePoints;
	}

	@Override
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		SavePoints savePoints = (SavePoints) savepoint;
		savePoints.rollback();
	}

	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		((SavePoints) savepoint).release();
	}

	public void registerTransactionManager(TransactionDefinition definition,
			PlatformTransactionManager transactionManager) {
		getTransactionStatusMap().put(transactionManager, transactionManager.getTransaction(definition));
	}

	public void commit(PlatformTransactionManager transactionManager) {
		TransactionStatus transactionStatus = getTransactionStatus(transactionManager);
		transactionManager.commit(transactionStatus);
	}

	private TransactionStatus getTransactionStatus(PlatformTransactionManager transactionManager) {
		return this.getTransactionStatusMap().get(transactionManager);
	}

	public void rollback(PlatformTransactionManager transactionManager) {
		transactionManager.rollback(getTransactionStatus(transactionManager));
	}

	@Override
	public void flush() {
		for (TransactionStatus transactionStatus : transactionStatusMap.values()) {
			transactionStatus.flush();
		}
	}
}