/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/**
 * Base class for an autocloseable that produce a result once it has
 * been closed.
 *
 * @param <T> Type of the result
 */
public abstract class BaseResultProducingCloseable<T> implements AutoCloseable {

    private boolean closed;

    protected final void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("Listener closed");
        }
    }

    /**
     * Returns the result.
     *
     * @throws IllegalStateException If this instance has not been closed yet
     */
    public final T getResult() {
        if (!closed) {
            throw new IllegalStateException("Cannot get result before listener is closed");
        }

        return getResultImpl();
    }

    /** Produce the final result. */
    protected abstract T getResultImpl();

    /**
     * @implNote Call super
     */
    @Override
    public void close() throws Exception {
        closed = true;
    }
}
