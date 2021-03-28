/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.startsWithCamelCaseWord;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JavaRuleUtilTest {

    @Test
    public void testCamelCaseWords() {
        assertFalse(startsWithCamelCaseWord("getter", "get"));
        assertFalse(startsWithCamelCaseWord("get", "get"));
        assertTrue(startsWithCamelCaseWord("getX", "get"));
    }

}
