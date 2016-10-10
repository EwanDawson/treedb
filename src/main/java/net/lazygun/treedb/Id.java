package net.lazygun.treedb;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
public interface Id<T extends Entity<T>> {
    Class<T> type();
}
