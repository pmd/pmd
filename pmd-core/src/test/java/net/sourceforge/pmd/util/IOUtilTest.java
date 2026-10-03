/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;

class IOUtilTest {

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
        IOUtils.closeQuietly(stream);
        assertTrue(stream.isClosed());
    }

    @Test
    void testReadStreamToString() throws IOException {
        String testString = "testReadStreamToString";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        assertEquals(testString, IOUtils.toString(stream, StandardCharsets.UTF_8));
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
