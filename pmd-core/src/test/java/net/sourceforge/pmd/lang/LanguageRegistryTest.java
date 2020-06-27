/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PmdContextualizedTest;

public class LanguageRegistryTest extends PmdContextualizedTest {

    @Test
    public void getDefaultLanguageTest() {
        Language defaultLanguage = languageRegistry().getDefaultLanguage();
        Assert.assertNotNull(defaultLanguage);
        // as we don't have java language in this test, we get the first
        // available language now -> DummyLanguage
        Assert.assertSame(DummyLanguageModule.class, defaultLanguage.getClass());
    }

    @Test
    public void getDefaultVersionLanguageTest() {
        Language dummy = languageRegistry().findLanguageByTerseName("dummy");
        LanguageVersion dummy12 = dummy.getVersion("1.2");
        Assert.assertNotNull(dummy12);

        LanguageVersion dummyDefault = dummy.getDefaultVersion();
        Assert.assertNotNull(dummyDefault);

        Assert.assertNotSame(dummy12, dummyDefault);
    }

    @Test
    public void getLanguageVersionByAliasTest() {
        Language dummy = languageRegistry().findLanguageByTerseName("dummy");

        LanguageVersion dummy17 = dummy.getVersion("1.7");
        Assert.assertNotNull(dummy17);
        Assert.assertEquals("1.7", dummy17.getVersion());

        LanguageVersion dummy7 = dummy.getVersion("7");
        Assert.assertNotNull(dummy7);
        Assert.assertEquals("1.7", dummy17.getVersion());
        Assert.assertSame(dummy17, dummy7);
    }
}
