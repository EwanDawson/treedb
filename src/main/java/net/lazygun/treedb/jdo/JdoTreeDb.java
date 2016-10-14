package net.lazygun.treedb.jdo;

import javaslang.Function1;
import javaslang.collection.Stream;
import net.lazygun.treedb.*;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
class JdoTreeDb implements TreeDb, AutoCloseable {

    private final PersistenceManagerFactory pmf;
    private final PersistenceManager pm;
    private final JdoTransaction transaction;

    JdoTreeDb(PersistenceManagerFactory persistenceManagerFactory) {
        this(persistenceManagerFactory, new JdoTransaction(persistenceManagerFactory, new JdoStorage()));
    }

    private PersistenceManager initPersistenceManager() {
        final PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty("datanucleus.nontx.atomic", "true");
        return pm;
    }

    private JdoTreeDb(PersistenceManagerFactory persistenceManagerFactory, JdoTransaction transaction) {
        this.pmf = persistenceManagerFactory;
        this.transaction = transaction;
        pm = initPersistenceManager();
        init();
    }

    private void init() {
        if (branches().isEmpty()) {
            final JdoRevision root = new JdoRevision(null, "master");
            final JdoRevision tip = (JdoRevision) root.commit("", "");
            final JdoBranch master = new JdoBranch("master", tip);
            pm.makePersistentAll(root, tip, master);
        }
    }

    private <T> T withPM(Function1<PersistenceManager, T> operations) {
        try (final PersistenceManager pm = pmf.getPersistenceManager()) {
            final javax.jdo.Transaction txn = pm.currentTransaction();
            try {
                txn.begin();
                final T result = operations.apply(pm);
                txn.commit();
                return result;
            } finally {
                if (txn.isActive()) {
                    txn.rollback();
                }
            }
        }
    }

    private void withPM(Consumer<PersistenceManager> operations) {
        try (final PersistenceManager pm = pmf.getPersistenceManager()) {
            final javax.jdo.Transaction txn = pm.currentTransaction();
            try {
                txn.begin();
                operations.accept(pm);
                txn.commit();
            } finally {
                if (txn.isActive()) {
                    txn.rollback();
                }
            }
        }
    }

    @Override
    public Stream<Branch> branches() {
        //noinspection unchecked
        final List<JdoBranch> branches = (List<JdoBranch>) pm.newQuery(JdoBranch.class).execute();
        return Stream.ofAll(branches);
    }

    @Override
    public Branch createBranch(String name, Revision base) {
        final JdoBranch branch = ((JdoRevision) base).fork(name);
        return pm.makePersistent(branch);
    }

    @Override
    public Branch getBranch(String name) {
        final Query query = pm.newQuery(JdoBranch.class);
        query.setFilter("name == :name");
        query.setUnique(true);
        final JdoBranch branch = (JdoBranch) query.execute(name);
        if (branch == null) throw new BranchNotFoundException(name);
        return branch;
    }

    @Override
    public void deleteBranch(Branch branch) {
        pm.deletePersistent(branch);
    }

    @Override
    public void renameBranch(Branch branch, String name) {
        ((JdoBranch) branch).rename(name);
    }

    @Override
    public void commitBranch(Branch branch, String committer, String message) {
        ((JdoBranch) branch).commit(committer, message);
    }

    @Override
    public <T extends Entity<T>> Id<T> nextId(Class<T> entityClass) {
        return new JdoId<>(entityClass);
    }

    @Override
    public Version initialVersion() {
        return new JdoVersion();
    }

    @Override
    public <T> T transaction(Revision revision, Function1<Transaction, T> operations) {
        transaction.setRevision(revision);
        return transaction.execute(operations);
    }

    @Override
    public <T extends Entity<T>> Stream<T> history(Revision fromRevision, Id<T> id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Diff diff(Revision from, Revision to) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Branch merge(Branch from, Branch to) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void close() throws Exception {
        pm.close();
        pmf.close();
    }
}
