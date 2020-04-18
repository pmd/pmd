/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.io.NewCharStream;
import net.sourceforge.pmd.util.document.TextDocument;

public class CharStreamImplTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testReadZeroChars() throws IOException {

        CharStream stream = getCharStream("");

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

        CharStream stream = getCharStream("");

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

        CharStream stream = getCharStream("");

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

        CharStream stream = getCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        expect.expect(EOFException.class);
        stream.readChar();
    }

    @Test
    public void testReadBacktrack() throws IOException {

        CharStream stream = getCharStream("abcd");

        assertEquals('a', stream.BeginToken());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        assertEquals("abcd", stream.GetImage());

        stream.backup(2);
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());


        expect.expect(EOFException.class);
        stream.readChar();
    }

    public CharStream getCharStream(String abcd) throws IOException {
        return NewCharStream.open(new JavaccTokenDocument(TextDocument.readOnlyString(abcd)));
    }

    @Test
    public void testBacktrackTooMuch() throws IOException {

        CharStream stream = getCharStream("abcd");

        assertEquals('a', stream.readChar());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.BeginToken());
        assertEquals('d', stream.readChar());

        expect.expect(IllegalArgumentException.class);
        expect.expectMessage("only 2 are saved");
        stream.backup(10);
    }
    @Test
    public void testBacktrackTooMuch2() throws IOException {

        CharStream stream = getCharStream("abcd");

        assertEquals('a', stream.BeginToken());
        assertEquals('b', stream.readChar());
        assertEquals('c', stream.readChar());
        assertEquals('d', stream.readChar());

        expect.expect(IllegalArgumentException.class);
        expect.expectMessage("only 4 are saved");
        stream.backup(10);
    }


}
