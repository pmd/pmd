/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BaseLanguageModuleTest {

    @Test
    void testHashCodeEquals() {
        Language l1 = new DummyLanguageModule();
        Language l1a = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        assertEquals(l1.hashCode(), l1a.hashCode());
        assertNotEquals(l1.hashCode(), l2.hashCode());

        assertEquals(l1, l1a);
        assertNotEquals(l1, l2);
    }

    @Test
    void testCompareTo() {
        Language l1 = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        assertTrue(l1.compareTo(l2) < 0);
    }
}
