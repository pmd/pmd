/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 *
 */
class CharsTest {

    @Test
    void wrapStringRoundTrip() {
        String s = "ooo";
        assertSame(s, Chars.wrap(s).toString());
    }

    @Test
    void wrapCharsRoundTrip() {
        Chars s = Chars.wrap("ooo");
        assertSame(s, Chars.wrap(s));
    }

    @Test
    void appendChars() {
        StringBuilder sb = new StringBuilder();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        bc.appendChars(sb);
        assertEquals("bc", sb.toString());
    }

    @Test
    void appendCharsWithOffsets() {
        StringBuilder sb = new StringBuilder();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        bc.appendChars(sb, 0, 1);
        assertEquals("b", sb.toString());
    }

    @Test
    void toStringBuilder() {
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        assertEquals("bc", bc.toStringBuilder().toString());
    }

    @Test
    void write() throws IOException {
        StringWriter writer = new StringWriter();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        bc.write(writer, 0, 1);
        assertEquals("b", writer.toString());
        writer = new StringWriter();
        bc.writeFully(writer);
        assertEquals("bc", writer.toString());
    }

    @Test
    void getChars() {
        char[] arr = new char[4];
        Chars bc = Chars.wrap("abcd").slice(1, 2);

        bc.getChars(0, arr, 1, 2);
        assertArrayEquals(arr, new char[] { 0, 'b', 'c', 0 });

        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(2, arr, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(-1, arr, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(0, arr, 0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(0, arr, 4, 3));
        assertThrows(NullPointerException.class, () -> bc.getChars(0, null, 0, 0));
    }

    @Test
    void indexOf() {
        Chars bc = Chars.wrap("aaaaabcdb").slice(5, 2);
        //                          --
        assertEquals(0, bc.indexOf('b', 0));
        assertEquals(1, bc.indexOf('c', 0));

        assertEquals(-1, bc.indexOf('b', 1));
        assertEquals(-1, bc.indexOf('d', 0));

        assertEquals(-1, bc.indexOf('x', 0));
        assertEquals(-1, bc.indexOf('a', -1));
    }

    @Test
    void indexOfString() {
        Chars bc = Chars.wrap("aaaaabcdb").slice(5, 2);
        //                          --
        assertEquals(0, bc.indexOf("b", 0));
        assertEquals(0, bc.indexOf("bc", 0));
        assertEquals(1, bc.indexOf("c", 0));

        assertEquals(-1, bc.indexOf("b", 1));
        assertEquals(-1, bc.indexOf("bc", 1));
        assertEquals(-1, bc.indexOf("d", 0));
        assertEquals(-1, bc.indexOf("bcd", 0));

        assertEquals(-1, bc.indexOf("x", 0));
        assertEquals(-1, bc.indexOf("ab", -1));

        bc = Chars.wrap("aaaaabcdbxdb").slice(5, 5);
        //                    -----
        assertEquals(3, bc.indexOf("bx", 0));

        bc = Chars.wrap("aaaaabcbxdb").slice(5, 5);
        //                    -----
        assertEquals(2, bc.indexOf("bx", 0));
    }

    @Test
    void lastIndexOf() {
        Chars bc = Chars.wrap("aaaaabcdb").slice(5, 2);
        //                          --
        assertEquals(0, bc.lastIndexOf('b', 0));
        assertEquals(0, bc.lastIndexOf('b', 1));
        assertEquals(1, bc.lastIndexOf('c', 1));
        assertEquals(-1, bc.lastIndexOf('c', 0));

        assertEquals(-1, bc.lastIndexOf('d', 0));

        assertEquals(-1, bc.lastIndexOf('x', 0));
        assertEquals(-1, bc.lastIndexOf('a', -1));
        assertEquals(-1, bc.lastIndexOf('a', 0));
        assertEquals(-1, bc.lastIndexOf('a', 1));
    }

    @Test
    void startsWith() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);

        assertTrue(bc.startsWith("bc"));
        assertTrue(bc.startsWith("bc", 0));
        assertTrue(bc.startsWith("c", 1));
        assertTrue(bc.startsWith('c', 1)); //with a char
        assertTrue(bc.startsWith("", 1));
        assertTrue(bc.startsWith("", 0));


        assertFalse(bc.startsWith("c", 0));
        assertFalse(bc.startsWith('c', 0)); //with a char

        assertFalse(bc.startsWith("bcd", 0));
        assertFalse(bc.startsWith("xcd", 0));

        assertFalse(bc.startsWith("b", -1));
        assertFalse(bc.startsWith('b', -1)); //with a char

        assertFalse(bc.startsWith("", -1));
        assertFalse(bc.startsWith("", 5));

    }

    @Test
    void removeSuffix() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        //                      --

        assertEquals("bc", bc.toString());
        assertEquals("b", bc.removeSuffix("c").toString());
        assertEquals("", bc.removeSuffix("bc").toString());

        bc = Chars.wrap("aaaaaaa").slice(2, 3);
        //                 ---

        assertEquals("", bc.removeSuffix("aaa").toString());
        assertEquals("aaa", bc.removeSuffix("aaaa").toString());
    }

    @Test
    void removePrefix() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        //                      --

        assertEquals("bc", bc.toString());
        assertEquals("bc", bc.removePrefix("c").toString());
        assertEquals("", bc.removePrefix("bc").toString());
        assertEquals("c", bc.removePrefix("b").toString());

        bc = Chars.wrap("aaaaaaa").slice(2, 3);
        //                 ---

        assertEquals("aaa", bc.toString());
        assertEquals("", bc.removePrefix("aaa").toString());
        assertEquals("aaa", bc.removePrefix("aaaa").toString());
    }

    @Test
    void trimNoop() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        assertEquals("bc", bc.toString());
        assertEquals("bc", bc.trimStart().toString());
        assertEquals("bc", bc.trimEnd().toString());
        assertEquals("bc", bc.trim().toString());
    }

    @Test
    void trimStartAndEnd() {
        Chars bc = Chars.wrap("a   bc db").slice(1, 6);
        //                      ------
        assertEquals("   bc ", bc.toString());
        assertEquals("bc ", bc.trimStart().toString());
        assertEquals("   bc", bc.trimEnd().toString());
        assertEquals("bc", bc.trim().toString());
    }

    @Test
    void charAt() {

        Chars bc = Chars.wrap("a   bc db").slice(1, 6);
        //                      ------
        assertEquals(' ', bc.charAt(0));
        assertEquals('b', bc.charAt(3));
        assertEquals('c', bc.charAt(4));
        assertEquals(' ', bc.charAt(5));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.charAt(7));
    }

    @Test
    void linesTest() {

        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------
        List<String> lines = CollectionUtil.map(bc.lines(), Chars::toString);
        assertEquals(listOf(" ", "  ", "bc "), lines);
    }

    @Test
    void linesTest2() {
        Chars bc = Chars.wrap("aa\n");
        List<String> lines = CollectionUtil.map(bc.lines(), Chars::toString);
        assertEquals(listOf("aa"), lines);
    }

    @Test
    void linesStreamTest() {
        Chars bc = Chars.wrap("aa\nb\rded\r\nlff");
        List<String> lines = bc.lineStream().map(Chars::toString).collect(Collectors.toList());
        assertEquals(listOf("aa", "b", "ded", "lff"), lines);
    }

    @Test
    void linesTest3WithCr() {
        Chars bc = Chars.wrap("aa\rb");
        List<String> lines = CollectionUtil.map(bc.lines(), Chars::toString);
        assertEquals(listOf("aa", "b"), lines);
    }

    @Test
    void testEqualsHashCode() {


        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        //                          -----
        assertEquals(Chars.wrap("a_b_c"), chars);
        assertNotEquals("a_b_c", chars);

        assertEquals(Chars.wrap("a_b_c").hashCode(), chars.hashCode());
        assertEquals(chars, chars);

        assertEquals("a_b_c".hashCode(), Chars.wrap("a_b_c").hashCode());
        assertEquals("a_b_c".hashCode(), chars.hashCode());

    }

    @Test
    void testContentEquals() {


        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        //                          -----
        assertTrue(chars.contentEquals("a_b_c"));
        assertTrue(chars.contentEquals(Chars.wrap("a_b_c")));

        assertFalse(chars.contentEquals("a_b_c_--"));
        assertFalse(chars.contentEquals(Chars.wrap("a_b_c_")));
        assertFalse(chars.contentEquals(Chars.wrap("a_b-c")));

        assertTrue(chars.contentEquals(Chars.wrap("A_B_C"), true));
    }

    @Test
    void testSplits() {
        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        assertEquals("a_b_c", chars.toString());

        testSplits(chars, "_");
        testSplits(chars, "a");
        testSplits(chars, "b");
        testSplits(chars, "c");
        assertEquals(listOf("", "_b_c"), listSplits(chars, "a"));

        chars = chars.subSequence(1, 5);
        assertEquals("_b_c", chars.toString());

        assertEquals(listOf("", "b", "c"), listSplits(chars, "_"));


        testSplits(Chars.wrap("abc"), "");
        testSplits(Chars.wrap(""), "");
    }

    private List<String> listSplits(Chars chars, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Iterator<Chars> splits = chars.splits(pattern).iterator();
        return IteratorUtil.toList(IteratorUtil.map(splits, Chars::toString));
    }

    private void testSplits(Chars chars, String regex) {
        List<String> splitList = listSplits(chars, regex);
        List<String> expected = Arrays.asList(chars.toString().split(regex));
        assertEquals(expected, splitList, "Split should behave like String#split");
    }

    @Test
    void testSlice() {
        // slice is offset + length
        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        //                          -----
        assertEquals(Chars.wrap("_b_"), chars.slice(1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.slice(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.slice(0, 6));
    }

    @Test
    void testSubsequence() {
        // subsequence is start + end
        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        //                          -----
        assertEquals(Chars.wrap("_b"), chars.subSequence(1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.slice(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.slice(0, 6));
    }

    @Test
    void testSubstring() {
        // substring is start + end
        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        //                          -----
        assertEquals("_b", chars.substring(1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.substring(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> chars.substring(0, 6));
    }


    @Test
    void testTrimBlankLines() {
        assertTrimBlankLinesEquals(" \n \n abc \n \n de \n \n ",
                                   " abc \n \n de ");
        assertTrimBlankLinesEquals("", "");
    }

    private void assertTrimBlankLinesEquals(String input, String expected) {
        Chars actual = Chars.wrap(input).trimBlankLines();
        assertEquals(Chars.wrap(expected), actual);
    }


    @Test
    void testReaderSingleChars() throws IOException {
        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------

        try (Reader reader = bc.newReader()) {
            assertEquals(' ', reader.read());
            assertEquals('\n', reader.read());
            assertEquals(' ', reader.read());
            assertEquals(' ', reader.read());
            assertEquals('\r', reader.read());
            assertEquals('\n', reader.read());
            assertEquals('b', reader.read());
            assertEquals('c', reader.read());
            assertEquals(' ', reader.read());
            assertEquals(-1, reader.read());
        }
    }

    @Test
    void testReaderBuffer() throws IOException {
        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------

        char[] cbuf = new char[4];

        try (Reader reader = bc.newReader()) {
            assertEquals(4, reader.read(cbuf));
            assertCharBufEquals(" \n  ", cbuf);
            assertEquals(4, reader.read(cbuf));
            assertCharBufEquals("\r\nbc", cbuf);
            assertEquals(1, reader.read(cbuf));
            assertCharBufEquals(" \nbc", cbuf);
            assertEquals(-1, reader.read(cbuf));
        }
    }

    @Test
    void testReaderSlicedBuffer() throws IOException {
        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------

        // use \0 as padding before and after
        char[] cbuf = new char[6];

        try (Reader reader = bc.newReader()) {
            assertEquals(4, reader.read(cbuf, 1, 4));
            assertCharBufEquals("\0 \n  \0", cbuf);
            assertEquals(5, reader.read(cbuf, 1, 5));
            assertCharBufEquals("\0\r\nbc ", cbuf);
            assertEquals(-1, reader.read(cbuf));
            assertEquals(-1, reader.read());
            assertEquals(-1, reader.read(cbuf, 1, 4));
        }
    }

    @Test
    void testReadClosed() throws IOException {
        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------

        Reader reader = bc.newReader();
        reader.close();
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReaderMark() throws IOException {
        Chars bc = Chars.wrap("abcdefghijklmnop").slice(1, 9);
        //                      ------------

        try (Reader reader = bc.newReader()) {
            assertTrue(reader.markSupported(), "markSupported");

            assertEquals('b', reader.read());
            assertEquals('c', reader.read());
            assertEquals('d', reader.read());
            assertEquals('e', reader.read());

            reader.mark(10);

            assertEquals('f', reader.read());
            assertEquals('g', reader.read());

            reader.reset();

            assertEquals('f', reader.read());
            assertEquals('g', reader.read());

            reader.reset(); // reset doesn't clear the mark

            assertEquals('f', reader.read());
            assertEquals('g', reader.read());
        }
    }

    @Test
    void testReaderMissingMark() throws IOException {
        Chars bc = Chars.wrap("abcdefghijklmnop").slice(1, 9);
        //                      ------------

        try (Reader reader = bc.newReader()) {
            assertTrue(reader.markSupported(), "markSupported");

            assertEquals('b', reader.read());
            assertThrows(IOException.class, reader::reset);
        }
    }

    @Test
    void testReaderSkip() throws IOException {
        Chars bc = Chars.wrap("abcdefghijklmnop").slice(1, 9);
        //                      ------------

        try (Reader reader = bc.newReader()) {
            assertEquals('b', reader.read());
            assertEquals('c', reader.read());
            assertEquals('d', reader.read());
            assertEquals('e', reader.read());

            reader.mark(10);
            assertEquals(2, reader.skip(2));

            assertEquals('h', reader.read());
            assertEquals('i', reader.read());

            reader.reset();

            assertEquals('f', reader.read());
            assertEquals('g', reader.read());
        }
    }

    @Test
    void testReaderInvalidParams() throws IOException {
        Chars bc = Chars.wrap("abcdefghijklmnop").slice(1, 9);
        //                      ------------
        char[] cbuf = new char[4];

        try (Reader reader = bc.newReader()) {
            assertTrue(reader.markSupported(), "markSupported");

            assertEquals('b', reader.read());
            assertThrows(NullPointerException.class, () -> reader.read(null, 0, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(cbuf, -1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(cbuf, 1, 12));
            assertThrows(IndexOutOfBoundsException.class, () -> reader.read(cbuf, 1, -1));
        }
    }

    private static void assertCharBufEquals(String expected, char[] cbuf) {
        String actual = new String(cbuf);
        assertEquals(expected, actual);
    }

}
