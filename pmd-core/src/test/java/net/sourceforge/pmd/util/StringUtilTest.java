/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testReplaceWithOneChar() {
        assertEquals("faa", StringUtil.replaceString("foo", 'o', "a"));
    }

    @Test
    public void testLineNumberSingleLine() {
        assertEquals(1, StringUtil.lineNumberAt("foo", 0));
        assertEquals(1, StringUtil.lineNumberAt("foo", 1));
        assertEquals(1, StringUtil.lineNumberAt("foo", 2));
        assertEquals(1, StringUtil.lineNumberAt("foo", 3));
        assertEquals(-1, StringUtil.lineNumberAt("foo", -1));
    }

    @Test
    public void testLineNumberLf() {
        assertEquals(1, StringUtil.lineNumberAt("foo\na", 3));
        assertEquals(2, StringUtil.lineNumberAt("foo\na", 4));
    }

    @Test
    public void testLineNumberCrLf() {
        assertEquals(1, StringUtil.lineNumberAt("f\r\na", 0));
        // notice both \r and \n are on line 1
        assertEquals(1, StringUtil.lineNumberAt("f\r\na", 1));
        assertEquals(1, StringUtil.lineNumberAt("f\r\na", 2));
        assertEquals(2, StringUtil.lineNumberAt("f\r\na", 3));
        assertEquals(2, StringUtil.lineNumberAt("f\r\na", 4));
        assertEquals(-1, StringUtil.lineNumberAt("f\r\na", 5));
        assertEquals(-1, StringUtil.lineNumberAt("f\r\na", 6));
    }

    @Test
    public void testLineNumberCr() {
        assertEquals(1, StringUtil.lineNumberAt("f\ra\nb", 0));
        assertEquals(1, StringUtil.lineNumberAt("f\ra\nb", 1));
        assertEquals(2, StringUtil.lineNumberAt("f\ra\nb", 2));
        assertEquals(2, StringUtil.lineNumberAt("f\ra\nb", 3));
        assertEquals(3, StringUtil.lineNumberAt("f\ra\nb", 4));
        assertEquals(3, StringUtil.lineNumberAt("f\ra\nb", 5));
    }

    @Test
    public void testColumnNumber() {
        assertEquals(-1, StringUtil.columnNumberAt("f\rah\nb", -1));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 0));
        assertEquals(2, StringUtil.columnNumberAt("f\rah\nb", 1));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 2));
        assertEquals(2, StringUtil.columnNumberAt("f\rah\nb", 3));
        assertEquals(3, StringUtil.columnNumberAt("f\rah\nb", 4));
        assertEquals(1, StringUtil.columnNumberAt("f\rah\nb", 5));
        assertEquals(-1, StringUtil.columnNumberAt("f\rah\nb", 6));
    }

    @Test
    public void testColumnNumberCrLf() {
        assertEquals(-1, StringUtil.columnNumberAt("f\r\nb", -1));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 0));
        assertEquals(2, StringUtil.columnNumberAt("f\r\nb", 1));
        assertEquals(3, StringUtil.columnNumberAt("f\r\nb", 2));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 3));
    }

    @Test
    public void testLineNumberTrailing() {
        assertEquals(1, StringUtil.lineNumberAt("\n", 0));
        assertEquals(2, StringUtil.lineNumberAt("\n", 1));
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

    /**
     * Usually you would set the system property
     * "net.sourceforge.pmd.supportUTF8" to either "no" or "yes", to switch UTF8
     * support.
     *
     * e.g.
     * <code>System.setProperty("net.sourceforge.pmd.supportUTF8","yes");</code>
     */
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringUtilTest.class);
    }
}
