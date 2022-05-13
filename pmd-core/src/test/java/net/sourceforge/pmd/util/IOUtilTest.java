/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class IOUtilTest {

    @Test
    public void testReadAllBytes() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            byte[] bytes = IOUtil.toByteArray(stream);
            Assert.assertEquals(5, bytes.length);
            Assert.assertArrayEquals(data, bytes);
        }
    }

    @Test
    public void testToByteArrayResize() throws IOException {
        int size = 8192 + 8192 + 10;
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (InputStream stream = new ByteArrayInputStream(data)) {
            byte[] bytes = IOUtil.toByteArray(stream);
            Assert.assertEquals(size, bytes.length);
            Assert.assertArrayEquals(data, bytes);
        }
    }

    @Test
    public void testSkipFully() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            Assert.assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
                @Override
                public void run() throws Throwable {
                    IOUtil.skipFully(stream, -1);
                }
            });

            Assert.assertEquals(3, IOUtil.skipFully(stream, 3));
            byte[] bytes = IOUtil.toByteArray(stream);
            Assert.assertEquals(2, bytes.length);
            Assert.assertArrayEquals("45".getBytes(StandardCharsets.UTF_8), bytes);
        }
    }

    @Test
    public void testSkipFully2() throws IOException {
        byte[] data = "12345".getBytes(StandardCharsets.UTF_8);
        try (InputStream stream = new ByteArrayInputStream(data)) {
            // skip more bytes than the stream contains
            Assert.assertEquals(data.length, IOUtil.skipFully(stream, data.length + 1));
            byte[] bytes = IOUtil.toByteArray(stream);
            Assert.assertEquals(0, bytes.length);
        }
    }

    @Test
    public void testNormalizePath() {
        if (SystemUtils.IS_OS_UNIX) {
            Assert.assertEquals("ab/cd.txt", IOUtil.normalizePath("ab/ef/../cd.txt"));
            Assert.assertEquals("/a.txt", IOUtil.normalizePath("/x/../../a.txt"));
            Assert.assertEquals("/foo", IOUtil.normalizePath("//../foo"));
            Assert.assertEquals("/foo", IOUtil.normalizePath("/foo//"));
            Assert.assertEquals("/foo", IOUtil.normalizePath("/foo/./"));
            Assert.assertEquals("/bar", IOUtil.normalizePath("/foo/../bar"));
            Assert.assertEquals("/bar", IOUtil.normalizePath("/foo/../bar/"));
            Assert.assertEquals("/baz", IOUtil.normalizePath("/foo/../bar/../baz"));
            Assert.assertEquals("/foo/bar", IOUtil.normalizePath("//foo//./bar"));
            Assert.assertEquals("foo", IOUtil.normalizePath("foo/bar/.."));
            Assert.assertEquals("bar", IOUtil.normalizePath("foo/../bar"));
            Assert.assertEquals("/foo/baz", IOUtil.normalizePath("//foo/bar/../baz"));
            Assert.assertEquals("~/bar", IOUtil.normalizePath("~/foo/../bar/"));
            Assert.assertEquals("/", IOUtil.normalizePath("/../"));
            Assert.assertEquals("bar", IOUtil.normalizePath("~/../bar"));
            Assert.assertEquals("bar", IOUtil.normalizePath("./bar"));

            Assert.assertNull(IOUtil.normalizePath("../foo"));
            Assert.assertNull(IOUtil.normalizePath("foo/../../bar"));
            Assert.assertNull(IOUtil.normalizePath("."));

            Assert.assertTrue(IOUtil.equalsNormalizedPaths("foo/../bar", "bar/./"));
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.assertEquals("ab\\cd.txt", IOUtil.normalizePath("ab\\ef\\..\\cd.txt"));
            Assert.assertEquals("\\a.txt", IOUtil.normalizePath("\\x\\..\\..\\a.txt"));
            Assert.assertEquals("\\foo", IOUtil.normalizePath("\\foo\\\\"));
            Assert.assertEquals("\\foo", IOUtil.normalizePath("\\foo\\.\\"));
            Assert.assertEquals("\\bar", IOUtil.normalizePath("\\foo\\..\\bar"));
            Assert.assertEquals("\\bar", IOUtil.normalizePath("\\foo\\..\\bar\\"));
            Assert.assertEquals("\\baz", IOUtil.normalizePath("\\foo\\..\\bar\\..\\baz"));
            Assert.assertEquals("\\\\foo\\bar\\", IOUtil.normalizePath("\\\\foo\\bar"));
            Assert.assertEquals("\\\\foo\\bar\\baz", IOUtil.normalizePath("\\\\foo\\bar\\..\\baz"));
            Assert.assertEquals("foo", IOUtil.normalizePath("foo\\bar\\.."));
            Assert.assertEquals("bar", IOUtil.normalizePath("foo\\..\\bar"));
            Assert.assertEquals("\\foo\\baz", IOUtil.normalizePath("\\foo\\bar\\..\\baz"));
            Assert.assertEquals("\\", IOUtil.normalizePath("\\..\\"));
            Assert.assertEquals("bar", IOUtil.normalizePath(".\\bar"));

            Assert.assertNull(IOUtil.normalizePath("\\\\..\\foo"));
            Assert.assertNull(IOUtil.normalizePath("..\\foo"));
            Assert.assertNull(IOUtil.normalizePath("foo\\..\\..\\bar"));
            Assert.assertNull(IOUtil.normalizePath("."));
            Assert.assertNull(IOUtil.normalizePath("\\\\foo\\\\.\\bar"));
            Assert.assertNull(IOUtil.normalizePath("\\\\foo\\.\\bar"));

            Assert.assertTrue(IOUtil.equalsNormalizedPaths("foo\\..\\bar", "bar\\.\\"));

            Assert.assertEquals("C:\\bar", IOUtil.normalizePath("C:\\..\\bar"));
            Assert.assertEquals("ab\\cd.txt", IOUtil.normalizePath("ab\\ef\\..\\cd.txt"));
            Assert.assertEquals("C:\\ab\\cd.txt", IOUtil.normalizePath("C:\\ab\\ef\\..\\.\\cd.txt"));
            Assert.assertNull(IOUtil.normalizePath("..\\foo"));
            Assert.assertNull(IOUtil.normalizePath("foo\\..\\..\\bar"));
        }
    }

    @Test
    public void testFilenameExtension() {
        Assert.assertEquals("txt", IOUtil.getFilenameExtension("ab/cd.txt"));
        Assert.assertEquals("txt", IOUtil.getFilenameExtension("ab.cd.txt"));
        Assert.assertEquals("", IOUtil.getFilenameExtension("ab/cd"));
        Assert.assertEquals("html", IOUtil.getFilenameExtension("cd.html"));
    }

    @Test
    public void testFilenameBase() {
        Assert.assertEquals("cd", IOUtil.getFilenameBase("ab/cd.txt"));
        Assert.assertEquals("ab.cd", IOUtil.getFilenameBase("ab.cd.txt"));
        Assert.assertEquals("cd", IOUtil.getFilenameBase("ab/cd"));
    }

    @Test
    public void testBomAwareStream() throws IOException {
        assertBomStream("No BOM".getBytes(StandardCharsets.UTF_8), "No BOM", null);
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_8), "BOM", StandardCharsets.UTF_8.name());
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_16LE), "BOM", StandardCharsets.UTF_16LE.name());
        assertBomStream("\ufeffBOM".getBytes(StandardCharsets.UTF_16BE), "BOM", StandardCharsets.UTF_16BE.name());
    }

    private void assertBomStream(byte[] data, String expectedData, String expectedCharset) throws IOException {
        try (IOUtil.BomAwareInputStream stream = new IOUtil.BomAwareInputStream(new ByteArrayInputStream(data))) {
            if (expectedCharset != null) {
                Assert.assertTrue(stream.hasBom());
                Assert.assertEquals(expectedCharset, stream.getBomCharsetName());
                Assert.assertEquals(expectedData, new String(IOUtil.toByteArray(stream), stream.getBomCharsetName()));

            } else {
                Assert.assertFalse(stream.hasBom());
                Assert.assertNull(stream.getBomCharsetName());
                Assert.assertEquals(expectedData, new String(IOUtil.toByteArray(stream), StandardCharsets.UTF_8));
            }
        }
    }

    @Test
    public void testOutputStreamFromWriter() throws IOException {
        StringWriter writer = new StringWriter();
        try (OutputStream outputStream = IOUtil.fromWriter(writer, "UTF-8")) {
            outputStream.write("abc".getBytes(StandardCharsets.UTF_8));
        }
        Assert.assertEquals("abc", writer.toString());
    }

    @Test
    public void testInputStreamFromReader() throws IOException {
        try (InputStream inputStream = IOUtil.fromReader(new StringReader("abc"))) {
            byte[] bytes = IOUtil.toByteArray(inputStream);
            Assert.assertEquals("abc", new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @Test
    public void testInputStreamFromReader2() throws IOException {
        int size = 8192 + 8192 + 10;
        char[] data = new char[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        data[8192] = 'ä'; // block size border - in UTF-8 these are two bytes. Decoding needs to take the bytes
        // from previous block and new block
        try (InputStream inputStream = IOUtil.fromReader(new StringReader(new String(data)))) {
            byte[] bytes = IOUtil.toByteArray(inputStream);
            Assert.assertEquals(new String(data), new String(bytes, StandardCharsets.UTF_8));
        }
    }

    @Test
    public void testCopyStream() throws IOException {
        int size = 8192 + 8192 + 10;
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (InputStream stream = new ByteArrayInputStream(data);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtil.copy(stream, out);
            byte[] bytes = out.toByteArray();
            Assert.assertEquals(size, bytes.length);
            Assert.assertArrayEquals(data, bytes);
        }
    }

    @Test
    public void testCopyReader() throws IOException {
        int size = 8192 + 8192 + 10;
        char[] data = new char[size];
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }
        try (Reader reader = new CharArrayReader(data);
             StringWriter writer = new StringWriter()) {
            IOUtil.copy(reader, writer);
            char[] chars = writer.toString().toCharArray();
            Assert.assertEquals(size, chars.length);
            Assert.assertArrayEquals(data, chars);
        }
    }

    @Test
    public void testReadEmptyStream() throws IOException {
        try (InputStream in = new ByteArrayInputStream(new byte[0])) {
            byte[] bytes = IOUtil.toByteArray(in);
            Assert.assertNotNull(bytes);
            Assert.assertEquals(0, bytes.length);
        }
    }

    @Test
    public void testCloseQuietly() {
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
        Assert.assertTrue(stream.isClosed());
    }

    @Test
    public void testReadFileToString() throws IOException {
        String testString = "Test ABC";
        Path tempFile = Files.createTempFile("pmd", ".txt");
        Files.write(tempFile, testString.getBytes(Charset.defaultCharset()));
        Assert.assertEquals(testString, IOUtil.readFileToString(tempFile.toFile()));
    }

    @Test
    public void testReadToString() throws IOException {
        String testString = "testReadToString";
        Reader reader = new StringReader(testString);
        Assert.assertEquals(testString, IOUtil.readToString(reader));
    }

    @Test
    public void testReadStreamToString() throws IOException {
        String testString = "testReadStreamToString";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(testString, IOUtil.readToString(stream, StandardCharsets.UTF_8));
    }
}
