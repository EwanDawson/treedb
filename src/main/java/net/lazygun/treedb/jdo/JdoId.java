package net.lazygun.treedb.jdo;

import net.lazygun.treedb.Entity;
import net.lazygun.treedb.Id;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.jdo.annotations.EmbeddedOnly;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ewan
 */
@Immutable
@EmbeddedOnly
final class JdoId<T extends Entity<T>> implements Id<T> {

    private String id = UUID.randomUUID().toString();
    private Class<T> type;

    JdoId(@Nonnull Class<T> type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public Class<T> type() {
        return type;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Id(" + type.getSimpleName() + ":" + id.substring(0, 7);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdoId<?> jdoId = (JdoId<?>) o;
        return Objects.equals(id, jdoId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
