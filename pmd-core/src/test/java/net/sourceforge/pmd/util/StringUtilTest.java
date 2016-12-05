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
