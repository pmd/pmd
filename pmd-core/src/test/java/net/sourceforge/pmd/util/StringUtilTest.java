/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;

class StringUtilTest {

    @Test
    void testColumnNumber() {
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
    void testColumnNumberCrLf() {
        assertEquals(-1, StringUtil.columnNumberAt("f\r\nb", -1));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 0));
        assertEquals(2, StringUtil.columnNumberAt("f\r\nb", 1));
        assertEquals(3, StringUtil.columnNumberAt("f\r\nb", 2));
        assertEquals(1, StringUtil.columnNumberAt("f\r\nb", 3));
        assertEquals(2, StringUtil.columnNumberAt("f\r\nb", 4));
        assertEquals(-1, StringUtil.columnNumberAt("f\r\nb", 5));
    }

    @Test
    void testColumnNumberTrailing() {
        assertEquals(1, StringUtil.columnNumberAt("\n", 0));
        assertEquals(2, StringUtil.columnNumberAt("\n", 1));
        assertEquals(-1, StringUtil.columnNumberAt("\n", 2));
    }

    @Test
    void testColumnNumberEmpty() {
        assertEquals(1, StringUtil.columnNumberAt("", 0));
        assertEquals(-1, StringUtil.columnNumberAt("", 1));
    }

    @Test
    void testRemoveSurrounding() {
        assertThat(StringUtil.removeSurrounding("", 'q'), equalTo(""));
        assertThat(StringUtil.removeSurrounding("q", 'q'), equalTo("q"));
        assertThat(StringUtil.removeSurrounding("qq", 'q'), equalTo(""));
        assertThat(StringUtil.removeSurrounding("qqq", 'q'), equalTo("q"));
    }

    @Test
    void testTrimIndent() {
        assertTrimIndent(" \n b \n c",
                         "\nb\nc");

        assertTrimIndent(" \nb \n c",
                         "\nb\n c");

        assertTrimIndent(" \n b \n c\n  ",
                         "\nb\nc\n");
        assertTrimIndent("", "");
    }

    private void assertTrimIndent(String input, String output) {
        String actual = StringUtil.trimIndent(Chars.wrap(input)).toString();
        assertThat(actual, equalTo(output));
    }

    @Test
    void testElide() {
        assertThat(StringUtil.elide("abc", 2, ""), equalTo("ab"));
        assertThat(StringUtil.elide("abc", 2, "."), equalTo("a."));
        assertThat(StringUtil.elide("abc", 2, ".."), equalTo(".."));
        assertThat(StringUtil.elide("abc", 3, ".."), equalTo("abc"));
    }

    @Test
    void substringAfterLast() {
        assertEquals("abc", StringUtil.substringAfterLast("a.abc", '.'));
        assertEquals("abc", StringUtil.substringAfterLast("abc", '.'));
    }

}
