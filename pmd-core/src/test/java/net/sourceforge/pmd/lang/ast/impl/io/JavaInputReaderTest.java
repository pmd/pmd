/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.util.document.Chars;


public class JavaInputReaderTest {

    @NonNull
    public JavaInputReader readString(String input) {
        return new JavaInputReader(Chars.wrap(input, true));
    }


    @Test
    public void testSimpleRead() throws IOException {

        String input = "abcdede";
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, chars.length);

            Assert.assertEquals("Should have read the entire text", input.length(), read);
            Assert.assertEquals(input, new String(chars, 0, input.length()));
        }
    }

    @Test
    public void testNotAnEscape1Read() throws IOException {

        String input = "abc\\dede";
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, chars.length);

            Assert.assertEquals("Should have read the entire text", input.length(), read);
            Assert.assertEquals(input, new String(chars, 0, input.length()));
        }
    }

    @Test
    public void testNotAnEscape1Read2() throws IOException {

        String input = "abc\\\\\\dede";
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, chars.length);

            Assert.assertEquals("Should have read the entire text", input.length(), read);
            Assert.assertEquals(input, new String(chars, 0, input.length()));
        }
    }

    @Test
    public void testNotAnEscape1Read3_SplitInTheMiddleOfBackslashes() throws IOException {

        String input = "abc\\\\\\dede";
        //                   ^
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, 4);

            Assert.assertEquals("Should have read just enough", 4, read);
            assertBufferIsJust("abc\\", chars, 0);

            read = r.read(chars, 4, 1);

            Assert.assertEquals(1, read);
            assertBufferIsJust("abc\\\\", chars, 0);

            read = r.read(chars, 5, 8);

            Assert.assertEquals(5, read);
            assertBufferIsJust("abc\\\\\\dede", chars, 0);
        }
    }

    @Test
    public void testAnEscapeStopAtEnd() throws IOException {

        String input = "abc\\\\\\u00a0dede";
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, 4);

            Assert.assertEquals(4, read);
            assertBufferIsJust("abc\u00a0", chars, 0);

            read = r.read(chars, 4, 2);

            Assert.assertEquals(2, read);
            assertBufferIsJust("abc\u00a0de", chars, 0);
        }
    }

    @Test
    public void testAnEscapeInsideBlock() throws IOException {

        String input = "abc\\\\\\u00a0dede\\u00a0";
        try (JavaInputReader r = readString(input)) {

            char[] chars = new char[12];

            int read = r.read(chars, 0, 12);

            Assert.assertEquals(9, read);
            assertBufferIsJust("abc\u00a0dede\u00a0", chars, 0);

            read = r.read(chars, 9, 10);

            Assert.assertEquals(-1, read);
            assertBufferIsJust("abc\u00a0dede\u00a0", chars, 0);
        }
    }

    private static void assertBufferIsJust(String contents, char[] chars, int off) {
        // asserts the rest of the buffer is null characters
        char[] chars2 = new char[chars.length];
        contents.getChars(0, contents.length(), chars2, off);
        Assert.assertArrayEquals(chars2, chars);
    }


}