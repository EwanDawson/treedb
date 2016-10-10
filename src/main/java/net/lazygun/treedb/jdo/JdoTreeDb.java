package net.lazygun.treedb.jdo;

import net.lazygun.treedb.*;
import net.lazygun.treedb.Transaction;
import javaslang.Function1;
import javaslang.collection.HashSet;
import javaslang.collection.Set;
import javaslang.collection.Stream;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.*;
import java.util.List;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
final class JdoTreeDb implements TreeDb {

    private final PersistenceManager pm;
    private final Storage storage;

    JdoTreeDb(PersistenceManager pm, Storage storage) {
        this.pm = pm;
        this.storage = storage;
    }

    @Override
    public Set<Branch> branches() {
        //noinspection unchecked
        final List<Branch> branches = (List<Branch>) pm.newQuery(Branch.class).execute();
        return HashSet.ofAll(branches);
    }

    @Override
    public Branch createBranch(String name, Revision base) {
        final Branch branch = ((JdoRevision) base).fork(name);
        return pm.makePersistent(branch);
    }

    @Override
    public Branch getBranch(String name) {
        final Query query = pm.newQuery(Branch.class);
        query.setFilter("name == :name");
        query.setUnique(true);
        final Branch branch = (Branch) query.execute(name);
        if (branch == null) throw new BranchNotFoundException(name);
        return branch;
    }

    @Override
    public void deleteBranch(Branch branch) {
        pm.deletePersistent(branch);
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
        final javax.jdo.Transaction transaction = pm.currentTransaction();
        return new JdoTransaction(transaction, revision, storage).execute(operations);
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
