/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.containsCamelCaseWord;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.startsWithCamelCaseWord;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JavaRuleUtilTest {

    @Test
    public void testCamelCaseWords() {
        assertFalse(startsWithCamelCaseWord("getter", "get"), "no word boundary");
        assertFalse(startsWithCamelCaseWord("get", "get"), "no following word");
        assertTrue(startsWithCamelCaseWord("getX", "get"), "ok prefix");
        assertFalse(startsWithCamelCaseWord("ge", "get"), "shorter word");

        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord("fnei", null));
    }

    @Test
    public void testContainsCamelCaseWords() {

        assertFalse(containsCamelCaseWord("isABoolean", "Bool"), "no word boundary");
        assertTrue(containsCamelCaseWord("isABoolean", "A"), "ok word");
        assertTrue(containsCamelCaseWord("isABoolean", "Boolean"), "ok word");

        assertThrows(NullPointerException.class, () -> containsCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> containsCamelCaseWord("fnei", null));
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", ""), "empty string");
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", "a"), "not capitalized");
    }

}
