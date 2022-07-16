/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.definitely;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OptionalBoolTest {

    @Test
    void testDefinitely() {
        assertEquals(YES, definitely(true));
        assertEquals(NO, definitely(false));
    }

    @Test
    void testIsKnown() {
        assertTrue(YES.isKnown());
        assertTrue(NO.isKnown());
        assertFalse(UNKNOWN.isKnown());
    }

    @Test
    void testIsTrue() {
        assertTrue(YES.isTrue());
        assertFalse(NO.isTrue());
        assertFalse(UNKNOWN.isTrue());
    }

    @Test
    void testComplement() {
        assertEquals(YES, NO.complement());
        assertEquals(NO, YES.complement());
        assertEquals(UNKNOWN, UNKNOWN.complement());
    }
}
