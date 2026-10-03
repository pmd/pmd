/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.Closeable;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
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

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Brian Remedios
 */
public final class IOUtil {
    public static final char UTF_BOM = ByteOrderMark.UTF_BOM;
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
            closeQuietly((Closeable) classLoader);
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


    // The following methods are taken from Apache Commons IO.
    // The dependency was removed from PMD 6 because it had a security issue,
    // and upgrading was not possible without upgrading to Java 8.
    // See https://github.com/pmd/pmd/pull/3968
    // TODO PMD 7: consider bringing back commons-io and cleaning this class up.

    public static void closeQuietly(Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    public static String normalizePath(String path) {
        return FilenameUtils.normalize(path);
    }

    public static String getFilenameBase(String name) {
        return FilenameUtils.getBaseName(name);
    }

    public static void copy(InputStream from, OutputStream to) throws IOException {
        IOUtils.copyLarge(from, to);
    }

    public static String readFileToString(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    public static String readFileToString(File file, Charset charset) throws IOException {
        return FileUtils.readFileToString(file, charset);
    }

    public static String readToString(Reader reader) throws IOException {
        return IOUtils.toString(reader);
    }

    public static String readToString(InputStream stream, Charset charset) throws IOException {
        return IOUtils.toString(stream, charset);
    }
}
