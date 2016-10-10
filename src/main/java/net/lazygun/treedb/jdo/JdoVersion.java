package net.lazygun.treedb.jdo;

import net.lazygun.treedb.Version;

import javax.jdo.annotations.EmbeddedOnly;

/**
 * @author Ewan
 */
@EmbeddedOnly
final class JdoVersion implements Version {

    private int version = 1;

    void increment() {
        version++;
    }

    @Override
    public String toString() {
        return String.valueOf(version);
    }
}
