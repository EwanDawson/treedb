package net.lazygun.treedb;

import javaslang.collection.Stream;

/**
 * @author Ewan
 */
public interface Branch {

    String name();

    Revision tip();

    Revision base();

    String parent();

    void rename(String name);

    Stream<Revision> historyUpToBase();

    Stream<Revision> completeHistory();
}
