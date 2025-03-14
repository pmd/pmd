/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.document.TextDocument;

class CharStreamTest {

    private LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();

    @Test
    void testReadZeroChars() throws IOException {

        CharStream stream = simpleCharStream("");

        assertThrows(EOFException.class, stream::readChar);

        assertEquals(stream.getStartOffset(), 0);
        assertEquals(stream.getEndOffset(), 0);
    }

    @Test
    void testMultipleEofReads() throws IOException {

        CharStream stream = simpleCharStream("");

        for (int i = 0; i < 3; i++) {
            assertThrows(EOFException.class, stream::readChar);
        }

    }

    @Test
    void testReadStuff() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        assertThrows(EOFException.class, stream::readChar);
    }

    @Test
    void testReadBacktrack() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.markTokenStart());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        assertEquals("abcd", stream.getTokenImage());

        stream.backup(2);
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());


        assertThrows(EOFException.class, stream::readChar);
    }

    @Test
    void testReadBacktrackWithEscapes() throws IOException {

        CharStream stream = javaCharStream("__\\u00a0_\\u00a0_");

        assertEquals('_', stream.markTokenStart());
        assertEquals('_', stream.readChar());
        assertEquals('\u00a0', stream.readChar());
        assertEquals('_', stream.readChar());

        assertEquals("__\u00a0_", stream.getTokenImage());

        stream.backup(2);
        assertEquals('\u00a0', stream.readChar());
        assertEquals('_', stream.readChar());
        assertEquals('\u00a0', stream.readChar());

        assertEquals("__\u00a0_\u00a0", stream.getTokenImage());
        assertEquals('_', stream.readChar());
        stream.backup(2);
        assertEquals('\u00a0', stream.markTokenStart());
        assertEquals('_', stream.readChar());

        assertEquals("\u00a0_", stream.getTokenImage());

        assertThrows(EOFException.class, stream::readChar);
    }

    @Test
    void testBacktrackTooMuch() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.markTokenStart());
        assertEquals('d', stream.readChar());

        stream.backup(2); // ok

        assertThrows(IllegalArgumentException.class, () -> stream.backup(1));
    }

    @Test
    void testBacktrackTooMuch2() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.markTokenStart());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        assertThrows(IllegalArgumentException.class, () -> stream.backup(10));
    }


    CharStream simpleCharStream(String abcd) {
        return CharStream.create(TextDocument.readOnlyString(abcd, dummyVersion), TokenDocumentBehavior.DEFAULT);
    }

    CharStream javaCharStream(String abcd) {
        return CharStream.create(
            TextDocument.readOnlyString(abcd, dummyVersion),
            new TokenDocumentBehavior(Collections.emptyList()) {
                @Override
                public TextDocument translate(TextDocument text) throws MalformedSourceException {
                    return new JavaEscapeTranslator(text).translateDocument();
                }
            });
    }

}
