/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

class IOUtilTest {

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
