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

    @Test
    public void getLanguageVersionByNameAliasTest() {
        LanguageVersion dummy17 = LanguageRegistry.findLanguageVersionByTerseName("dummy 1.7");
        Assert.assertNotNull(dummy17);
        Assert.assertEquals("1.7", dummy17.getVersion());

        LanguageVersion dummy7 = LanguageRegistry.findLanguageVersionByTerseName("dummy 7");
        Assert.assertNotNull(dummy7);
        Assert.assertEquals("1.7", dummy17.getVersion());
        Assert.assertSame(dummy17, dummy7);
    }

    @Test
    public void getLanguageVersionByAliasTest() {
        Language dummy = LanguageRegistry.findLanguageByTerseName("dummy");

        LanguageVersion dummy17 = dummy.getVersion("1.7");
        Assert.assertNotNull(dummy17);
        Assert.assertEquals("1.7", dummy17.getVersion());

        LanguageVersion dummy7 = dummy.getVersion("7");
        Assert.assertNotNull(dummy7);
        Assert.assertEquals("1.7", dummy17.getVersion());
        Assert.assertSame(dummy17, dummy7);
    }
}
