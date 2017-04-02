/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;
import org.junit.Test;

public class BaseLanguageModuleTest {

    @Test
    public void testHashCodeEquals() {
        Language l1 = new DummyLanguageModule();
        Language l1a = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        Assert.assertEquals(l1.hashCode(), l1a.hashCode());
        Assert.assertNotEquals(l1.hashCode(), l2.hashCode());

        Assert.assertEquals(l1, l1a);
        Assert.assertNotEquals(l1, l2);
    }

    @Test
    public void testCompareTo() {
        Language l1 = new DummyLanguageModule();
        Language l2 = new Dummy2LanguageModule();

        Assert.assertTrue(l1.compareTo(l2) < 0);
    }
}
