/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 *
 */
public class CharsTest {

    @Test
    public void wrapStringRoundTrip() {
        String s = "ooo";
        assertSame(s, Chars.wrap(s).toString());
    }

    @Test
    public void wrapCharsRoundTrip() {
        Chars s = Chars.wrap("ooo");
        assertSame(s, Chars.wrap(s));
    }

    @Test
    public void appendChars() {
        StringBuilder sb = new StringBuilder();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        bc.appendChars(sb);
        assertEquals("bc", sb.toString());
    }

    @Test
    public void appendCharsWithOffsets() {
        StringBuilder sb = new StringBuilder();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        assertEquals("bc", bc.toString());

        bc.appendChars(sb, 0, 1);
        assertEquals("b", sb.toString());
    }

    @Test
    public void write() throws IOException {
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
    public void getChars() {
        char[] arr = new char[4];
        Chars bc = Chars.wrap("abcd").slice(1, 2);

        bc.getChars(0, arr, 1, 2);
        assertArrayEquals(arr, new char[] {0, 'b', 'c', 0});

        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(2, arr, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(-1, arr, 0, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(0, arr, 0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> bc.getChars(0, arr, 4, 3));
        assertThrows(NullPointerException.class, () -> bc.getChars(0, null, 0, 0));
    }

    @Test
    public void indexOf() {
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
    public void indexOfString() {
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
    public void startsWith() {
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
    public void removeSuffix() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        //                      --

        Assert.assertEquals("bc", bc.toString());
        Assert.assertEquals("b", bc.removeSuffix("c").toString());
        Assert.assertEquals("", bc.removeSuffix("bc").toString());

        bc = Chars.wrap("aaaaaaa").slice(2, 3);
        //                 ---

        Assert.assertEquals("", bc.removeSuffix("aaa").toString());
        Assert.assertEquals("aaa", bc.removeSuffix("aaaa").toString());
    }

    @Test
    public void trimNoop() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        assertEquals("bc", bc.toString());
        assertEquals("bc", bc.trimStart().toString());
        assertEquals("bc", bc.trimEnd().toString());
        assertEquals("bc", bc.trim().toString());
    }

    @Test
    public void trimStartAndEnd() {
        Chars bc = Chars.wrap("a   bc db").slice(1, 6);
        //                      ------
        assertEquals("   bc ", bc.toString());
        assertEquals("bc ", bc.trimStart().toString());
        assertEquals("   bc", bc.trimEnd().toString());
        assertEquals("bc", bc.trim().toString());
    }

    @Test
    public void charAt() {

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
    public void linesTest() {

        Chars bc = Chars.wrap("a \n  \r\nbc db").slice(1, 9);
        //                      ------------
        List<String> lines = CollectionUtil.map(bc.lines(), Chars::toString);
        assertEquals(listOf(" ", "  ", "bc "), lines);
    }

    @Test
    public void linesTest2() {
        Chars bc = Chars.wrap("aa\n");
        List<String> lines = CollectionUtil.map(bc.lines(), Chars::toString);
        assertEquals(listOf("aa"), lines);
    }

    @Test
    public void testEqualsHashCode() {


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
    public void testContentEquals() {


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
    public void testSplits() {
        Chars chars = Chars.wrap("a_a_b_c_s").slice(2, 5);
        Assert.assertEquals("a_b_c", chars.toString());

        testSplits(chars, "_");
        testSplits(chars, "a");
        testSplits(chars, "b");
        testSplits(chars, "c");
        Assert.assertEquals(listOf("", "_b_c"), listSplits(chars, "a"));

        chars = chars.subSequence(1, 5);
        Assert.assertEquals("_b_c", chars.toString());

        Assert.assertEquals(listOf("", "b", "c"), listSplits(chars, "_"));


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
        Assert.assertEquals("Split should behave like String#split", expected, splitList);
    }

}
