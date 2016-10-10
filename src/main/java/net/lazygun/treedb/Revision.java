package net.lazygun.treedb;

import javaslang.collection.List;

import java.time.Instant;

/**
 * @author Ewan
 */
public interface Revision {

    String id();

    String committer();

    Instant date();

    String message();

    Revision parent();

    List<Change> changes();

    String branch();

    boolean isCommitted();
}
