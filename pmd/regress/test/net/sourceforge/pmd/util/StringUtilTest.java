/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.util;

import junit.framework.TestCase;
import net.sourceforge.pmd.util.StringUtil;

public class StringUtilTest extends TestCase {

    public void testReplaceWithOneChar() {
        assertEquals("faa", StringUtil.replaceString("foo", 'o', "a"));
    }

    public void testReplaceWithMultipleChars() {
        assertEquals("faaaa", StringUtil.replaceString("foo", 'o', "aa"));
    }

    public void testReplaceStringWithString() {
        assertEquals("foo]]&gt;bar", StringUtil.replaceString("foo]]>bar", "]]>", "]]&gt;"));
    }

    public void testReplaceStringWithString2() {
        assertEquals("replaceString didn't work with a >", "foobar", StringUtil.replaceString("foobar", "]]>", "]]&gt;"));
    }

    public void testReplaceWithNull() {
        assertEquals("replaceString didn't work with a char", "f", StringUtil.replaceString("foo", 'o', null));
    }

/*
    // FIXME
    public void testUTF8NotSupported() {
        System.setProperty("net.sourceforge.pmd.supportUTF8","no");
        StringBuffer sb = new StringBuffer();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test);
        assertEquals("&#233;", sb.toString());
    }
    public void testUTF8Supported() {
        System.setProperty("net.sourceforge.pmd.supportUTF8","yes");
        StringBuffer sb = new StringBuffer();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test);
        assertEquals("é", sb.toString());
        System.setProperty("net.sourceforge.pmd.supportUTF8","no");
    }
*/
}
