/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;
import org.junit.Test;

public class LanguageRegistryTest {

    @Test
    public void getDefaultLanguageTest() {
        Language defaultLanguage = LanguageRegistry.getDefaultLanguage();
        Assert.assertNotNull(defaultLanguage);
        // as we don't have java language in this test, we get the first
        // available language now -> DummyLanguage
        Assert.assertSame(DummyLanguageModule.class, defaultLanguage.getClass());
    }
}
