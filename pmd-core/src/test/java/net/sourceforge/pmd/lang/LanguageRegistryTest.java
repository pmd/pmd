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

    @Test
    public void getDefaultVersionLanguageTest() {
        LanguageVersion dummy12 = LanguageRegistry.findLanguageVersionByTerseName("dummy 1.2");
        Assert.assertNotNull(dummy12);

        Language dummy = LanguageRegistry.findLanguageByTerseName("dummy");
        LanguageVersion dummyDefault = dummy.getDefaultVersion();

        LanguageVersion dummyDefault2 = LanguageRegistry.findLanguageVersionByTerseName("dummy ");
        Assert.assertNotNull(dummyDefault2);
        Assert.assertSame(dummyDefault, dummyDefault2);
    }
}
