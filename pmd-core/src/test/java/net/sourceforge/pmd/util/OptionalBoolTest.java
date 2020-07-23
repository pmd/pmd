/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.definitely;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptionalBoolTest {

    @Test
    public void testDefinitely() {
        assertEquals(YES, definitely(true));
        assertEquals(NO, definitely(false));
    }

    @Test
    public void testIsKnow() {
        assertTrue(YES.isKnown());
        assertTrue(NO.isKnown());
        assertFalse(UNKNOWN.isKnown());
    }

    @Test
    public void testIsTrue() {
        assertTrue(YES.isTrue());
        assertFalse(NO.isTrue());
        assertFalse(UNKNOWN.isTrue());
    }
}
