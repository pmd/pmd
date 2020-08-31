/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class CharsTest {

    @Test
    public void wrapStringRoundTrip() {
        String s = "ooo";
        Assert.assertSame(s, Chars.wrap(s).toString());
    }

    @Test
    public void wrapCharsRoundTrip() {
        Chars s = Chars.wrap("ooo");
        Assert.assertSame(s, Chars.wrap(s));
    }

    @Test
    public void appendChars() {
        StringBuilder sb = new StringBuilder();
        Chars bc = Chars.wrap("abcd").slice(1, 2);
        Assert.assertEquals("bc", bc.toString());

        bc.appendChars(sb);
        Assert.assertEquals("bc", sb.toString());
    }

    @Test
    public void indexOf() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        Assert.assertEquals(0, bc.indexOf('b', 0));
        Assert.assertEquals(1, bc.indexOf('c', 0));

        Assert.assertEquals(-1, bc.indexOf('b', 1));
        Assert.assertEquals(-1, bc.indexOf('d', 0));
    }

    @Test
    public void startsWith() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);

        Assert.assertTrue(bc.startsWith("bc"));
        Assert.assertTrue(bc.startsWith("bc", 0));
        Assert.assertTrue(bc.startsWith("c", 1));
        Assert.assertTrue(bc.startsWith("", 1));
        Assert.assertTrue(bc.startsWith("", 0));


        Assert.assertFalse(bc.startsWith("c", 0));
        Assert.assertFalse(bc.startsWith("bcd", 0));
        Assert.assertFalse(bc.startsWith("b", -1));
        Assert.assertFalse(bc.startsWith("", -1));
        Assert.assertFalse(bc.startsWith("", 5));

    }

    @Test
    public void trimNoop() {
        Chars bc = Chars.wrap("abcdb").slice(1, 2);
        Assert.assertEquals("bc", bc.toString());
        Assert.assertEquals("bc", bc.trimStart().toString());
        Assert.assertEquals("bc", bc.trimEnd().toString());
        Assert.assertEquals("bc", bc.trim().toString());
    }

    @Test
    public void trimStartAndEnd() {
        Chars bc = Chars.wrap("a   bc db").slice(1, 6);
        Assert.assertEquals("   bc ", bc.toString());
        Assert.assertEquals("bc ", bc.trimStart().toString());
        Assert.assertEquals("   bc", bc.trimEnd().toString());
        Assert.assertEquals("bc", bc.trim().toString());
    }

    @Test
    public void charAt() {

        Chars bc = Chars.wrap("a   bc db").slice(1, 6);
        //                      ------
        Assert.assertEquals(' ', bc.charAt(0));
        Assert.assertEquals('b', bc.charAt(3));
        Assert.assertEquals('c', bc.charAt(4));
        Assert.assertEquals(' ', bc.charAt(5));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> bc.charAt(-1));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> bc.charAt(7));
    }

}
