/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Brian Remedios
 */
public final class IOUtil {
    /**
     * Unicode BOM character. Replaces commons io ByteOrderMark.
     */
    public static final char UTF_BOM = '\uFEFF';
    /** Conventional return value for readers. */
    public static final int EOF = -1;
    private static final int BUFFER_SIZE = 8192;

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
    public static Writer createWriter(Charset charset, String reportFile) {
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


    // The following methods are taken from Apache Commons IO.
    // The dependency was removed from PMD 6 because it had a security issue,
    // and upgrading was not possible without upgrading to Java 8.
    // See https://github.com/pmd/pmd/pull/3968
    // TODO PMD 7: consider bringing back commons-io and cleaning this class up.

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
            // ignored
        }
    }

    public static byte[] toByteArray(InputStream stream) throws IOException {
        byte[] result = new byte[0];
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = stream.read(buffer);
        while (count > -1) {
            byte[] newResult = new byte[result.length + count];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(buffer, 0, newResult, result.length, count);
            result = newResult;
            count = stream.read(buffer);
        }
        return result;
    }

    public static long skipFully(InputStream stream, long n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        long bytesToSkip = n;
        byte[] buffer = new byte[(int) Math.min(BUFFER_SIZE, bytesToSkip)];
        while (bytesToSkip > 0) {
            int count = stream.read(buffer, 0, (int) Math.min(BUFFER_SIZE, bytesToSkip));
            if (count < 0) {
                // reached eof
                break;
            }
            bytesToSkip -= count;
        }
        return n - bytesToSkip;
    }

    public static String normalizePath(String path) {
        Path path1 = Paths.get(path);
        path1.isAbsolute();
        String normalized = path1.normalize().toString();
        if (normalized.contains("." + File.separator) || normalized.contains(".." + File.separator) || "".equals(normalized)) {
            return null;
        }
        return normalized;
    }

    public static boolean equalsNormalizedPaths(String path1, String path2) {
        return Objects.equals(normalizePath(path1), normalizePath(path2));
    }

    public static String getFilenameExtension(String name) {
        String filename = Paths.get(name).getFileName().toString();
        int dot = filename.lastIndexOf('.');
        if (dot > -1) {
            return filename.substring(dot + 1);
        }
        return "";
    }

    public static String getFilenameBase(String name) {
        String filename = Paths.get(name).getFileName().toString();
        int dot = filename.lastIndexOf('.');
        if (dot > -1) {
            return filename.substring(0, dot);
        }
        return filename;
    }

    public static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int count = from.read(buffer);
        while (count > -1) {
            to.write(buffer, 0, count);
            count = from.read(buffer);
        }
    }

    public static void copy(Reader from, Writer to) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int count = from.read(buffer);
        while (count > -1) {
            to.write(buffer, 0, count);
            count = from.read(buffer);
        }
    }

    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }

    public static String readFileToString(File file, Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return charset.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public static String readToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(BUFFER_SIZE);
        char[] buffer = new char[BUFFER_SIZE];
        int count = reader.read(buffer);
        while (count > -1) {
            sb.append(buffer, 0, count);
            count = reader.read(buffer);
        }
        return sb.toString();
    }

    public static String readToString(InputStream stream, Charset charset) throws IOException {
        byte[] bytes = toByteArray(stream);
        return charset.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public static InputStream fromReader(Reader reader) throws IOException {
        class ReaderInputStream extends InputStream {
            private final Reader reader;
            private final CharBuffer charBuffer = CharBuffer.allocate(BUFFER_SIZE);
            private final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            private final CharsetEncoder encoder;

            private boolean eof;

            ReaderInputStream(Reader reader) {
                this.reader = reader;
                encoder = Charset.defaultCharset().newEncoder()
                        .onMalformedInput(CodingErrorAction.REPLACE)
                        .onUnmappableCharacter(CodingErrorAction.REPLACE);
                charBuffer.clear();
                byteBuffer.clear();
                byteBuffer.flip(); // byte buffer is empty at the beginning, no bytes read yet
            }

            @Override
            public int read() throws IOException {
                if (!byteBuffer.hasRemaining()) {
                    if (charBuffer.hasRemaining() && !eof) {
                        int count = reader.read(charBuffer);
                        eof = count == -1;
                    }
                    byteBuffer.clear();
                    charBuffer.flip();
                    encoder.encode(charBuffer, byteBuffer, eof);
                    byteBuffer.flip();
                    charBuffer.compact();
                }

                if (byteBuffer.hasRemaining()) {
                    return byteBuffer.get();
                }

                return -1;
            }

            @Override
            public int available() throws IOException {
                return byteBuffer.remaining();
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        }

        return new ReaderInputStream(reader);
    }

    public static OutputStream fromWriter(Writer writer, String encoding) throws UnsupportedCharsetException {
        class WriterOutputStream extends OutputStream {
            private final Writer writer;
            private final CharsetDecoder decoder;
            private final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            private final CharBuffer charBuffer = CharBuffer.allocate(BUFFER_SIZE);

            WriterOutputStream(Writer writer, String encoding) throws UnsupportedCharsetException {
                this.writer = writer;
                Charset charset = Charset.forName(encoding);
                decoder = charset.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPLACE)
                        .onUnmappableCharacter(CodingErrorAction.REPLACE);
                byteBuffer.clear();
                charBuffer.clear();
            }

            @Override
            public void write(int b) throws IOException {
                if (!byteBuffer.hasRemaining()) {
                    decodeByteBuffer(false);
                }
                byteBuffer.put((byte) b);
            }

            @Override
            public void flush() throws IOException {
                decodeByteBuffer(false);
            }

            private void decodeByteBuffer(boolean isClosing) throws IOException {
                byteBuffer.flip();
                charBuffer.clear();
                decoder.decode(byteBuffer, charBuffer, isClosing);
                writer.write(charBuffer.array(), 0, charBuffer.position());
                writer.flush();
                byteBuffer.compact();
            }

            @Override
            public void close() throws IOException {
                flush();
                decodeByteBuffer(true);
                writer.close();
            }
        }

        return new WriterOutputStream(writer, encoding);
    }

    /**
     * <p>
     * Input stream that skips an optional byte order mark at the beginning
     * of the stream. Whether the stream had a byte order mark (encoded in either UTF-8,
     * UTF-16LE or UTF-16BE) can be checked with {@link #hasBom()}. The corresponding
     * charset can be retrieved with {@link #getBomCharsetName()}.
     * </p>
     *
     * <p>
     * If the stream didn't had a BOM, then no bytes are skipped.
     * </p>
     */
    public static class BomAwareInputStream extends FilterInputStream {

        private byte[] begin;
        int beginIndex;

        private String charset;

        public BomAwareInputStream(InputStream in) {
            super(in);
            begin = determineBom();
        }

        private byte[] determineBom() {
            byte[] bytes = new byte[3];
            try {
                int count = in.read(bytes);
                if (count == 3 && bytes[0] == (byte) 0xef && bytes[1] == (byte) 0xbb && bytes[2] == (byte) 0xbf) {
                    charset = StandardCharsets.UTF_8.name();
                    return new byte[0]; // skip all 3 bytes
                } else if (count >= 2 && bytes[0] == (byte) 0xfe && bytes[1] == (byte) 0xff) {
                    charset = StandardCharsets.UTF_16BE.name();
                    return new byte[] { bytes[2] };
                } else if (count >= 2 && bytes[0] == (byte) 0xff && bytes[1] == (byte) 0xfe) {
                    charset = StandardCharsets.UTF_16LE.name();
                    return new byte[] { bytes[2] };
                } else if (count == 3) {
                    return bytes;
                }

                if (count < 0) {
                    return new byte[0];
                }

                byte[] read = new byte[count];
                for (int i = 0; i < count; i++) {
                    read[i] = bytes[i];
                }
                return read;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int read() throws IOException {
            if (beginIndex < begin.length) {
                return begin[beginIndex++];
            }
            return super.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (beginIndex < begin.length) {
                int count = 0;
                for (; count < len && beginIndex < begin.length; beginIndex++) {
                    b[off + count] = begin[beginIndex];
                    count++;
                }
                return count;
            }
            return super.read(b, off, len);
        }

        public boolean hasBom() {
            return charset != null;
        }

        public String getBomCharsetName() {
            return charset;
        }
    }
}
