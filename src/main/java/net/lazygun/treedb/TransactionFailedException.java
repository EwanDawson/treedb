package net.lazygun.treedb;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Ewan
 */
@ParametersAreNonnullByDefault
public final class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(Exception e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return "Transaction failed due to " + getCause().getClass() + ": " + getCause().getMessage();
    }
}
