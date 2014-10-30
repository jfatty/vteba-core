package com.vteba.tx.jdbc.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;

import com.vteba.service.tenant.SchemaWeightHolder;

/**
 * 多数据源下，spring实现的一阶段提交，链式事务管理器。
 * @author yinlei
 * @since 2014-1-9
 */
public class ChainedTransactionManager implements PlatformTransactionManager, InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(ChainedTransactionManager.class);

	private List<PlatformTransactionManager> transactionManagers;
	private List<PlatformTransactionManager> reversedTransactionManagers;
	private SynchronizationManager synchronizationManager;
	private Map<String, PlatformTransactionManager> transactionManagerMap = new LinkedHashMap<String, PlatformTransactionManager>();

	public ChainedTransactionManager() {
		super();
//		transactionManagers = new ArrayList<PlatformTransactionManager>();
//		reversedTransactionManagers = new ArrayList<PlatformTransactionManager>();
		synchronizationManager = new DefaultSynchronizationManager();
	}

	public ChainedTransactionManager(
			List<PlatformTransactionManager> transactionManagers) {
		this(new DefaultSynchronizationManager(), transactionManagers);
	}

	public ChainedTransactionManager(
			SynchronizationManager synchronizationManager,
			List<PlatformTransactionManager> transactionManagers) {
		this.synchronizationManager = synchronizationManager;
		this.transactionManagers = transactionManagers;
		this.reversedTransactionManagers = reverse(transactionManagers);
	}

	@Override
	public MultiTransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
		
		// 获取当前线程绑定的事务管理器
		PlatformTransactionManager txm = transactionManagerMap.get(SchemaWeightHolder.getSchema());
		
		MultiTransactionStatus mts = new MultiTransactionStatus(txm);

		if (!synchronizationManager.isSynchronizationActive()) {
			synchronizationManager.initSynchronization();
			mts.setNewSynchonization();
		}

		// 这里将所有的数据源事务都开启
		for (PlatformTransactionManager transactionManager : transactionManagers) {
			mts.registerTransactionManager(definition, transactionManager);
		}
//		mts.registerTransactionManager(definition, txm);

		return mts;
	}

	@Override
	public void commit(TransactionStatus status) throws TransactionException {

		MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

		boolean commit = true;
		Exception commitException = null;
//		PlatformTransactionManager commitExceptionTransactionManager = null;

		for (PlatformTransactionManager transactionManager : reversedTransactionManagers) {
			if (commit) {
				try {
					multiTransactionStatus.commit(transactionManager);
				} catch (Exception ex) {
					commit = false;
					commitException = ex;
//					commitExceptionTransactionManager = transactionManager;
				}
			} else {
				// after unsucessfull commit we must try to rollback remaining
				// transaction managers
				try {
					multiTransactionStatus.rollback(transactionManager);
				} catch (Exception ex) {
					logger.warn("Rollback exception (after commit) ("
							+ transactionManager + ") " + ex.getMessage(), ex);
				}
			}
		}

		if (multiTransactionStatus.isNewSynchonization()) {
			synchronizationManager.clearSynchronization();
		}

		if (commitException != null) {
//			boolean firstTransactionManagerFailed = commitExceptionTransactionManager == getLastTransactionManager();
//			int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK
//					: HeuristicCompletionException.STATE_MIXED;
//			throw new HeuristicCompletionException(transactionState, commitException);
			throw new HeuristicCompletionException(HeuristicCompletionException.STATE_ROLLED_BACK, commitException);
		}

	}

	@Override
	public void rollback(TransactionStatus status) throws TransactionException {

		Exception rollbackException = null;
		PlatformTransactionManager rollbackExceptionTransactionManager = null;

		MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

		for (PlatformTransactionManager transactionManager : reversedTransactionManagers) {
			try {
				multiTransactionStatus.rollback(transactionManager);
			} catch (Exception ex) {
				if (rollbackException == null) {
					rollbackException = ex;
					rollbackExceptionTransactionManager = transactionManager;
				} else {
					logger.warn("Rollback exception (" + transactionManager
							+ ") " + ex.getMessage(), ex);
				}
			}
		}

		if (multiTransactionStatus.isNewSynchonization()) {
			synchronizationManager.clearSynchronization();
		}

		if (rollbackException != null) {
			throw new UnexpectedRollbackException(
					"Rollback exception, originated at ("
							+ rollbackExceptionTransactionManager + ") "
							+ rollbackException.getMessage(), rollbackException);
		}
	}

//	private PlatformTransactionManager getLastTransactionManager() {
//		return transactionManagers.get(lastTransactionManagerIndex());
//	}
//
//	private int lastTransactionManagerIndex() {
//		return transactionManagers.size() - 1;
//	}

//	public List<PlatformTransactionManager> getTransactionManagers() {
//		return transactionManagers;
//	}
//
//	public void setTransactionManagers(
//			List<PlatformTransactionManager> transactionManagers) {
//		this.transactionManagers = transactionManagers;
//	}

	public SynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
	}

	public void setSynchronizationManager(
			SynchronizationManager synchronizationManager) {
		this.synchronizationManager = synchronizationManager;
	}

	public Map<String, PlatformTransactionManager> getTransactionManagerMap() {
		return transactionManagerMap;
	}

	public void setTransactionManagerMap(
			Map<String, PlatformTransactionManager> transactionManagerMap) {
		this.transactionManagerMap = transactionManagerMap;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (transactionManagerMap == null) {
			throw new IllegalArgumentException("属性[transactionManagerMap]是必须的。");
		}
		if (synchronizationManager == null) {
			throw new IllegalArgumentException("属性[synchronizationManager]是必须的。");
		}
		transactionManagers = new ArrayList<PlatformTransactionManager>();
		for (Entry<String, PlatformTransactionManager> entry : transactionManagerMap.entrySet()) {
			transactionManagers.add(entry.getValue());
		}
		reversedTransactionManagers = reverse(transactionManagers);
	}

	private <T> List<T> reverse(Collection<T> collection) {
		List<T> list = new ArrayList<T>(collection);
		Collections.reverse(list);
		return list;
	}
}