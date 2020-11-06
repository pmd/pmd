/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testColumnNumber() {
        assertEquals(-1, StringUtil.columnNumberAt("f\rah\nb", -1));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 0));
        assertEquals(2, StringUtil.columnNumberAt("f\rah\nb", 1));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 2));
        assertEquals(2, StringUtil.columnNumberAt("f\rah\nb", 3));
        assertEquals(3, StringUtil.columnNumberAt("f\rah\nb", 4));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 5));
        assertEquals(2, StringUtil.columnNumberAt("f\rah\nb", 6));
        assertEquals(-1, StringUtil.columnNumberAt("f\rah\nb", 7));
    }

    @Test
    public void testColumnNumberCrLf() {
        assertEquals(-1, StringUtil.columnNumberAt("f\r\nb", -1));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 0));
        assertEquals(2, StringUtil.columnNumberAt("f\r\nb", 1));
        assertEquals(3, StringUtil.columnNumberAt("f\r\nb", 2));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 3));
        assertEquals(2, StringUtil.columnNumberAt("f\r\nb", 4));
        assertEquals(-1, StringUtil.columnNumberAt("f\r\nb", 5));
    }

    @Test
    public void testColumnNumberTrailing() {
        assertEquals(1, StringUtil.columnNumberAt("\n", 0));
        assertEquals(2, StringUtil.columnNumberAt("\n", 1));
        assertEquals(-1, StringUtil.columnNumberAt("\n", 2));
    }

    @Test
    public void testColumnNumberEmpty() {
        assertEquals(1, StringUtil.columnNumberAt("", 0));
        assertEquals(-1, StringUtil.columnNumberAt("", 1));
    }

    @Test
    public void testReplaceWithOneChar() {
        assertEquals("faa", StringUtil.replaceString("foo", 'o', "a"));
    }

    @Test
    public void testReplaceWithMultipleChars() {
        assertEquals("faaaa", StringUtil.replaceString("foo", 'o', "aa"));
    }

    @Test
    public void testReplaceStringWithString() {
        assertEquals("foo]]&gt;bar", StringUtil.replaceString("foo]]>bar", "]]>", "]]&gt;"));
    }

    @Test
    public void testReplaceStringWithString2() {
        assertEquals("replaceString didn't work with a >", "foobar",
                StringUtil.replaceString("foobar", "]]>", "]]&gt;"));
    }

    @Test
    public void testReplaceWithNull() {
        assertEquals("replaceString didn't work with a char", "f", StringUtil.replaceString("foo", 'o', null));
    }

    @Test
    public void testUTF8NotSupported() {
        StringBuilder sb = new StringBuilder();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test, false);
        assertEquals("&#xe9;", sb.toString());
    }

    @Test
    public void testUTF8NotSupportedSurrogates() {
        // D8 34 DD 1E -> U+1D11E
        StringBuilder sb = new StringBuilder();
        String test = new String(new char[] {0xd834, 0xdd1e});
        StringUtil.appendXmlEscaped(sb, test, false);
        assertEquals("&#x1d11e;", sb.toString());
    }

    @Test
    public void testUTF8Supported() {
        StringBuilder sb = new StringBuilder();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test, true);
        assertEquals("é", sb.toString());
    }
}
