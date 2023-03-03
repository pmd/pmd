/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

class IOUtilTest {

    @Test
    void testReadAllBytes() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            byte[] bytes = IOUtil.toByteArray(stream);
            assertEquals(5, bytes.length);
            assertArrayEquals(data, bytes);
        }
    }

    @Test
    void testToByteArrayResize() throws IOException {
        int size = 8192 + 8192 + 10;
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (InputStream stream = new ByteArrayInputStream(data)) {
            byte[] bytes = IOUtil.toByteArray(stream);
            assertEquals(size, bytes.length);
            assertArrayEquals(data, bytes);
        }
    }

    @Test
    void testSkipFully() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            assertThrows(IllegalArgumentException.class, () -> IOUtil.skipFully(stream, -1));

            assertEquals(3, IOUtil.skipFully(stream, 3));
            byte[] bytes = IOUtil.toByteArray(stream);
            assertEquals(2, bytes.length);
            assertArrayEquals("45".getBytes(StandardCharsets.UTF_8), bytes);
        }
    }

    @Test
    void testSkipFully2() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            // skip more bytes than the stream contains
            assertEquals(data.length, IOUtil.skipFully(stream, data.length + 1));
            byte[] bytes = IOUtil.toByteArray(stream);
            assertEquals(0, bytes.length);
        }
    }

    @Test
    void testNormalizePath() {
        if (SystemUtils.IS_OS_UNIX) {
            assertEquals("ab/cd.txt", IOUtil.normalizePath("ab/ef/../cd.txt"));
            assertEquals("/a.txt", IOUtil.normalizePath("/x/../../a.txt"));
            assertEquals("/foo", IOUtil.normalizePath("//../foo"));
            assertEquals("/foo", IOUtil.normalizePath("/foo//"));
            assertEquals("/foo", IOUtil.normalizePath("/foo/./"));
            assertEquals("/bar", IOUtil.normalizePath("/foo/../bar"));
            assertEquals("/bar", IOUtil.normalizePath("/foo/../bar/"));
            assertEquals("/baz", IOUtil.normalizePath("/foo/../bar/../baz"));
            assertEquals("/foo/bar", IOUtil.normalizePath("//foo//./bar"));
            assertEquals("foo", IOUtil.normalizePath("foo/bar/.."));
            assertEquals("bar", IOUtil.normalizePath("foo/../bar"));
            assertEquals("/foo/baz", IOUtil.normalizePath("//foo/bar/../baz"));
            assertEquals("~/bar", IOUtil.normalizePath("~/foo/../bar/"));
            assertEquals("/", IOUtil.normalizePath("/../"));
            assertEquals("bar", IOUtil.normalizePath("~/../bar"));
            assertEquals("bar", IOUtil.normalizePath("./bar"));

            assertNull(IOUtil.normalizePath("../foo"));
            assertNull(IOUtil.normalizePath("foo/../../bar"));
            assertNull(IOUtil.normalizePath("."));

            assertTrue(IOUtil.equalsNormalizedPaths("foo/../bar", "bar/./"));
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            assertEquals("ab\\cd.txt", IOUtil.normalizePath("ab\\ef\\..\\cd.txt"));
            assertEquals("\\a.txt", IOUtil.normalizePath("\\x\\..\\..\\a.txt"));
            assertEquals("\\foo", IOUtil.normalizePath("\\foo\\\\"));
            assertEquals("\\foo", IOUtil.normalizePath("\\foo\\.\\"));
            assertEquals("\\bar", IOUtil.normalizePath("\\foo\\..\\bar"));
            assertEquals("\\bar", IOUtil.normalizePath("\\foo\\..\\bar\\"));
            assertEquals("\\baz", IOUtil.normalizePath("\\foo\\..\\bar\\..\\baz"));
            assertEquals("\\\\foo\\bar\\", IOUtil.normalizePath("\\\\foo\\bar"));
            assertEquals("\\\\foo\\bar\\baz", IOUtil.normalizePath("\\\\foo\\bar\\..\\baz"));
            assertEquals("foo", IOUtil.normalizePath("foo\\bar\\.."));
            assertEquals("bar", IOUtil.normalizePath("foo\\..\\bar"));
            assertEquals("\\foo\\baz", IOUtil.normalizePath("\\foo\\bar\\..\\baz"));
            assertEquals("\\", IOUtil.normalizePath("\\..\\"));
            assertEquals("bar", IOUtil.normalizePath(".\\bar"));

            assertNull(IOUtil.normalizePath("\\\\..\\foo"));
            assertNull(IOUtil.normalizePath("..\\foo"));
            assertNull(IOUtil.normalizePath("foo\\..\\..\\bar"));
            assertNull(IOUtil.normalizePath("."));
            assertNull(IOUtil.normalizePath("\\\\foo\\\\.\\bar"));
            assertNull(IOUtil.normalizePath("\\\\foo\\.\\bar"));

            assertTrue(IOUtil.equalsNormalizedPaths("foo\\..\\bar", "bar\\.\\"));

            assertEquals("C:\\bar", IOUtil.normalizePath("C:\\..\\bar"));
            assertEquals("ab\\cd.txt", IOUtil.normalizePath("ab\\ef\\..\\cd.txt"));
            assertEquals("C:\\ab\\cd.txt", IOUtil.normalizePath("C:\\ab\\ef\\..\\.\\cd.txt"));
            assertNull(IOUtil.normalizePath("..\\foo"));
            assertNull(IOUtil.normalizePath("foo\\..\\..\\bar"));
        }
    }

    @Test
    void testFilenameExtension() {
        assertEquals("txt", IOUtil.getFilenameExtension("ab/cd.txt"));
        assertEquals("txt", IOUtil.getFilenameExtension("ab.cd.txt"));
        assertEquals("", IOUtil.getFilenameExtension("ab/cd"));
        assertEquals("html", IOUtil.getFilenameExtension("cd.html"));
    }

    @Test
    void testFilenameBase() {
        assertEquals("cd", IOUtil.getFilenameBase("ab/cd.txt"));
        assertEquals("ab.cd", IOUtil.getFilenameBase("ab.cd.txt"));
        assertEquals("cd", IOUtil.getFilenameBase("ab/cd"));
    }

    @Test
    void testBomAwareStream() throws IOException {
        assertBomStream("No BOM".getBytes(StandardCharsets.UTF_8), "No BOM", null);
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_8), "BOM", StandardCharsets.UTF_8.name());
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_16LE), "BOM", StandardCharsets.UTF_16LE.name());
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_16BE), "BOM", StandardCharsets.UTF_16BE.name());
    }

    private void assertBomStream(byte[] data, String expectedData, String expectedCharset) throws IOException {
        try (IOUtil.BomAwareInputStream stream = new IOUtil.BomAwareInputStream(new ByteArrayInputStream(data))) {
            if (expectedCharset != null) {
                assertTrue(stream.hasBom());
                assertEquals(expectedCharset, stream.getBomCharsetName());
                assertEquals(expectedData, new String(IOUtil.toByteArray(stream), stream.getBomCharsetName()));

            } else {
                assertFalse(stream.hasBom());
                assertNull(stream.getBomCharsetName());
                assertEquals(expectedData, new String(IOUtil.toByteArray(stream), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    void testOutputStreamFromWriter() throws IOException {
        StringWriter writer = new StringWriter();
        try (OutputStream outputStream = IOUtil.fromWriter(writer, "UTF-8")) {
            outputStream.write("abc".getBytes(StandardCharsets.UTF_8));
        }
        assertEquals("abc", writer.toString());
    }

    @Test
    void testInputStreamFromReader() throws IOException {
        try (InputStream inputStream = IOUtil.fromReader(new StringReader("abc"))) {
            byte[] bytes = IOUtil.toByteArray(inputStream);
            assertEquals("abc", new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @Test
    void testInputStreamFromReader2() throws IOException {
        int size = 8192 + 8192 + 10;
        char[] data = new char[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        data[8192] = 'ä'; // block size border - in UTF-8 these are two bytes. Decoding needs to take the bytes
        // from previous block and new block
        try (InputStream inputStream = IOUtil.fromReader(new StringReader(new String(data)))) {
            byte[] bytes = IOUtil.toByteArray(inputStream);
            assertEquals(new String(data), new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @Test
    void testCopyStream() throws IOException {
        int size = 8192 + 8192 + 10;
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (InputStream stream = new ByteArrayInputStream(data);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtil.copy(stream, out);
            byte[] bytes = out.toByteArray();
            assertEquals(size, bytes.length);
            assertArrayEquals(data, bytes);
        }
    }

    @Test
    void testCopyReader() throws IOException {
        int size = 8192 + 8192 + 10;
        char[] data = new char[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (Reader reader = new CharArrayReader(data);
             StringWriter writer = new StringWriter()) {
            IOUtil.copy(reader, writer);
            char[] chars = writer.toString().toCharArray();
            assertEquals(size, chars.length);
            assertArrayEquals(data, chars);
        }
    }

    @Test
    void testReadEmptyStream() throws IOException {
        try (InputStream in = new ByteArrayInputStream(new byte[0])) {
            byte[] bytes = IOUtil.toByteArray(in);
            assertNotNull(bytes);
            assertEquals(0, bytes.length);
        }
    }

    @Test
    void testCloseQuietly() {
        class Stream extends InputStream {
            private boolean closed = false;

            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public void close() throws IOException {
                closed = true;
                throw new IOException("test");
            }

            public boolean isClosed() {
                return closed;
            }
        }

        Stream stream = new Stream();
        IOUtil.closeQuietly(stream);
        assertTrue(stream.isClosed());
    }

    @Test
    void testReadFileToString() throws IOException {
        String testString = "Test ABC";
        Path tempFile = Files.createTempFile("pmd", ".txt");
        Files.write(tempFile, testString.getBytes(Charset.defaultCharset()));
        assertEquals(testString, IOUtil.readFileToString(tempFile.toFile()));
    }

    @Test
    void testReadToString() throws IOException {
        String testString = "testReadToString";
        Reader reader = new StringReader(testString);
        assertEquals(testString, IOUtil.readToString(reader));
    }

    @Test
    void testReadStreamToString() throws IOException {
        String testString = "testReadStreamToString";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        assertEquals(testString, IOUtil.readToString(stream, StandardCharsets.UTF_8));
    }

    @Test
    void testCreateWriterStdout() throws IOException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(new FilterOutputStream(data) {
            @Override
            public void close() {
                fail("Stream must not be closed");
            }
        });

        try {
            System.setOut(out);
            Writer writer = IOUtil.createWriter();
            writer.write("Test");
            writer.close();
            assertEquals("Test", data.toString());
        } finally {
            System.setOut(originalOut);
        }
    }
}
