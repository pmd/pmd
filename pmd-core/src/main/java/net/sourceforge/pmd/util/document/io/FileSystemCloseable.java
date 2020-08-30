/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.internal.util.BaseCloseable;

/**
 * Tracks unclosed references to a resource. Zip files containing
 * {@link TextFile}s are closed when all of their dependent
 * {@link TextFile} entries have been closed.
 */
public final class FileSystemCloseable extends BaseCloseable implements Closeable {

    private final AtomicInteger numOpenResources = new AtomicInteger();
    private final Closeable closeAction;

    /**
     * Create a new filesystem closeable which when closed, executes
     * the {@link Closeable#close()} action of the parameter. Dependent
     * resources need to be registered using {@link PmdFiles#forPath(Path, Charset, FileSystemCloseable) forPath}.
     *
     * @param closeAction A closeable
     */
    public FileSystemCloseable(Closeable closeAction) {
        this.closeAction = closeAction;
    }

    void addDependent() {
        ensureOpenIllegalState();
        numOpenResources.incrementAndGet();
    }

    void closeDependent() throws IOException {
        ensureOpenIllegalState();
        if (numOpenResources.decrementAndGet() == 0) {
            // no more open references, we can close it
            // is this thread-safe?
            close();
        }
    }

    @Override
    protected void doClose() throws IOException {
        closeAction.close();
    }
}
