package net.lazygun.treedb;

/**
 * @author Ewan
 */
public interface Storage {

    <T extends Entity<T>> T getEntity(Revision revision, Id<T> id);

    <T extends Entity<T>> void writeEntity(Revision revision, Entity<T> entity);

    <T extends Entity<T>> void dropEntity(Revision revision, Entity<T> entity);
}
