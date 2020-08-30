/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public final class FileSystemCloseable implements AutoCloseable {

    private final AtomicInteger numOpenResources = new AtomicInteger();
    private final Closeable closeAction;
    private boolean isClosed;

    public FileSystemCloseable(Closeable closeAction) {
        this.closeAction = closeAction;
    }

    public void addDependent() {
        numOpenResources.incrementAndGet();
    }

    public void closeDependent() throws IOException {
        if (numOpenResources.decrementAndGet() == 0) {
            synchronized (this) {
                closeAction.close();
                isClosed = true;
            }
        }
    }

    @Override
    public void close() throws Exception {
        synchronized (this) {
            if (!isClosed) {
                closeAction.close();
                isClosed = true;
            }
        }
    }
}
