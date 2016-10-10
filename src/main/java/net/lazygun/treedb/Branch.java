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

    Stream<Revision> historyUpToBase();

    Stream<Revision> completeHistory();
}
