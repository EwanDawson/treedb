package net.lazygun.treedb.jdo;

import com.google.common.base.MoreObjects;
import javaslang.collection.Stream;
import net.lazygun.treedb.Branch;
import net.lazygun.treedb.Revision;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.*;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
final class JdoBranch implements Branch {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    @Unique
    private String name;

    private JdoRevision base;

    private JdoRevision tip;

    private String parent;

    JdoBranch(String name, JdoRevision tip) {
        this.name = name;
        this.tip = tip;
        this.base = tip.parent();
        this.parent = base.branch();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Revision tip() {
        return tip;
    }

    @Override
    public Revision base() {
        return base;
    }

    @Override
    public String parent() {
        return parent;
    }

    void rename(String name) {
        this.name = name;
    }

    void commit(String committer, String message) {
        tip = (JdoRevision) tip.commit(committer, message);
    }

    long version() {
        return (long) JDOHelper.getVersion(this);
    }

    @Override
    public Stream<Revision> historyUpToBase() {
        return completeHistory().takeUntil(revision -> revision.equals(base));
    }

    @Override
    public Stream<Revision> completeHistory() {
        return Stream.iterate(tip, Revision::parent).takeUntil(revision -> revision == null);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdoBranch jdoBranch = (JdoBranch) o;
        return Objects.equals(id, jdoBranch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name())
            .add("parent", parent())
            .toString();
    }
}
