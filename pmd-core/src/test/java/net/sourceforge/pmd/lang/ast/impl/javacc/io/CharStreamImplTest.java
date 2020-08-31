/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextDocument;

public class CharStreamImplTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();
    private LanguageVersion dummyVersion = LanguageRegistry.getDefaultLanguage().getDefaultVersion();

    @Test
    public void testReadZeroChars() throws IOException {

        CharStream stream = simpleCharStream("");

        expect.expect(EOFException.class);

        try {
            stream.readChar();
        } catch (Exception e) {
            assertEquals(stream.getStartOffset(), 0);
            assertEquals(stream.getEndOffset(), 0);
            throw e;
        }
    }

    @Test
    public void testReadEofChars() throws IOException {

        CharStream stream = simpleCharStream("");

        expect.expect(EOFException.class);

        try {
            stream.readChar();
        } catch (Exception e) {
            assertEquals(stream.getStartOffset(), 0);
            assertEquals(stream.getEndOffset(), 0);
            throw e;
        }
    }

    @Test
    public void testMultipleEofReads() throws IOException {

        CharStream stream = simpleCharStream("");

        for (int i = 0; i < 3; i++) {
            try {
                stream.readChar();
                fail();
            } catch (EOFException ignored) {

            }
        }

    }

    @Test
    public void testReadStuff() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        expect.expect(EOFException.class);
        stream.readChar();
    }

    @Test
    public void testReadBacktrack() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.markTokenStart());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        assertEquals("abcd", stream.getTokenImage());

        stream.backup(2);
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());


        expect.expect(EOFException.class);
        stream.readChar();
    }

    @Test
    public void testReadBacktrackWithEscapes() throws IOException {

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

        expect.expect(EOFException.class);
        stream.readChar();
    }

    public CharStream simpleCharStream(String abcd) throws IOException {
        return CharStream.create(new JavaccTokenDocument(TextDocument.readOnlyString(abcd, dummyVersion)));
    }

    public CharStream javaCharStream(String abcd) throws IOException {
        return CharStream.create(new JavaccTokenDocument(TextDocument.readOnlyString(abcd, dummyVersion)) {
            @Override
            public EscapeAwareReader newReader(Chars text) {
                return new JavaEscapeReader(text);
            }
        });
    }

    @Test
    public void testBacktrackTooMuch() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.markTokenStart());
        assertEquals('d', stream.readChar());

        expect.expect(IllegalArgumentException.class);
        stream.backup(10);
    }
    @Test
    public void testBacktrackTooMuch2() throws IOException {

        CharStream stream = simpleCharStream("abcd");

        assertEquals('a', stream.markTokenStart());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        expect.expect(IllegalArgumentException.class);
        stream.backup(10);
    }


}
