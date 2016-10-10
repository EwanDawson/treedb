package net.lazygun.treedb;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
public final class BranchNotFoundException extends RuntimeException {

    private final String name;

    public BranchNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Could not find Branch named '" + name + "'";
    }
}
