/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseLanguageModuleTest {

    @Test
    public void testHashCodeEquals() {
        Language l1 = new DummyLanguageModule();
        Language l1a = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        Assertions.assertEquals(l1.hashCode(), l1a.hashCode());
        Assertions.assertNotEquals(l1.hashCode(), l2.hashCode());

        Assertions.assertEquals(l1, l1a);
        Assertions.assertNotEquals(l1, l2);
    }

    @Test
    public void testCompareTo() {
        Language l1 = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        Assertions.assertTrue(l1.compareTo(l2) < 0);
    }
}
