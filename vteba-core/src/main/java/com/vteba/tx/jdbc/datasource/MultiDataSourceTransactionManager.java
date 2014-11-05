package com.vteba.tx.jdbc.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.util.Assert;

/**
 * {@link PlatformTransactionManager} implementation that orchestrates
 * transaction creation, commits and rollbacks to a list of delegates. Using
 * this implementation assumes that errors causing a transaction rollback will
 * usually happen before the transaction completion or during the commit of the
 * most inner {@link PlatformTransactionManager}.
 * <p />
 * The configured instances will start transactions in the order given and
 * commit/rollback in <em>reverse</em> order, which means the
 * {@link PlatformTransactionManager} most likely to break the transaction
 * should be the <em>last</em> in the list configured. A
 * {@link PlatformTransactionManager} throwing an exception during commit will
 * automatically cause the remaining transaction managers to roll back instead
 * of committing.
 * 
 * @author Michael Hunger
 * @author Oliver Gierke
 * @since 1.6
 */
public class MultiDataSourceTransactionManager implements PlatformTransactionManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChainedTransactionManager.class);

	private List<PlatformTransactionManager> transactionManagers;
	private List<PlatformTransactionManager> reverseTxManagers;
	private final SynchronizationManager synchronizationManager;
	
	public MultiDataSourceTransactionManager() {
		synchronizationManager = DefaultSynchronizationManager.INSTANCE;
	}

	/**
	 * Creates a new {@link ChainedTransactionManager} delegating to the given
	 * {@link PlatformTransactionManager}s.
	 * 
	 * @param transactionManagers
	 *            must not be {@literal null} or empty.
	 */
	public MultiDataSourceTransactionManager(
			PlatformTransactionManager... transactionManagers) {
		this(DefaultSynchronizationManager.INSTANCE, transactionManagers);
	}

	/**
	 * Creates a new {@link ChainedTransactionManager} using the given
	 * {@link SynchronizationManager} and {@link PlatformTransactionManager}s.
	 * 
	 * @param synchronizationManager
	 *            must not be {@literal null}.
	 * @param transactionManagers
	 *            must not be {@literal null} or empty.
	 */
	public MultiDataSourceTransactionManager(
			SynchronizationManager synchronizationManager,
			PlatformTransactionManager... transactionManagers) {

		Assert.notNull(synchronizationManager, "SynchronizationManager must not be null!");
		Assert.notNull(transactionManagers, "Transaction managers must not be null!");
		Assert.isTrue(transactionManagers.length > 0, "At least one PlatformTransactionManager must be given!");

		this.synchronizationManager = synchronizationManager;
		this.transactionManagers = new CopyOnWriteArrayList<PlatformTransactionManager>(transactionManagers);
		
		List<PlatformTransactionManager> txs = new CopyOnWriteArrayList<PlatformTransactionManager>(transactionManagers);
		Collections.reverse(txs);
		this.reverseTxManagers = txs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.transaction.PlatformTransactionManager#getTransaction
	 * (org.springframework.transaction.TransactionDefinition)
	 */
	public MultiTransactionStatus getTransaction(
			TransactionDefinition definition) throws TransactionException {

		MultiTransactionStatus mts = new MultiTransactionStatus(transactionManagers.get(0));

		if (!synchronizationManager.isSynchronizationActive()) {
			synchronizationManager.initSynchronization();
			mts.setNewSynchonization();
		}

		try {
			for (PlatformTransactionManager transactionManager : transactionManagers) {
				mts.registerTransactionManager(definition, transactionManager);
			}
		} catch (Exception ex) {
			Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = mts.getTransactionStatuses();

			for (PlatformTransactionManager transactionManager : transactionManagers) {
				try {
					if (transactionStatuses.get(transactionManager) != null) {
						transactionManager.rollback(transactionStatuses.get(transactionManager));
					}
				} catch (Exception ex2) {
					LOGGER.warn("Rollback exception (" + transactionManager + ") " + ex2.getMessage(), ex2);
				}
			}

			if (mts.isNewSynchonization()) {
				synchronizationManager.clearSynchronization();
			}

			throw new CannotCreateTransactionException(ex.getMessage(), ex);
		}

		return mts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.transaction.PlatformTransactionManager#commit(org
	 * .springframework.transaction.TransactionStatus)
	 */
	public void commit(TransactionStatus status) throws TransactionException {

		MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

		boolean commit = true;
		Exception commitException = null;
		PlatformTransactionManager commitExceptionTransactionManager = null;

		for (PlatformTransactionManager transactionManager : reverseTxManagers) {
			if (commit) {
				try {
					multiTransactionStatus.commit(transactionManager);
				} catch (Exception ex) {
					commit = false;
					commitException = ex;
					commitExceptionTransactionManager = transactionManager;
				}

			} else {
				// after unsucessfull commit we must try to rollback remaining
				// transaction managers
				try {
					multiTransactionStatus.rollback(transactionManager);
				} catch (Exception ex) {
					LOGGER.warn("Rollback exception (after commit) ("
							+ transactionManager + ") " + ex.getMessage(), ex);
				}
			}
		}

		if (multiTransactionStatus.isNewSynchonization()) {
			synchronizationManager.clearSynchronization();
		}

		if (commitException != null) {
			boolean firstTransactionManagerFailed = commitExceptionTransactionManager == getLastTransactionManager();
			int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK
					: HeuristicCompletionException.STATE_MIXED;
			throw new HeuristicCompletionException(transactionState,
					commitException);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.transaction.PlatformTransactionManager#rollback(org
	 * .springframework.transaction.TransactionStatus)
	 */
	public void rollback(TransactionStatus status) throws TransactionException {

		Exception rollbackException = null;
		PlatformTransactionManager rollbackExceptionTransactionManager = null;

		MultiTransactionStatus multiTransactionStatus = (MultiTransactionStatus) status;

		for (PlatformTransactionManager transactionManager : reverseTxManagers) {
			try {
				multiTransactionStatus.rollback(transactionManager);
			} catch (Exception ex) {
				if (rollbackException == null) {
					rollbackException = ex;
					rollbackExceptionTransactionManager = transactionManager;
				} else {
					LOGGER.warn("Rollback exception (" + transactionManager
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

	protected <T> Iterable<T> reverse(Collection<T> collection) {
		List<T> list = new ArrayList<T>(collection);
		Collections.reverse(list);
		return list;
	}

	private PlatformTransactionManager getLastTransactionManager() {
		return transactionManagers.get(lastTransactionManagerIndex());
	}

	private int lastTransactionManagerIndex() {
		return transactionManagers.size() - 1;
	}

	public List<PlatformTransactionManager> getTransactionManagers() {
		return transactionManagers;
	}

	public void setTransactionManagers(
			List<PlatformTransactionManager> transactionManagers) {
		this.transactionManagers = new CopyOnWriteArrayList<PlatformTransactionManager>(transactionManagers);
		
		Collections.reverse(transactionManagers);
		List<PlatformTransactionManager> txs = new CopyOnWriteArrayList<PlatformTransactionManager>(this.transactionManagers);
		this.reverseTxManagers = txs;
	}
	
	public void addTransactionManager(PlatformTransactionManager manager) {
		this.transactionManagers.add(manager);
		this.reverseTxManagers.add(0, manager);
	}
}
