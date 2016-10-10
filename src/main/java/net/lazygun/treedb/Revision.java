package net.lazygun.treedb;

import javaslang.collection.Stack;

/**
 * @author Ewan
 */
public interface Revision {

    String id();

    String committer();

    String date();

    String message();

    Revision parent();

    Stack<Change> changes();

    String branch();

    boolean isCommitted();
}
