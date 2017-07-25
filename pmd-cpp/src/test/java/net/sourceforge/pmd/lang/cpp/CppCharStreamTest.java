/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class CppCharStreamTest {

    @Test
    public void testContinuationUnix() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testContinuationWindows() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\\r\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testBackup() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\b\\\rc"));
        assertStream(stream, "a\\b\\\rc");
    }

    private void assertStream(CppCharStream stream, String token) throws IOException {
        char c = stream.BeginToken();
        assertEquals(token.charAt(0), c);
        for (int i = 1; i < token.length(); i++) {
            c = stream.readChar();
            assertEquals(token.charAt(i), c);
        }
        assertEquals(token, stream.GetImage());
        assertEquals(token, new String(stream.GetSuffix(token.length())));
    }
}
