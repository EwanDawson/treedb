package net.lazygun.treedb.jdo;

import com.google.common.base.MoreObjects;
import net.lazygun.treedb.Entity;
import net.lazygun.treedb.Id;
import net.lazygun.treedb.Revision;
import net.lazygun.treedb.Version;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import java.util.Objects;

/**
 * @author Ewan
 */
@PersistenceCapable
@ParametersAreNonnullByDefault
final class LatestVersion {

    static LatestVersion create(Revision revision, Entity entity) {
        final LatestVersion latestVersion = new LatestVersion();
        latestVersion.revision = revision.id();
        latestVersion.id = entity.id();
        latestVersion.version = entity.version();
        return latestVersion;
    }

    @PrimaryKey
    private String revision;

    @PrimaryKey
    Id id;

    Version version;

    void update(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("revision", revision)
            .add("id", id)
            .add("version", version)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatestVersion that = (LatestVersion) o;
        return Objects.equals(revision, that.revision) &&
            Objects.equals(id, that.id) &&
            Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revision, id, version);
    }
}
