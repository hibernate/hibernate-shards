/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.hibernate.shards.transaction;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.shards.Shard;
import org.hibernate.shards.ShardedTransaction;
import org.hibernate.shards.engine.ShardedSessionImplementor;
import org.hibernate.shards.session.OpenSessionEvent;
import org.hibernate.shards.session.SetupTransactionOpenSessionEvent;
import org.hibernate.shards.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tomislav Nad
 */
public class ShardedTransactionImpl implements ShardedTransaction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<Transaction> transactions;

    private boolean begun;
    private boolean rolledBack;
    private boolean committed;
    private boolean commitFailed;
    private List<Synchronization> synchronizations;
    private boolean timeoutSet;
    private int timeout;

    public ShardedTransactionImpl(final ShardedSessionImplementor ssi) {
        final OpenSessionEvent osEvent = new SetupTransactionOpenSessionEvent(this);
        transactions = Collections.synchronizedList(new ArrayList<Transaction>());
        for (final Shard shard : ssi.getShards()) {
            if (shard.getSession() != null) {
                transactions.add(shard.getSession().getTransaction());
            } else {
                shard.addOpenSessionEvent(osEvent);
            }
        }
    }

    @Override
    public void setupTransaction(final Session session) {
        log.debug("Setting up transaction");
        transactions.add(session.getTransaction());
        if (begun) {
            session.beginTransaction();
        }
        if (timeoutSet) {
            session.getTransaction().setTimeout(timeout);
        }
    }

    @Override
    public void begin() throws HibernateException {
        if (begun) {
            return;
        }
        if (commitFailed) {
            throw new TransactionException("cannot re-start transaction after failed commit");
        }
        boolean beginException = false;
        for (Transaction t : transactions) {
            try {
                t.begin();
            } catch (HibernateException he) {
                log.warn("exception starting underlying transaction", he);
                beginException = true;
            }
        }
        if (beginException) {
            for (Transaction t : transactions) {
                if (t.isActive()) {
                    try {
                        t.rollback();
                    } catch (HibernateException he) {
                        // TODO(maxr) What do we do?
                    }

                }
            }
            throw new TransactionException("Begin failed");
        }
        begun = true;
        committed = false;
        rolledBack = false;
    }

    @Override
    public void commit() throws HibernateException {
        if (!begun) {
            throw new TransactionException("Transaction not succesfully started");
        }
        log.debug("Starting transaction commit");
        beforeTransactionCompletion();
        boolean commitException = false;
        HibernateException firstCommitException = null;
        for (Transaction t : transactions) {
            try {
                t.commit();
            } catch (HibernateException he) {
                log.warn("exception commiting underlying transaction", he);
                commitException = true;
                // we're only going to rethrow the first commit exception we receive
                if (firstCommitException == null) {
                    firstCommitException = he;
                }
            }
        }
        if (commitException) {
            commitFailed = true;
            afterTransactionCompletion(Status.STATUS_UNKNOWN);
            throw new TransactionException("Commit failed", firstCommitException);
        }
        afterTransactionCompletion(Status.STATUS_COMMITTED);
        committed = true;
    }

    @Override
    public void rollback() throws HibernateException {
        if (!begun && !commitFailed) {
            throw new TransactionException("Transaction not successfully started");
        }
        boolean rollbackException = false;
        HibernateException firstRollbackException = null;
        for (Transaction t : transactions) {
            if (t.wasCommitted()) {
                continue;
            }
            try {
                t.rollback();
            } catch (HibernateException he) {
                log.warn("exception rolling back underlying transaction", he);
                rollbackException = true;
                if (firstRollbackException == null) {
                    firstRollbackException = he;
                }
            }
        }
        if (rollbackException) {
            // we're only going to rethrow the first rollback exception
            throw new TransactionException("Rollback failed", firstRollbackException);
        }
        rolledBack = true;
    }

    @Override
    public boolean wasRolledBack() throws HibernateException {
        return rolledBack;
    }

    @Override
    public boolean wasCommitted() throws HibernateException {
        return committed;
    }

    @Override
    public boolean isActive() throws HibernateException {
        return begun && !(rolledBack || committed || commitFailed);
    }

    @Override
    public void registerSynchronization(final Synchronization sync) throws HibernateException {
        if (sync == null) {
            throw new NullPointerException("null Synchronization");
        }
        if (synchronizations == null) {
            synchronizations = Lists.newArrayList();
        }
        synchronizations.add(sync);
    }

    @Override
    public void setTimeout(final int seconds) {
        timeoutSet = true;
        timeout = seconds;
        for (final Transaction t : transactions) {
            t.setTimeout(timeout);
        }
    }

    private void beforeTransactionCompletion() {
        if (synchronizations != null) {
            for (Synchronization sync : synchronizations) {
                try {
                    sync.beforeCompletion();
                } catch (Throwable t) {
                    log.warn("exception calling user Synchronization", t);
                }
            }
        }
    }

    private void afterTransactionCompletion(final int status) {
        begun = false;
        if (synchronizations != null) {
            for (Synchronization sync : synchronizations) {
                try {
                    sync.afterCompletion(status);
                } catch (Throwable t) {
                    log.warn("exception calling user Synchronization", t);
                }
            }
        }
    }
}
