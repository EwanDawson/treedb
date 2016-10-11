package net.lazygun.treedb.jdo;

import javaslang.collection.List;
import net.lazygun.treedb.Branch;
import net.lazygun.treedb.Change;
import net.lazygun.treedb.Revision;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
@PersistenceCapable
final class JdoRevision implements Revision {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String committer = "";

    private Instant date = Instant.now();

    private String message = "";

    private JdoRevision parent;

    private List<Change> changes = List.empty();

    private String branch;

    private boolean isCommitted = false;

    JdoRevision(@Nullable JdoRevision parent, String branch) {
        this.parent = parent;
        this.branch = branch;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String committer() {
        return committer;
    }

    @Override
    public Instant date() {
        return date;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Revision parent() {
        return parent;
    }

    @Override
    public List<Change> changes() {
        return changes;
    }

    @Override
    public String branch() {
        return branch;
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    synchronized Revision commit(String committer, String message) {
        if (!isCommitted) {
            this.committer = committer;
            this.message = message;
            this.isCommitted = true;
            return new JdoRevision(this, branch);
        }
        else {
            throw new IllegalStateException("Cannot commit already committed revision");
        }
    }

    Branch fork(String branchName) {
        final JdoRevision tip = new JdoRevision(this, branchName);
        return new JdoBranch(branchName, tip);
    }
}
