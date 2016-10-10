package net.lazygun.treedb.jdo;

import net.lazygun.treedb.*;
import net.lazygun.treedb.Transaction;
import javaslang.Function1;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.*;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
final class JdoTransaction implements Transaction<Query> {

    private final javax.jdo.Transaction transaction;
    private final Revision revision;
    private final Storage storage;
    private final boolean isReadOnly;

    JdoTransaction(javax.jdo.Transaction transaction, Revision revision, Storage storage) {
        this.transaction = transaction;
        this.revision = revision;
        this.storage = storage;
        this.isReadOnly = revision.isCommitted();
    }

    @Override
    public <T extends Entity<T>> T getEntity(Id<T> id) {
        return storage.getEntity(revision, id);
    }

    @Override
    public <T extends Entity<T>> T addEntity(T entity) {
        if (isReadOnly) throw new CommittedRevisionIsUnmodifiableException(revision);
        storage.writeEntity(revision, entity);
        return entity;
    }

    @Override
    public <T extends Entity<T>> void removeEntity(T entity) {
        if (isReadOnly) throw new CommittedRevisionIsUnmodifiableException(revision);
        storage.dropEntity(revision, entity);
    }

    @Override
    public <T extends Entity<T>> T updateEntity(T entity) {
        if (isReadOnly) throw new CommittedRevisionIsUnmodifiableException(revision);
        storage.writeEntity(revision, entity);
        return entity;
    }

    @Override
    public Query query() {
        final Query query = transaction.getPersistenceManager().newQuery();
        query.set
        return query;
    }

    <T> T execute(Function1<Transaction, T> operations) {
        transaction.begin();
        try {
            operations.apply(this);
        } catch (Exception e) {
            transaction.rollback();
            throw new TransactionFailedException(e);
        }
        return null;
    }
}
