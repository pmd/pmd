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

}
