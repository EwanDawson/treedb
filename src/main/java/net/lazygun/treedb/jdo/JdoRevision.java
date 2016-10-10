package net.lazygun.treedb.jdo;

import net.lazygun.treedb.Branch;
import net.lazygun.treedb.Change;
import net.lazygun.treedb.Revision;
import javaslang.collection.Stack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
final class JdoRevision implements Revision {
    @Override
    public String id() {
        return null;
    }

    @Override
    public String committer() {
        return null;
    }

    @Override
    public String date() {
        return null;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public Revision parent() {
        return null;
    }

    @Override
    public Stack<Change> changes() {
        return null;
    }

    @Override
    public String branch() {
        return null;
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    Branch fork(String branchName) {
        final JdoRevision tip = new JdoRevision();
        return new JdoBranch(branchName, tip);
    }
}
