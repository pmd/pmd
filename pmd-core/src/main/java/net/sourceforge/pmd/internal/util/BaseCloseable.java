/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.Closeable;
import java.io.IOException;

public abstract class BaseCloseable implements Closeable {

    protected boolean open = true;

    protected void ensureOpen() throws IOException {
        if (!open) {
            throw new IOException("Closed " + this);
        }
    }

    @Override
    public void close() throws IOException {
        if (open) {
            open = false;
            doClose();
        }
    }

    protected abstract void doClose() throws IOException;
}
