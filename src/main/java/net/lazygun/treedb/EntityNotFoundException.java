package net.lazygun.treedb;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
public final class EntityNotFoundException extends RuntimeException {

    private final String revision;
    private final Id id;

    public EntityNotFoundException(Revision revision, Id id) {
        this.revision = revision.id();
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Could not find entity " + id + " in revision " + revision;
    }
}
