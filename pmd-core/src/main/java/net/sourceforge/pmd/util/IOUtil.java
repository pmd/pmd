/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 *
 * @author Brian Remedios
 * @deprecated Is internal API
 */
@InternalApi
@Deprecated
public final class IOUtil {

    private IOUtil() {
    }

    public static Writer createWriter() {
        return new OutputStreamWriter(System.out);
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
        try {
            if (StringUtils.isBlank(reportFile)) {
                return createWriter();
            }
            Path path = new File(reportFile).toPath().toAbsolutePath();
            Files.createDirectories(path.getParent()); // ensure parent dir exists
            // this will create the file if it doesn't exist
            return Files.newBufferedWriter(path, getDefaultCharset());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Reader skipBOM(Reader source) {
        Reader in = new BufferedReader(source);
        try {
            in.mark(1);
            int firstCharacter = in.read();
            if (firstCharacter != '\ufeff') {
                in.reset();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while trying to skip BOM marker", e);
        }
        return in;
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
     * pending exception if not null, or the exception retuned by {@link #closeAll(Collection)}
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
