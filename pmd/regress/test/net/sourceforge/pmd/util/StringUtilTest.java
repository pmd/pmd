/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.util;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.util.StringUtil;

import org.junit.Ignore;
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
        assertEquals("replaceString didn't work with a >", "foobar", StringUtil.replaceString("foobar", "]]>", "]]&gt;"));
    }

    @Test
    public void testReplaceWithNull() {
        assertEquals("replaceString didn't work with a char", "f", StringUtil.replaceString("foo", 'o', null));
    }

    @Ignore
    @Test
    public void testUTF8NotSupported() {
        System.setProperty("net.sourceforge.pmd.supportUTF8","no");
        StringBuffer sb = new StringBuffer();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test);
        assertEquals("&#233;", sb.toString());
    }

    @Ignore
    @Test
    public void testUTF8Supported() {
        System.setProperty("net.sourceforge.pmd.supportUTF8","yes");
        StringBuffer sb = new StringBuffer();
        String test = "é";
        StringUtil.appendXmlEscaped(sb, test);
        assertEquals("é", sb.toString());
        System.setProperty("net.sourceforge.pmd.supportUTF8","no");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringUtilTest.class);
    }
}

