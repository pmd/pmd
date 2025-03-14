/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;

/**
 * @author Clément Fournier
 */
class CollectionUtilTest {

    @Test
    void testJoinOn() {
        testJoinOn(listOf("a", "b", "c"), ".",
                   "a.b.c");
        testJoinOn(Collections.emptyList(), ".",
                   "");
    }

    private void testJoinOn(List<String> toJoin, String delimiter, String expected) {
        String actual = CollectionUtil.joinCharsIntoStringBuilder(
            CollectionUtil.map(toJoin, Chars::wrap),
            delimiter
        ).toString();
        assertEquals(expected, actual);
    }
}
