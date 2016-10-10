package net.lazygun.treedb.jdo;

import net.lazygun.treedb.*;
import net.lazygun.treedb.Transaction;
import javaslang.Function1;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
final class JdoTransaction implements Transaction<Query> {

    private final PersistenceManager pm;
    private final Revision revision;
    private final Storage storage;
    private final boolean isReadOnly;

    JdoTransaction(PersistenceManager pm, Revision revision, Storage storage) {
        this.pm = pm;
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
    public <T extends Entity<T>> Query query(Class<T> entityClass) {
        return new LatestVersionQuery<>(revision, pm, entityClass);
    }

    <T> T execute(Function1<Transaction, T> operations) {
        final javax.jdo.Transaction transaction = pm.currentTransaction();
        try {
            transaction.begin();
            final T result = operations.apply(this);
            transaction.commit();
            return result;
        } catch (Exception e) {
            throw new TransactionFailedException(e);
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    private static class LatestVersionQuery<T extends Entity<T>> implements Query {

        static final String LATEST_VERSION_VARIABLE = "net.lazygun.treedb.Version __latest_version";
        static final String LATEST_VERSION_FILTER = "version == __latest_version";

        private final Query q;

        private LatestVersionQuery(Revision revision, PersistenceManager pm, Class<T> entityClass) {
            this.q = pm.newQuery(entityClass);
            setFilter("true");
            declareVariables("");
            final Query latestVersionSubquery = prepareLatestVersionSubQuery(pm);
            this.q.addSubquery(latestVersionSubquery, LATEST_VERSION_VARIABLE, null, revisionId(revision), "this.id");
        }

        private Query prepareLatestVersionSubQuery(PersistenceManager pm) {
            final Query query = pm.newQuery(LatestVersion.class);
            query.setResult("this.version");
            query.setFilter("this.revision == :revisionParam && this.id == :idParam");
            return query;
        }

        private String revisionId(Revision revision) {
            return "'" + revision.id() + "'";
        }

        @Override
        public void setClass(Class cls) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCandidates(Extent pcs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCandidates(Collection pcs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFilter(String filter) {
            if (!filter.isEmpty()) q.setFilter(LATEST_VERSION_FILTER + " && " + filter);
        }

        @Override
        public void declareImports(String imports) {
            q.declareImports(imports);
        }

        @Override
        public void declareParameters(String parameters) {
            q.declareParameters(parameters);
        }

        @Override
        public void declareVariables(String variables) {
            q.declareVariables(LATEST_VERSION_VARIABLE + "; " + variables);
        }

        @Override
        public void setOrdering(String ordering) {
            q.setOrdering(ordering);
        }

        @Override
        public void setIgnoreCache(boolean ignoreCache) {
            q.setIgnoreCache(ignoreCache);
        }

        @Override
        public boolean getIgnoreCache() {
            return q.getIgnoreCache();
        }

        @Override
        public void compile() {
            q.compile();
        }

        @Override
        public Object execute() {
            return q.execute();
        }

        @Override
        public Object execute(Object p1) {
            return q.execute(p1);
        }

        @Override
        public Object execute(Object p1, Object p2) {
            return q.execute(p1, p2);
        }

        @Override
        public Object execute(Object p1, Object p2, Object p3) {
            return q.execute(p1, p2, p3);
        }

        @Override
        public Object executeWithMap(Map parameters) {
            return q.executeWithMap(parameters);
        }

        @Override
        public Object executeWithArray(Object... parameters) {
            return q.executeWithArray(parameters);
        }

        @Override
        public PersistenceManager getPersistenceManager() {
            return q.getPersistenceManager();
        }

        @Override
        public void close(Object queryResult) {
            q.close(queryResult);
        }

        @Override
        public void closeAll() {
            q.closeAll();
        }

        @Override
        public void setGrouping(String group) {
            q.setGrouping(group);
        }

        @Override
        public void setUnique(boolean unique) {
            q.setUnique(unique);
        }

        @Override
        public void setResult(String data) {
            q.setResult(data);
        }

        @Override
        public void setResultClass(Class cls) {
            q.setResultClass(cls);
        }

        @Override
        public void setRange(long fromIncl, long toExcl) {
            q.setRange(fromIncl, toExcl);
        }

        @Override
        public void setRange(String fromInclToExcl) {
            q.setRange(fromInclToExcl);
        }

        @Override
        public void addExtension(String key, Object value) {
            q.addExtension(key, value);
        }

        @Override
        public void setExtensions(Map extensions) {
            q.setExtensions(extensions);
        }

        @Override
        public FetchPlan getFetchPlan() {
            return q.getFetchPlan();
        }

        @Override
        public long deletePersistentAll(Object... parameters) {
            return q.deletePersistentAll(parameters);
        }

        @Override
        public long deletePersistentAll(Map parameters) {
            return q.deletePersistentAll(parameters);
        }

        @Override
        public long deletePersistentAll() {
            return q.deletePersistentAll();
        }

        @Override
        public void setUnmodifiable() {
            q.setUnmodifiable();
        }

        @Override
        public boolean isUnmodifiable() {
            return q.isUnmodifiable();
        }

        @Override
        public void addSubquery(Query sub, String variableDeclaration, String candidateCollectionExpression) {
            q.addSubquery(sub, variableDeclaration, candidateCollectionExpression);
        }

        @Override
        public void addSubquery(Query sub, String variableDeclaration, String candidateCollectionExpression,
                                String parameter) {
            q.addSubquery(sub, variableDeclaration, candidateCollectionExpression, parameter);
        }

        @Override
        public void addSubquery(Query sub, String variableDeclaration, String candidateCollectionExpression,
                                String... parameters) {
            q.addSubquery(sub, variableDeclaration, candidateCollectionExpression, parameters);
        }

        @Override
        public void addSubquery(Query sub, String variableDeclaration, String candidateCollectionExpression,
                                Map parameters) {
            q.addSubquery(sub, variableDeclaration, candidateCollectionExpression, parameters);
        }

        @Override
        public void setDatastoreReadTimeoutMillis(Integer interval) {
            q.setDatastoreReadTimeoutMillis(interval);
        }

        @Override
        public Integer getDatastoreReadTimeoutMillis() {
            return q.getDatastoreReadTimeoutMillis();
        }

        @Override
        public void setDatastoreWriteTimeoutMillis(Integer interval) {
            q.setDatastoreWriteTimeoutMillis(interval);
        }

        @Override
        public Integer getDatastoreWriteTimeoutMillis() {
            return q.getDatastoreWriteTimeoutMillis();
        }

        @Override
        public void cancelAll() {
            q.cancelAll();
        }

        @Override
        public void cancel(Thread thread) {
            q.cancel(thread);
        }

        @Override
        public void setSerializeRead(Boolean serialize) {
            q.setSerializeRead(serialize);
        }

        @Override
        public Boolean getSerializeRead() {
            return q.getSerializeRead();
        }
    }
}
