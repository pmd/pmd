/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.Closeable;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Brian Remedios
 */
public final class IOUtil {
    /** Conventional return value for readers. */
    public static final int EOF = -1;

    private IOUtil() {
    }

    /**
     * Creates a writer that writes to stdout using the system default charset.
     *
     * @return a writer, never null
     *
     * @see #createWriter(String)
     * @see #createWriter(Charset, String)
     */
    public static Writer createWriter() {
        return createWriter(null);
    }

    /**
     * Gets the current default charset.
     *
     * <p>In contrast to {@link Charset#defaultCharset()}, the result is not cached,
     * so that in unit tests, the charset can be changed.
     * @return
     */
    private static Charset getDefaultCharset() {
        String csn = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("file.encoding");
            }
        });
        try {
            return Charset.forName(csn);
        } catch (UnsupportedCharsetException e) {
            return StandardCharsets.UTF_8;
        }
    }

    /**
     * Creates a writer that writes to the given file or to stdout.
     * The file is created if it does not exist.
     *
     * <p>Warning: This writer always uses the system default charset.
     *
     * @param reportFile the file name (optional)
     *
     * @return the writer, never null
     */
    public static Writer createWriter(String reportFile) {
        return createWriter(getDefaultCharset(), reportFile);
    }

    /**
     * Creates a writer that writes to the given file or to stdout.
     * The file is created if it does not exist.
     *
     * <p>Unlike {@link #createWriter(String)}, this method always uses
     * the given charset. Even for writing to stdout. It never
     * falls back to the default charset.</p>
     *
     * @param charset the charset to be used (required)
     * @param reportFile the file name (optional)
     * @return
     */
    public static Writer createWriter(Charset charset, @Nullable String reportFile) {
        try {
            if (StringUtils.isBlank(reportFile)) {
                return new OutputStreamWriter(new FilterOutputStream(System.out) {
                    @Override
                    public void close() {
                        // avoid closing stdout, simply flush
                        try {
                            out.flush();
                        } catch (IOException ignored) {
                            // Nothing left to do
                        }
                    }
                    
                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        /*
                         * FilterOutputStream iterates over each byte, asking subclasses to provide more efficient implementations
                         * It therefore negates any such optimizations that the underlying stream actually may implement.
                         */
                        out.write(b, off, len);
                    }
                }, charset);
            }
            Path path = new File(reportFile).toPath().toAbsolutePath();
            Files.createDirectories(path.getParent()); // ensure parent dir exists
            // this will create the file if it doesn't exist
            return Files.newBufferedWriter(path, charset);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void tryCloseClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) classLoader);
        }
    }

    /**
     * Close all closeable resources in order. If any exception occurs,
     * it is saved and returned. If more than one exception occurs, the
     * following are accumulated as suppressed exceptions in the first.
     *
     * @param closeables Resources to close
     *
     * @return An exception, or null if no 'close' routine threw
     */
    @SuppressWarnings("PMD.CloseResource") // false-positive
    public static Exception closeAll(Collection<? extends AutoCloseable> closeables) {
        Exception composed = null;
        for (AutoCloseable it : closeables) {
            try {
                it.close();
            } catch (Exception e) {
                if (composed == null) {
                    composed = e;
                } else {
                    composed.addSuppressed(e);
                }
            }
        }
        return composed;
    }

    /**
     * Ensure that the closeables are closed. In the end, throws the
     * pending exception if not null, or the exception returned by {@link #closeAll(Collection)}
     * if not null. If both are non-null, adds one of them to the suppress
     * list of the other, and throws that one.
     */
    public static void ensureClosed(List<? extends AutoCloseable> toClose,
                                    @Nullable Exception pendingException) throws Exception {
        Exception closeException = closeAll(toClose);
        if (closeException != null) {
            if (pendingException != null) {
                closeException.addSuppressed(pendingException);
                throw closeException;
            }
            // else no exception at all
        } else if (pendingException != null) {
            throw pendingException;
        }
    }
}
