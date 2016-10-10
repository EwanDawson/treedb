package net.lazygun.treedb;

import javaslang.Function1;
import javaslang.collection.Stream;

/**
 * @author Ewan
 */
public interface TreeDb {

    Stream<Branch> branches();

    Branch createBranch(String name, Revision base);

    Branch getBranch(String name);

    void deleteBranch(Branch branch);

    void renameBranch(Branch branch, String name);

    void commitBranch(Branch branch, String committer, String message);

    <T extends Entity<T>> Id<T> nextId(Class<T> entityClass);

    Version initialVersion();

    <T> T transaction(Revision revision, Function1<Transaction, T> operations);

    <T extends Entity<T>> Stream<T> history(Revision fromRevision, Id<T> id);

    Diff diff(Revision from, Revision to);

    Branch merge(Branch from, Branch to);
}
