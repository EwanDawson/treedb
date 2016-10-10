package net.lazygun.treedb;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
public final class CommittedRevisionIsUnmodifiableException extends RuntimeException {
    private final String revision;

    public CommittedRevisionIsUnmodifiableException(Revision revision) {
        this.revision = revision.id();
    }

    @Override
    public String getMessage() {
        return "Illegal attempt to modify committed revision '" + revision + "'";
    }
}
