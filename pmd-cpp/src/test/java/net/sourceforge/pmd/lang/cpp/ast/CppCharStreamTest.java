/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.lang.document.CpdCompat;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;

public class CppCharStreamTest {

    private @NonNull CppCharStream newCharStream(String code) {
        TextDocument tf = TextDocument.readOnlyString(code, TextFile.UNKNOWN_FILENAME, CpdCompat.dummyVersion());
        return CppCharStream.newCppCharStream(tf);
    }

    @Test
    public void testContinuationUnix() throws IOException {
        CppCharStream stream = newCharStream("a\\\nb");
        assertStream(stream, "ab");
    }

    @Test
    public void testContinuationWindows() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CppCharStream stream = newCharStream("a\\\r\nb");
        assertStream(stream, "ab");
    }

    @Test
    public void testBackup() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CppCharStream stream = newCharStream("a\\b\\qc");
        assertStream(stream, "a\\b\\qc");
    }

    private void assertStream(CppCharStream stream, String token) throws IOException {
        char c = stream.BeginToken();
        assertEquals(token.charAt(0), c);
        for (int i = 1; i < token.length(); i++) {
            c = stream.readChar();
            assertEquals(token + " char at " + i + ": " + token.charAt(i) + " != " + c, token.charAt(i), c);
        }
        assertEquals(token, stream.GetImage());
        assertEquals(token, new String(stream.GetSuffix(token.length())));
    }
}
