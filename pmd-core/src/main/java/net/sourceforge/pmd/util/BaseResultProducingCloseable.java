/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.function.Consumer;

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
    public void close() {
        closed = true;
    }


    public static <U, C extends BaseResultProducingCloseable<U>> U using(C closeable, Consumer<? super C> it) {
        try {
            it.accept(closeable);
        } finally {
            closeable.close();
        }
        return closeable.getResult();
    }
}
