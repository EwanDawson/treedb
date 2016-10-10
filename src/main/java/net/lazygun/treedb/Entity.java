package net.lazygun.treedb;

/**
 * @author Ewan
 */
public interface Entity<T extends Entity<T>> {

    Id<T> id();

    Version version();
}
