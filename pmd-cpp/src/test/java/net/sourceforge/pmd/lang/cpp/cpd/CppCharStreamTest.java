/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

class CppCharStreamTest {

    public CharStream charStreamFor(String source) {
        CppLanguageModule cpp = CppLanguageModule.getInstance();
        TextDocument textDoc = TextDocument.readOnlyString(source, FileId.UNKNOWN, cpp.getDefaultVersion());
        CppCpdLexer tokenizer = new CppCpdLexer(cpp.newPropertyBundle());
        return tokenizer.newCharStream(textDoc);
    }

    @Test
    void testContinuationUnix() throws IOException {
        CharStream stream = charStreamFor("a\\\nb");
        assertStream(stream, "ab");
    }

    @Test
    void testContinuationWindows() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CharStream stream = charStreamFor("a\\\r\nb");
        assertStream(stream, "ab");
    }

    @Test
    void testBackup() throws IOException {
        // note that the \r is normalized to a \n by the TextFile
        CharStream stream = charStreamFor("a\\b\\qc");
        assertStream(stream, "a\\b\\qc");
    }

    private void assertStream(CharStream stream, String token) throws IOException {
        char c = stream.markTokenStart();
        assertEquals(token.charAt(0), c);
        for (int i = 1; i < token.length(); i++) {
            c = stream.readChar();
            assertEquals(token.charAt(i), c, token + " char at " + i + ": " + token.charAt(i) + " != " + c);
        }
        assertEquals(token, stream.getTokenImage());
        // StringBuilder sb = new StringBuilder();
        // stream.appendSuffix(sb, token.length());
        // assertEquals(token, sb.toString());
    }
}
