/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.Closeable;
import java.io.IOException;

public abstract class BaseCloseable implements Closeable {

    protected boolean open = true;

    protected final void ensureOpen() throws IOException {
        if (!open) {
            throw new IOException("Closed " + this);
        }
    }

    protected final void ensureOpenIllegalState() throws IllegalStateException {
        if (!open) {
            throw new IllegalStateException("Closed " + this);
        }
    }


    /**
     * Noop if called several times. Thread-safe.
     */
    @Override
    public void close() throws IOException {
        if (open) {
            synchronized (this) {
                if (open) {
                    open = false;
                    doClose();
                }
            }
        }
    }

    /** Called at most once. */
    protected abstract void doClose() throws IOException;
}
