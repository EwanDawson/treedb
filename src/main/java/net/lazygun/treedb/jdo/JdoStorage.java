package net.lazygun.treedb.jdo;

import javaslang.control.Option;
import net.lazygun.treedb.*;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import static javaslang.API.*;
import static javaslang.Patterns.None;
import static javaslang.Patterns.Some;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
class JdoStorage implements Storage {

    private final ThreadLocal<PersistenceManager> pm = new ThreadLocal<>();

    void setPersistenceManager(PersistenceManager persistenceManager) {
        pm.set(persistenceManager);
    }

    PersistenceManager getPersistenceManager() {
        return pm.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity<T>> T getEntity(Revision revision, Id<T> id) {
        return (T) getLatestVersion(revision, id)
            .map(this::getLatestVersionOfEntity)
            .getOrElseThrow(() -> new EntityNotFoundException(revision, id));
    }

    private <T extends Entity<T>> Option<LatestVersion> getLatestVersion(Revision revision, Id<T> id) {
        final Query query = pm.get().newQuery(LatestVersion.class);
        query.setFilter("revision == :revision && id == :id");
        query.setUnique(true);
        return Option.of((LatestVersion) query.execute(revision.id(), id));
    }

    private Object getLatestVersionOfEntity(LatestVersion latestVersion) {
        final Query query = pm.get().newQuery(latestVersion.id.type());
        query.setFilter("id == :id && version == :version");
        query.setUnique(true);
        return query.execute(latestVersion.id, latestVersion.version);
    }

    @Override
    public <T extends Entity<T>> void writeEntity(Revision revision, Entity<T> entity) {
        incrementEntityVersion(entity);
        updateLatestVersion(revision, entity);
        writeUpdatedEntity(entity);
    }

    private <T extends Entity<T>> void incrementEntityVersion(Entity<T> entity) {
        ((JdoVersion) entity.version()).increment();
    }

    private <T extends Entity<T>> void updateLatestVersion(Revision revision, Entity<T> entity) {
        Match(getLatestVersion(revision, entity.id())).of(
            Case(Some($()), latestVersion -> run(() -> latestVersion.update(entity.version()))),
            Case(None(), run(() -> pm.get().makePersistent(LatestVersion.create(revision, entity))))
        );
    }

    private void writeUpdatedEntity(Entity entity) {
        pm.get().makePersistent(entity);
    }

    @Override
    public <T extends Entity<T>> void dropEntity(Revision revision, Entity<T> entity) {
        removeLatestVersion(revision, entity);
    }

    private <T extends Entity<T>> void removeLatestVersion(Revision revision, Entity<T> entity) {
        final Option<LatestVersion> latestVersion = getLatestVersion(revision, entity.id());
        if (latestVersion.isDefined()) pm.get().deletePersistent(latestVersion);
        else
            throw new IllegalStateException("No latest version exists for entity " + entity + " in revision " + revision);
    }
}
