package net.lazygun.treedb.jdo;

import javaslang.Function1;
import javaslang.collection.Stream;
import net.lazygun.treedb.*;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.List;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
class JdoTreeDb implements TreeDb {

    private final PersistenceManagerFactory pmf;
    private final Storage storage;

    JdoTreeDb(PersistenceManagerFactory pmf, Storage storage) {
        this.pmf = pmf;
        this.storage = storage;
        init();
    }

    private void init() {
        if (branches().isEmpty()) {
            final JdoRevision root = new JdoRevision(null, "master");
            final JdoRevision tip = (JdoRevision) root.commit("", "");
            final JdoBranch master = new JdoBranch("master", tip);
            noTxPm().deletePersistentAll(root, tip, master);
        }
    }

    private PersistenceManager noTxPm() {
        final PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty("datanucleus.nontx.atomic", "true");
        return pm;
    }

    @Override
    public Stream<Branch> branches() {
        //noinspection unchecked
        final List<Branch> branches = (List<Branch>) noTxPm().newQuery(Branch.class).execute();
        return Stream.ofAll(branches);
    }

    @Override
    public Branch createBranch(String name, Revision base) {
        final Branch branch = ((JdoRevision) base).fork(name);
        return noTxPm().makePersistent(branch);
    }

    @Override
    public Branch getBranch(String name) {
        final Query query = noTxPm().newQuery(Branch.class);
        query.setFilter("name == :name");
        query.setUnique(true);
        final Branch branch = (Branch) query.execute(name);
        if (branch == null) throw new BranchNotFoundException(name);
        return branch;
    }

    @Override
    public void deleteBranch(Branch branch) {
        noTxPm().deletePersistent(branch);
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
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            return new JdoTransaction(pm, revision, storage).execute(operations);
        } finally {
            pm.close();
        }
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
}
