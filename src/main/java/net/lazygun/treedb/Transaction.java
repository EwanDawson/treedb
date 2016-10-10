package net.lazygun.treedb;

/**
 * @author Ewan
 */
public interface Transaction<Q> {

    <T extends Entity<T>> T getEntity(Id<T> id);

    <T extends Entity<T>> T addEntity(T entity);

    <T extends Entity<T>> void removeEntity(T entity);

    <T extends Entity<T>> T updateEntity(T entity);

    <T extends Entity<T>> Q query(Class<T> entityClass);
}
