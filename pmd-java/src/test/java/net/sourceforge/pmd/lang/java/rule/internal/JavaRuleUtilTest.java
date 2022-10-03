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

import net.sourceforge.pmd.lang.java.BaseParserTest;

class JavaRuleUtilTest extends BaseParserTest {

    @Test
    void testCamelCaseWords() {
        assertFalse(startsWithCamelCaseWord("getter", "get"), "no word boundary");
        assertFalse(startsWithCamelCaseWord("get", "get"), "no following word");
        assertTrue(startsWithCamelCaseWord("getX", "get"), "ok prefix");
        assertFalse(startsWithCamelCaseWord("ge", "get"), "shorter word");

        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord(null, "get"));
        assertThrows(NullPointerException.class, () -> startsWithCamelCaseWord("fnei", null));
    }

    @Test
    void testContainsCamelCaseWords() {

        assertFalse(containsCamelCaseWord("isABoolean", "Bool"), "no word boundary");
        assertTrue(containsCamelCaseWord("isABoolean", "A"), "ok word in the middle");
        assertTrue(containsCamelCaseWord("isABoolean", "Boolean"), "ok word at the end");

        assertThrows(NullPointerException.class, () -> containsCamelCaseWord(null, "A"));
        assertThrows(NullPointerException.class, () -> containsCamelCaseWord("fnei", null));
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", ""), "empty string");
        assertThrows(AssertionError.class, () -> containsCamelCaseWord("fnei", "a"), "not capitalized");
    }

}
